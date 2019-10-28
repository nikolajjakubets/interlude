//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import gnu.trove.TIntObjectIterator;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.SummonAI;
import l2.gameserver.dao.EffectsDAO;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.recorder.SummonStatsChangeRecorder;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Events;
import l2.gameserver.stats.Stats;
import l2.gameserver.taskmanager.DecayTaskManager;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public abstract class Summon extends Playable {
  private static final int SUMMON_DISAPPEAR_RANGE = 2500;
  private final Player _owner;
  private int _spawnAnimation = 2;
  protected long _exp = 0L;
  protected int _sp = 0;
  private int _maxLoad;
  private int _spsCharged;
  private boolean _follow = true;
  private boolean _depressed = false;
  private boolean _ssCharged = false;
  private Future<?> _decayTask;
  private Future<?> _updateEffectIconsTask;
  private ScheduledFuture<?> _broadcastCharInfoTask;
  private Future<?> _petInfoTask;

  public Summon(int objectId, NpcTemplate template, Player owner) {
    super(objectId, template);
    this._owner = owner;
    if (template.getSkills().size() > 0) {
      TIntObjectIterator iterator = template.getSkills().iterator();

      while(iterator.hasNext()) {
        iterator.advance();
        this.addSkill((Skill)iterator.value());
      }
    }

    this.setLoc(Location.findPointToStay(owner, (int)owner.getColRadius(), 100));
  }

//  public HardReference<? extends Summon> getRef() {
//    return super.getRef();
//  }

  protected void onSpawn() {
    super.onSpawn();
    this._spawnAnimation = 0;
    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
  }

  public SummonAI getAI() {
    if (this._ai == null) {
      synchronized(this) {
        if (this._ai == null) {
          this._ai = new SummonAI(this);
        }
      }
    }

    return (SummonAI)this._ai;
  }

  public NpcTemplate getTemplate() {
    return (NpcTemplate)this._template;
  }

  public boolean isUndead() {
    return this.getTemplate().isUndead();
  }

  public abstract int getSummonType();

  public abstract int getEffectIdentifier();

  public boolean isMountable() {
    return false;
  }

  public void onAction(Player player, boolean shift) {
    if (this.isFrozen()) {
      player.sendPacket(ActionFail.STATIC);
    } else if (Events.onAction(player, this, shift)) {
      player.sendPacket(ActionFail.STATIC);
    } else {
      Player owner = this.getPlayer();
      if (player.getTarget() != this) {
        player.setTarget(this);
        if (player.getTarget() == this) {
          player.sendPacket(new IStaticPacket[]{new MyTargetSelected(this.getObjectId(), 0), this.makeStatusUpdate(new int[]{9, 10, 11, 12})});
        } else {
          player.sendPacket(ActionFail.STATIC);
        }
      } else if (player == owner) {
        player.sendPacket((new PetInfo(this)).update());
        if (!player.isActionsDisabled()) {
          player.sendPacket(new PetStatusShow(this));
        }

        player.sendPacket(ActionFail.STATIC);
      } else if (this.isAutoAttackable(player)) {
        player.getAI().Attack(this, false, shift);
      } else if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
        if (!shift) {
          player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this);
        } else {
          player.sendActionFailed();
        }
      } else {
        player.sendActionFailed();
      }

    }
  }

  public long getExpForThisLevel() {
    return Experience.getExpForLevel(this.getLevel());
  }

  public long getExpForNextLevel() {
    return Experience.getExpForLevel(this.getLevel() + 1);
  }

  public int getNpcId() {
    return this.getTemplate().npcId;
  }

  public final long getExp() {
    return this._exp;
  }

  public final void setExp(long exp) {
    this._exp = exp;
  }

  public final int getSp() {
    return this._sp;
  }

  public void setSp(int sp) {
    this._sp = sp;
  }

  public int getMaxLoad() {
    return this._maxLoad;
  }

  public void setMaxLoad(int maxLoad) {
    this._maxLoad = maxLoad;
  }

  public int getBuffLimit() {
    Player owner = this.getPlayer();
    return (int)this.calcStat(Stats.BUFF_LIMIT, (double)owner.getBuffLimit(), (Creature)null, (Skill)null);
  }

  public abstract int getCurrentFed();

  public abstract int getMaxFed();

  protected void onDeath(Creature killer) {
    super.onDeath((Creature)killer);
    this.startDecay(8500L);
    Player owner = this.getPlayer();
    if (killer != null && killer != owner && killer != this && !this.isInZoneBattle() && !((Creature)killer).isInZoneBattle()) {
      if (killer instanceof Summon) {
        killer = ((Creature)killer).getPlayer();
      }

      if (killer != null) {
        if (((Creature)killer).isPlayer()) {
          Player pk = (Player)killer;
          if (this.isInZone(ZoneType.SIEGE)) {
            return;
          }

          if (this.isInZone(ZoneType.fun)) {
            return;
          }

          DuelEvent duelEvent = (DuelEvent)this.getEvent(DuelEvent.class);
          if (owner.getPvpFlag() <= 0 && !owner.atMutualWarWith(pk)) {
            if ((duelEvent == null || duelEvent != pk.getEvent(DuelEvent.class)) && this.getKarma() <= 0) {
              this.doPurePk(pk);
            }
          } else {
            pk.setPvpKills(pk.getPvpKills() + 1);
          }

          pk.sendChanges();
        }

      }
    }
  }

  protected void startDecay(long delay) {
    this.stopDecay();
    this._decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
  }

  protected void stopDecay() {
    if (this._decayTask != null) {
      this._decayTask.cancel(false);
      this._decayTask = null;
    }

  }

  protected void onDecay() {
    this.deleteMe();
  }

  public void endDecayTask() {
    this.stopDecay();
    this.doDecay();
  }

  public void broadcastStatusUpdate() {
    if (this.needStatusUpdate()) {
      Player owner = this.getPlayer();
      this.sendStatusUpdate();
      StatusUpdate su = this.makeStatusUpdate(new int[]{10, 9});
      this.broadcastToStatusListeners(new L2GameServerPacket[]{su});
    }
  }

  public void sendStatusUpdate() {
    Player owner = this.getPlayer();
    owner.sendPacket(new PetStatusUpdate(this));
  }

  protected void onDelete() {
    Player owner = this.getPlayer();
    owner.sendPacket(new PetDelete(this.getObjectId(), this.getSummonType()));
    owner.setPet((Summon)null);
    this.stopDecay();
    super.onDelete();
  }

  public void unSummon() {
    this.deleteMe();
  }

  public void saveEffects() {
    Player owner = this.getPlayer();
    if (owner != null) {
      if (owner.isOlyParticipant()) {
        this.getEffectList().stopAllEffects();
      }

      EffectsDAO.getInstance().insert(this);
    }
  }

  public void setFollowMode(boolean state) {
    Player owner = this.getPlayer();
    this._follow = state;
    if (this._follow) {
      if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner);
      }
    } else if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
      this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

  }

  public boolean isFollowMode() {
    return this._follow;
  }

  public void updateEffectIcons() {
    if (Config.USER_INFO_INTERVAL == 0L) {
      if (this._updateEffectIconsTask != null) {
        this._updateEffectIconsTask.cancel(false);
        this._updateEffectIconsTask = null;
      }

      this.updateEffectIconsImpl();
    } else if (this._updateEffectIconsTask == null) {
      this._updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new Summon.UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
    }
  }

  public void updateEffectIconsImpl() {
    Player owner = this.getPlayer();
    PartySpelled ps = new PartySpelled(this, true);
    Party party = owner.getParty();
    if (party != null) {
      party.broadCast(new IStaticPacket[]{ps});
    } else {
      owner.sendPacket(ps);
    }

  }

  public int getControlItemObjId() {
    return 0;
  }

  public PetInventory getInventory() {
    return null;
  }

  public void doPickupItem(GameObject object) {
  }

  public void doRevive() {
    super.doRevive();
    this.setRunning();
    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    this.setFollowMode(true);
  }

  public ItemInstance getActiveWeaponInstance() {
    return null;
  }

  public WeaponTemplate getActiveWeaponItem() {
    return null;
  }

  public ItemInstance getSecondaryWeaponInstance() {
    return null;
  }

  public WeaponTemplate getSecondaryWeaponItem() {
    return null;
  }

  public abstract void displayGiveDamageMessage(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6);

  public abstract void displayReceiveDamageMessage(Creature var1, int var2);

  public boolean unChargeShots(boolean spirit) {
    Player owner = this.getPlayer();
    if (spirit) {
      if (this._spsCharged != 0) {
        this._spsCharged = 0;
        owner.autoShot();
        return true;
      }
    } else if (this._ssCharged) {
      this._ssCharged = false;
      owner.autoShot();
      return true;
    }

    return false;
  }

  public boolean getChargedSoulShot() {
    return this._ssCharged;
  }

  public int getChargedSpiritShot() {
    return this._spsCharged;
  }

  public void chargeSoulShot() {
    this._ssCharged = true;
  }

  public void chargeSpiritShot(int state) {
    this._spsCharged = state;
  }

  public int getSoulshotConsumeCount() {
    return this.getLevel() / 27 + 1;
  }

  public int getSpiritshotConsumeCount() {
    return this.getLevel() / 58 + 1;
  }

  public boolean isDepressed() {
    return this._depressed;
  }

  public void setDepressed(boolean depressed) {
    this._depressed = depressed;
  }

  public boolean isInRange() {
    Player owner = this.getPlayer();
    return this.getDistance(owner) < 2500.0D;
  }

  public void teleportToOwner() {
    Player owner = this.getPlayer();
    this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
    if (owner.isOlyParticipant()) {
      this.teleToLocation(owner.getLoc(), owner.getReflection());
    } else {
      this.teleToLocation(Location.findPointToStay(owner, 50, 150), owner.getReflection());
    }

    if (!this.isDead() && this._follow) {
      this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner);
    }

  }

  public void broadcastCharInfo() {
    if (this._broadcastCharInfoTask == null) {
      this._broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new Summon.BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
    }
  }

  public void broadcastCharInfoImpl() {
    Player owner = this.getPlayer();
    Iterator var2 = World.getAroundPlayers(this).iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (player == owner) {
        player.sendPacket((new PetInfo(this)).update());
      } else {
        player.sendPacket((new NpcInfo(this, player)).update());
      }
    }

  }

  private void sendPetInfoImpl() {
    Player owner = this.getPlayer();
    owner.sendPacket((new PetInfo(this)).update());
  }

  public void sendPetInfo() {
    if (Config.USER_INFO_INTERVAL == 0L) {
      if (this._petInfoTask != null) {
        this._petInfoTask.cancel(false);
        this._petInfoTask = null;
      }

      this.sendPetInfoImpl();
    } else if (this._petInfoTask == null) {
      this._petInfoTask = ThreadPoolManager.getInstance().schedule(new Summon.PetInfoTask(), Config.USER_INFO_INTERVAL);
    }
  }

  public int getSpawnAnimation() {
    return this._spawnAnimation;
  }

  public void startPvPFlag(Creature target) {
    Player owner = this.getPlayer();
    owner.startPvPFlag(target);
  }

  public int getPvpFlag() {
    Player owner = this.getPlayer();
    return owner.getPvpFlag();
  }

  public int getKarma() {
    Player owner = this.getPlayer();
    return owner.getKarma();
  }

  public TeamType getTeam() {
    Player owner = this.getPlayer();
    return owner.getTeam();
  }

  public Player getPlayer() {
    return this._owner;
  }

  public abstract double getExpPenalty();

  public SummonStatsChangeRecorder getStatsRecorder() {
    if (this._statsRecorder == null) {
      synchronized(this) {
        if (this._statsRecorder == null) {
          this._statsRecorder = new SummonStatsChangeRecorder(this);
        }
      }
    }

    return (SummonStatsChangeRecorder)this._statsRecorder;
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    List<L2GameServerPacket> list = new ArrayList<>();
    Player owner = this.getPlayer();
    if (owner == forPlayer) {
      list.add(new PetInfo(this));
      list.add(new PartySpelled(this, true));
      if (this.isPet()) {
        list.add(new PetItemList((PetInstance)this));
      }
    } else {
      Party party = forPlayer.getParty();
      if (this.getReflection() == ReflectionManager.GIRAN_HARBOR && (owner == null || party == null || party != owner.getParty())) {
        return list;
      }

      list.add(new NpcInfo(this, forPlayer));
      if (owner != null && party != null && party == owner.getParty()) {
        list.add(new PartySpelled(this, true));
      }

      list.add(RelationChanged.create(forPlayer, this, forPlayer));
    }

    if (this.isInCombat()) {
      list.add(new AutoAttackStart(this.getObjectId()));
    }

    if (this.isMoving() || this.isFollowing()) {
      list.add(this.movePacket());
    }

    return list;
  }

  public void startAttackStanceTask() {
    this.startAttackStanceTask0();
    Player player = this.getPlayer();
    if (player != null) {
      player.startAttackStanceTask0();
    }

  }

  public <E extends GlobalEvent> E getEvent(Class<E> eventClass) {
    Player player = this.getPlayer();
    return player != null ? player.getEvent(eventClass) : super.getEvent(eventClass);
  }

  public Set<GlobalEvent> getEvents() {
    Player player = this.getPlayer();
    return player != null ? player.getEvents() : super.getEvents();
  }

  public void sendReuseMessage(Skill skill) {
    Player player = this.getPlayer();
    if (player != null && this.isSkillDisabled(skill)) {
      player.sendPacket((new SystemMessage(48)).addSkillName(skill.getDisplayId(), skill.getDisplayLevel()));
    }

  }

  private class PetInfoTask extends RunnableImpl {
    private PetInfoTask() {
    }

    public void runImpl() throws Exception {
      Summon.this.sendPetInfoImpl();
      Summon.this._petInfoTask = null;
    }
  }

  public class BroadcastCharInfoTask extends RunnableImpl {
    public BroadcastCharInfoTask() {
    }

    public void runImpl() throws Exception {
      Summon.this.broadcastCharInfoImpl();
      Summon.this._broadcastCharInfoTask = null;
    }
  }

  private class UpdateEffectIcons extends RunnableImpl {
    private UpdateEffectIcons() {
    }

    public void runImpl() throws Exception {
      Summon.this.updateEffectIconsImpl();
      Summon.this._updateEffectIconsTask = null;
    }
  }
}
