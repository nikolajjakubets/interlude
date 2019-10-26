//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class ExJumpToLocation extends L2GameServerPacket {
  private int _objectId;
  private Location _current;
  private Location _destination;

  public ExJumpToLocation(int objectId, Location from, Location to) {
    this._objectId = objectId;
    this._current = from;
    this._destination = to;
  }

  protected final void writeImpl() {
    this.writeEx(136);
    this.writeD(this._objectId);
    this.writeD(this._destination.x);
    this.writeD(this._destination.y);
    this.writeD(this._destination.z);
    this.writeD(this._current.x);
    this.writeD(this._current.y);
    this.writeD(this._current.z);
  }
}
