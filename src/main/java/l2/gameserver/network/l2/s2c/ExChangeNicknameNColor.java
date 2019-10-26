//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExChangeNicknameNColor extends L2GameServerPacket {
  private int _itemObjId;

  public ExChangeNicknameNColor(int itemObjId) {
    this._itemObjId = itemObjId;
  }

  protected void writeImpl() {
    this.writeEx(131);
    this.writeD(this._itemObjId);
  }
}
