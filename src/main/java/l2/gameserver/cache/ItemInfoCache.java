//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.cache;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ItemInfoCache {
  private static final ItemInfoCache _instance = new ItemInfoCache();
  private Cache cache = CacheManager.getInstance().getCache(this.getClass().getName());

  public static final ItemInfoCache getInstance() {
    return _instance;
  }

  private ItemInfoCache() {
  }

  public void put(ItemInstance item) {
    this.cache.put(new Element(item.getObjectId(), new ItemInfo(item)));
  }

  public ItemInfo get(int objectId) {
    Element element = this.cache.get(objectId);
    ItemInfo info = null;
    if (element != null) {
      info = (ItemInfo)element.getObjectValue();
    }

    Player player = null;
    if (info != null) {
      player = World.getPlayer(info.getOwnerId());
      ItemInstance item = null;
      if (player != null) {
        item = player.getInventory().getItemByObjectId(objectId);
      }

      if (item != null && item.getItemId() == info.getItemId()) {
        this.cache.put(new Element(item.getObjectId(), info = new ItemInfo(item)));
      }
    }

    return info;
  }
}
