//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;

public class TradeOtherAdd extends L2GameServerPacket {
  private ItemInfo _temp;
  private long _amount;

  public TradeOtherAdd(ItemInfo item, long amount) {
    this._temp = item;
    this._amount = amount;
  }

  protected final void writeImpl() {
    this.writeC(33);
    this.writeH(1);
    this.writeH(0);
    this.writeD(this._temp.getObjectId());
    this.writeD(this._temp.getItemId());
    this.writeD((int)this._amount);
    this.writeH(this._temp.getItem().getType2ForPackets());
    this.writeH(this._temp.getCustomType1());
    this.writeD(this._temp.getItem().getBodyPart());
    this.writeH(this._temp.getEnchantLevel());
    this.writeH(0);
    this.writeH(0);
  }
}
