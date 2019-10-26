//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.model.Player;
import l2.gameserver.model.PremiumItem;

public class ExGetPremiumItemList extends L2GameServerPacket {
  private int _objectId;
  private Map<Integer, PremiumItem> _list;

  public ExGetPremiumItemList(Player activeChar) {
    this._objectId = activeChar.getObjectId();
    this._list = activeChar.getPremiumItemList();
  }

  protected void writeImpl() {
    this.writeEx(134);
    if (!this._list.isEmpty()) {
      this.writeD(this._list.size());
      Iterator var1 = this._list.entrySet().iterator();

      while(var1.hasNext()) {
        Entry<Integer, PremiumItem> entry = (Entry)var1.next();
        this.writeD((Integer)entry.getKey());
        this.writeD(this._objectId);
        this.writeD(((PremiumItem)entry.getValue()).getItemId());
        this.writeQ(((PremiumItem)entry.getValue()).getCount());
        this.writeD(0);
        this.writeS(((PremiumItem)entry.getValue()).getSender());
      }
    }

  }
}
