//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;

public class ExShowBaseAttributeCancelWindow extends L2GameServerPacket {
  private final List<ItemInstance> _items = new ArrayList<>();

  public ExShowBaseAttributeCancelWindow(Player activeChar) {
    ItemInstance[] var2 = activeChar.getInventory().getItems();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      ItemInstance item = var2[var4];
      if (item.getAttributeElement() != Element.NONE && item.canBeEnchanted(true) && getAttributeRemovePrice(item) != 0L) {
        this._items.add(item);
      }
    }

  }

  protected final void writeImpl() {
    this.writeEx(116);
    this.writeD(this._items.size());
    Iterator var1 = this._items.iterator();

    while(var1.hasNext()) {
      ItemInstance item = (ItemInstance)var1.next();
      this.writeD(item.getObjectId());
      this.writeQ(getAttributeRemovePrice(item));
    }

  }

  public static long getAttributeRemovePrice(ItemInstance item) {
    switch(item.getCrystalType()) {
      case S:
        return item.getTemplate().getType2() == 0 ? 50000L : 40000L;
      default:
        return 0L;
    }
  }
}
