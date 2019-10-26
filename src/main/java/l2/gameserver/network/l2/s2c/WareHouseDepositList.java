//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse.ItemClassComparator;
import l2.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseDepositList extends L2GameServerPacket {
  private int _whtype;
  private long _adena;
  private List<ItemInfo> _itemList;

  public WareHouseDepositList(Player cha, WarehouseType whtype) {
    this._whtype = whtype.ordinal();
    this._adena = cha.getAdena();
    ItemInstance[] items = cha.getInventory().getItems();
    ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
    this._itemList = new ArrayList(items.length);
    ItemInstance[] var4 = items;
    int var5 = items.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      ItemInstance item = var4[var6];
      if (item.canBeStored(cha, this._whtype == 1)) {
        this._itemList.add(new ItemInfo(item));
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(65);
    this.writeH(this._whtype);
    this.writeD((int)this._adena);
    this.writeH(this._itemList.size());
    Iterator var1 = this._itemList.iterator();

    while(var1.hasNext()) {
      ItemInfo item = (ItemInfo)var1.next();
      this.writeH(item.getItem().getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCount());
      this.writeH(item.getItem().getType2ForPackets());
      this.writeH(item.getCustomType1());
      this.writeD(item.getItem().getBodyPart());
      this.writeH(item.getEnchantLevel());
      this.writeH(item.getCustomType2());
      this.writeH(0);
      this.writeD(item.getObjectId());
      this.writeD(item.getVariationStat1());
      this.writeD(item.getVariationStat2());
    }

  }
}
