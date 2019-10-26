//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class MyTargetSelected extends L2GameServerPacket {
  private int _objectId;
  private int _color;

  public MyTargetSelected(int objectId, int color) {
    this._objectId = objectId;
    this._color = color;
  }

  protected final void writeImpl() {
    this.writeC(166);
    this.writeD(this._objectId);
    this.writeH(this._color);
    this.writeD(0);
  }
}
