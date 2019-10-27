//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.math.SafeMath;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ItemContainer {
  private static final Logger _log = LoggerFactory.getLogger(ItemContainer.class);
  protected static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
  protected final List<ItemInstance> _items = new ArrayList<>();
  protected final ReadWriteLock lock = new ReentrantReadWriteLock();
  protected final Lock readLock;
  protected final Lock writeLock;

  protected ItemContainer() {
    this.readLock = this.lock.readLock();
    this.writeLock = this.lock.writeLock();
  }

  public int getSize() {
    return this._items.size();
  }

  public ItemInstance[] getItems() {
    this.readLock();

    ItemInstance[] var1;
    try {
      var1 = (ItemInstance[])this._items.toArray(new ItemInstance[this._items.size()]);
    } finally {
      this.readUnlock();
    }

    return var1;
  }

  public void clear() {
    this.writeLock();

    try {
      this._items.clear();
    } finally {
      this.writeUnlock();
    }

  }

  public final void writeLock() {
    this.writeLock.lock();
  }

  public final void writeUnlock() {
    this.writeLock.unlock();
  }

  public final void readLock() {
    this.readLock.lock();
  }

  public final void readUnlock() {
    this.readLock.unlock();
  }

  public ItemInstance getItemByObjectId(int objectId) {
    this.readLock();

    try {
      for(int i = 0; i < this._items.size(); ++i) {
        ItemInstance item = (ItemInstance)this._items.get(i);
        if (item.getObjectId() == objectId) {
          ItemInstance var4 = item;
          return var4;
        }
      }

      return null;
    } finally {
      this.readUnlock();
    }
  }

  public ItemInstance getItemByItemId(int itemId) {
    this.readLock();

    try {
      for(int i = 0; i < this._items.size(); ++i) {
        ItemInstance item = (ItemInstance)this._items.get(i);
        if (item.getItemId() == itemId) {
          ItemInstance var4 = item;
          return var4;
        }
      }
    } finally {
      this.readUnlock();
    }

    return null;
  }

  public List<ItemInstance> getItemsByItemId(int itemId) {
    List<ItemInstance> result = new ArrayList<>();
    this.readLock();

    try {
      for(int i = 0; i < this._items.size(); ++i) {
        ItemInstance item = (ItemInstance)this._items.get(i);
        if (item.getItemId() == itemId) {
          result.add(item);
        }
      }
    } finally {
      this.readUnlock();
    }

    return result;
  }

  public long getCountOf(int itemId) {
    long count = 0L;
    this.readLock();

    try {
      for(int i = 0; i < this._items.size(); ++i) {
        ItemInstance item = (ItemInstance)this._items.get(i);
        if (item.getItemId() == itemId) {
          count = SafeMath.addAndLimit(count, item.getCount());
        }
      }
    } finally {
      this.readUnlock();
    }

    return count;
  }

  public ItemInstance addItem(int itemId, long count) {
    if (count < 1L) {
      return null;
    } else {
      this.writeLock();

      ItemInstance item;
      try {
        item = this.getItemByItemId(itemId);
        if (item != null && item.isStackable()) {
          synchronized(item) {
            item.setCount(SafeMath.addAndLimit(item.getCount(), count));
            this.onModifyItem(item);
          }
        } else {
          item = ItemFunctions.createItem(itemId);
          item.setCount(count);
          this._items.add(item);
          this.onAddItem(item);
        }
      } finally {
        this.writeUnlock();
      }

      return item;
    }
  }

  public ItemInstance addItem(ItemInstance item) {
    if (item == null) {
      return null;
    } else if (item.getCount() < 1L) {
      return null;
    } else {
      ItemInstance result = null;
      this.writeLock();

      try {
        if (this.getItemByObjectId(item.getObjectId()) != null) {
          Object var11 = null;
          return (ItemInstance)var11;
        }

        if (item.isStackable()) {
          int itemId = item.getItemId();
          result = this.getItemByItemId(itemId);
          if (result != null) {
            synchronized(result) {
              result.setCount(SafeMath.addAndLimit(item.getCount(), result.getCount()));
              this.onModifyItem(result);
              this.onDestroyItem(item);
            }
          }
        }

        if (result == null) {
          this._items.add(item);
          result = item;
          this.onAddItem(item);
        }
      } finally {
        this.writeUnlock();
      }

      return result;
    }
  }

  public ItemInstance removeItemByObjectId(int objectId, long count) {
    if (count < 1L) {
      return null;
    } else {
      this.writeLock();

      ItemInstance result;
      try {
        ItemInstance item;
        if ((item = this.getItemByObjectId(objectId)) == null) {
          Object var6 = null;
          return (ItemInstance)var6;
        }

        synchronized(item) {
          result = this.removeItem(item, count);
        }
      } finally {
        this.writeUnlock();
      }

      return result;
    }
  }

  public ItemInstance removeItemByItemId(int itemId, long count) {
    if (count < 1L) {
      return null;
    } else {
      this.writeLock();

      ItemInstance result;
      try {
        ItemInstance item;
        if ((item = this.getItemByItemId(itemId)) == null) {
          Object var6 = null;
          return (ItemInstance)var6;
        }

        synchronized(item) {
          result = this.removeItem(item, count);
        }
      } finally {
        this.writeUnlock();
      }

      return result;
    }
  }

  public ItemInstance removeItem(ItemInstance item, long count) {
    if (item == null) {
      return null;
    } else if (count < 1L) {
      return null;
    } else if (item.getCount() < count) {
      return null;
    } else {
      this.writeLock();

      ItemInstance newItem;
      try {
        if (!this._items.contains(item)) {
          newItem = null;
          return newItem;
        }

        if (item.getCount() > count) {
          item.setCount(item.getCount() - count);
          this.onModifyItem(item);
          newItem = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
          newItem.setCount(count);
          ItemInstance var5 = newItem;
          return var5;
        }

        newItem = this.removeItem(item);
      } finally {
        this.writeUnlock();
      }

      return newItem;
    }
  }

  public ItemInstance removeItem(ItemInstance item) {
    if (item == null) {
      return null;
    } else {
      this.writeLock();

      ItemInstance var2;
      try {
        if (!this._items.remove(item)) {
          var2 = null;
          return var2;
        }

        this.onRemoveItem(item);
        var2 = item;
      } finally {
        this.writeUnlock();
      }

      return var2;
    }
  }

  public boolean destroyItemByObjectId(int objectId, long count) {
    this.writeLock();

    boolean var5;
    try {
      ItemInstance item;
      if ((item = this.getItemByObjectId(objectId)) != null) {
        synchronized(item) {
          boolean var6 = this.destroyItem(item, count);
          return var6;
        }
      }

      var5 = false;
    } finally {
      this.writeUnlock();
    }

    return var5;
  }

  public boolean destroyItemByItemId(int itemId, long count) {
    this.writeLock();

    boolean var6;
    try {
      ItemInstance item;
      if ((item = this.getItemByItemId(itemId)) == null) {
        boolean var5 = false;
        return var5;
      }

      synchronized(item) {
        var6 = this.destroyItem(item, count);
      }
    } finally {
      this.writeUnlock();
    }

    return var6;
  }

  public boolean destroyItem(ItemInstance item, long count) {
    if (item == null) {
      return false;
    } else if (count < 1L) {
      return false;
    } else if (item.getCount() < count) {
      return false;
    } else {
      this.writeLock();

      boolean var4;
      try {
        if (!this._items.contains(item)) {
          var4 = false;
          return var4;
        }

        if (item.getCount() > count) {
          item.setCount(item.getCount() - count);
          this.onModifyItem(item);
          var4 = true;
          return var4;
        }

        var4 = this.destroyItem(item);
      } finally {
        this.writeUnlock();
      }

      return var4;
    }
  }

  public boolean destroyItem(ItemInstance item) {
    if (item == null) {
      return false;
    } else {
      this.writeLock();

      boolean var2;
      try {
        if (this._items.remove(item)) {
          this.onRemoveItem(item);
          this.onDestroyItem(item);
          var2 = true;
          return var2;
        }

        var2 = false;
      } finally {
        this.writeUnlock();
      }

      return var2;
    }
  }

  protected abstract void onAddItem(ItemInstance var1);

  protected abstract void onModifyItem(ItemInstance var1);

  protected abstract void onRemoveItem(ItemInstance var1);

  protected abstract void onDestroyItem(ItemInstance var1);
}
