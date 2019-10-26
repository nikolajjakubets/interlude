//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestGetItemFromPet extends L2GameClientPacket {
  private int _objectId;
  private long _amount;
  private int _unknown;

  public RequestGetItemFromPet() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._amount = (long)this.readD();
    this._unknown = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._amount >= 1L) {
      PetInstance pet = (PetInstance)activeChar.getPet();
      if (pet == null) {
        activeChar.sendActionFailed();
      } else if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (!activeChar.isInTrade() && !activeChar.isProcessingRequest()) {
        if (activeChar.isFishing()) {
          activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_);
        } else {
          PetInventory petInventory = pet.getInventory();
          PcInventory playerInventory = activeChar.getInventory();
          petInventory.writeLock();
          playerInventory.writeLock();

          try {
            ItemInstance item = petInventory.getItemByObjectId(this._objectId);
            if (item == null || item.getCount() < this._amount || item.isEquipped()) {
              activeChar.sendActionFailed();
              return;
            }

            int slots = 0;
            long weight = (long)item.getTemplate().getWeight() * this._amount;
            if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
              slots = 1;
            }

            if (!activeChar.getInventory().validateWeight(weight)) {
              activeChar.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
              return;
            }

            if (!activeChar.getInventory().validateCapacity((long)slots)) {
              activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
              return;
            }

            item = petInventory.removeItemByObjectId(this._objectId, this._amount);
            Log.LogItem(activeChar, ItemLog.FromPet, item);
            playerInventory.addItem(item);
            pet.sendChanges();
            activeChar.sendChanges();
          } finally {
            playerInventory.writeUnlock();
            petInventory.writeUnlock();
          }

        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
