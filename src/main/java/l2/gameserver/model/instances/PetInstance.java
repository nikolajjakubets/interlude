//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.PetData;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetInstance extends Summon {
  private static final Logger _log = LoggerFactory.getLogger(PetInstance.class);
  private static final int DELUXE_FOOD_FOR_STRIDER = 5169;
  private final int _controlItemObjId;
  private int _curFed;
  protected PetData _data;
  private Future<?> _feedTask;
  protected PetInventory _inventory;
  private int _level;
  private boolean _respawned;
  private int lostExp;

  public static final PetInstance restore(ItemInstance control, NpcTemplate template, Player owner) {
    PetInstance pet = null;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    Object var7;
    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT objId, name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?");
      statement.setInt(1, control.getObjectId());
      rset = statement.executeQuery();
      if (rset.next()) {
        if (!PetDataTable.isBabyPet(template.getNpcId()) && !PetDataTable.isImprovedBabyPet(template.getNpcId())) {
          pet = new PetInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));
        } else {
          pet = new PetBabyInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));
        }

        ((PetInstance)pet).setRespawned(true);
        String name = rset.getString("name");
        ((PetInstance)pet).setName(name != null && !name.isEmpty() ? name : template.name);
        ((PetInstance)pet).setCurrentHpMp(rset.getDouble("curHp"), (double)rset.getInt("curMp"), true);
        ((PetInstance)pet).setCurrentCp((double)((PetInstance)pet).getMaxCp());
        ((PetInstance)pet).setSp(rset.getInt("sp"));
        ((PetInstance)pet).setCurrentFed(rset.getInt("fed"));
        return (PetInstance)pet;
      }

      if (!PetDataTable.isBabyPet(template.getNpcId()) && !PetDataTable.isImprovedBabyPet(template.getNpcId())) {
        pet = new PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
      } else {
        pet = new PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, control);
      }

      var7 = pet;
    } catch (Exception var12) {
      _log.error("Could not restore Pet data from item: " + control + "!", var12);
      Object var8 = null;
      return (PetInstance)var8;
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return (PetInstance)var7;
  }

  public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control) {
    this(objectId, template, owner, control, 0, 0L);
  }

  public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int currentLevel, long exp) {
    super(objectId, template, owner);
    this._controlItemObjId = control.getObjectId();
    this._exp = exp;
    this._level = control.getEnchantLevel();
    if (this._level <= 0) {
      if (template.npcId == 12564) {
        this._level = owner.getLevel();
      } else {
        this._level = template.level;
      }

      this._exp = this.getExpForThisLevel();
    }

    int minLevel = PetDataTable.getMinLevel(template.npcId);
    if (this._level < minLevel) {
      this._level = minLevel;
    }

    if (this._exp < this.getExpForThisLevel()) {
      this._exp = this.getExpForThisLevel();
    }

    while(this._exp >= this.getExpForNextLevel() && this._level < Experience.getMaxLevel()) {
      ++this._level;
    }

    while(this._exp < this.getExpForThisLevel() && this._level > minLevel) {
      --this._level;
    }

    this._data = PetDataTable.getInstance().getInfo(template.npcId, this._level);
    this._inventory = new PetInventory(this);
  }

  protected void onSpawn() {
    super.onSpawn();
    this.startFeed(false);
  }

  protected void onDespawn() {
    super.onSpawn();
    this.stopFeed();
  }

  public boolean tryFeedItem(ItemInstance item) {
    if (item == null) {
      return false;
    } else {
      boolean deluxFood = PetDataTable.isStrider(this.getNpcId()) && item.getItemId() == 5169;
      if (this.getFoodId() != item.getItemId() && !deluxFood) {
        return false;
      } else {
        int newFed = Math.min(this.getMaxFed(), this.getCurrentFed() + Math.max(this.getMaxFed() * this.getAddFed() * (deluxFood ? 2 : 1) / 100, 1));
        if (this.getCurrentFed() != newFed && this.getInventory().destroyItem(item, 1L)) {
          this.getPlayer().sendPacket((new SystemMessage(1527)).addItemName(item.getItemId()));
          this.setCurrentFed(newFed);
          this.sendStatusUpdate();
        }

        return true;
      }
    }
  }

  public boolean tryFeed() {
    ItemInstance food = this.getInventory().getItemByItemId(this.getFoodId());
    if (food == null && PetDataTable.isStrider(this.getNpcId())) {
      food = this.getInventory().getItemByItemId(5169);
    }

    return this.tryFeedItem(food);
  }

  public void addExpAndSp(long addToExp, long addToSp) {
    Player owner = this.getPlayer();
    this._exp += addToExp;
    this._sp = (int)((long)this._sp + addToSp);
    if (this._exp > this.getMaxExp()) {
      this._exp = this.getMaxExp();
    }

    if (addToExp > 0L || addToSp > 0L) {
      owner.sendPacket((new SystemMessage(1014)).addNumber(addToExp));
    }

    int old_level;
    for(old_level = this._level; this._exp >= this.getExpForNextLevel() && this._level < Experience.getMaxLevel(); ++this._level) {
    }

    while(this._exp < this.getExpForThisLevel() && this._level > this.getMinLevel()) {
      --this._level;
    }

    if (old_level < this._level) {
      owner.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2PetInstance.PetLevelUp", owner, new Object[0])).addNumber((long)this._level));
      this.broadcastPacket(new L2GameServerPacket[]{new SocialAction(this.getObjectId(), 15)});
      this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp());
    }

    if (old_level != this._level) {
      this.updateControlItem();
      this.updateData();
    }

    if (addToExp > 0L || addToSp > 0L) {
      this.sendStatusUpdate();
    }

  }

  public boolean consumeItem(int itemConsumeId, long itemCount) {
    return this.getPlayer().getInventory().destroyItemByItemId(itemConsumeId, itemCount);
  }

  private void deathPenalty() {
    if (!this.isInZoneBattle()) {
      int lvl = this.getLevel();
      double percentLost = -0.07D * (double)lvl + 6.5D;
      this.lostExp = (int)Math.round((double)(this.getExpForNextLevel() - this.getExpForThisLevel()) * percentLost / 100.0D);
      this.addExpAndSp((long)(-this.lostExp), 0L);
    }
  }

  private void destroyControlItem() {
    Player owner = this.getPlayer();
    if (this.getControlItemObjId() != 0) {
      if (owner.getInventory().destroyItemByObjectId(this.getControlItemObjId(), 1L)) {
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
          statement.setInt(1, this.getControlItemObjId());
          statement.execute();
        } catch (Exception var8) {
          _log.warn("could not delete pet:" + var8);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }

      }
    }
  }

  protected void onDeath(Creature killer) {
    super.onDeath(killer);
    Player owner = this.getPlayer();
    owner.sendPacket(Msg.THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS);
    this.startDecay(86400000L);
    this.stopFeed();
    this.deathPenalty();
  }

  public void doPickupItem(GameObject object) {
    Player owner = this.getPlayer();
    this.stopMove();
    if (object.isItem()) {
      ItemInstance item = (ItemInstance)object;
      if (item.isCursed()) {
        owner.sendPacket((new SystemMessage(56)).addItemName(item.getItemId()));
      } else {
        synchronized(item) {
          if (!item.isVisible()) {
            return;
          }

          if (item.isHerb()) {
            Skill[] skills = item.getTemplate().getAttachedSkills();
            if (skills.length > 0) {
              Skill[] var6 = skills;
              int var7 = skills.length;

              for(int var8 = 0; var8 < var7; ++var8) {
                Skill skill = var6[var8];
                this.altUseSkill(skill, this);
              }
            }

            item.deleteMe();
            return;
          }

          if (!this.getInventory().validateWeight(item)) {
            this.sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
            return;
          }

          if (!this.getInventory().validateCapacity(item)) {
            this.sendPacket(Msg.DUE_TO_THE_VOLUME_LIMIT_OF_THE_PETS_INVENTORY_NO_MORE_ITEMS_CAN_BE_PLACED_THERE);
            return;
          }

          if (!item.getTemplate().getHandler().pickupItem(this, item)) {
            return;
          }

          FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)item.getAttachment() : null;
          if (attachment != null) {
            return;
          }

          item.pickupMe();
        }

        if (owner.getParty() != null && owner.getParty().getLootDistribution() != 0) {
          owner.getParty().distributeItem(owner, item, (NpcInstance)null);
        } else {
          Log.LogItem(owner, ItemLog.PetPickup, item);
          this.getInventory().addItem(item);
          this.sendChanges();
          this.broadcastPickUpMsg(item);
          item.pickupMe();
        }

        this.broadcastPickUpMsg(item);
      }
    }
  }

  public void doRevive(double percent) {
    this.restoreExp(percent);
    this.doRevive();
  }

  public void doRevive() {
    this.stopDecay();
    super.doRevive();
    this.startFeed(false);
    this.setRunning();
  }

  public int getAccuracy() {
    return (int)this.calcStat(Stats.ACCURACY_COMBAT, (double)this._data.getAccuracy(), (Creature)null, (Skill)null);
  }

  public ItemInstance getActiveWeaponInstance() {
    return null;
  }

  public WeaponTemplate getActiveWeaponItem() {
    return null;
  }

  public ItemInstance getControlItem() {
    Player owner = this.getPlayer();
    if (owner == null) {
      return null;
    } else {
      int item_obj_id = this.getControlItemObjId();
      return item_obj_id == 0 ? null : owner.getInventory().getItemByObjectId(item_obj_id);
    }
  }

  public int getControlItemObjId() {
    return this._controlItemObjId;
  }

  public int getCriticalHit(Creature target, Skill skill) {
    return (int)this.calcStat(Stats.CRITICAL_BASE, (double)this._data.getCritical(), target, skill);
  }

  public int getCurrentFed() {
    return this._curFed;
  }

  public int getEvasionRate(Creature target) {
    return (int)this.calcStat(Stats.EVASION_RATE, (double)this._data.getEvasion(), target, (Skill)null);
  }

  public long getExpForNextLevel() {
    return PetDataTable.getInstance().getInfo(this.getNpcId(), this._level + 1).getExp();
  }

  public long getExpForThisLevel() {
    return PetDataTable.getInstance().getInfo(this.getNpcId(), this._level).getExp();
  }

  public int getFoodId() {
    return this._data.getFoodId();
  }

  public int getAddFed() {
    return this._data.getAddFed();
  }

  public PetInventory getInventory() {
    return this._inventory;
  }

  public long getWearedMask() {
    return this._inventory.getWearedMask();
  }

  public final int getLevel() {
    return this._level;
  }

  public void setLevel(int level) {
    this._level = level;
  }

  public double getLevelMod() {
    return (89.0D + (double)this.getLevel()) / 100.0D;
  }

  public int getMinLevel() {
    return this._data.getMinLevel();
  }

  public long getMaxExp() {
    return PetDataTable.getInstance().getInfo(this.getNpcId(), Experience.getMaxLevel() + 1).getExp();
  }

  public int getMaxFed() {
    return this._data.getFeedMax();
  }

  public int getMaxLoad() {
    return (int)this.calcStat(Stats.MAX_LOAD, (double)this._data.getMaxLoad(), (Creature)null, (Skill)null);
  }

  public int getInventoryLimit() {
    return Config.ALT_PET_INVENTORY_LIMIT;
  }

  public int getMaxHp() {
    return (int)this.calcStat(Stats.MAX_HP, (double)this._data.getHP(), (Creature)null, (Skill)null);
  }

  public int getMaxMp() {
    return (int)this.calcStat(Stats.MAX_MP, (double)this._data.getMP(), (Creature)null, (Skill)null);
  }

  public int getPAtk(Creature target) {
    double mod = BaseStats.STR.calcBonus(this) * this.getLevelMod();
    return (int)this.calcStat(Stats.POWER_ATTACK, (double)this._data.getPAtk() / mod, target, (Skill)null);
  }

  public int getPDef(Creature target) {
    double mod = this.getLevelMod();
    return (int)this.calcStat(Stats.POWER_DEFENCE, (double)this._data.getPDef() / mod, target, (Skill)null);
  }

  public int getMAtk(Creature target, Skill skill) {
    double ib = BaseStats.INT.calcBonus(this);
    double lvlb = this.getLevelMod();
    double mod = lvlb * lvlb * ib * ib;
    return (int)this.calcStat(Stats.MAGIC_ATTACK, (double)this._data.getMAtk() / mod, target, skill);
  }

  public int getMDef(Creature target, Skill skill) {
    double mod = BaseStats.MEN.calcBonus(this) * this.getLevelMod();
    return (int)this.calcStat(Stats.MAGIC_DEFENCE, (double)this._data.getMDef() / mod, target, skill);
  }

  public int getPAtkSpd() {
    return (int)this.calcStat(Stats.POWER_ATTACK_SPEED, this.calcStat(Stats.ATK_BASE, (double)this._data.getAtkSpeed(), (Creature)null, (Skill)null), (Creature)null, (Skill)null);
  }

  public int getMAtkSpd() {
    return (int)this.calcStat(Stats.MAGIC_ATTACK_SPEED, (double)this._data.getCastSpeed(), (Creature)null, (Skill)null);
  }

  public int getRunSpeed() {
    return this.getSpeed(this._data.getSpeed());
  }

  public int getSoulshotConsumeCount() {
    return PetDataTable.getSoulshots(this.getNpcId());
  }

  public int getSpiritshotConsumeCount() {
    return PetDataTable.getSpiritshots(this.getNpcId());
  }

  public ItemInstance getSecondaryWeaponInstance() {
    return null;
  }

  public WeaponTemplate getSecondaryWeaponItem() {
    return null;
  }

  public int getSkillLevel(int skillId) {
    if (this._skills != null && this._skills.get(skillId) != null) {
      int lvl = this.getLevel();
      return lvl > 70 ? 7 + (lvl - 70) / 5 : lvl / 10;
    } else {
      return -1;
    }
  }

  public int getSummonType() {
    return 2;
  }

  public NpcTemplate getTemplate() {
    return (NpcTemplate)this._template;
  }

  public boolean isMountable() {
    return this._data.isMountable();
  }

  public boolean isRespawned() {
    return this._respawned;
  }

  public void restoreExp(double percent) {
    if (this.lostExp != 0) {
      this.addExpAndSp((long)((double)this.lostExp * percent / 100.0D), 0L);
      this.lostExp = 0;
    }

  }

  public void setCurrentFed(int num) {
    this._curFed = Math.min(this.getMaxFed(), Math.max(0, num));
  }

  public void setRespawned(boolean respawned) {
    this._respawned = respawned;
  }

  public void setSp(int sp) {
    this._sp = sp;
  }

  public void startFeed(boolean battleFeed) {
    boolean first = this._feedTask == null;
    this.stopFeed();
    if (!this.isDead()) {
      int feedTime = Math.max(first ? 15000 : 1000, '\uea60' / (battleFeed ? this._data.getFeedBattle() : this._data.getFeedNormal()));
      this._feedTask = ThreadPoolManager.getInstance().schedule(new PetInstance.FeedTask(), (long)feedTime);
    }

  }

  private void stopFeed() {
    if (this._feedTask != null) {
      this._feedTask.cancel(false);
      this._feedTask = null;
    }

  }

  public void store() {
    if (this.getControlItemObjId() != 0 && this._exp != 0L) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        String req;
        if (!this.isRespawned()) {
          req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,objId,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?)";
        } else {
          req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,objId=? WHERE item_obj_id = ?";
        }

        statement = con.prepareStatement(req);
        statement.setString(1, this.getName().equalsIgnoreCase(this.getTemplate().name) ? "" : this.getName());
        statement.setInt(2, this._level);
        statement.setDouble(3, this.getCurrentHp());
        statement.setDouble(4, this.getCurrentMp());
        statement.setLong(5, this._exp);
        statement.setLong(6, (long)this._sp);
        statement.setInt(7, this._curFed);
        statement.setInt(8, this.getObjectId());
        statement.setInt(9, this._controlItemObjId);
        statement.executeUpdate();
      } catch (Exception var7) {
        _log.error("Could not store pet data!", var7);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      this._respawned = true;
    }
  }

  protected void onDecay() {
    this.getInventory().store();
    this.destroyControlItem();
    super.onDecay();
  }

  public void unSummon() {
    this.stopFeed();
    this.getInventory().store();
    this.store();
    super.unSummon();
  }

  public void updateControlItem() {
    ItemInstance controlItem = this.getControlItem();
    if (controlItem != null) {
      controlItem.setEnchantLevel(this._level);
      controlItem.setDamaged(this.isDefaultName() ? 0 : 1);
      Player owner = this.getPlayer();
      owner.sendPacket((new InventoryUpdate()).addModifiedItem(controlItem));
    }
  }

  private void updateData() {
    this._data = PetDataTable.getInstance().getInfo(this.getTemplate().npcId, this._level);
  }

  public double getExpPenalty() {
    return PetDataTable.getExpPenalty(this.getTemplate().npcId);
  }

  public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
    Player owner = this.getPlayer();
    if (crit) {
      owner.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
    }

    if (miss) {
      owner.sendPacket(new SystemMessage(43));
    } else {
      owner.sendPacket((new SystemMessage(1015)).addNumber(damage));
    }

  }

  public void displayReceiveDamageMessage(Creature attacker, int damage) {
    Player owner = this.getPlayer();
    if (!this.isDead()) {
      SystemMessage sm = new SystemMessage(1016);
      if (attacker.isNpc()) {
        sm.addNpcName(((NpcInstance)attacker).getTemplate().npcId);
      } else {
        sm.addString(attacker.getName());
      }

      sm.addNumber((long)damage);
      owner.sendPacket(sm);
    }

  }

  public int getFormId() {
    switch(this.getNpcId()) {
      case 16025:
      case 16037:
      case 16041:
      case 16042:
        if (this.getLevel() >= 70) {
          return 3;
        } else if (this.getLevel() >= 65) {
          return 2;
        } else if (this.getLevel() >= 60) {
          return 1;
        }
      default:
        return 0;
    }
  }

  public boolean isPet() {
    return true;
  }

  public boolean isDefaultName() {
    return StringUtils.isEmpty(this._name) || this.getName().equalsIgnoreCase(this.getTemplate().name);
  }

  public int getEffectIdentifier() {
    return 0;
  }

  class FeedTask extends RunnableImpl {
    FeedTask() {
    }

    public void runImpl() throws Exception {
      Player owner = PetInstance.this.getPlayer();

      while((double)PetInstance.this.getCurrentFed() <= 0.55D * (double)PetInstance.this.getMaxFed() && PetInstance.this.tryFeed()) {
      }

      if ((double)PetInstance.this.getCurrentFed() <= 0.1D * (double)PetInstance.this.getMaxFed()) {
        owner.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2PetInstance.UnSummonHungryPet", owner, new Object[0]));
        PetInstance.this.unSummon();
      } else {
        PetInstance.this.setCurrentFed(PetInstance.this.getCurrentFed() - 5);
        PetInstance.this.sendStatusUpdate();
        PetInstance.this.startFeed(PetInstance.this.isInCombat());
      }
    }
  }
}
