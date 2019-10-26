//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class VehicleDeparture extends L2GameServerPacket {
  private int _moveSpeed;
  private int _rotationSpeed;
  private int _boatObjId;
  private Location _loc;

  public VehicleDeparture(Boat boat) {
    this._boatObjId = boat.getObjectId();
    this._moveSpeed = boat.getMoveSpeed();
    this._rotationSpeed = boat.getRotationSpeed();
    this._loc = boat.getDestination();
    if (this._loc == null) {
      this._loc = boat.getReturnLoc();
    }

  }

  public VehicleDeparture(Boat boat, Location dest) {
    this._boatObjId = boat.getObjectId();
    this._moveSpeed = boat.getMoveSpeed();
    this._rotationSpeed = boat.getRotationSpeed();
    this._loc = dest;
  }

  protected final void writeImpl() {
    this.writeC(90);
    this.writeD(this._boatObjId);
    this.writeD(this._moveSpeed);
    this.writeD(this._rotationSpeed);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
