//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Map;

public class ShopPreviewInfo extends L2GameServerPacket {
  private Map<Integer, Integer> _itemlist;

  public ShopPreviewInfo(Map<Integer, Integer> itemlist) {
    this._itemlist = itemlist;
  }

  protected void writeImpl() {
    this.writeC(240);
    this.writeD(17);
    this.writeD(this.getFromList(0));
    this.writeD(this.getFromList(1));
    this.writeD(this.getFromList(2));
    this.writeD(this.getFromList(3));
    this.writeD(this.getFromList(4));
    this.writeD(this.getFromList(5));
    this.writeD(this.getFromList(7));
    this.writeD(this.getFromList(8));
    this.writeD(this.getFromList(9));
    this.writeD(this.getFromList(10));
    this.writeD(this.getFromList(11));
    this.writeD(this.getFromList(12));
    this.writeD(this.getFromList(13));
    this.writeD(this.getFromList(15));
    this.writeD(this.getFromList(15));
    this.writeD(this.getFromList(16));
  }

  private int getFromList(int key) {
    return this._itemlist.get(key) != null ? (Integer)this._itemlist.get(key) : 0;
  }
}
