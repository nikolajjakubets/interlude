//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.network.l2.s2c.PetInventoryUpdate;
import l2.gameserver.utils.ItemFunctions;

public class PetInventory extends Inventory {
  private final PetInstance _actor;

  public PetInventory(PetInstance actor) {
    super(actor.getPlayer().getObjectId());
    this._actor = actor;
  }

  public PetInstance getActor() {
    return this._actor;
  }

  public Player getOwner() {
    return this._actor.getPlayer();
  }

  protected ItemLocation getBaseLocation() {
    return ItemLocation.PET_INVENTORY;
  }

  protected ItemLocation getEquipLocation() {
    return ItemLocation.PET_PAPERDOLL;
  }

  protected void onRefreshWeight() {
    this.getActor().sendPetInfo();
  }

  protected void sendAddItem(ItemInstance item) {
    this.getOwner().sendPacket((new PetInventoryUpdate()).addNewItem(item));
  }

  protected void sendModifyItem(ItemInstance item) {
    this.getOwner().sendPacket((new PetInventoryUpdate()).addModifiedItem(item));
  }

  protected void sendRemoveItem(ItemInstance item) {
    this.getOwner().sendPacket((new PetInventoryUpdate()).addRemovedItem(item));
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

      items = _itemsDAO.loadItemsByOwnerIdAndLoc(ownerId, this.getEquipLocation());
      var3 = items.iterator();

      while(var3.hasNext()) {
        item = (ItemInstance)var3.next();
        this._items.add(item);
        this.onRestoreItem(item);
        if (ItemFunctions.checkIfCanEquip(this.getActor(), item) == null) {
          this.setPaperdollItem(item.getEquipSlot(), item);
        }
      }
    } finally {
      this.writeUnlock();
    }

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

  public void validateItems() {
    ItemInstance[] var1 = this._paperdoll;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemInstance item = var1[var3];
      if (item != null && (ItemFunctions.checkIfCanEquip(this.getActor(), item) != null || !item.getTemplate().testCondition(this.getActor(), item, false))) {
        this.unEquipItem(item);
      }
    }

  }
}
