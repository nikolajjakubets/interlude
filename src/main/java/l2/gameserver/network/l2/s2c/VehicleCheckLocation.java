//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class VehicleCheckLocation extends L2GameServerPacket {
  private int _boatObjectId;
  private Location _loc;

  public VehicleCheckLocation(Boat instance) {
    this._boatObjectId = instance.getObjectId();
    this._loc = instance.getLoc();
  }

  protected final void writeImpl() {
    this.writeC(91);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
  }
}
