//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;

public abstract class Warehouse extends ItemContainer {
  private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
  protected final int _ownerId;

  protected Warehouse(int ownerId) {
    this._ownerId = ownerId;
  }

  public int getOwnerId() {
    return this._ownerId;
  }

  public abstract ItemLocation getItemLocation();

  public ItemInstance[] getItems(ItemClass itemClass) {
    List<ItemInstance> result = new ArrayList();
    this.readLock();

    try {
      for(int i = 0; i < this._items.size(); ++i) {
        ItemInstance item = (ItemInstance)this._items.get(i);
        if (itemClass == null || itemClass == ItemClass.ALL || item.getItemClass() == itemClass) {
          result.add(item);
        }
      }
    } finally {
      this.readUnlock();
    }

    return (ItemInstance[])result.toArray(new ItemInstance[result.size()]);
  }

  public long getCountOfAdena() {
    return this.getCountOf(57);
  }

  protected void onAddItem(ItemInstance item) {
    item.setOwnerId(this.getOwnerId());
    item.setLocation(this.getItemLocation());
    item.setLocData(0);
    item.save();
  }

  protected void onModifyItem(ItemInstance item) {
    item.save();
  }

  protected void onRemoveItem(ItemInstance item) {
    item.setLocData(-1);
    item.save();
  }

  protected void onDestroyItem(ItemInstance item) {
    item.setCount(0L);
    item.delete();
  }

  public void restore() {
    int ownerId = this.getOwnerId();
    this.writeLock();

    try {
      Collection<ItemInstance> items = _itemsDAO.loadItemsByOwnerIdAndLoc(ownerId, this.getItemLocation());
      Iterator var3 = items.iterator();

      while(var3.hasNext()) {
        ItemInstance item = (ItemInstance)var3.next();
        this._items.add(item);
      }
    } finally {
      this.writeUnlock();
    }

  }

  public static class ItemClassComparator implements Comparator<ItemInstance> {
    private static final Comparator<ItemInstance> instance = new Warehouse.ItemClassComparator();

    public ItemClassComparator() {
    }

    public static final Comparator<ItemInstance> getInstance() {
      return instance;
    }

    public int compare(ItemInstance o1, ItemInstance o2) {
      if (o1 != null && o2 != null) {
        int diff = o1.getItemClass().ordinal() - o2.getItemClass().ordinal();
        if (diff == 0) {
          diff = o1.getCrystalType().ordinal() - o2.getCrystalType().ordinal();
        }

        if (diff == 0) {
          diff = o1.getItemId() - o2.getItemId();
        }

        if (diff == 0) {
          diff = o1.getEnchantLevel() - o2.getEnchantLevel();
        }

        return diff;
      } else {
        return 0;
      }
    }
  }

  public static enum WarehouseType {
    NONE,
    PRIVATE,
    CLAN,
    CASTLE,
    FREIGHT;

    private WarehouseType() {
    }
  }
}
