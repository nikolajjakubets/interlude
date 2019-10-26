//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class ChangeWaitType extends L2GameServerPacket {
  private int _objectId;
  private int _moveType;
  private int _x;
  private int _y;
  private int _z;
  public static final int WT_SITTING = 0;
  public static final int WT_STANDING = 1;
  public static final int WT_START_FAKEDEATH = 2;
  public static final int WT_STOP_FAKEDEATH = 3;

  public ChangeWaitType(Creature cha, int newMoveType) {
    this._objectId = cha.getObjectId();
    this._moveType = newMoveType;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
  }

  protected final void writeImpl() {
    this.writeC(47);
    this.writeD(this._objectId);
    this.writeD(this._moveType);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
  }
}
