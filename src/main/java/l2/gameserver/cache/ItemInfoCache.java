//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.cache;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class ItemInfoCache {
  private static final ItemInfoCache _instance = new ItemInfoCache();
  private Cache<Integer, ItemInfo> cache = CacheManagerBuilder.newCacheManagerBuilder()
    .withCache("preConfigured",
      CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, ItemInfo.class,
        ResourcePoolsBuilder.heap(100000))
        .build())
    .build(true)
    .createCache(this.getClass().getName(), CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, ItemInfo.class,
      ResourcePoolsBuilder.heap(100000)).build());

  public static ItemInfoCache getInstance() {
    return _instance;
  }

  private ItemInfoCache() {
  }

  public void put(ItemInstance item) {
    this.cache.put(item.getObjectId(), new ItemInfo(item));
  }

  public ItemInfo get(int objectId) {
    ItemInfo info = this.cache.get(objectId);
    Player player;
    if (info != null) {
      player = World.getPlayer(info.getOwnerId());
      ItemInstance item = null;
      if (player != null) {
        item = player.getInventory().getItemByObjectId(objectId);
      }

      if (item != null && item.getItemId() == info.getItemId()) {
        this.cache.put(item.getObjectId(), info = new ItemInfo(item));
      }
    }

    return info;
  }
}
