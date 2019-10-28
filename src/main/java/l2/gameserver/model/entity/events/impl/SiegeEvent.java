//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.DoorTemplate.DoorType;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TimeUtils;

import java.util.*;

public abstract class SiegeEvent<R extends Residence, S extends SiegeClanObject> extends GlobalEvent {
  public static final String OWNER = "owner";
  public static final String OLD_OWNER = "old_owner";
  public static final String ATTACKERS = "attackers";
  public static final String DEFENDERS = "defenders";
  public static final String SPECTATORS = "spectators";
  public static final String SIEGE_ZONES = "siege_zones";
  public static final String FLAG_ZONES = "flag_zones";
  public static final String DAY_OF_WEEK = "day_of_week";
  public static final String HOUR_OF_DAY = "hour_of_day";
  public static final String REGISTRATION = "registration";
  public static final String DOORS = "doors";
  protected R _residence;
  private boolean _isInProgress;
  private boolean _isRegistrationOver;
  protected int _dayOfWeek;
  protected int _hourOfDay;
  protected Clan _oldOwner;
  protected OnDeathListener _doorDeathListener = new SiegeEvent.DoorDeathListener();
  protected List<HardReference<SummonInstance>> _siegeSummons = new ArrayList<>();

  public SiegeEvent(MultiValueSet<String> set) {
    super(set);
    this._dayOfWeek = set.getInteger("day_of_week", 0);
    this._hourOfDay = set.getInteger("hour_of_day", 0);
  }

  public void startEvent() {
    this.setInProgress(true);
    super.startEvent();
  }

  public final void stopEvent() {
    this.stopEvent(false);
  }

  public void stopEvent(boolean step) {
    this.despawnSiegeSummons();
    this.setInProgress(false);
    this.reCalcNextTime(false);
    super.stopEvent();
  }

  public void processStep(Clan clan) {
  }

  public void reCalcNextTime(boolean onInit) {
    this.clearActions();
    Calendar startSiegeDate = this.getResidence().getSiegeDate();
    if (onInit) {
      if (startSiegeDate.getTimeInMillis() <= System.currentTimeMillis()) {
        startSiegeDate.set(7, this._dayOfWeek);
        startSiegeDate.set(11, this._hourOfDay);
        this.validateSiegeDate(startSiegeDate, 2);
        this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
      }
    } else {
      startSiegeDate.add(3, 2);
      this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
    }

    this.registerActions();
    this.getResidence().update();
  }

  protected void validateSiegeDate(Calendar calendar, int add) {
    calendar.set(12, 0);
    calendar.set(13, 0);
    calendar.set(14, 0);

    while(calendar.getTimeInMillis() < System.currentTimeMillis()) {
      calendar.add(3, add);
    }

  }

  protected long startTimeMillis() {
    return this.getResidence().getSiegeDate().getTimeInMillis();
  }

