//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import l2.commons.collections.JoinedIterator;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDuelStart;
import l2.gameserver.network.l2.s2c.ExDuelUpdateUserInfo;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public abstract class DuelEvent extends GlobalEvent implements Iterable<DuelSnapshotObject> {
  public static final String RED_TEAM;
  public static final String BLUE_TEAM;
  protected OnPlayerExitListener _playerExitListener = new DuelEvent.OnPlayerExitListenerImpl();
  protected TeamType _winner;
  protected boolean _aborted;
  protected final AtomicReference<DuelEvent.DuelState> _duelState;

  public DuelEvent(MultiValueSet<String> set) {
    super(set);
    this._winner = TeamType.NONE;
    this._duelState = new AtomicReference(DuelEvent.DuelState.EPrepare);
  }

  protected DuelEvent(int id, String name) {
    super(id, name);
    this._winner = TeamType.NONE;
    this._duelState = new AtomicReference(DuelEvent.DuelState.EPrepare);
  }

  public void initEvent() {
  }

  public abstract boolean canDuel(Player var1, Player var2, boolean var3);

  public abstract void askDuel(Player var1, Player var2);

  public abstract void createDuel(Player var1, Player var2);

  public abstract void playerExit(Player var1);

  public abstract void packetSurrender(Player var1);

  public abstract void onDie(Player var1);

  public abstract int getDuelType();

  private boolean canStart() {
    if (this._duelState.get() != DuelEvent.DuelState.EPrepare) {
      return false;
    } else {
      Iterator var1 = this.iterator();

      Player player;
      IStaticPacket pkt;
      do {
        if (!var1.hasNext()) {
          return true;
        }

        DuelSnapshotObject dso = (DuelSnapshotObject)var1.next();
        player = dso.getPlayer();
        if (player == null) {
          return false;
        }

        pkt = this.checkPlayer(player);
      } while(pkt == null);

      this.sendPacket(pkt);
      this.abortDuel(player);
      return false;
    }
  }

  public void action(String name, boolean start) {
    if (name.equalsIgnoreCase("event")) {
      if (start) {
        if (this.canStart()) {
          this.startEvent();
        }
      } else {
        this.stopEvent();
      }
    }

  }

  public void startEvent() {
    if (this._duelState.compareAndSet(DuelEvent.DuelState.EPrepare, DuelEvent.DuelState.EInProgress)) {
      this.updatePlayers(true, false);
      this.sendPackets(new ExDuelStart(this), PlaySound.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
        DuelSnapshotObject $snapshot = (DuelSnapshotObject)var1.next();
        this.sendPacket(new ExDuelUpdateUserInfo($snapshot.getPlayer()), $snapshot.getTeam().revert().name());
      }
    }

  }

  public void sendPacket(IStaticPacket packet, String... ar) {
    String[] var3 = ar;
    int var4 = ar.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String a = var3[var5];
      List<DuelSnapshotObject> objs = this.getObjects(a);
      Iterator var8 = objs.iterator();

      while(var8.hasNext()) {
        DuelSnapshotObject obj = (DuelSnapshotObject)var8.next();
        obj.getPlayer().sendPacket(packet);
      }
    }

  }

  public void sendPacket(IStaticPacket packet) {
    this.sendPackets(packet);
  }

  public void sendPackets(IStaticPacket... packet) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      DuelSnapshotObject d = (DuelSnapshotObject)var2.next();
      d.getPlayer().sendPacket(packet);
    }

  }

  public void abortDuel(Player player) {
    this._aborted = true;
    this._winner = TeamType.NONE;
    this.stopEvent();
  }

  protected IStaticPacket checkPlayer(Player player) {
    IStaticPacket packet = null;
    if (player.isInCombat()) {
      packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE)).addName(player);
    } else if (!player.isDead() && !player.isAlikeDead() && player.getCurrentHpPercents() >= 50.0D && player.getCurrentMpPercents() >= 50.0D && player.getCurrentCpPercents() >= 50.0D) {
      if (player.getEvent(DuelEvent.class) != null) {
        packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL)).addName(player);
      } else if (player.getEvent(ClanHallSiegeEvent.class) == null && player.getEvent(ClanHallNpcSiegeEvent.class) == null) {
        if (player.getEvent(SiegeEvent.class) != null) {
          packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_SIEGE_WAR)).addName(player);
        } else if (player.isOlyParticipant()) {
          packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD)).addName(player);
        } else if (!player.isCursedWeaponEquipped() && player.getKarma() <= 0 && player.getPvpFlag() <= 0) {
          if (player.isInStoreMode()) {
            packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE)).addName(player);
          } else if (!player.isMounted() && !player.isInBoat()) {
            if (player.isFishing()) {
              packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING)).addName(player);
            } else if (!player.isInCombatZone() && !player.isInPeaceZone() && !player.isInWater() && !player.isInZone(ZoneType.no_restart)) {
              if (player.getTransformation() != 0) {
                packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED)).addName(player);
              }
            } else {
              packet = (new SystemMessage2(SystemMsg.C1_CANNOT_MAKE_A_CHALLENGE_TO_A_DUEL_BECAUSE_C1_IS_CURRENTLY_IN_A_DUELPROHIBITED_AREA_PEACEFUL_ZONE__SEVEN_SIGNS_ZONE__NEAR_WATER__RESTART_PROHIBITED_AREA)).addName(player);
            }
          } else {
            packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_STEED_OR_STRIDER)).addName(player);
          }
        } else {
          packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_STATE)).addName(player);
        }
      } else {
        packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR)).addName(player);
      }
    } else {
      packet = (new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1S_HP_OR_MP_IS_BELOW_50)).addName(player);
    }

    return packet;
  }

  protected IStaticPacket canDuel0(Player requester, Player target) {
    IStaticPacket packet = this.checkPlayer(target);
    if (packet == null && !requester.isInRangeZ(target, 1200L)) {
      packet = (new SystemMessage2(SystemMsg.C1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_C1_IS_TOO_FAR_AWAY)).addName(target);
    }

    return (IStaticPacket)packet;
  }

  protected void updatePlayers(boolean start, boolean teleport) {
    Iterator var3 = this.iterator();

    while(var3.hasNext()) {
      DuelSnapshotObject dso = (DuelSnapshotObject)var3.next();
      Player player = dso.getPlayer();
      if (player != null) {
        if (teleport) {
          dso.teleport();
        } else if (start) {
          player.addEvent(this);
          player.setTeam(dso.getTeam());
        } else {
          player.removeEvent(this);
          dso.restore(this._aborted);
          player.setTeam(TeamType.NONE);
        }
      }
    }

  }

  public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
    if (target.getTeam() != TeamType.NONE && attacker.getTeam() != TeamType.NONE && target.getTeam() != attacker.getTeam()) {
      DuelEvent duelEvent = (DuelEvent)target.getEvent(DuelEvent.class);
      return duelEvent != null && duelEvent == this ? null : SystemMsg.INVALID_TARGET;
    } else {
      return SystemMsg.INVALID_TARGET;
    }
  }

  public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force) {
    if (target.getTeam() != TeamType.NONE && attacker.getTeam() != TeamType.NONE && target.getTeam() != attacker.getTeam()) {
      DuelEvent duelEvent = (DuelEvent)target.getEvent(DuelEvent.class);
      return duelEvent != null && duelEvent == this;
    } else {
      return false;
    }
  }

  public void onAddEvent(GameObject o) {
    if (o.isPlayer()) {
      o.getPlayer().addListener(this._playerExitListener);
    }

  }

  public void onRemoveEvent(GameObject o) {
    if (o.isPlayer()) {
      o.getPlayer().removeListener(this._playerExitListener);
    }

  }

  public Iterator<DuelSnapshotObject> iterator() {
    List<DuelSnapshotObject> blue = this.getObjects(BLUE_TEAM);
    List<DuelSnapshotObject> red = this.getObjects(RED_TEAM);
    return new JoinedIterator(new Iterator[]{blue.iterator(), red.iterator()});
  }

  public void reCalcNextTime(boolean onInit) {
    this.registerActions();
  }

  public void announce(int i) {
    this.sendPacket((new SystemMessage2(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS)).addInteger((double)i));
  }

  static {
    RED_TEAM = TeamType.RED.name();
    BLUE_TEAM = TeamType.BLUE.name();
  }

  protected static enum DuelState {
    EPrepare,
    EInProgress,
    EEnd;

    private DuelState() {
    }
  }

  private class OnPlayerExitListenerImpl implements OnPlayerExitListener {
    private OnPlayerExitListenerImpl() {
    }

    public void onPlayerExit(Player player) {
      DuelEvent.this.playerExit(player);
    }
  }
}
