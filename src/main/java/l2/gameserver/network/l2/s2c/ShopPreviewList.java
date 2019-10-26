//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.templates.item.ItemTemplate;

public class ShopPreviewList extends L2GameServerPacket {
  private int _listId;
  private List<ItemInfo> _itemList;
  private long _money;

  public ShopPreviewList(NpcTradeList list, Player player) {
    this._listId = list.getListId();
    this._money = player.getAdena();
    List<TradeItem> tradeList = list.getItems();
    this._itemList = new ArrayList(tradeList.size());
    Iterator var4 = list.getItems().iterator();

    while(var4.hasNext()) {
      TradeItem item = (TradeItem)var4.next();
      if (item.getItem().isEquipable()) {
        this._itemList.add(item);
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(239);
    this.writeC(192);
    this.writeC(19);
    this.writeC(0);
    this.writeC(0);
    this.writeD((int)this._money);
    this.writeD(this._listId);
    this.writeH(this._itemList.size());
    Iterator var1 = this._itemList.iterator();

    while(var1.hasNext()) {
      ItemInfo item = (ItemInfo)var1.next();
      if (item.getItem().isEquipable()) {
        this.writeD(item.getItemId());
        this.writeH(item.getItem().getType2ForPackets());
        this.writeH(item.getItem().isEquipable() ? item.getItem().getBodyPart() : 0);
        this.writeD(getWearPrice(item.getItem()));
      }
    }

  }

  public static int getWearPrice(ItemTemplate item) {
    switch(item.getItemGrade()) {
      case D:
        return 50;
      case C:
        return 100;
      case B:
        return 200;
      case A:
        return 500;
      case S:
        return 1000;
      default:
        return 10;
    }
  }
}
