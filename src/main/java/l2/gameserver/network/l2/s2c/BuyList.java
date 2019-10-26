//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;

public class BuyList extends L2GameServerPacket {
  private final int _listId;
  private final List<TradeItem> _buyList;
  private final long _adena;
  private final double _taxRate;

  public BuyList(NpcTradeList tradeList, Player activeChar, double taxRate) {
    this._adena = activeChar.getAdena();
    this._taxRate = taxRate;
    if (tradeList != null) {
      this._listId = tradeList.getListId();
      this._buyList = tradeList.getItems();
      activeChar.setBuyListId(this._listId);
    } else {
      this._listId = 0;
      this._buyList = Collections.emptyList();
      activeChar.setBuyListId(0);
    }

  }

  protected void writeImpl() {
    this.writeC(17);
    this.writeD((int)this._adena);
    this.writeD(this._listId);
    this.writeH(this._buyList.size());
    Iterator var1 = this._buyList.iterator();

    while(var1.hasNext()) {
      TradeItem item = (TradeItem)var1.next();
      this.writeH(item.getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCurrentValue());
      this.writeH(item.getType2());
      this.writeH(item.getCustomType1());
      this.writeD(item.getBodyPart());
      this.writeH(item.getEnchantLevel());
      this.writeD(0);
      this.writeD((int)((double)item.getOwnersPrice() * (1.0D + this._taxRate)));
    }

  }
}
