//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;

public class PrivateStoreListBuy extends L2GameServerPacket {
  private int _buyerId;
  private long _adena;
  private List<TradeItem> _sellList;

  public PrivateStoreListBuy(Player seller, Player buyer) {
    this._adena = seller.getAdena();
    this._buyerId = buyer.getObjectId();
    this._sellList = new ArrayList();
    List<TradeItem> buyList = buyer.getBuyList();
    List<ItemInstance> items = new LinkedList(Arrays.asList(seller.getInventory().getItems()));
    Iterator var5 = buyList.iterator();

    while(true) {
      label46:
      while(var5.hasNext()) {
        TradeItem bi = (TradeItem)var5.next();
        TradeItem si = null;
        Iterator itemsIt = items.iterator();

        ItemInstance item;
        do {
          do {
            do {
              do {
                if (!itemsIt.hasNext()) {
                  if (si == null) {
                    si = new TradeItem();
                    si.setItemId(bi.getItemId());
                    si.setOwnersPrice(bi.getOwnersPrice());
                    si.setCount(bi.getCount());
                    si.setCurrentValue(0L);
                    si.setEnchantLevel(bi.getEnchantLevel());
                    this._sellList.add(si);
                  }
                  continue label46;
                }

                item = (ItemInstance)itemsIt.next();
              } while(item.getItemId() != bi.getItemId());
            } while(!item.canBeTraded(seller));
          } while(Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() != bi.getEnchantLevel());
        } while(!Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() < bi.getEnchantLevel());

        si = new TradeItem(item);
        this._sellList.add(si);
        si.setOwnersPrice(bi.getOwnersPrice());
        si.setCount(bi.getCount());
        si.setCurrentValue(Math.min(bi.getCount(), item.getCount()));
        si.setEnchantLevel(item.getEnchantLevel());
        itemsIt.remove();
      }

      return;
    }
  }

  protected final void writeImpl() {
    this.writeC(184);
    this.writeD(this._buyerId);
    this.writeD((int)this._adena);
    this.writeD(this._sellList.size());
    Iterator var1 = this._sellList.iterator();

    while(var1.hasNext()) {
      TradeItem si = (TradeItem)var1.next();
      this.writeD(si.getObjectId());
      this.writeD(si.getItemId());
      this.writeH(si.getEnchantLevel());
      this.writeD((int)si.getCurrentValue());
      this.writeD((int)si.getStorePrice());
      this.writeH(si.getCustomType2());
      this.writeD(si.getBodyPart());
      this.writeH(si.getType2());
      this.writeD((int)si.getOwnersPrice());
      this.writeD((int)si.getCount());
    }

  }
}
