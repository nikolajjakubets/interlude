//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.NextTime;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CastleDamageZoneDAO;
import l2.gameserver.dao.CastleDoorUpgradeDAO;
import l2.gameserver.dao.CastleHiredGuardDAO;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.objects.*;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import l2.gameserver.utils.TeleportUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.TreeIntSet;

import java.util.*;
import java.util.concurrent.Future;

public class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject> {
  public static final int MAX_SIEGE_CLANS = 20;
  public static final long DAY_IN_MILISECONDS = 86400000L;
  public static final String DEFENDERS_WAITING = "defenders_waiting";
  public static final String DEFENDERS_REFUSED = "defenders_refused";
  public static final String CONTROL_TOWERS = "control_towers";
  public static final String FLAME_TOWERS = "flame_towers";
  public static final String BOUGHT_ZONES = "bought_zones";
  public static final String GUARDS = "guards";
  public static final String HIRED_GUARDS = "hired_guards";
  private IntSet _nextSiegeTimes;
  private Future<?> _nextSiegeDateSetTask;
  private boolean _firstStep;
  private NextTime[] _nextSiegeTimesPatterns;
  private long _nextSiegeDateSetDelay;
  private Pair<ItemTemplate, Long> _onSiegeEndAttackerOwnedLeaderReward;
  private Pair<ItemTemplate, Long> _onSiegeEndDefenderOwnedLeaderReward;

  public CastleSiegeEvent(MultiValueSet<String> set) {
    super(set);
    this._nextSiegeTimes = Containers.EMPTY_INT_SET;
    this._nextSiegeDateSetTask = null;
    this._firstStep = false;
    this._nextSiegeTimesPatterns = new NextTime[0];
    this._onSiegeEndAttackerOwnedLeaderReward = null;
    this._onSiegeEndDefenderOwnedLeaderReward = null;
    String nextSiegeTimesPattern = set.getString("siege_schedule");

    for(StringTokenizer st = new StringTokenizer(nextSiegeTimesPattern, "|;"); st.hasMoreTokens(); this._nextSiegeTimesPatterns = (NextTime[])ArrayUtils.add(this._nextSiegeTimesPatterns, new SchedulingPattern(st.nextToken()))) {
    }

    String onSiegeEndAttackerOwnedLeaderReward = set.getString("on_siege_end_attacker_owned_leader_reward", (String)null);
    if (onSiegeEndAttackerOwnedLeaderReward != null) {
      String[] onSiegEndAttackerOwnedLeaderRewardParts = onSiegeEndAttackerOwnedLeaderReward.split(":");
      this._onSiegeEndAttackerOwnedLeaderReward = Pair.of(ItemHolder.getInstance().getTemplate(Integer.parseInt(onSiegEndAttackerOwnedLeaderRewardParts[0])), Long.parseLong(onSiegEndAttackerOwnedLeaderRewardParts[1]));
    }

    String onSiegeEndDefenderOwnedLeaderReward = set.getString("on_siege_end_defender_owned_leader_reward", (String)null);
    if (onSiegeEndDefenderOwnedLeaderReward != null) {
      String[] onSiegeEndDefenderOwnedLeaderRewardParts = onSiegeEndDefenderOwnedLeaderReward.split(":");
      this._onSiegeEndDefenderOwnedLeaderReward = Pair.of(ItemHolder.getInstance().getTemplate(Integer.parseInt(onSiegeEndDefenderOwnedLeaderRewardParts[0])), Long.parseLong(onSiegeEndDefenderOwnedLeaderRewardParts[1]));
    }

    this._nextSiegeDateSetDelay = set.getLong("next_siege_date_set_delay", 86400L) * 1000L;
  }

  public void initEvent() {
    super.initEvent();
    List<DoorObject> doorObjects = this.getObjects("doors");
    this.addObjects("bought_zones", CastleDamageZoneDAO.getInstance().load(this.getResidence()));
    Iterator var2 = doorObjects.iterator();

    while(var2.hasNext()) {
      DoorObject doorObject = (DoorObject)var2.next();
      doorObject.setUpgradeValue(this, CastleDoorUpgradeDAO.getInstance().load(doorObject.getUId()));
      doorObject.getDoor().addListener(this._doorDeathListener);
    }

  }

