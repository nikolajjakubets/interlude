//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse.ItemClassComparator;
import l2.gameserver.model.items.Warehouse.WarehouseType;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;

public class WareHouseWithdrawList extends L2GameServerPacket {
  private long _adena;
  private List<ItemInfo> _itemList = new ArrayList<>();
  private int _type;

  public WareHouseWithdrawList(Player player, WarehouseType type, ItemClass clss) {
    this._adena = player.getAdena();
    this._type = type.ordinal();
    ItemInstance[] items;
    switch(type) {
      case PRIVATE:
        items = player.getWarehouse().getItems(clss);
        break;
      case FREIGHT:
        items = player.getFreight().getItems(clss);
        break;
      case CLAN:
      case CASTLE:
        items = player.getClan().getWarehouse().getItems(clss);
        break;
      default:
        this._itemList = Collections.emptyList();
        return;
    }

    this._itemList = new ArrayList(items.length);
    ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
    ItemInstance[] var5 = items;
    int var6 = items.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      ItemInstance item = var5[var7];
      this._itemList.add(new ItemInfo(item));
    }

  }

  protected final void writeImpl() {
    this.writeC(66);
    this.writeH(this._type);
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
