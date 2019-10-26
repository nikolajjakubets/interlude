//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;

public class ItemList extends L2GameServerPacket {
  private final int _size;
  private final ItemInstance[] _items;
  private final boolean _showWindow;
  private LockType _lockType;
  private int[] _lockItems;

  public ItemList(int size, ItemInstance[] items, boolean showWindow, LockType lockType, int[] lockItems) {
    this._size = size;
    this._items = items;
    this._showWindow = showWindow;
    this._lockType = lockType;
    this._lockItems = lockItems;
  }

  protected final void writeImpl() {
    this.writeC(27);
    this.writeH(this._showWindow ? 1 : 0);
    this.writeH(this._size);
    ItemInstance[] var1 = this._items;
    int var2 = var1.length;

    int var3;
    for(var3 = 0; var3 < var2; ++var3) {
      ItemInstance temp = var1[var3];
      this.writeItemInfo(temp);
    }

    this.writeH(this._lockItems.length);
    if (this._lockItems.length > 0) {
      this.writeC(this._lockType.ordinal());
      int[] var5 = this._lockItems;
      var2 = var5.length;

      for(var3 = 0; var3 < var2; ++var3) {
        int i = var5[var3];
        this.writeD(i);
      }
    }

  }
}
