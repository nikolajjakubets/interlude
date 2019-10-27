//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import l2.commons.collections.CollectionUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.Inventory.ItemOrderComparator;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.items.listeners.AccessoryListener;
import l2.gameserver.model.items.listeners.ArmorSetListener;
import l2.gameserver.model.items.listeners.BowListener;
import l2.gameserver.model.items.listeners.ItemAugmentationListener;
import l2.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2.gameserver.model.items.listeners.ItemSkillsListener;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

public class PcInventory extends Inventory {
  private final Player _owner;
  private LockType _lockType;
  private int[] _lockItems;
  public boolean isRefresh;
  private static final int[][] arrows = new int[][]{{17}, {1341, 22067}, {1342, 22068}, {1343, 22069}, {1344, 22070}, {1345, 22071}};

  public PcInventory(Player owner) {
    super(owner.getObjectId());
    this._lockType = LockType.NONE;
    this._lockItems = ArrayUtils.EMPTY_INT_ARRAY;
    this.isRefresh = false;
    this._owner = owner;
    this.addListener(ItemSkillsListener.getInstance());
    this.addListener(ItemAugmentationListener.getInstance());
    this.addListener(ItemEnchantOptionsListener.getInstance());
    this.addListener(ArmorSetListener.getInstance());
    this.addListener(BowListener.getInstance());
    this.addListener(AccessoryListener.getInstance());
  }

  public Player getActor() {
    return this._owner;
  }

  protected ItemLocation getBaseLocation() {
    return ItemLocation.INVENTORY;
  }

  protected ItemLocation getEquipLocation() {
    return ItemLocation.PAPERDOLL;
  }

  public long getAdena() {
    ItemInstance _adena = this.getItemByItemId(57);
    return _adena == null ? 0L : _adena.getCount();
  }

  public ItemInstance addAdena(long amount) {
    return this.addItem(57, amount);
  }

  public boolean reduceAdena(long adena) {
    return this.destroyItemByItemId(57, adena);
  }

  public int getPaperdollAugmentationId(int slot) {
    ItemInstance item = this._paperdoll[slot];
    return item != null && item.isAugmented() ? item.getVariationStat1() & '\uffff' | item.getVariationStat2() << 16 : 0;
  }

  protected void onRefreshWeight() {
    this.getActor().refreshOverloaded();
  }