  public void processStep(Clan newOwnerClan) {
    Clan oldOwnerClan = ((Castle)this.getResidence()).getOwner();
    ((Castle)this.getResidence()).changeOwner(newOwnerClan);
    SiegeClanObject newOwnerSiegeClan;
    List attackers;
    Iterator var6;
    if (oldOwnerClan != null) {
      newOwnerSiegeClan = this.getSiegeClan("defenders", oldOwnerClan);
      this.removeObject("defenders", newOwnerSiegeClan);
      newOwnerSiegeClan.setType("attackers");
      this.addObject("attackers", newOwnerSiegeClan);
    } else {
      if (this.getObjects("attackers").size() == 1) {
        this.stopEvent();
        return;
      }

      int allianceObjectId = newOwnerClan.getAllyId();
      if (allianceObjectId > 0) {
        attackers = this.getObjects("attackers");
        boolean sameAlliance = true;
        var6 = attackers.iterator();

        while(var6.hasNext()) {
          SiegeClanObject sc = (SiegeClanObject)var6.next();
          if (sc != null && sc.getClan().getAllyId() != allianceObjectId) {
            sameAlliance = false;
          }
        }

        if (sameAlliance) {
          this.stopEvent();
          return;
        }
      }
    }

    newOwnerSiegeClan = this.getSiegeClan("attackers", newOwnerClan);
    newOwnerSiegeClan.deleteFlag();
    newOwnerSiegeClan.setType("defenders");
    this.removeObject("attackers", newOwnerSiegeClan);
    attackers = this.removeObjects("defenders");
    Iterator var9 = attackers.iterator();

    while(var9.hasNext()) {
      SiegeClanObject siegeClan = (SiegeClanObject)var9.next();
      siegeClan.setType("attackers");
    }

    this.addObject("defenders", newOwnerSiegeClan);
    this.addObjects("attackers", attackers);
    this.updateParticles(true, new String[]{"attackers", "defenders"});
    this.teleportPlayers("attackers");
    this.teleportPlayers("spectators");
    if (!this._firstStep) {
      this._firstStep = true;
      this.broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, new String[]{"attackers", "defenders"});
      if (this._oldOwner != null) {
        this.spawnAction("hired_guards", false);
        this.damageZoneAction(false);
        this.removeObjects("hired_guards");
        this.removeObjects("bought_zones");
        CastleDamageZoneDAO.getInstance().delete(this.getResidence());
      } else {
        this.spawnAction("guards", false);
      }

      List<DoorObject> doorObjects = this.getObjects("doors");
      var6 = doorObjects.iterator();

      while(var6.hasNext()) {
        DoorObject doorObject = (DoorObject)var6.next();
        doorObject.setWeak(true);
        doorObject.setUpgradeValue(this, 0);
        CastleDoorUpgradeDAO.getInstance().delete(doorObject.getUId());
      }
    }

