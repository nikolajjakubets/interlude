//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;

public class SellRefundList extends L2GameServerPacket {
  private final List<TradeItem> _sellList;
  private final int _adena;
  private final boolean _isDone;

  public SellRefundList(Player player, boolean isDone) {
    this._adena = (int)player.getAdena();
    this._isDone = isDone;
    ItemInstance[] items = player.getInventory().getItems();
    this._sellList = new LinkedList();
    ItemInstance[] var4 = items;
    int var5 = items.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      ItemInstance item = var4[var6];
      if (item.canBeSold(player)) {
        this._sellList.add(new TradeItem(item));
      }
    }

  }

  protected void writeImpl() {
    this.writeC(16);
    this.writeD(this._adena);
    this.writeD(this._isDone);
    this.writeH(this._sellList.size());
    Iterator var1 = this._sellList.iterator();

    while(var1.hasNext()) {
      TradeItem item = (TradeItem)var1.next();
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
      this.writeD((int)Math.max(1L, item.getReferencePrice() / Config.ALT_SHOP_REFUND_SELL_DIVISOR));
    }

  }
}
