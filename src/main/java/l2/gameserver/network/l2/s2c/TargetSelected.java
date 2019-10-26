//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class TargetSelected extends L2GameServerPacket {
  private int _objectId;
  private int _targetId;
  private Location _loc;

  public TargetSelected(int objectId, int targetId, Location loc) {
    this._objectId = objectId;
    this._targetId = targetId;
    this._loc = loc;
  }

  protected final void writeImpl() {
    this.writeC(41);
    this.writeD(this._objectId);
    this.writeD(this._targetId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
