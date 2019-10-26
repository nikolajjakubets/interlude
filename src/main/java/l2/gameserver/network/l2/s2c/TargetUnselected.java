//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;
import l2.gameserver.utils.Location;

public class TargetUnselected extends L2GameServerPacket {
  private int _targetId;
  private Location _loc;

  public TargetUnselected(GameObject obj) {
    this._targetId = obj.getObjectId();
    this._loc = obj.getLoc();
  }

  protected final void writeImpl() {
    this.writeC(42);
    this.writeD(this._targetId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
