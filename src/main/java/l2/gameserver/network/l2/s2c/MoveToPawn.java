//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;

public class MoveToPawn extends L2GameServerPacket {
  private int _chaId;
  private int _targetId;
  private int _minRange;
  private int _x;
  private int _y;
  private int _z;

  public MoveToPawn(Creature cha, GameObject target, int minRange) {
    this._chaId = cha.getObjectId();
    this._targetId = target.getObjectId();
    this._minRange = minRange;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
  }

  protected final void writeImpl() {
    this.writeC(96);
    this.writeD(this._chaId);
    this.writeD(this._targetId);
    this.writeD(this._minRange);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
  }
}
