//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.boat;

import l2.gameserver.ai.BoatAI;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.BoatWayEvent;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Boat extends Creature {
  private int _moveSpeed;
  private int _rotationSpeed;
  protected int _fromHome;
  protected int _runState;
  private final BoatWayEvent[] _ways = new BoatWayEvent[2];
  protected final Set<Player> _players = new CopyOnWriteArraySet();

  public Boat(int objectId, CharTemplate template) {
    super(objectId, template);
  }

  public void onSpawn() {
    this._fromHome = 1;
    this.getCurrentWay().reCalcNextTime(false);
  }

  public void setXYZ(int x, int y, int z, boolean MoveTask) {
    super.setXYZ(x, y, z, MoveTask);
    this.updatePeopleInTheBoat(x, y, z);
  }

  public void onEvtArrived() {
    this.getCurrentWay().moveNext();
  }

  protected void updatePeopleInTheBoat(int x, int y, int z) {
    Iterator var4 = this._players.iterator();

    while(var4.hasNext()) {
      Player player = (Player)var4.next();
      if (player != null) {
        player.setXYZ(x, y, z, true);
      }
    }

  }

  public void addPlayer(Player player, Location boatLoc) {
    synchronized(this._players) {
      this._players.add(player);
      player.setBoat(this);
      player.setLoc(this.getLoc(), true);
      player.setInBoatPosition(boatLoc);
      player.stopMove(true, false, true);
      player.broadcastPacket(new L2GameServerPacket[]{this.getOnPacket(player, boatLoc), this.inStopMovePacket(player)});
    }
  }

  public void moveInBoat(Player player, Location ori, Location loc) {
    if (player.getPet() != null) {
      player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN, ActionFail.getStatic()});
    } else if (player.getTransformation() != 0) {
      player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, ActionFail.getStatic()});
    } else if (!player.isMovementDisabled() && !player.isSitting()) {
      if (!player.isInBoat()) {
        player.setBoat(this);
      }

      loc.h = PositionUtils.getHeadingTo(ori, loc);
      player.setInBoatPosition(loc);
      player.broadcastPacket(new L2GameServerPacket[]{this.inMovePacket(player, ori, loc)});
    } else {
      player.sendActionFailed();
    }
  }

  public void trajetEnded(boolean oust) {
    this._runState = 0;
    this._fromHome = this._fromHome == 1 ? 0 : 1;
    L2GameServerPacket checkLocation = this.checkLocationPacket();
    if (checkLocation != null) {
      this.broadcastPacket(this.infoPacket(), checkLocation);
    }

    if (oust) {
      this.oustPlayers();
      this.getCurrentWay().reCalcNextTime(false);
    }

  }

  public void teleportShip(int x, int y, int z) {
    if (this.isMoving()) {
      this.stopMove(false);
    }

    Iterator var4 = this._players.iterator();

    while(var4.hasNext()) {
      Player player = (Player)var4.next();
      player.teleToLocation(x, y, z);
    }

    this.setHeading(this.calcHeading(x, y));
    this.setXYZ(x, y, z, true);
    this.getCurrentWay().moveNext();
  }

  public void oustPlayer(Player player, Location loc, boolean teleport) {
    synchronized(this._players) {
      player._stablePoint = null;
      player.setBoat((Boat)null);
      player.setInBoatPosition((Location)null);
      player.broadcastPacket(new L2GameServerPacket[]{this.getOffPacket(player, loc)});
      if (this.getLoc().distance3D(loc) < (double)(2 * this.getActingRange())) {
        player.setLoc(loc, true);
      }

      if (teleport) {
        player.teleToLocation(loc);
      }

      this._players.remove(player);
    }
  }

  public void removePlayer(Player player) {
    synchronized(this._players) {
      this._players.remove(player);
    }
  }

  public void broadcastPacketToPassengers(IStaticPacket packet) {
    Iterator var2 = this._players.iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(packet);
    }

  }

  public int getActingRange() {
    return 150;
  }

  public abstract L2GameServerPacket infoPacket();

  public abstract L2GameServerPacket movePacket();

  public abstract L2GameServerPacket inMovePacket(Player var1, Location var2, Location var3);

  public abstract L2GameServerPacket stopMovePacket();

  public abstract L2GameServerPacket inStopMovePacket(Player var1);

  public abstract L2GameServerPacket startPacket();

  public abstract L2GameServerPacket validateLocationPacket(Player var1);

  public abstract L2GameServerPacket checkLocationPacket();

  public abstract L2GameServerPacket getOnPacket(Player var1, Location var2);

  public abstract L2GameServerPacket getOffPacket(Player var1, Location var2);

  public abstract void oustPlayers();

  public CharacterAI getAI() {
    if (this._ai == null) {
      this._ai = new BoatAI(this);
    }

    return this._ai;
  }

  public void broadcastCharInfo() {
    this.broadcastPacket(this.infoPacket());
  }

  public void broadcastPacket(L2GameServerPacket... packets) {
    List<Player> players = new ArrayList<>();
    players.addAll(this._players);
    players.addAll(World.getAroundPlayers(this));
    Iterator var3 = players.iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      if (player != null) {
        player.sendPacket(packets);
      }
    }

  }

  public void validateLocation(int broadcast) {
  }

  public void sendChanges() {
  }

  public int getMoveSpeed() {
    return this._moveSpeed;
  }

  public int getRunSpeed() {
    return this._moveSpeed;
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

  public int getLevel() {
    return 0;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return false;
  }

  public int getRunState() {
    return this._runState;
  }

  public void setRunState(int runState) {
    this._runState = runState;
  }

  public void setMoveSpeed(int moveSpeed) {
    this._moveSpeed = moveSpeed;
  }

  public void setRotationSpeed(int rotationSpeed) {
    this._rotationSpeed = rotationSpeed;
  }

  public int getRotationSpeed() {
    return this._rotationSpeed;
  }

  public BoatWayEvent getCurrentWay() {
    return this._ways[this._fromHome];
  }

  public void setWay(int id, BoatWayEvent v) {
    this._ways[id] = v;
  }

  public Set<Player> getPlayers() {
    return this._players;
  }

  public boolean isDocked() {
    return this._runState == 0;
  }

  public Location getReturnLoc() {
    return this.getCurrentWay().getReturnLoc();
  }

  public boolean isBoat() {
    return true;
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    if (!this.isMoving()) {
      return Collections.singletonList(this.infoPacket());
    } else {
      List<L2GameServerPacket> list = new ArrayList(2);
      list.add(this.infoPacket());
      list.add(this.movePacket());
      return list;
    }
  }
}
