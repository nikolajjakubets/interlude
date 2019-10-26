//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;

public class TradeOwnAdd extends L2GameServerPacket {
  private ItemInfo _item;
  private long _amount;

  public TradeOwnAdd(ItemInfo item, long amount) {
    this._item = item;
    this._amount = amount;
  }

  protected final void writeImpl() {
    this.writeC(32);
    this.writeH(1);
    this.writeH(0);
    this.writeD(this._item.getObjectId());
    this.writeD(this._item.getItemId());
    this.writeD((int)this._amount);
    this.writeH(this._item.getItem().getType2ForPackets());
    this.writeH(this._item.getCustomType1());
    this.writeD(this._item.getItem().getBodyPart());
    this.writeH(this._item.getEnchantLevel());
    this.writeH(0);
    this.writeH(0);
  }
}