  public void teleportPlayers(String t) {
    List<Player> players = new ArrayList<>();
    Clan ownerClan = this.getResidence().getOwner();
    Iterator var4;
    Player player;
    SiegeClanObject siegeClan;
    if (t.equalsIgnoreCase("owner")) {
      if (ownerClan != null) {
        var4 = this.getPlayersInZone().iterator();

        while(var4.hasNext()) {
          player = (Player)var4.next();
          if (player.getClan() == ownerClan) {
            ((List)players).add(player);
          }
        }
      }
    } else if (t.equalsIgnoreCase("attackers")) {
      var4 = this.getPlayersInZone().iterator();

      while(var4.hasNext()) {
        player = (Player)var4.next();
        siegeClan = this.getSiegeClan("attackers", player.getClan());
        if (siegeClan != null && siegeClan.isParticle(player)) {
          ((List)players).add(player);
        }
      }
    } else if (t.equalsIgnoreCase("defenders")) {
      var4 = this.getPlayersInZone().iterator();

      label101:
      while(true) {
        do {
          if (!var4.hasNext()) {
            break label101;
          }

          player = (Player)var4.next();
        } while(ownerClan != null && player.getClan() != null && player.getClan() == ownerClan);

        siegeClan = this.getSiegeClan("defenders", player.getClan());
        if (siegeClan != null && siegeClan.isParticle(player)) {
          ((List)players).add(player);
        }
      }
    } else if (t.equalsIgnoreCase("spectators")) {
      var4 = this.getPlayersInZone().iterator();

      label87:
      while(true) {
        do {
          do {
            if (!var4.hasNext()) {
              break label87;
            }

            player = (Player)var4.next();
          } while(ownerClan != null && player.getClan() != null && player.getClan() == ownerClan);
        } while(player.getClan() != null && (this.getSiegeClan("attackers", player.getClan()) != null || this.getSiegeClan("defenders", player.getClan()) != null));

        ((List)players).add(player);
      }
    } else {
      players = this.getPlayersInZone();
    }

    Location loc;
    for(var4 = ((List)players).iterator(); var4.hasNext(); player.teleToLocation(loc, ReflectionManager.DEFAULT)) {
      player = (Player)var4.next();
      siegeClan = null;
      if (!t.equalsIgnoreCase("owner") && !t.equalsIgnoreCase("defenders")) {
        loc = this.getResidence().getNotOwnerRestartPoint(player);
      } else {
        loc = this.getResidence().getOwnerRestartPoint();
      }
    }

  }

  public List<Player> getPlayersInZone() {
    List<ZoneObject> zones = this.getObjects("siege_zones");
    List<Player> result = new LazyArrayList();
    Iterator var3 = zones.iterator();

    while(var3.hasNext()) {
      ZoneObject zone = (ZoneObject)var3.next();
      result.addAll(zone.getInsidePlayers());
    }

    return result;
  }

