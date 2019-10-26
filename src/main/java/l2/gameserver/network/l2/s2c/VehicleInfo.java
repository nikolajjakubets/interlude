//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class VehicleInfo extends L2GameServerPacket {
  private int _boatObjectId;
  private Location _loc;

  public VehicleInfo(Boat boat) {
    this._boatObjectId = boat.getObjectId();
    this._loc = boat.getLoc();
  }

  protected final void writeImpl() {
    this.writeC(89);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
  }
}
