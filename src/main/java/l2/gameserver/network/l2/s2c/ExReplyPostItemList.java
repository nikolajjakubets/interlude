//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;

public class ExReplyPostItemList extends L2GameServerPacket {
  private List<ItemInfo> _itemsList = new ArrayList<>();

  public ExReplyPostItemList(Player activeChar) {
    ItemInstance[] items = activeChar.getInventory().getItems();
    ItemInstance[] var3 = items;
    int var4 = items.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      ItemInstance item = var3[var5];
      if (item.canBeTraded(activeChar)) {
        this._itemsList.add(new ItemInfo(item));
      }
    }

  }

  protected void writeImpl() {
    this.writeEx(178);
    this.writeD(this._itemsList.size());
    Iterator var1 = this._itemsList.iterator();

    while(var1.hasNext()) {
      ItemInfo item = (ItemInfo)var1.next();
      this.writeItemInfo(item);
    }

  }
}
