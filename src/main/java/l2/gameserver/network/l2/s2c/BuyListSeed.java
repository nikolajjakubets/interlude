//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.model.items.TradeItem;

public final class BuyListSeed extends L2GameServerPacket {
  private int _manorId;
  private List<TradeItem> _list = new ArrayList<>();
  private long _money;

  public BuyListSeed(NpcTradeList list, int manorId, long currentMoney) {
    this._money = currentMoney;
    this._manorId = manorId;
    this._list = list.getItems();
  }

  protected final void writeImpl() {
    this.writeC(232);
    this.writeD((int)this._money);
    this.writeD(this._manorId);
    this.writeH(this._list.size());
    Iterator var1 = this._list.iterator();

    while(var1.hasNext()) {
      TradeItem item = (TradeItem)var1.next();
      this.writeH(item.getItem().getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCount());
      this.writeH(item.getItem().getType2ForPackets());
      this.writeH(item.getCustomType1());
      this.writeD((int)item.getOwnersPrice());
    }

  }
}
