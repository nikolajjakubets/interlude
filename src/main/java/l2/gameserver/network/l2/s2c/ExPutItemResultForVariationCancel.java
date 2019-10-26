//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;

public class ExPutItemResultForVariationCancel extends L2GameServerPacket {
  private int _itemObjectId;
  private int _itemId;
  private int _aug1;
  private int _aug2;
  private long _price;
  private boolean _isSuccess;

  public ExPutItemResultForVariationCancel(ItemInstance item, long price, boolean isSuccess) {
    this._itemObjectId = item.getObjectId();
    this._itemId = item.getItemId();
    this._aug1 = item.getVariationStat1();
    this._aug2 = item.getVariationStat2();
    this._price = price;
    this._isSuccess = isSuccess;
  }

  protected void writeImpl() {
    this.writeEx(86);
    this.writeD(this._itemObjectId);
    this.writeD(this._itemId);
    this.writeD(this._aug1);
    this.writeD(this._aug2);
    this.writeD((int)this._price);
    this.writeD(0);
    this.writeD(this._isSuccess ? 1 : 0);
  }
}
