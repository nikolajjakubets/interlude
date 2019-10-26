//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ShowTownMap extends L2GameServerPacket {
  String _texture;
  int _x;
  int _y;

  public ShowTownMap(String texture, int x, int y) {
    this._texture = texture;
    this._x = x;
    this._y = y;
  }

  protected final void writeImpl() {
    this.writeC(222);
    this.writeS(this._texture);
    this.writeD(this._x);
    this.writeD(this._y);
  }
}
