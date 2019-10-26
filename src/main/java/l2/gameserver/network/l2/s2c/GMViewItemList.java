//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public class GMViewItemList extends L2GameServerPacket {
  private int _size;
  private ItemInstance[] _items;
  private int _limit;
  private String _name;

  public GMViewItemList(Player cha, ItemInstance[] items, int size) {
    this._size = size;
    this._items = items;
    this._name = cha.getName();
    this._limit = cha.getInventoryLimit();
  }

  protected final void writeImpl() {
    this.writeC(148);
    this.writeS(this._name);
    this.writeD(this._limit);
    this.writeH(1);
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
      this.writeH(temp.isEquipped() ? 1 : 0);
      this.writeD(temp.getBodyPart());
      this.writeH(temp.getEnchantLevel());
      this.writeH(temp.getTemplate().getType2());
      this.writeH(temp.getVariationStat1());
      this.writeH(temp.getVariationStat2());
      this.writeD(temp.getDuration());
    }

  }
}
