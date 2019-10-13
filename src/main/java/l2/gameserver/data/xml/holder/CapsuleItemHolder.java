//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.util.RandomUtils;
import l2.commons.util.Rnd;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapsuleItemHolder extends AbstractHolder {
  private static final Logger LOG = LoggerFactory.getLogger(CapsuleItemHolder.class);
  private static final CapsuleItemHolder INSTANCE = new CapsuleItemHolder();
  private final Map<Integer, List<CapsuleItemHolder.CapsuledItem>> _capsuleItems = new HashMap();
  private final Map<Integer, Pair<Integer, Long>> _capsuleRequiredItems = new HashMap();
  private CapsuleItemHolder.CapsuleItemsHandler _itemsHandler;

  public static CapsuleItemHolder getInstance() {
    return INSTANCE;
  }

  public CapsuleItemHolder() {
  }

  public Pair<Integer, Long> getCapsuleRequiredItems(int capsuleItemId) {
    return (Pair)this._capsuleRequiredItems.get(capsuleItemId);
  }

  public List<CapsuleItemHolder.CapsuledItem> getCapsuledItems(int capsuleItemId) {
    return (List)this._capsuleItems.get(capsuleItemId);
  }

  public void add(int itemId, List<CapsuleItemHolder.CapsuledItem> capsuledItems_) {
    this.add(itemId, (Pair)null, capsuledItems_);
  }

  public void add(int itemId, Pair<Integer, Long> requiredItem, List<CapsuleItemHolder.CapsuledItem> capsuledItems_) {
    if (this._capsuleItems.containsKey(itemId)) {
      LOG.warn("Capsule item " + itemId + " already defined.");
    }

    this._capsuleItems.put(itemId, Collections.unmodifiableList(capsuledItems_));
    if (requiredItem != null) {
      this._capsuleRequiredItems.put(itemId, requiredItem);
    }

  }

  public int size() {
    return this._capsuleItems.size();
  }

  public void clear() {
    ItemHandler.getInstance().unregisterItemHandler(this._itemsHandler);
    this._capsuleItems.clear();
  }

  protected void process() {
    int[] itemIds = new int[this._capsuleItems.size()];
    Iterator<Integer> capsuleItemIdIt = this._capsuleItems.keySet().iterator();

    for(int i = 0; capsuleItemIdIt.hasNext(); ++i) {
      itemIds[i] = (Integer)capsuleItemIdIt.next();
    }

    this._itemsHandler = new CapsuleItemHolder.CapsuleItemsHandler(itemIds);
    ItemHandler.getInstance().registerItemHandler(this._itemsHandler);
  }

  public static class CapsuledItem {
    private final int _itemId;
    private final long _min;
    private final long _max;
    private final double _chance;
    private final int _minEnchant;
    private final int _maxEnchant;

    public CapsuledItem(int itemId, long min, long max, double chance, int minEnchant, int maxEnchant) {
      this._itemId = itemId;
      this._min = min;
      this._max = max;
      this._chance = chance;
      this._minEnchant = minEnchant;
      this._maxEnchant = maxEnchant;
    }

    public int getItemId() {
      return this._itemId;
    }

    public double getChance() {
      return this._chance;
    }

    public long getMax() {
      return this._max;
    }

    public long getMin() {
      return this._min;
    }

    public int getMinEnchant() {
      return this._minEnchant;
    }

    public int getMaxEnchant() {
      return this._maxEnchant;
    }
  }

  public static class CapsuleItemsHandler implements IItemHandler {
    private final int[] _capsuleItemIds;

    public CapsuleItemsHandler(int[] capsuleItemIds) {
      this._capsuleItemIds = capsuleItemIds;
    }

    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
      int itemId = item.getItemId();
      List<CapsuleItemHolder.CapsuledItem> capsuledItems = CapsuleItemHolder.getInstance().getCapsuledItems(itemId);
      if (capsuledItems == null) {
        return false;
      } else {
        Pair<Integer, Long> reqiredItem = CapsuleItemHolder.getInstance().getCapsuleRequiredItems(itemId);
        if (reqiredItem != null && (Integer)reqiredItem.getKey() > 0 && (Long)reqiredItem.getValue() > 0L && !playable.getInventory().destroyItemByItemId((Integer)reqiredItem.getKey(), (Long)reqiredItem.getValue())) {
          playable.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
          return false;
        } else if (!playable.getInventory().destroyItem(item, 1L)) {
          playable.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
          return false;
        } else {
          playable.sendPacket((new SystemMessage(47)).addItemName(item.getItemId()));
          List<Pair<CapsuleItemHolder.CapsuledItem, Double>> chancedItems = new ArrayList();
          Iterator var8 = capsuledItems.iterator();

          while(true) {
            while(var8.hasNext()) {
              CapsuleItemHolder.CapsuledItem capsuledItem = (CapsuleItemHolder.CapsuledItem)var8.next();
              if (capsuledItem.getChance() == 100.0D) {
                long count = capsuledItem.getMax() > capsuledItem.getMin() ? Rnd.get(capsuledItem.getMin(), capsuledItem.getMax()) : capsuledItem.getMin();
                List<ItemInstance> addedItems = addItem(playable, capsuledItem.getItemId(), count);
                Iterator var13 = addedItems.iterator();

                while(var13.hasNext()) {
                  ItemInstance addedItem = (ItemInstance)var13.next();
                  if (addedItem.canBeEnchanted(true)) {
                    if (capsuledItem.getMaxEnchant() > capsuledItem.getMinEnchant()) {
                      addedItem.setEnchantLevel(Rnd.get(capsuledItem.getMinEnchant(), capsuledItem.getMaxEnchant()));
                    } else {
                      addedItem.setEnchantLevel(capsuledItem.getMinEnchant());
                    }

                    playable.sendPacket((new InventoryUpdate()).addModifiedItem(addedItem));
                  }
                }
              } else {
                chancedItems.add(Pair.of(capsuledItem, capsuledItem.getChance()));
              }
            }

            if (!chancedItems.isEmpty()) {
              Collections.sort(chancedItems, RandomUtils.DOUBLE_GROUP_COMPARATOR);
              CapsuleItemHolder.CapsuledItem capsuledItem = (CapsuleItemHolder.CapsuledItem)RandomUtils.pickRandomSortedGroup(chancedItems, 100.0D);
              if (capsuledItem != null) {
                long count = capsuledItem.getMax() > capsuledItem.getMin() ? Rnd.get(capsuledItem.getMin(), capsuledItem.getMax()) : capsuledItem.getMin();
                List<ItemInstance> addedItems = addItem(playable, capsuledItem.getItemId(), count);
                Iterator var17 = addedItems.iterator();

                while(var17.hasNext()) {
                  ItemInstance addedItem = (ItemInstance)var17.next();
                  if (addedItem.canBeEnchanted(true)) {
                    if (capsuledItem.getMaxEnchant() > capsuledItem.getMinEnchant()) {
                      addedItem.setEnchantLevel(Rnd.get(capsuledItem.getMinEnchant(), capsuledItem.getMaxEnchant()));
                    } else {
                      addedItem.setEnchantLevel(capsuledItem.getMinEnchant());
                    }

                    playable.sendPacket((new InventoryUpdate()).addModifiedItem(addedItem));
                  }
                }
              }
            }

            return true;
          }
        }
      }
    }

    private static List<ItemInstance> addItem(Playable playable, int itemId, long count) {
      if (playable != null && count >= 1L) {
        Object player;
        if (playable.isSummon()) {
          player = playable.getPlayer();
        } else {
          player = playable;
        }

        List<ItemInstance> result = new LinkedList();
        ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
        if (t.isStackable()) {
          result.add(((Playable)player).getInventory().addItem(itemId, count));
        } else {
          for(long i = 0L; i < count; ++i) {
            result.add(((Playable)player).getInventory().addItem(itemId, 1L));
          }
        }

        ((Playable)player).sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
        return result;
      } else {
        return Collections.emptyList();
      }
    }

    public void dropItem(Player player, ItemInstance item, long count, Location loc) {
      IItemHandler.NULL.dropItem(player, item, count, loc);
    }

    public boolean pickupItem(Playable playable, ItemInstance item) {
      return IItemHandler.NULL.pickupItem(playable, item);
    }

    public int[] getItemIds() {
      return this._capsuleItemIds;
    }
  }
}
