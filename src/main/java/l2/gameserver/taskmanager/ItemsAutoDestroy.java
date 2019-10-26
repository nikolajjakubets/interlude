//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;

public class ItemsAutoDestroy {
  private static ItemsAutoDestroy _instance;
  private ConcurrentLinkedQueue<ItemInstance> _items = null;
  private ConcurrentLinkedQueue<ItemInstance> _herbs = null;

  private ItemsAutoDestroy() {
    this._herbs = new ConcurrentLinkedQueue();
    if (Config.AUTODESTROY_ITEM_AFTER > 0) {
      this._items = new ConcurrentLinkedQueue();
      ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemsAutoDestroy.CheckItemsForDestroy(), 60000L, 60000L);
    }

    ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemsAutoDestroy.CheckHerbsForDestroy(), 1000L, 1000L);
  }

  public static ItemsAutoDestroy getInstance() {
    if (_instance == null) {
      _instance = new ItemsAutoDestroy();
    }

    return _instance;
  }

  public void addItem(ItemInstance item) {
    item.setDropTime(System.currentTimeMillis());
    this._items.add(item);
  }

  public void addHerb(ItemInstance herb) {
    herb.setDropTime(System.currentTimeMillis());
    this._herbs.add(herb);
  }

  public class CheckHerbsForDestroy extends RunnableImpl {
    static final long _sleep = 60000L;

    public CheckHerbsForDestroy() {
    }

    public void runImpl() throws Exception {
      long curtime = System.currentTimeMillis();
      Iterator var3 = ItemsAutoDestroy.this._herbs.iterator();

      while(true) {
        while(var3.hasNext()) {
          ItemInstance item = (ItemInstance)var3.next();
          if (item != null && item.getLastDropTime() != 0L && item.getLocation() == ItemLocation.VOID) {
            if (item.getLastDropTime() + 60000L < curtime) {
              item.deleteMe();
              ItemsAutoDestroy.this._herbs.remove(item);
            }
          } else {
            ItemsAutoDestroy.this._herbs.remove(item);
          }
        }

        return;
      }
    }
  }

  public class CheckItemsForDestroy extends RunnableImpl {
    public CheckItemsForDestroy() {
    }

    public void runImpl() throws Exception {
      long _sleep = (long)Config.AUTODESTROY_ITEM_AFTER * 1000L;
      long curtime = System.currentTimeMillis();
      Iterator var5 = ItemsAutoDestroy.this._items.iterator();

      while(true) {
        while(var5.hasNext()) {
          ItemInstance item = (ItemInstance)var5.next();
          if (item != null && item.getLastDropTime() != 0L && item.getLocation() == ItemLocation.VOID) {
            if (item.getLastDropTime() + _sleep < curtime) {
              item.deleteMe();
              item.delete();
              ItemsAutoDestroy.this._items.remove(item);
            }
          } else {
            ItemsAutoDestroy.this._items.remove(item);
          }
        }

        return;
      }
    }
  }
}
