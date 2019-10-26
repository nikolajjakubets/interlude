//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ShowXMasSeal extends L2GameServerPacket {
  private int _item;

  public ShowXMasSeal(int item) {
    this._item = item;
  }

  protected void writeImpl() {
    this.writeC(242);
    this.writeD(this._item);
  }
}
