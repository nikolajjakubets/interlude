//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;

public class StopMove extends L2GameServerPacket {
  private final int _objectId;
  private final int _x;
  private final int _y;
  private final int _z;
  private final int _h;

  public StopMove(Creature cha) {
    this._objectId = cha.getObjectId();
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
    this._h = cha.getHeading();
  }

  public StopMove(int obj_id, Location loc) {
    this._objectId = obj_id;
    this._x = loc.x;
    this._y = loc.y;
    this._z = loc.z;
    this._h = loc.h;
  }

  protected final void writeImpl() {
    this.writeC(71);
    this.writeD(this._objectId);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
    this.writeD(this._h);
  }
}
