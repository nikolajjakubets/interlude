//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class Ride extends L2GameServerPacket {
  private int _mountType;
  private int _id;
  private int _rideClassID;
  private Location _loc;

  public Ride(Player cha) {
    this._id = cha.getObjectId();
    this._mountType = cha.getMountType();
    this._rideClassID = cha.getMountNpcId() + 1000000;
    this._loc = cha.getLoc();
  }

  protected final void writeImpl() {
    this.writeC(134);
    this.writeD(this._id);
    this.writeD(this._mountType == 0 ? 0 : 1);
    this.writeD(this._mountType);
    this.writeD(this._rideClassID);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
