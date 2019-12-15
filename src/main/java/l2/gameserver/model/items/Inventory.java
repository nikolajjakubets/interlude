//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.items.listeners.StatsListener;
import l2.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@Slf4j
public abstract class Inventory extends ItemContainer {
  public static final int PAPERDOLL_UNDER = 0;
  public static final int PAPERDOLL_REAR = 1;
  public static final int PAPERDOLL_LEAR = 2;
  public static final int PAPERDOLL_NECK = 3;
  public static final int PAPERDOLL_RFINGER = 4;
  public static final int PAPERDOLL_LFINGER = 5;
  public static final int PAPERDOLL_HEAD = 6;
  public static final int PAPERDOLL_RHAND = 7;
  public static final int PAPERDOLL_LHAND = 8;
  public static final int PAPERDOLL_GLOVES = 9;
  public static final int PAPERDOLL_CHEST = 10;
  public static final int PAPERDOLL_LEGS = 11;
  public static final int PAPERDOLL_FEET = 12;
  public static final int PAPERDOLL_BACK = 13;
  public static final int PAPERDOLL_LRHAND = 14;
  public static final int PAPERDOLL_HAIR = 15;
  public static final int PAPERDOLL_DHAIR = 16;
  public static final int PAPERDOLL_MAX = 17;
  public static final int[] PAPERDOLL_ORDER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 7, 15, 16};
  protected final int _ownerId;
  protected final ItemInstance[] _paperdoll = new ItemInstance[17];
  protected final Inventory.InventoryListenerList _listeners = new Inventory.InventoryListenerList();
  protected int _totalWeight;
  protected long _wearedMask;

  protected Inventory(int ownerId) {
    this._ownerId = ownerId;
    this.addListener(StatsListener.getInstance());
  }

  public abstract Playable getActor();

  protected abstract ItemLocation getBaseLocation();

  protected abstract ItemLocation getEquipLocation();

  public int getOwnerId() {
    return this._ownerId;
  }

  protected void onRestoreItem(ItemInstance item) {
    this._totalWeight = (int)((long)this._totalWeight + (long)item.getTemplate().getWeight() * item.getCount());
  }

  protected void onAddItem(ItemInstance item) {
    item.setOwnerId(this.getOwnerId());
    item.setLocation(this.getBaseLocation());
    item.setLocData(this.findSlot());
    this.sendAddItem(item);
    this.refreshWeight();
    item.save();
  }

  protected void onModifyItem(ItemInstance item) {
    this.sendModifyItem(item);
    this.refreshWeight();
  }

  protected void onRemoveItem(ItemInstance item) {
    if (item.isEquipped()) {
      this.unEquipItem(item);
    }

    this.sendRemoveItem(item);
    item.setLocData(-1);
    item.save();
    this.refreshWeight();
  }

  protected void onDestroyItem(ItemInstance item) {
    item.setCount(0L);
    item.delete();
  }

  protected void onEquip(int slot, ItemInstance item) {
    this._listeners.onEquip(slot, item);
    item.setLocation(this.getEquipLocation());
    item.setLocData(slot);
    item.setEquipped(true);
    this.sendModifyItem(item);
    this._wearedMask |= item.getTemplate().getItemMask();
    item.save();
  }

  protected void onUnequip(int slot, ItemInstance item) {
    item.setLocation(this.getBaseLocation());
    item.setLocData(this.findSlot());
    item.setEquipped(false);
    item.setChargedSpiritshot(0);
    item.setChargedSoulshot(0);
    this.sendModifyItem(item);
    this._wearedMask &= ~item.getTemplate().getItemMask();
    this._listeners.onUnequip(slot, item);
    item.save();
  }

  private int findSlot() {
//    int slot = false;
    int slot = 0;

    label28:
    while(slot < this._items.size()) {
      for (ItemInstance item : this._items) {
        if (!item.isEquipped() && !item.getTemplate().isQuest() && item.getEquipSlot() == slot) {
          ++slot;
          continue label28;
        }
      }

      return slot;
    }

    return slot;
  }

  public ItemInstance getPaperdollItem(int slot) {
    return this._paperdoll[slot];
  }

  public ItemInstance[] getPaperdollItems() {
    return this._paperdoll;
  }

  public int getPaperdollBodyPart(int slot) {
    ItemInstance item = this.getPaperdollItem(slot);
    if (item != null) {
      return item.getBodyPart();
    } else {
      if (slot == 15) {
        item = this._paperdoll[16];
        if (item != null) {
          return item.getBodyPart();
        }
      }

      return 0;
    }
  }

  public int getPaperdollItemId(int slot) {
    ItemInstance item = this.getPaperdollItem(slot);
    if (item != null) {
      return item.getVisibleItemId();
    } else {
      if (slot == 15) {
        item = this._paperdoll[16];
        if (item != null) {
          return item.getVisibleItemId();
        }
      }

      return 0;
    }
  }

  public int getPaperdollObjectId(int slot) {
    ItemInstance item = this._paperdoll[slot];
    if (item != null) {
      return item.getObjectId();
    } else {
      if (slot == 15) {
        item = this._paperdoll[16];
        if (item != null) {
          return item.getObjectId();
        }
      }

      return 0;
    }
  }

  public void addListener(OnEquipListener listener) {
    this._listeners.add(listener);
  }

  public void removeListener(OnEquipListener listener) {
    this._listeners.remove(listener);
  }

  public Inventory.InventoryListenerList getListeners() {
    return this._listeners;
  }

  public ItemInstance setPaperdollItem(int slot, ItemInstance item) {
    this.writeLock();

    ItemInstance old;
    try {
      old = this._paperdoll[slot];
      if (old != item) {
        if (old != null) {
          this._paperdoll[slot] = null;
          this.onUnequip(slot, old);
        }

        if (item != null) {
          this._paperdoll[slot] = item;
          this.onEquip(slot, item);
        }
      }
    } finally {
      this.writeUnlock();
    }

    return old;
  }

  public long getWearedMask() {
    return this._wearedMask;
  }

  public void unEquipItem(ItemInstance item) {
    if (item.isEquipped()) {
      this.unEquipItemInBodySlot(item.getBodyPart(), item);
    }

  }

  public void unEquipItemInBodySlot(int bodySlot) {
    this.unEquipItemInBodySlot(bodySlot, null);
  }

  private void unEquipItemInBodySlot(int bodySlot, ItemInstance item) {
    int pdollSlot = -1;
    switch(bodySlot) {
      case 1:
        pdollSlot = 0;
        break;
      case 2:
        pdollSlot = 1;
        break;
      case 4:
        pdollSlot = 2;
        break;
      case 6:
        if (item == null) {
          return;
        }

        if (this.getPaperdollItem(2) == item) {
          pdollSlot = 2;
        }

        if (this.getPaperdollItem(1) == item) {
          pdollSlot = 1;
        }
        break;
      case 8:
        pdollSlot = 3;
        break;
      case 16:
        pdollSlot = 4;
        break;
      case 32:
        pdollSlot = 5;
        break;
      case 48:
        if (item == null) {
          return;
        }

        if (this.getPaperdollItem(5) == item) {
          pdollSlot = 5;
        }

        if (this.getPaperdollItem(4) == item) {
          pdollSlot = 4;
        }
        break;
      case 64:
        pdollSlot = 6;
        break;
      case 128:
        pdollSlot = 7;
        break;
      case 256:
        pdollSlot = 8;
        break;
      case 512:
        pdollSlot = 9;
        break;
      case 1024:
      case 32768:
      case 131072:
        pdollSlot = 10;
        break;
      case 2048:
        pdollSlot = 11;
        break;
      case 4096:
        pdollSlot = 12;
        break;
      case 8192:
        pdollSlot = 13;
        break;
      case 16384:
        this.setPaperdollItem(8, null);
        pdollSlot = 7;
        break;
      case 65536:
        pdollSlot = 15;
        break;
      case 262144:
        pdollSlot = 16;
        break;
      case 524288:
        this.setPaperdollItem(16, null);
        pdollSlot = 15;
        break;
      default:
        log.warn("Requested invalid body slot: " + bodySlot + ", Item: " + item + ", ownerId: '" + this.getOwnerId() + "'");
        return;
    }

    if (pdollSlot >= 0) {
      this.setPaperdollItem(pdollSlot, null);
    }

  }

  public void equipItem(ItemInstance item) {
    int bodySlot = item.getBodyPart();
    double hp = this.getActor().getCurrentHp();
    double mp = this.getActor().getCurrentMp();
    double cp = this.getActor().getCurrentCp();
    ItemInstance old;
    switch(bodySlot) {
      case 1:
        this.setPaperdollItem(0, item);
        break;
      case 2:
      case 4:
      case 6:
        if (this._paperdoll[1] == null) {
          this.setPaperdollItem(1, item);
        } else if (this._paperdoll[2] == null) {
          this.setPaperdollItem(2, item);
        } else {
          this.setPaperdollItem(2, item);
        }
        break;
      case 8:
        this.setPaperdollItem(3, item);
        break;
      case 16:
      case 32:
      case 48:
        if (this._paperdoll[4] == null) {
          this.setPaperdollItem(4, item);
        } else if (this._paperdoll[5] == null) {
          this.setPaperdollItem(5, item);
        } else {
          this.setPaperdollItem(5, item);
        }
        break;
      case 64:
        if (this.getPaperdollBodyPart(10) == 131072) {
          this.setPaperdollItem(10, null);
        }

        this.setPaperdollItem(6, item);
        break;
      case 128:
        this.setPaperdollItem(7, item);
        break;
      case 256:
        old = this.getPaperdollItem(7);
        ItemTemplate rHandItemTemplate = old == null ? null : old.getTemplate();
        ItemTemplate newItem = item.getTemplate();
        if (newItem.getItemType() == EtcItemType.ARROW) {
          if (rHandItemTemplate == null) {
            return;
          }

          if (rHandItemTemplate.getItemType() != WeaponType.BOW) {
            return;
          }

          if (rHandItemTemplate.getCrystalType() != newItem.getCrystalType()) {
            return;
          }
        } else if (newItem.getItemType() == EtcItemType.BAIT) {
          if (rHandItemTemplate == null) {
            return;
          }

          if (rHandItemTemplate.getItemType() != WeaponType.ROD) {
            return;
          }

          if (!this.getActor().isPlayer()) {
            return;
          }

          Player owner = (Player)this.getActor();
          owner.setVar("LastLure", String.valueOf(item.getObjectId()), -1L);
        } else if (rHandItemTemplate != null && rHandItemTemplate.getBodyPart() == 16384) {
          this.setPaperdollItem(7, null);
        }

        this.setPaperdollItem(8, item);
        break;
      case 512:
        if (this.getPaperdollBodyPart(10) == 131072) {
          this.setPaperdollItem(10, null);
        }

        this.setPaperdollItem(9, item);
        break;
      case 1024:
        this.setPaperdollItem(10, item);
        break;
      case 2048:
        old = this.getPaperdollItem(10);
        if (old != null && old.getBodyPart() == 32768 || this.getPaperdollBodyPart(10) == 131072) {
          this.setPaperdollItem(10, null);
        }

        this.setPaperdollItem(11, item);
        break;
      case 4096:
        if (this.getPaperdollBodyPart(10) == 131072) {
          this.setPaperdollItem(10, null);
        }

        this.setPaperdollItem(12, item);
        break;
      case 8192:
        this.setPaperdollItem(13, item);
        break;
      case 16384:
        this.setPaperdollItem(8, null);
        this.setPaperdollItem(7, item);
        break;
      case 32768:
        this.setPaperdollItem(11, null);
        this.setPaperdollItem(10, item);
        break;
      case 65536:
        old = this.getPaperdollItem(16);
        if (old != null && old.getBodyPart() == 524288) {
          this.setPaperdollItem(16, null);
        }

        this.setPaperdollItem(15, item);
        break;
      case 131072:
        this.setPaperdollItem(11, null);
        this.setPaperdollItem(6, null);
        this.setPaperdollItem(12, null);
        this.setPaperdollItem(9, null);
        this.setPaperdollItem(10, item);
        break;
      case 262144:
        ItemInstance slot2 = this.getPaperdollItem(16);
        if (slot2 != null && slot2.getBodyPart() == 524288) {
          this.setPaperdollItem(15, null);
        }

        this.setPaperdollItem(16, item);
        break;
      case 524288:
        this.setPaperdollItem(15, null);
        this.setPaperdollItem(16, item);
        break;
      default:
        log.warn("unknown body slot:" + bodySlot + " for item id: " + item.getItemId());
        return;
    }

    this.getActor().setCurrentHp(hp, false);
    this.getActor().setCurrentMp(mp);
    this.getActor().setCurrentCp(cp);
    if (this.getActor().isPlayer()) {
      ((Player)this.getActor()).autoShot();
    }

  }

  protected abstract void sendAddItem(ItemInstance var1);

  protected abstract void sendModifyItem(ItemInstance var1);

  protected abstract void sendRemoveItem(ItemInstance var1);

  protected void refreshWeight() {
    int weight = 0;
    this.readLock();

    try {
      for (ItemInstance item : this._items) {
        weight = (int) ((long) weight + (long) item.getTemplate().getWeight() * item.getCount());
      }
    } finally {
      this.readUnlock();
    }

    if (this._totalWeight != weight) {
      this._totalWeight = weight;
      this.onRefreshWeight();
    }
  }

  protected abstract void onRefreshWeight();

  public int getTotalWeight() {
    return this._totalWeight;
  }

  public boolean validateCapacity(ItemInstance item) {
    long slots = 0L;
    if (!item.isStackable() || this.getItemByItemId(item.getItemId()) == null) {
      ++slots;
    }

    return this.validateCapacity(slots);
  }

  public boolean validateCapacity(int itemId, long count) {
    ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
    return this.validateCapacity(item, count);
  }

  public boolean validateCapacity(ItemTemplate item, long count) {
    long slots = 0L;
    if (!item.isStackable() || this.getItemByItemId(item.getItemId()) == null) {
      slots = count;
    }

    return this.validateCapacity(slots);
  }

  public boolean validateCapacity(long slots) {
    if (slots == 0L) {
      return true;
    } else if (slots >= -2147483648L && slots <= 2147483647L) {
      if (this.getSize() + (int)slots < 0) {
        return false;
      } else {
        return (long)this.getSize() + slots <= (long)this.getActor().getInventoryLimit();
      }
    } else {
      return false;
    }
  }

  public boolean validateWeight(ItemInstance item) {
    long weight = (long)item.getTemplate().getWeight() * item.getCount();
    return this.validateWeight(weight);
  }

  public boolean validateWeight(int itemId, long count) {
    ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
    return this.validateWeight(item, count);
  }

  public boolean validateWeight(ItemTemplate item, long count) {
    long weight = (long)item.getWeight() * count;
    return this.validateWeight(weight);
  }

  public boolean validateWeight(long weight) {
    if (weight == 0L) {
      return true;
    } else if (weight >= -2147483648L && weight <= 2147483647L) {
      if (this.getTotalWeight() + (int)weight < 0) {
        return false;
      } else {
        return (long)this.getTotalWeight() + weight <= (long)this.getActor().getMaxLoad();
      }
    } else {
      return false;
    }
  }

  public abstract void restore();

  public abstract void store();

  public static int getPaperdollIndex(int slot) {
    switch(slot) {
      case 1:
        return 0;
      case 2:
        return 1;
      case 4:
        return 2;
      case 8:
        return 3;
      case 16:
        return 4;
      case 32:
        return 5;
      case 64:
        return 6;
      case 128:
        return 7;
      case 256:
        return 8;
      case 512:
        return 9;
      case 1024:
      case 32768:
      case 131072:
        return 10;
      case 2048:
        return 11;
      case 4096:
        return 12;
      case 8192:
        return 13;
      case 16384:
        return 14;
      case 65536:
      case 524288:
        return 15;
      case 262144:
        return 16;
      default:
        return -1;
    }
  }

  public int getSize() {
    return super.getSize();
  }

  public static class ItemOrderComparator implements Comparator<ItemInstance> {
    private static final Comparator<ItemInstance> instance = new Inventory.ItemOrderComparator();

    public ItemOrderComparator() {
    }

    public static final Comparator<ItemInstance> getInstance() {
      return instance;
    }

    public int compare(ItemInstance o1, ItemInstance o2) {
      return o1 != null && o2 != null ? o1.getLocData() - o2.getLocData() : 0;
    }
  }

  public class InventoryListenerList extends ListenerList<Playable> {
    public InventoryListenerList() {
    }

    public void onEquip(int slot, ItemInstance item) {

      for (Listener<Playable> playableListener : this.getListeners()) {
        ((OnEquipListener) playableListener).onEquip(slot, item, Inventory.this.getActor());
      }

    }

    public void onUnequip(int slot, ItemInstance item) {

      for (Listener<Playable> playableListener : this.getListeners()) {
        ((OnEquipListener) playableListener).onUnequip(slot, item, Inventory.this.getActor());
      }

    }
  }
}
