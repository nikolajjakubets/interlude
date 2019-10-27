//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;

public class PrivateStoreManageListSell extends L2GameServerPacket {
  private int _sellerId;
  private long _adena;
  private boolean _package;
  private List<TradeItem> _sellList;
  private List<TradeItem> _sellList0;

  public PrivateStoreManageListSell(Player seller, boolean pkg) {
    this._sellerId = seller.getObjectId();
    this._adena = seller.getAdena();
    this._package = pkg;
    this._sellList0 = seller.getSellList(this._package);
    this._sellList = new ArrayList<>();
    Iterator var3 = this._sellList0.iterator();

    while(true) {
      while(var3.hasNext()) {
        TradeItem si = (TradeItem)var3.next();
        if (si.getCount() <= 0L) {
          this._sellList0.remove(si);
        } else {
          ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
          if (item == null) {
            item = seller.getInventory().getItemByItemId(si.getItemId());
          }

          if (item != null && item.canBeTraded(seller) && item.getItemId() != 57) {
            si.setCount(Math.min(item.getCount(), si.getCount()));
          } else {
            this._sellList0.remove(si);
          }
        }
      }

      ItemInstance[] items = seller.getInventory().getItems();
      ItemInstance[] var12 = items;
      int var13 = items.length;

      label44:
      for(int var6 = 0; var6 < var13; ++var6) {
        ItemInstance item = var12[var6];
        if (item.canBeTraded(seller) && item.getItemId() != 57) {
          Iterator var8 = this._sellList0.iterator();

          TradeItem si;
          do {
            if (!var8.hasNext()) {
              this._sellList.add(new TradeItem(item));
              continue label44;
            }

            si = (TradeItem)var8.next();
          } while(si.getObjectId() != item.getObjectId());

          if (si.getCount() != item.getCount()) {
            TradeItem ti = new TradeItem(item);
            ti.setCount(item.getCount() - si.getCount());
            this._sellList.add(ti);
          }
        }
      }

      return;
    }
  }

  protected final void writeImpl() {
    this.writeC(154);
    this.writeD(this._sellerId);
    this.writeD(this._package ? 1 : 0);
    this.writeD((int)this._adena);
    this.writeD(this._sellList.size());
    Iterator var1 = this._sellList.iterator();

    TradeItem si;
    while(var1.hasNext()) {
      si = (TradeItem)var1.next();
      this.writeD(si.getItem().getType2ForPackets());
      this.writeD(si.getObjectId());
      this.writeD(si.getItemId());
      this.writeD((int)si.getCount());
      this.writeH(0);
      this.writeH(si.getEnchantLevel());
      this.writeH(0);
      this.writeD(si.getItem().getBodyPart());
      this.writeD((int)si.getStorePrice());
    }

    this.writeD(this._sellList0.size());
    var1 = this._sellList0.iterator();

    while(var1.hasNext()) {
      si = (TradeItem)var1.next();
      this.writeD(si.getItem().getType2ForPackets());
      this.writeD(si.getObjectId());
      this.writeD(si.getItemId());
      this.writeD((int)si.getCount());
      this.writeH(0);
      this.writeH(si.getEnchantLevel());
      this.writeH(0);
      this.writeD(si.getItem().getBodyPart());
      this.writeD((int)si.getOwnersPrice());
      this.writeD((int)si.getStorePrice());
    }

  }
}
