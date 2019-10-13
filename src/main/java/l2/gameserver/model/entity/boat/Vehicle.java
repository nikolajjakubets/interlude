//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.boat;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.GetOffVehicle;
import l2.gameserver.network.l2.s2c.GetOnVehicle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MoveToLocationInVehicle;
import l2.gameserver.network.l2.s2c.StopMove;
import l2.gameserver.network.l2.s2c.StopMoveToLocationInVehicle;
import l2.gameserver.network.l2.s2c.ValidateLocationInVehicle;
import l2.gameserver.network.l2.s2c.VehicleCheckLocation;
import l2.gameserver.network.l2.s2c.VehicleDeparture;
import l2.gameserver.network.l2.s2c.VehicleInfo;
import l2.gameserver.network.l2.s2c.VehicleStart;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.utils.Location;

public class Vehicle extends Boat {
  public Vehicle(int objectId, CharTemplate template) {
    super(objectId, template);
  }

  public L2GameServerPacket startPacket() {
    return new VehicleStart(this);
  }

  public L2GameServerPacket validateLocationPacket(Player player) {
    return new ValidateLocationInVehicle(player);
  }

  public L2GameServerPacket checkLocationPacket() {
    return new VehicleCheckLocation(this);
  }

  public L2GameServerPacket infoPacket() {
    return new VehicleInfo(this);
  }

  public L2GameServerPacket movePacket() {
    return new VehicleDeparture(this, this.getDestination());
  }

  public L2GameServerPacket inMovePacket(Player player, Location src, Location desc) {
    return new MoveToLocationInVehicle(player, this, src, desc);
  }

  public L2GameServerPacket stopMovePacket() {
    return new StopMove(this);
  }

  public L2GameServerPacket inStopMovePacket(Player player) {
    return new StopMoveToLocationInVehicle(player);
  }

  public L2GameServerPacket getOnPacket(Player player, Location location) {
    return new GetOnVehicle(player, this, location);
  }

  public L2GameServerPacket getOffPacket(Player player, Location location) {
    return new GetOffVehicle(player, this, location);
  }

  public void oustPlayers() {
  }

  public boolean isVehicle() {
    return true;
  }
}
