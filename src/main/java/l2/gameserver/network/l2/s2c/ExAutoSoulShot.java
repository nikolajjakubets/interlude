//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExAutoSoulShot extends L2GameServerPacket {
  private final int _itemId;
  private final boolean _type;

  public ExAutoSoulShot(int itemId, boolean type) {
    this._itemId = itemId;
    this._type = type;
  }

  protected final void writeImpl() {
    this.writeEx(18);
    this.writeD(this._itemId);
    this.writeD(this._type ? 1 : 0);
  }
}
