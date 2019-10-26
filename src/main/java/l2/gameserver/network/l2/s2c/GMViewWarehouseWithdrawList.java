//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public class GMViewWarehouseWithdrawList extends L2GameServerPacket {
  private final ItemInstance[] _items;
  private String _charName;
  private long _charAdena;

  public GMViewWarehouseWithdrawList(Player cha) {
    this._charName = cha.getName();
    this._charAdena = cha.getAdena();
    this._items = cha.getWarehouse().getItems();
  }

  protected final void writeImpl() {
    this.writeC(149);
    this.writeS(this._charName);
    this.writeD((int)this._charAdena);
    this.writeH(this._items.length);
    ItemInstance[] var1 = this._items;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance temp = var1[var3];
      this.writeH(temp.getTemplate().getType1());
      this.writeD(temp.getObjectId());
      this.writeD(temp.getItemId());
      this.writeD((int)temp.getCount());
      this.writeH(temp.getTemplate().getType2ForPackets());
      this.writeH(temp.getBlessed());
      if (temp.getTemplate().getType1() < 4) {
        this.writeD(temp.getTemplate().getBodyPart());
        this.writeH(temp.getEnchantLevel());
        this.writeH(temp.getDamaged());
        this.writeH(0);
        this.writeD(temp.getVariationStat1());
        this.writeD(temp.getVariationStat2());
      }

      this.writeD(temp.getObjectId());
    }

  }
}