  public void validateItems() {
    ItemInstance[] var1 = this._paperdoll;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      if (item != null && (ItemFunctions.checkIfCanEquip(this.getActor(), item) != null || !item.getTemplate().testCondition(this.getActor(), item, true))) {
        this.unEquipItem(item);
        this.getActor().sendDisarmMessage(item);
      }
    }

  }

  public void validateItemsSkills() {
    ItemInstance[] var1 = this._paperdoll;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      if (item != null && item.getTemplate().getType2() == 0) {
        boolean needUnequipSkills = this.getActor().getGradePenalty() > 0;
        boolean has;
        if (item.getTemplate().getAttachedSkills().length > 0) {
          has = this.getActor().getSkillLevel(item.getTemplate().getAttachedSkills()[0].getId()) > 0;
          if (needUnequipSkills && has) {
            ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, this.getActor());
          } else if (!needUnequipSkills && !has) {
            ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, this.getActor());
          }
        } else if (item.getTemplate().getEnchant4Skill() != null) {
          has = this.getActor().getSkillLevel(item.getTemplate().getEnchant4Skill().getId()) > 0;
          if (needUnequipSkills && has) {
            ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, this.getActor());
          } else if (!needUnequipSkills && !has) {
            ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, this.getActor());
          }
        } else if (!item.getTemplate().getTriggerList().isEmpty()) {
          if (needUnequipSkills) {
            ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, this.getActor());
          } else {
            ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, this.getActor());
          }
        }
      }
    }

  }

  public void refreshEquip() {
    this.isRefresh = true;
    ItemInstance[] var1 = this.getItems();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      if (item.isEquipped()) {
        int slot = item.getEquipSlot();
        this._listeners.onUnequip(slot, item);
        this._listeners.onEquip(slot, item);
      } else if (item.getItemType() == EtcItemType.RUNE) {
        this._listeners.onUnequip(-1, item);
        this._listeners.onEquip(-1, item);
      }
    }

    this.isRefresh = false;
  }

  public void sort(int[][] order) {
    boolean needSort = false;
    int[][] var3 = order;
    int var4 = order.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int[] element = var3[var5];
      ItemInstance item = this.getItemByObjectId(element[0]);
      if (item != null && item.getLocation() == ItemLocation.INVENTORY && item.getLocData() != element[1]) {
        item.setLocData(element[1]);
        needSort = true;
      }
    }

    if (needSort) {
      this._items.sort(ItemOrderComparator.getInstance());
    }

  }

  public ItemInstance findArrowForBow(ItemTemplate bow) {
    int[] arrowsId = arrows[bow.getCrystalType().gradeOrd()];
    ItemInstance ret = null;
    int var5 = arrowsId.length;

    for (int id : arrowsId) {
      if ((ret = this.getItemByItemId(id)) != null) {
        return ret;
      }
    }

    return null;
  }

  public ItemInstance findEquippedLure() {
    ItemInstance res = null;
    int last_lure = 0;
    Player owner = this.getActor();
    String LastLure = owner.getVar("LastLure");
    if (LastLure != null && !LastLure.isEmpty()) {
      last_lure = Integer.parseInt(LastLure);
    }

    ItemInstance[] var5 = this.getItems();
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      ItemInstance temp = var5[var7];
      if (temp.getItemType() == EtcItemType.BAIT) {
        if (temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == 8) {
          return temp;
        }

        if (last_lure > 0 && res == null && temp.getObjectId() == last_lure) {
          res = temp;
        }
      }
    }

    return res;
  }

  public void lockItems(LockType lock, int[] items) {
    if (this._lockType == LockType.NONE) {
      this._lockType = lock;
      this._lockItems = items;
      this.getActor().sendItemList(false);
    }
  }

  public void unlock() {
    if (this._lockType != LockType.NONE) {
      this._lockType = LockType.NONE;
      this._lockItems = ArrayUtils.EMPTY_INT_ARRAY;
      this.getActor().sendItemList(false);
    }
  }

  public boolean isLockedItem(ItemInstance item) {
    switch(this._lockType) {
      case INCLUDE:
        return ArrayUtils.contains(this._lockItems, item.getItemId());
      case EXCLUDE:
        return !ArrayUtils.contains(this._lockItems, item.getItemId());
      default:
        return false;
    }
  }

  public LockType getLockType() {
    return this._lockType;
  }

  public int[] getLockItems() {
    return this._lockItems;
  }

  protected void onRestoreItem(ItemInstance item) {
    super.onRestoreItem(item);
    if (item.getItemType() == EtcItemType.RUNE) {
      this._listeners.onEquip(-1, item);
    }

    if (item.isTemporalItem()) {
      item.startTimer(new PcInventory.LifeTimeTask(item));
    }

    if (item.isCursed()) {
      CursedWeaponsManager.getInstance().checkPlayer(this.getActor(), item);
    }

  }

  protected void onAddItem(ItemInstance item) {
    super.onAddItem(item);
    if (item.getItemType() == EtcItemType.RUNE) {
      this._listeners.onEquip(-1, item);
    }

    if (item.isTemporalItem()) {
      item.startTimer(new PcInventory.LifeTimeTask(item));
    }

    if (item.isCursed()) {
      CursedWeaponsManager.getInstance().checkPlayer(this.getActor(), item);
    }

  }

  protected void onRemoveItem(ItemInstance item) {
    super.onRemoveItem(item);
    this.getActor().removeItemFromShortCut(item.getObjectId());
    if (item.getItemType() == EtcItemType.RUNE) {
      this._listeners.onUnequip(-1, item);
    }

    if (item.isTemporalItem()) {
      item.stopTimer();
    }

  }

  protected void onEquip(int slot, ItemInstance item) {
    super.onEquip(slot, item);
    if (item.isShadowItem()) {
      item.startTimer(new PcInventory.ShadowLifeTimeTask(item));
    }

  }

  protected void onUnequip(int slot, ItemInstance item) {
    super.onUnequip(slot, item);
    if (item.isShadowItem()) {
      item.stopTimer();
    }

  }

  public void restore() {
    int ownerId = this.getOwnerId();
    this.writeLock();

    try {
      Collection<ItemInstance> items = _itemsDAO.loadItemsByOwnerIdAndLoc(ownerId, this.getBaseLocation());
      Iterator var3 = items.iterator();

      ItemInstance item;
      while(var3.hasNext()) {
        item = (ItemInstance)var3.next();
        this._items.add(item);
        this.onRestoreItem(item);
      }

      this._items.sort(ItemOrderComparator.getInstance());
      items = _itemsDAO.loadItemsByOwnerIdAndLoc(ownerId, this.getEquipLocation());
      var3 = items.iterator();

      while(var3.hasNext()) {
        item = (ItemInstance)var3.next();
        this._items.add(item);
        this.onRestoreItem(item);
        if (item.getEquipSlot() >= 17) {
          item.setLocation(this.getBaseLocation());
          item.setLocData(0);
          item.setEquipped(false);
        } else {
          this.setPaperdollItem(item.getEquipSlot(), item);
        }
      }
    } finally {
      this.writeUnlock();
    }

    DelayedItemsManager.getInstance().loadDelayed(this.getActor(), false);
    this.refreshWeight();
  }

  public void store() {
    this.writeLock();

    try {
      _itemsDAO.store(this._items);
    } finally {
      this.writeUnlock();
    }

  }

  protected void sendAddItem(ItemInstance item) {
    Player actor = this.getActor();
    actor.sendPacket((new InventoryUpdate()).addNewItem(item));
  }

  protected void sendModifyItem(ItemInstance item) {
    Player actor = this.getActor();
    actor.sendPacket((new InventoryUpdate()).addModifiedItem(item));
  }

  protected void sendRemoveItem(ItemInstance item) {
    this.getActor().sendPacket((new InventoryUpdate()).addRemovedItem(item));
  }

  public void startTimers() {
  }

  public void stopAllTimers() {
    ItemInstance[] var1 = this.getItems();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      if (item.isShadowItem() || item.isTemporalItem()) {
        item.stopTimer();
      }
    }

  }

  protected class LifeTimeTask extends RunnableImpl {
    private ItemInstance item;

    LifeTimeTask(ItemInstance item) {
      this.item = item;
    }

    public void runImpl() throws Exception {
      Player player = PcInventory.this.getActor();
      int left;
      synchronized(this.item) {
        left = this.item.getPeriod();
        if (left <= 0) {
          PcInventory.this.destroyItem(this.item);
        }
      }

      if (left <= 0) {
        player.sendPacket((new SystemMessage(1726)).addItemName(this.item.getItemId()));
      }

    }
  }

  protected class ShadowLifeTimeTask extends RunnableImpl {
    private ItemInstance item;

    ShadowLifeTimeTask(ItemInstance item) {
      this.item = item;
    }

    public void runImpl() throws Exception {
      Player player = PcInventory.this.getActor();
      if (this.item.isEquipped()) {
        int duration;
        synchronized(this.item) {
          duration = Math.max(0, this.item.getDuration() - 1);
          this.item.setDuration(duration);
          if (duration == 0) {
            PcInventory.this.destroyItem(this.item);
          }
        }

        SystemMessage sm = null;
        if (duration == 10) {
          sm = new SystemMessage(1979);
        } else if (duration == 5) {
          sm = new SystemMessage(1980);
        } else if (duration == 1) {
          sm = new SystemMessage(1981);
        } else if (duration <= 0) {
          sm = new SystemMessage(1982);
        } else {
          player.sendPacket((new InventoryUpdate()).addModifiedItem(this.item));
        }

        if (sm != null) {
          sm.addItemName(this.item.getItemId());
          player.sendPacket(sm);
        }

      }
    }
  }
}
