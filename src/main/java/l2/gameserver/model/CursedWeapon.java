//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Skill.AddedSkill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExRedSky;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.ShortCutInit;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class CursedWeapon {
  private final String _name;
  private String _transformationName;
  private int _itemId;
  private int _skillMaxLevel;
  private int _skillId;
  private int _dropRate;
  private int _disapearChance;
  private int _durationMin;
  private int _durationMax;
  private int _durationLost;
  private int _transformationId;
  private int _transformationTemplateId;
  private int _stageKills;
  private int _nbKills = 0;
  private int _playerKarma = 0;
  private int _playerPkKills = 0;
  private CursedWeapon.CursedWeaponState _state;
  private Location _loc;
  private long _endTime;
  private long _owner;
  private ItemInstance _item;

  public CursedWeapon(int itemId, int skillId, String name) {
    this._state = CursedWeapon.CursedWeaponState.NONE;
    this._loc = null;
    this._endTime = 0L;
    this._owner = 0L;
    this._item = null;
    this._name = name;
    this._itemId = itemId;
    this._skillId = skillId;
    this._skillMaxLevel = SkillTable.getInstance().getMaxLevel(this._skillId);
  }

  public void initWeapon() {
    this.zeroOwner();
    this.setState(CursedWeapon.CursedWeaponState.NONE);
    this._endTime = 0L;
    this._item = null;
    this._nbKills = 0;
  }

  public void create(NpcInstance attackable, Player killer) {
    this._item = ItemFunctions.createItem(this._itemId);
    if (this._item != null) {
      this.zeroOwner();
      this.setState(CursedWeapon.CursedWeaponState.DROPPED);
      if (this._endTime == 0L) {
        this._endTime = System.currentTimeMillis() + (long)(this.getRndDuration() * '\uea60');
      }

      this._item.dropToTheGround(attackable, Location.findPointToStay(attackable, 100));
      this._loc = this._item.getLoc();
      this._item.setDropTime(0L);
      World.broadcast(new L2GameServerPacket[]{new ExRedSky(10), new Earthquake(killer.getLoc(), 30, 12)});
    }

  }

  public boolean dropIt(NpcInstance attackable, Player killer, Player owner) {
    if (Rnd.chance(this._disapearChance)) {
      return false;
    } else {
      Player player = this.getOnlineOwner();
      if (player == null) {
        if (owner == null) {
          return false;
        }

        player = owner;
      }

      ItemInstance oldItem;
      if ((oldItem = player.getInventory().removeItemByItemId(this._itemId, 1L)) == null) {
        return false;
      } else {
        player.setKarma(this._playerKarma);
        player.setPkKills(this._playerPkKills);
        player.setCursedWeaponEquippedId(0);
        player.setTransformation(0);
        this.clearSkills(player);
        player.setTransformationName((String)null);
        player.validateLocation(0);
        Skill skill = SkillTable.getInstance().getInfo(this._skillId, player.getSkillLevel(this._skillId));
        if (skill != null) {
          AddedSkill[] var7 = skill.getAddedSkills();
          int var8 = var7.length;

          for(int var9 = 0; var9 < var8; ++var9) {
            AddedSkill s = var7[var9];
            player.removeSkillById(s.id);
          }
        }

        player.removeSkillById(this._skillId);
        player.abortAttack(true, false);
        this.zeroOwner();
        this.setState(CursedWeapon.CursedWeaponState.DROPPED);
        oldItem.dropToTheGround(player, Location.findPointToStay(player, 100));
        this._loc = oldItem.getLoc();
        oldItem.setDropTime(0L);
        this._item = oldItem;
        player.sendPacket((new SystemMessage(298)).addItemName(oldItem.getItemId()));
        player.broadcastUserInfo(true);
        player.broadcastPacket(new L2GameServerPacket[]{new Earthquake(player.getLoc(), 30, 12)});
        return true;
      }
    }
  }

  public void clearSkills(Player player) {
    if (!player._transformationSkills.isEmpty()) {
      Iterator var2 = player._transformationSkills.values().iterator();

      while(var2.hasNext()) {
        Skill s = (Skill)var2.next();
        if (!s.isCommon() && !SkillAcquireHolder.getInstance().isSkillPossible(player, s) && !s.isHeroic()) {
          player.removeSkill(s);
        }
      }

      player._transformationSkills.clear();
      player.sendPacket(new IStaticPacket[]{new SkillList(player), new ShortCutInit(player)});
      var2 = player.getAutoSoulShot().iterator();

      while(var2.hasNext()) {
        int shotId = (Integer)var2.next();
        player.sendPacket(new ExAutoSoulShot(shotId, true));
      }
    }

  }

  public void giveSkill(Player player) {
    Iterator var2 = this.getSkills(player).iterator();

    while(var2.hasNext()) {
      Skill s = (Skill)var2.next();
      player.addSkill(s, false);
      player._transformationSkills.put(s.getId(), s);
    }

    player.sendPacket(new SkillList(player));
  }

  private Collection<Skill> getSkills(Player player) {
    int level = 1 + this._nbKills / this._stageKills;
    if (level > this._skillMaxLevel) {
      level = this._skillMaxLevel;
    }

    Skill skill = SkillTable.getInstance().getInfo(this._skillId, level);
    List<Skill> ret = new ArrayList();
    ret.add(skill);
    AddedSkill[] var5 = skill.getAddedSkills();
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      AddedSkill s = var5[var7];
      ret.add(SkillTable.getInstance().getInfo(s.id, s.level));
    }

    return ret;
  }

  public boolean reActivate() {
    if (this.getTimeLeft() <= 0L) {
      if (this.getPlayerId() != 0) {
        this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
      }

      return false;
    } else {
      if (this.getPlayerId() == 0) {
        if (this._loc == null || (this._item = ItemFunctions.createItem(this._itemId)) == null) {
          return false;
        }

        this._item.dropMe((Creature)null, this._loc);
        this._item.setDropTime(0L);
        this.setState(CursedWeapon.CursedWeaponState.DROPPED);
      } else {
        this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
      }

      return true;
    }
  }

  public void activate(Player player, ItemInstance item) {
    if (this.isDropped() || this.getPlayerId() != player.getObjectId()) {
      this._playerKarma = player.getKarma();
      this._playerPkKills = player.getPkKills();
    }

    this.setPlayer(player);
    this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
    player.leaveParty();
    if (player.isMounted()) {
      player.setMount(0, 0, 0);
    }

    this._item = item;
    player.getInventory().setPaperdollItem(8, (ItemInstance)null);
    player.getInventory().setPaperdollItem(7, (ItemInstance)null);
    player.getInventory().setPaperdollItem(7, this._item);
    player.sendPacket((new SystemMessage(49)).addItemName(this._item.getItemId()));
    player.setTransformation(0);
    player.setCursedWeaponEquippedId(this._itemId);
    player.setTransformation(this._transformationId);
    if (!player.isOlyParticipant() && player.isHero() && player.getBaseClassId() == player.getActiveClassId()) {
      player._transformationSkills.put(395, SkillTable.getInstance().getInfo(395, 1));
      player._transformationSkills.put(396, SkillTable.getInstance().getInfo(396, 1));
      player._transformationSkills.put(1374, SkillTable.getInstance().getInfo(1374, 1));
      player._transformationSkills.put(1375, SkillTable.getInstance().getInfo(1375, 1));
      player._transformationSkills.put(1376, SkillTable.getInstance().getInfo(1376, 1));
    }

    player.setTransformationName(this._transformationName);
    player.setTransformationTemplate(this._transformationTemplateId);
    player.setKarma(9999999);
    player.setPkKills(this._nbKills);
    if (this._endTime == 0L) {
      this._endTime = System.currentTimeMillis() + (long)(this.getRndDuration() * '\uea60');
    }

    this.giveSkill(player);
    player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
    player.setCurrentCp((double)player.getMaxCp());
    player.broadcastUserInfo(true);
  }

  public void increaseKills() {
    Player player = this.getOnlineOwner();
    if (player != null) {
      ++this._nbKills;
      player.setPkKills(this._nbKills);
      player.updateStats();
      if (this._nbKills % this._stageKills == 0 && this._nbKills <= this._stageKills * (this._skillMaxLevel - 1)) {
        this.giveSkill(player);
      }

      this._endTime -= (long)(this._durationLost * '\uea60');
    }
  }

  public void giveSkillAndUpdateStats() {
    Player player = this.getOnlineOwner();
    if (player != null) {
      if (this._nbKills <= this._stageKills * (this._skillMaxLevel - 1)) {
        this.giveSkill(player);
      }

      player.updateStats();
    }
  }

  public void setDisapearChance(int disapearChance) {
    this._disapearChance = disapearChance;
  }

  public void setDropRate(int dropRate) {
    this._dropRate = dropRate;
  }

  public void setDurationMin(int duration) {
    this._durationMin = duration;
  }

  public void setDurationMax(int duration) {
    this._durationMax = duration;
  }

  public void setDurationLost(int durationLost) {
    this._durationLost = durationLost;
  }

  public void setStageKills(int stageKills) {
    this._stageKills = stageKills;
  }

  public void setTransformationId(int transformationId) {
    this._transformationId = transformationId;
  }

  public int getTransformationId() {
    return this._transformationId;
  }

  public void setTransformationTemplateId(int transformationTemplateId) {
    this._transformationTemplateId = transformationTemplateId;
  }

  public void setTransformationName(String name) {
    this._transformationName = name;
  }

  public void setNbKills(int nbKills) {
    this._nbKills = nbKills;
  }

  public void setPlayerId(int playerId) {
    this._owner = playerId == 0 ? 0L : GameObjectsStorage.objIdNoStore(playerId);
  }

  public void setPlayerKarma(int playerKarma) {
    this._playerKarma = playerKarma;
  }

  public void setPlayerPkKills(int playerPkKills) {
    this._playerPkKills = playerPkKills;
  }

  public void setState(CursedWeapon.CursedWeaponState state) {
    this._state = state;
  }

  public void setEndTime(long endTime) {
    this._endTime = endTime;
  }

  public void setPlayer(Player player) {
    if (player != null) {
      this._owner = player.getStoredId();
    } else if (this._owner != 0L) {
      this.setPlayerId(this.getPlayerId());
    }

  }

  private void zeroOwner() {
    this._owner = 0L;
    this._playerKarma = 0;
    this._playerPkKills = 0;
  }

  public void setItem(ItemInstance item) {
    this._item = item;
  }

  public void setLoc(Location loc) {
    this._loc = loc;
  }

  public CursedWeapon.CursedWeaponState getState() {
    return this._state;
  }

  public boolean isActivated() {
    return this.getState() == CursedWeapon.CursedWeaponState.ACTIVATED;
  }

  public boolean isDropped() {
    return this.getState() == CursedWeapon.CursedWeaponState.DROPPED;
  }

  public long getEndTime() {
    return this._endTime;
  }

  public String getName() {
    return this._name;
  }

  public int getItemId() {
    return this._itemId;
  }

  public ItemInstance getItem() {
    return this._item;
  }

  public int getSkillId() {
    return this._skillId;
  }

  public int getDropRate() {
    return this._dropRate;
  }

  public int getPlayerId() {
    return this._owner == 0L ? 0 : GameObjectsStorage.getStoredObjectId(this._owner);
  }

  public Player getPlayer() {
    return this._owner == 0L ? null : GameObjectsStorage.getAsPlayer(this._owner);
  }

  public int getPlayerKarma() {
    return this._playerKarma;
  }

  public int getPlayerPkKills() {
    return this._playerPkKills;
  }

  public int getNbKills() {
    return this._nbKills;
  }

  public int getStageKills() {
    return this._stageKills;
  }

  public Location getLoc() {
    return this._loc;
  }

  public int getRndDuration() {
    if (this._durationMin > this._durationMax) {
      this._durationMax = 2 * this._durationMin;
    }

    return Rnd.get(this._durationMin, this._durationMax);
  }

  public boolean isActive() {
    return this.isActivated() || this.isDropped();
  }

  public int getLevel() {
    return Math.min(1 + this._nbKills / this._stageKills, this._skillMaxLevel);
  }

  public long getTimeLeft() {
    return this._endTime - System.currentTimeMillis();
  }

  public Location getWorldPosition() {
    if (this.isActivated()) {
      Player player = this.getOnlineOwner();
      if (player != null) {
        return player.getLoc();
      }
    } else if (this.isDropped() && this._item != null) {
      return this._item.getLoc();
    }

    return null;
  }

  public Player getOnlineOwner() {
    Player player = this.getPlayer();
    return player != null && player.isOnline() ? player : null;
  }

  public boolean isOwned() {
    return this._owner != 0L;
  }

  public static enum CursedWeaponState {
    NONE,
    ACTIVATED,
    DROPPED;

    private CursedWeaponState() {
    }
  }
}
