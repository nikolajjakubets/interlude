//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.model.items.Warehouse.WarehouseType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;

public class SendWareHouseDepositList extends L2GameClientPacket {
  private static final long _WAREHOUSE_FEE = 30L;
  private int _count;
  private int[] _items;
  private long[] _itemQ;

  public SendWareHouseDepositList() {
  }

  protected void readImpl() {
    this._count = this.readD();
    if (this._count * 8 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        if (this._itemQ[i] < 1L || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
          this._count = 0;
          return;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      if (!activeChar.getPlayerAccess().UseWarehouse) {
        activeChar.sendActionFailed();
      } else if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isInTrade()) {
        activeChar.sendActionFailed();
      } else {
        NpcInstance whkeeper = activeChar.getLastNpc();
        if (whkeeper != null && whkeeper.isInActingRange(activeChar)) {
          PcInventory inventory = activeChar.getInventory();
          boolean privatewh = activeChar.getUsingWarehouseType() != WarehouseType.CLAN;
          Warehouse warehouse;
          if (privatewh) {
            warehouse = activeChar.getWarehouse();
          } else {
            warehouse = activeChar.getClan().getWarehouse();
          }

          inventory.writeLock();
          warehouse.writeLock();

          try {
//            int slotsleft = false;
            long adenaDeposit = 0L;
            int slotsleft;
            if (privatewh) {
              slotsleft = activeChar.getWarehouseLimit() - warehouse.getSize();
            } else {
              slotsleft = activeChar.getClan().getWhBonus() + Config.WAREHOUSE_SLOTS_CLAN - warehouse.getSize();
            }

            int items = 0;

            for(int i = 0; i < this._count; ++i) {
              ItemInstance item = inventory.getItemByObjectId(this._items[i]);
              if (item != null && item.getCount() >= this._itemQ[i] && item.canBeStored(activeChar, privatewh)) {
                if (!item.isStackable() || warehouse.getItemByItemId(item.getItemId()) == null) {
                  if (slotsleft <= 0) {
                    this._items[i] = 0;
                    this._itemQ[i] = 0L;
                    continue;
                  }

                  --slotsleft;
                }

                if (item.getItemId() == 57) {
                  adenaDeposit = this._itemQ[i];
                }

                ++items;
              } else {
                this._items[i] = 0;
                this._itemQ[i] = 0L;
              }
            }

            if (slotsleft <= 0) {
              activeChar.sendPacket(Msg.YOUR_WAREHOUSE_IS_FULL);
            }

            if (items == 0) {
              activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
              return;
            }

            long fee = SafeMath.mulAndCheck((long)items, 30L);
            if (fee + adenaDeposit > activeChar.getAdena()) {
              activeChar.sendPacket(Msg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
              return;
            }

            if (!activeChar.reduceAdena(fee, true)) {
              this.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
              return;
            }

            for(int i = 0; i < this._count; ++i) {
              if (this._items[i] != 0) {
                ItemInstance item = inventory.removeItemByObjectId(this._items[i], this._itemQ[i]);
                Log.LogItem(activeChar, privatewh ? ItemLog.WarehouseDeposit : ItemLog.ClanWarehouseDeposit, item);
                warehouse.addItem(item);
              }
            }
          } catch (ArithmeticException var17) {
            this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
          } finally {
            warehouse.writeUnlock();
            inventory.writeUnlock();
          }

          activeChar.sendChanges();
          activeChar.sendPacket(Msg.THE_TRANSACTION_IS_COMPLETE);
        } else {
          activeChar.sendPacket(Msg.WAREHOUSE_IS_TOO_FAR);
        }
      }
    }
  }
}
