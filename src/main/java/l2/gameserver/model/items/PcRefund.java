//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance.ItemLocation;

public class PcRefund extends ItemContainer {
  public PcRefund(Player player) {
  }

  protected void onAddItem(ItemInstance item) {
    item.setLocation(ItemLocation.VOID);
    item.save();
    if (this._items.size() > 12) {
      this.destroyItem((ItemInstance)this._items.remove(0));
    }

  }

  protected void onModifyItem(ItemInstance item) {
    item.save();
  }

  protected void onRemoveItem(ItemInstance item) {
    item.save();
  }

  protected void onDestroyItem(ItemInstance item) {
    item.setCount(0L);
    item.delete();
  }

  public void clear() {
    this.writeLock();

    try {
      _itemsDAO.delete(this._items);
      this._items.clear();
    } finally {
      this.writeUnlock();
    }

  }
}