    this.spawnAction("doors", true);
    this.spawnAction("control_towers", true);
    this.spawnAction("flame_towers", true);
    this.despawnSiegeSummons();
  }

  public void startEvent() {
    this._oldOwner = ((Castle)this.getResidence()).getOwner();
    if (this._oldOwner != null) {
      this.addObject("defenders", new SiegeClanObject("defenders", this._oldOwner, 0L));
      if (((Castle)this.getResidence()).getSpawnMerchantTickets().size() > 0) {
        Iterator var1 = ((Castle)this.getResidence()).getSpawnMerchantTickets().iterator();

        while(var1.hasNext()) {
          ItemInstance item = (ItemInstance)var1.next();
          MerchantGuard guard = ((Castle)this.getResidence()).getMerchantGuard(item.getItemId());
          this.addObject("hired_guards", new SpawnSimpleObject(guard.getNpcId(), item.getLoc()));
          item.deleteMe();
        }

        CastleHiredGuardDAO.getInstance().delete(this.getResidence());
        this.spawnAction("hired_guards", true);
      }
    }

    List<SiegeClanObject> attackers = this.getObjects("attackers");
    if (attackers.isEmpty()) {
      if (this._oldOwner == null) {
        this.broadcastToWorld((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST)).addResidenceName(this.getResidence()));
      } else {
        this.broadcastToWorld((new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED)).addResidenceName(this.getResidence()));
      }

      this.reCalcNextTime(false);
    } else {
      SiegeClanDAO.getInstance().delete(this.getResidence());
      this.updateParticles(true, new String[]{"attackers", "defenders"});
      this.broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, new String[]{"attackers"});
      super.startEvent();
      if (this._oldOwner == null) {
        this.initControlTowers();
      } else {
        this.damageZoneAction(true);
      }

    }
  }

  public void stopEvent(boolean step) {
    List<DoorObject> doorObjects = this.getObjects("doors");
    Iterator var3 = doorObjects.iterator();

    while(var3.hasNext()) {
      DoorObject doorObject = (DoorObject)var3.next();
      doorObject.setWeak(false);
    }

    this.damageZoneAction(false);
    this.updateParticles(false, new String[]{"attackers", "defenders"});
    List<SiegeClanObject> attackers = this.removeObjects("attackers");
    Iterator var11 = attackers.iterator();

    while(var11.hasNext()) {
      SiegeClanObject siegeClan = (SiegeClanObject)var11.next();
      siegeClan.deleteFlag();
    }

    this.broadcastToWorld((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED)).addResidenceName(this.getResidence()));
    this.removeObjects("defenders");
    this.removeObjects("defenders_waiting");
    this.removeObjects("defenders_refused");
    Clan ownerClan = ((Castle)this.getResidence()).getOwner();
    if (ownerClan != null) {
      Player leaderPlayer;
      UnitMember leader;
      int rewardItemId;
      long rewardItemCount;
      if (this._oldOwner == ownerClan) {
        ownerClan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)).addInteger((double)ownerClan.incReputation(1000, false, this.toString()))});
        leader = ownerClan.getLeader();
        if (leader != null && this._onSiegeEndDefenderOwnedLeaderReward != null) {
          rewardItemId = ((ItemTemplate)this._onSiegeEndDefenderOwnedLeaderReward.getLeft()).getItemId();
          rewardItemCount = (Long)this._onSiegeEndDefenderOwnedLeaderReward.getRight();
          leaderPlayer = World.getPlayer(leader.getObjectId());
          if (leaderPlayer != null && leaderPlayer.isOnline()) {
            ItemFunctions.addItem(leaderPlayer, rewardItemId, rewardItemCount, true);
            Log.LogItem(leaderPlayer, ItemLog.PostSend, rewardItemId, rewardItemCount);
          } else {
            DelayedItemsManager.getInstance().addDelayed(leader.getObjectId(), rewardItemId, (int)rewardItemCount, 0, "End siege owner leader reward item " + rewardItemId + "(" + rewardItemCount + ")");
          }
        }
      } else {
        this.broadcastToWorld(((SystemMessage2)(new SystemMessage2(SystemMsg.CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE)).addString(ownerClan.getName())).addResidenceName(this.getResidence()));
        ownerClan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)).addInteger((double)ownerClan.incReputation(1500, false, this.toString()))});
        if (this._oldOwner != null) {
          this._oldOwner.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOU_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS)).addInteger((double)(-this._oldOwner.incReputation(-1500, false, this.toString())))});
        }

        leader = ownerClan.getLeader();
        Iterator var6 = ownerClan.iterator();

        while(var6.hasNext()) {
          UnitMember member = (UnitMember)var6.next();
          Player player = member.getPlayer();
          if (player != null) {
            player.sendPacket(PlaySound.SIEGE_VICTORY);
            if (player.isOnline() && player.isNoble()) {
              HeroController.getInstance().addHeroDiary(player.getObjectId(), 3, ((Castle)this.getResidence()).getId());
            }
          }
        }

        if (this._onSiegeEndAttackerOwnedLeaderReward != null) {
          rewardItemId = ((ItemTemplate)this._onSiegeEndAttackerOwnedLeaderReward.getLeft()).getItemId();
          rewardItemCount = (Long)this._onSiegeEndAttackerOwnedLeaderReward.getRight();
          leaderPlayer = World.getPlayer(leader.getObjectId());
          if (leaderPlayer != null && leaderPlayer.isOnline()) {
            ItemFunctions.addItem(leaderPlayer, rewardItemId, rewardItemCount, true);
            Log.LogItem(leaderPlayer, ItemLog.PostSend, rewardItemId, rewardItemCount);
          } else {
            DelayedItemsManager.getInstance().addDelayed(leader.getObjectId(), rewardItemId, (int)rewardItemCount, 0, "End siege owner leader reward item " + rewardItemId + "(" + rewardItemCount + ")");
          }
        }
      }

      ((Castle)this.getResidence()).getOwnDate().setTimeInMillis(System.currentTimeMillis());
      ((Castle)this.getResidence()).getLastSiegeDate().setTimeInMillis(((Castle)this.getResidence()).getSiegeDate().getTimeInMillis());
    } else {
      this.broadcastToWorld((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence()));
      ((Castle)this.getResidence()).getOwnDate().setTimeInMillis(0L);
      ((Castle)this.getResidence()).getLastSiegeDate().setTimeInMillis(0L);
    }

    this.despawnSiegeSummons();
    if (this._oldOwner != null) {
      this.spawnAction("hired_guards", false);
      this.removeObjects("hired_guards");
    }

    super.stopEvent(step);
  }

  public void reCalcNextTime(boolean onInit) {
    this.clearActions();
    long currentTimeMillis = System.currentTimeMillis();
    Calendar startSiegeDate = ((Castle)this.getResidence()).getSiegeDate();
    Calendar ownSiegeDate = ((Castle)this.getResidence()).getOwnDate();
    if (onInit) {
      if (startSiegeDate.getTimeInMillis() > currentTimeMillis) {
        this.registerActions();
      } else if (startSiegeDate.getTimeInMillis() == 0L) {
        if (currentTimeMillis - ownSiegeDate.getTimeInMillis() > this._nextSiegeDateSetDelay) {
          this.setNextSiegeTime();
        } else {
          this.generateNextSiegeDates();
        }
      } else if (startSiegeDate.getTimeInMillis() <= currentTimeMillis) {
        this.setNextSiegeTime();
      }
    } else if (((Castle)this.getResidence()).getOwner() != null) {
      ((Castle)this.getResidence()).getSiegeDate().setTimeInMillis(0L);
      ((Castle)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
      ((Castle)this.getResidence()).update();
      this.generateNextSiegeDates();
    } else {
      this.setNextSiegeTime();
    }

  }

  public void loadSiegeClans() {
    super.loadSiegeClans();
    this.addObjects("defenders_waiting", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders_waiting"));
    this.addObjects("defenders_refused", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders_refused"));
  }

  public void setRegistrationOver(boolean b) {
    if (b) {
      this.broadcastToWorld((new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED)).addResidenceName(this.getResidence()));
    }

    super.setRegistrationOver(b);
  }

  public void announce(int val) {
    int min = val / 60;
    int hour = min / 60;
    SystemMessage2 msg;
    if (hour > 0) {
      msg = (SystemMessage2)(new SystemMessage2(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION)).addInteger((double)hour);
    } else if (min > 0) {
      msg = (SystemMessage2)(new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION)).addInteger((double)min);
    } else {
      msg = (SystemMessage2)(new SystemMessage2(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS)).addInteger((double)val);
    }

    this.broadcastTo(msg, new String[]{"attackers", "defenders"});
  }

  private void initControlTowers() {
    List<SpawnExObject> objects = this.getObjects("guards");
    List<Spawner> spawns = new ArrayList<>();
    Iterator var3 = objects.iterator();

    while(var3.hasNext()) {
      SpawnExObject o = (SpawnExObject)var3.next();
      spawns.addAll(o.getSpawns());
    }

    List<SiegeToggleNpcObject> ct = this.getObjects("control_towers");
    Iterator var9 = spawns.iterator();

    while(var9.hasNext()) {
      Spawner spawn = (Spawner)var9.next();
      Location spawnLoc = spawn.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
      SiegeToggleNpcInstance closestCt = null;
      double distanceClosest = 0.0D;

      for(Iterator var12 = ct.iterator(); var12.hasNext(); closestCt.register(spawn)) {
        SiegeToggleNpcObject c = (SiegeToggleNpcObject)var12.next();
        SiegeToggleNpcInstance npcTower = c.getToggleNpc();
        double distance = npcTower.getDistance(spawnLoc);
        if (closestCt == null || distance < distanceClosest) {
          closestCt = npcTower;
          distanceClosest = distance;
        }
      }
    }

  }

  private void damageZoneAction(boolean active) {
    this.zoneAction("bought_zones", active);
  }

  private void setNextSiegeTime() {
    Calendar calendar = (Calendar)Config.CASTLE_VALIDATION_DATE.clone();
    calendar.set(7, 1);
    calendar.set(11, ((Castle)this.getResidence()).getLastSiegeDate().get(11));
    if (calendar.before(Config.CASTLE_VALIDATION_DATE)) {
      calendar.add(3, 1);
    }

    calendar.setTimeInMillis(this.scheduleNextTime(calendar.getTimeInMillis(), this._nextSiegeTimesPatterns[0]));
    this.setNextSiegeTime(calendar.getTimeInMillis());
  }

  public void generateNextSiegeDates() {
    if (((Castle)this.getResidence()).getSiegeDate().getTimeInMillis() == 0L) {
      Calendar calendar = (Calendar)Config.CASTLE_VALIDATION_DATE.clone();
      calendar.set(7, 1);
      if (calendar.before(Config.CASTLE_VALIDATION_DATE)) {
        calendar.add(3, 1);
      }

      this._nextSiegeTimes = new TreeIntSet();
      NextTime[] var2 = this._nextSiegeTimesPatterns;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        NextTime nextTime = var2[var4];
        this._nextSiegeTimes.add((int)(this.scheduleNextTime(calendar.getTimeInMillis(), nextTime) / 1000L));
      }

      long diff = ((Castle)this.getResidence()).getOwnDate().getTimeInMillis() + this._nextSiegeDateSetDelay - System.currentTimeMillis();
      this._nextSiegeDateSetTask = ThreadPoolManager.getInstance().schedule(new CastleSiegeEvent.NextSiegeDateSet(), diff);
    }
  }

  protected long scheduleNextTime(long baseMs, NextTime nextTime) {
    Calendar cal = new GregorianCalendar(TimeZone.getDefault());
    cal.setTimeInMillis(baseMs);
    cal.set(13, 0);
    cal.set(14, 0);

    long nextTimeMs;
    for(nextTimeMs = cal.getTimeInMillis(); nextTimeMs < System.currentTimeMillis(); nextTimeMs = nextTime.next(nextTimeMs)) {
    }

    return nextTimeMs;
  }

  public void setNextSiegeTime(int id) {
    if (this._nextSiegeTimes.contains(id) && this._nextSiegeDateSetTask != null) {
      this._nextSiegeTimes = Containers.EMPTY_INT_SET;
      this._nextSiegeDateSetTask.cancel(false);
      this._nextSiegeDateSetTask = null;
      this.setNextSiegeTime((long)id * 1000L);
    }
  }

  private void setNextSiegeTime(long g) {
    this.broadcastToWorld((new SystemMessage2(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME)).addResidenceName(this.getResidence()));
    this.clearActions();
    ((Castle)this.getResidence()).getSiegeDate().setTimeInMillis(g);
    ((Castle)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
    ((Castle)this.getResidence()).update();
    this.registerActions();
  }

  public boolean isAttackersInAlly() {
    return !this._firstStep;
  }

  public int[] getNextSiegeTimes() {
    return this._nextSiegeTimes.toArray();
  }

  public boolean canResurrect(Player resurrectPlayer, Creature target, boolean force) {
    boolean playerInZone = resurrectPlayer.isInZone(ZoneType.SIEGE);
    boolean targetInZone = target.isInZone(ZoneType.SIEGE);
    if (!playerInZone && !targetInZone) {
      return true;
    } else if (!targetInZone) {
      return false;
    } else {
      Player targetPlayer = target.getPlayer();
      CastleSiegeEvent siegeEvent = (CastleSiegeEvent)target.getEvent(CastleSiegeEvent.class);
      if (siegeEvent != this) {
        if (force) {
          targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
        }

        resurrectPlayer.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
        return false;
      } else {
        SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan("attackers", targetPlayer.getClan());
        if (targetSiegeClan == null) {
          targetSiegeClan = siegeEvent.getSiegeClan("defenders", targetPlayer.getClan());
        }

        if (targetSiegeClan.getType() == "attackers") {
          if (targetSiegeClan.getFlag() == null) {
            if (force) {
              targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            }

            resurrectPlayer.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            return false;
          }
        } else {
          List<SiegeToggleNpcObject> towers = this.getObjects("control_towers");
          int deadTowerCnt = 0;
          Iterator var11 = towers.iterator();

          while(var11.hasNext()) {
            SiegeToggleNpcObject t = (SiegeToggleNpcObject)var11.next();
            if (!t.isAlive()) {
              ++deadTowerCnt;
            }
          }

          if (deadTowerCnt > 1) {
            if (force) {
              targetPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
            }

            resurrectPlayer.sendPacket(force ? SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            return false;
          }
        }

        return true;
      }
    }
  }

  public Location getRestartLoc(Player player, RestartType type) {
    Location loc;
    switch(type) {
      case TO_VILLAGE:
        loc = TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE);
        break;
      default:
        loc = super.getRestartLoc(player, type);
    }

    return loc;
  }

  private class NextSiegeDateSet extends RunnableImpl {
    private NextSiegeDateSet() {
    }

    public void runImpl() throws Exception {
      CastleSiegeEvent.this.setNextSiegeTime();
    }
  }
}
