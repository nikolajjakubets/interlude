//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class Dice extends L2GameServerPacket {
  private int _playerId;
  private int _itemId;
  private int _number;
  private int _x;
  private int _y;
  private int _z;

  public Dice(int playerId, int itemId, int number, int x, int y, int z) {
    this._playerId = playerId;
    this._itemId = itemId;
    this._number = number;
    this._x = x;
    this._y = y;
    this._z = z;
  }

  protected final void writeImpl() {
    this.writeC(212);
    this.writeD(this._playerId);
    this.writeD(this._itemId);
    this.writeD(this._number);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
  }
}