  public void broadcastInZone(L2GameServerPacket... packet) {
    Iterator var2 = this.getPlayersInZone().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(packet);
    }

  }

  public void broadcastInZone(IStaticPacket... packet) {
    Iterator var2 = this.getPlayersInZone().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(packet);
    }

  }

  public boolean checkIfInZone(Creature character) {
    List<ZoneObject> zones = this.getObjects("siege_zones");
    Iterator var3 = zones.iterator();

    ZoneObject zone;
    do {
      if (!var3.hasNext()) {
        return false;
      }

      zone = (ZoneObject)var3.next();
    } while(!zone.checkIfInZone(character));

    return true;
  }

  public void broadcastInZone2(IStaticPacket... packet) {
    Iterator var2 = this.getResidence().getZone().getInsidePlayers().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(packet);
    }

  }

  public void broadcastInZone2(L2GameServerPacket... packet) {
    Iterator var2 = this.getResidence().getZone().getInsidePlayers().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(packet);
    }

  }

  public void loadSiegeClans() {
    this.addObjects("attackers", SiegeClanDAO.getInstance().load(this.getResidence(), "attackers"));
    this.addObjects("defenders", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders"));
  }

  public S newSiegeClan(String type, int clanId, long param, long date) {
    Clan clan = ClanTable.getInstance().getClan(clanId);
    return clan == null ? null : (S) new SiegeClanObject(type, clan, param, date);
  }

  public void updateParticles(boolean start, String... arg) {
    String[] var3 = arg;
    int var4 = arg.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String a = var3[var5];
      List<SiegeClanObject> siegeClans = this.getObjects(a);
      Iterator var8 = siegeClans.iterator();

      while(var8.hasNext()) {
        SiegeClanObject s = (SiegeClanObject)var8.next();
        s.setEvent(start, this);
      }
    }

  }

  public S getSiegeClan(String name, Clan clan) {
    return clan == null ? null : this.getSiegeClan(name, clan.getClanId());
  }

  public S getSiegeClan(String name, int objectId) {
    List<SiegeClanObject> siegeClanList = this.getObjects(name);
    if (siegeClanList.isEmpty()) {
      return null;
    } else {
      for (SiegeClanObject siegeClan : siegeClanList) {
        if (siegeClan.getObjectId() == objectId) {
          return (S) siegeClan;
        }
      }

      return null;
    }
  }

  public void broadcastTo(IStaticPacket packet, String... types) {
    String[] var3 = types;
    int var4 = types.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String type = var3[var5];
      List<SiegeClanObject> siegeClans = this.getObjects(type);
      Iterator var8 = siegeClans.iterator();

      while(var8.hasNext()) {
        SiegeClanObject siegeClan = (SiegeClanObject)var8.next();
        siegeClan.broadcast(new IStaticPacket[]{packet});
      }
    }

  }

  public void broadcastTo(L2GameServerPacket packet, String... types) {
    String[] var3 = types;
    int var4 = types.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String type = var3[var5];
      List<SiegeClanObject> siegeClans = this.getObjects(type);
      Iterator var8 = siegeClans.iterator();

      while(var8.hasNext()) {
        SiegeClanObject siegeClan = (SiegeClanObject)var8.next();
        siegeClan.broadcast(new L2GameServerPacket[]{packet});
      }
    }

  }

  public void initEvent() {
    this._residence = (R) ResidenceHolder.getInstance().getResidence(this.getId());
    this.loadSiegeClans();
    this.clearActions();
    super.initEvent();
  }

  protected void printInfo() {
    long startSiegeMillis = this.startTimeMillis();
    if (startSiegeMillis == 0L) {
      this.info(this.getName() + " time - undefined");
    } else {
      this.info(this.getName() + " time - " + TimeUtils.toSimpleFormat(startSiegeMillis));
    }

  }

  public boolean ifVar(String name) {
    if (name.equals("owner")) {
      return this.getResidence().getOwner() != null;
    } else if (name.equals("old_owner")) {
      return this._oldOwner != null;
    } else {
      return false;
    }
  }

  public boolean isParticle(Player player) {
    if (this.isInProgress() && player.getClan() != null) {
      return this.getSiegeClan("attackers", player.getClan()) != null || this.getSiegeClan("defenders", player.getClan()) != null;
    } else {
      return false;
    }
  }

  public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
    if (!this.getObjects("flag_zones").isEmpty()) {
      S clan = this.getSiegeClan("attackers", player.getClan());
      if (clan != null && clan.getFlag() != null) {
        r.put(RestartType.TO_FLAG, Boolean.TRUE);
      }

    }
  }

  public Location getRestartLoc(Player player, RestartType type) {
    S attackerClan = this.getSiegeClan("attackers", player.getClan());
    Location loc = null;
    switch(type) {
      case TO_FLAG:
        if (!this.getObjects("flag_zones").isEmpty() && attackerClan != null && attackerClan.getFlag() != null) {
          loc = Location.findPointToStay(attackerClan.getFlag(), 50, 75);
        } else {
          player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
        }
      default:
        return loc;
    }
  }

  public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
    Clan clan1 = thisPlayer.getClan();
    Clan clan2 = targetPlayer.getClan();
    if (clan1 != null && clan2 != null) {
      SiegeEvent<?, ?> siegeEvent2 = (SiegeEvent)targetPlayer.getEvent(SiegeEvent.class);
      if (this == siegeEvent2) {
        result |= 512;
        SiegeClanObject siegeClan1 = this.getSiegeClan("attackers", clan1);
        SiegeClanObject siegeClan2 = this.getSiegeClan("attackers", clan2);
        if ((siegeClan1 != null || siegeClan2 != null) && (siegeClan1 == null || siegeClan2 == null || siegeClan1 != siegeClan2 && !this.isAttackersInAlly())) {
          result |= 4096;
        } else {
          result |= 2048;
        }

        if (siegeClan1 != null) {
          result |= 1024;
        }
      }

      return result;
    } else {
      return result;
    }
  }

  public int getUserRelation(Player thisPlayer, int oldRelation) {
    SiegeClanObject siegeClan = this.getSiegeClan("attackers", thisPlayer.getClan());
    if (siegeClan != null) {
      oldRelation |= 384;
    } else {
      oldRelation |= 128;
    }

    return oldRelation;
  }

  public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
    SiegeEvent<?, ?> siegeEvent = (SiegeEvent)target.getEvent(SiegeEvent.class);
    if (this != siegeEvent) {
      return null;
    } else if (this.checkIfInZone(target) && this.checkIfInZone(attacker)) {
      Player player = target.getPlayer();
      if (player == null) {
        return null;
      } else {
        SiegeClanObject siegeClan1 = this.getSiegeClan("attackers", player.getClan());
        if (siegeClan1 == null && attacker.isSiegeGuard()) {
          return SystemMsg.INVALID_TARGET;
        } else {
          Player playerAttacker = attacker.getPlayer();
          if (playerAttacker == null) {
            return SystemMsg.INVALID_TARGET;
          } else {
            SiegeClanObject siegeClan2 = this.getSiegeClan("attackers", playerAttacker.getClan());
            if (!Config.ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE || (siegeClan1 != null || siegeClan2 != null) && (siegeClan1 == null || siegeClan2 == null || siegeClan1 != siegeClan2 && !this.isAttackersInAlly())) {
              return siegeClan1 == null && siegeClan2 == null ? SystemMsg.INVALID_TARGET : null;
            } else {
              return SystemMsg.INVALID_TARGET;
            }
          }
        }
      }
    } else {
      return null;
    }
  }

  public boolean isInProgress() {
    return this._isInProgress;
  }

  public void action(String name, boolean start) {
    if (name.equalsIgnoreCase("registration")) {
      this.setRegistrationOver(!start);
    } else {
      super.action(name, start);
    }

  }

  public boolean isAttackersInAlly() {
    return false;
  }

  public List<Player> broadcastPlayers(int range) {
    return this.itemObtainPlayers();
  }

  public List<Player> itemObtainPlayers() {
    List<Player> playersInZone = this.getPlayersInZone();
    List<Player> list = new LazyArrayList(playersInZone.size());
    Iterator var3 = this.getPlayersInZone().iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      if (player.getEvent(this.getClass()) == this) {
        list.add(player);
      }
    }

    return list;
  }

  public Location getEnterLoc(Player player) {
    S siegeClan = this.getSiegeClan("attackers", player.getClan());
    if (siegeClan != null) {
      return siegeClan.getFlag() != null ? Location.findAroundPosition(siegeClan.getFlag(), 50, 75) : this.getResidence().getNotOwnerRestartPoint(player);
    } else {
      return this.getResidence().getOwnerRestartPoint();
    }
  }

  public R getResidence() {
    return this._residence;
  }

  public void setInProgress(boolean b) {
    this._isInProgress = b;
  }

  public boolean isRegistrationOver() {
    return this._isRegistrationOver;
  }

  public void setRegistrationOver(boolean b) {
    this._isRegistrationOver = b;
  }

  public void addSiegeSummon(SummonInstance summon) {
    this._siegeSummons.add((HardReference<SummonInstance>) summon.getRef());
  }

  public boolean containsSiegeSummon(SummonInstance cha) {
    return this._siegeSummons.contains(cha.getRef());
  }

  public void despawnSiegeSummons() {
    Iterator var1 = this._siegeSummons.iterator();

    while(var1.hasNext()) {
      HardReference<SummonInstance> ref = (HardReference)var1.next();
      SummonInstance summon = (SummonInstance)ref.get();
      if (summon != null) {
        summon.unSummon();
      }
    }

    this._siegeSummons.clear();
  }

  public class DoorDeathListener implements OnDeathListener {
    public DoorDeathListener() {
    }

    public void onDeath(Creature actor, Creature killer) {
      if (SiegeEvent.this.isInProgress()) {
        DoorInstance door = (DoorInstance)actor;
        if (door.getDoorType() != DoorType.WALL) {
          SiegeEvent.this.broadcastTo((IStaticPacket)SystemMsg.THE_CASTLE_GATE_HAS_BEEN_DESTROYED, "attackers", "defenders");
        }
      }
    }
  }
}
