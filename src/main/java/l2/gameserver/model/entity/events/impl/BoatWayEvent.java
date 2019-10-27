//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.Config;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.BoatPoint;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.MapUtils;

public class BoatWayEvent extends GlobalEvent {
  public static final String BOAT_POINTS = "boat_points";
  private final int _ticketId;
  private final Location _returnLoc;
  private final Boat _boat;

  public BoatWayEvent(MultiValueSet<String> set) {
    super(set);
    this._ticketId = set.getInteger("ticketId", 0);
    this._returnLoc = Location.parseLoc(set.getString("return_point"));
    String className = set.getString("class", (String)null);
    if (className != null) {
      this._boat = BoatHolder.getInstance().initBoat(this.getName(), className);
      Location loc = Location.parseLoc(set.getString("spawn_point"));
      this._boat.setLoc(loc, true);
      this._boat.setHeading(loc.h);
    } else {
      this._boat = BoatHolder.getInstance().getBoat(this.getName());
    }

    this._boat.setWay(className != null ? 1 : 0, this);
  }

  public void initEvent() {
  }

  public void startEvent() {
    L2GameServerPacket startPacket = this._boat.startPacket();
    Iterator var2 = this._boat.getPlayers().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (this._ticketId > 0) {
        if (player.consumeItem(this._ticketId, 1L)) {
          if (startPacket != null) {
            player.sendPacket(startPacket);
          }
        } else {
          player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
          this._boat.oustPlayer(player, this._returnLoc, true);
        }
      } else if (startPacket != null) {
        player.sendPacket(startPacket);
      }
    }

    this.moveNext();
  }

  public void moveNext() {
    List<BoatPoint> points = this.getObjects("boat_points");
    if (this._boat.getRunState() >= points.size()) {
      this._boat.trajetEnded(true);
    } else {
      BoatPoint bp = (BoatPoint)points.get(this._boat.getRunState());
      if (bp.getSpeed1() >= 0) {
        this._boat.setMoveSpeed(bp.getSpeed1());
      }

      if (bp.getSpeed2() >= 0) {
        this._boat.setRotationSpeed(bp.getSpeed2());
      }

      if (this._boat.getRunState() == 0) {
        this._boat.broadcastCharInfo();
      }

      this._boat.setRunState(this._boat.getRunState() + 1);
      if (bp.isTeleport()) {
        this._boat.teleportShip(bp.getX(), bp.getY(), bp.getZ());
      } else {
        this._boat.moveToLocation(bp.getX(), bp.getY(), bp.getZ(), 0, false);
      }

    }
  }

  public void reCalcNextTime(boolean onInit) {
    this.registerActions();
  }

  protected long startTimeMillis() {
    return System.currentTimeMillis();
  }

  public List<Player> broadcastPlayers(int range) {
    if (range <= 0) {
      List<Player> list = new ArrayList<>();
      int rx = MapUtils.regionX(this._boat.getX());
      int ry = MapUtils.regionY(this._boat.getY());
      int offset = Config.SHOUT_OFFSET;
      Iterator var6 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(var6.hasNext()) {
        Player player = (Player)var6.next();
        if (player.getReflection() == this._boat.getReflection()) {
          int tx = MapUtils.regionX(player);
          int ty = MapUtils.regionY(player);
          if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset) {
            list.add(player);
          }
        }
      }

      return list;
    } else {
      return World.getAroundPlayers(this._boat, range, Math.max(range / 2, 200));
    }
  }

  protected void printInfo() {
  }

  public Location getReturnLoc() {
    return this._returnLoc;
  }
}
