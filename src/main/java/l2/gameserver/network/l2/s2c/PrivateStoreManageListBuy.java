//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.model.items.Warehouse.ItemClassComparator;

public class PrivateStoreManageListBuy extends L2GameServerPacket {
  private int _buyerId;
  private long _adena;
  private List<TradeItem> _buyList0;
  private List<TradeItem> _buyList;

  public PrivateStoreManageListBuy(Player buyer) {
    this._buyerId = buyer.getObjectId();
    this._adena = buyer.getAdena();
    this._buyList0 = buyer.getBuyList();
    this._buyList = new ArrayList<>();
    List<ItemInstance> items = new LinkedList(Arrays.asList(buyer.getInventory().getItems()));
    Collections.sort(items, ItemClassComparator.getInstance());
    Iterator var4 = items.iterator();

    while(var4.hasNext()) {
      ItemInstance item = (ItemInstance)var4.next();
      if (item.canBeTraded(buyer) && item.getItemId() != 57) {
        TradeItem bi;
        this._buyList.add(bi = new TradeItem(item));
        bi.setObjectId(0);
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(183);
    this.writeD(this._buyerId);
    this.writeD((int)this._adena);
    this.writeD(this._buyList.size());
    Iterator var1 = this._buyList.iterator();

    TradeItem bi;
    while(var1.hasNext()) {
      bi = (TradeItem)var1.next();
      this.writeD(bi.getItemId());
      this.writeH(bi.getEnchantLevel());
      this.writeD((int)bi.getCount());
      this.writeD((int)bi.getStorePrice());
      this.writeH(bi.getCustomType2());
      this.writeD(bi.getBodyPart());
      this.writeH(bi.getType2());
    }

    this.writeD(this._buyList0.size());
    var1 = this._buyList0.iterator();

    while(var1.hasNext()) {
      bi = (TradeItem)var1.next();
      this.writeD(bi.getItemId());
      this.writeH(bi.getEnchantLevel());
      this.writeD((int)bi.getCount());
      this.writeD((int)bi.getStorePrice());
      this.writeH(bi.getCustomType2());
      this.writeD(bi.getBodyPart());
      this.writeH(bi.getType2());
      this.writeD((int)bi.getOwnersPrice());
      this.writeD((int)bi.getReferencePrice());
    }

  }
}
