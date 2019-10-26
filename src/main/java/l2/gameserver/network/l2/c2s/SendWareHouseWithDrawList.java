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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendWareHouseWithDrawList extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);
  private int _count;
  private int[] _items;
  private long[] _itemQ;

  public SendWareHouseWithDrawList() {
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
          break;
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
        if ((whkeeper == null || !whkeeper.isInActingRange(activeChar)) && !Config.ALT_ALLOW_REMOTE_USE_CARGO_BOX) {
          activeChar.sendPacket(Msg.WAREHOUSE_IS_TOO_FAR);
        } else {
          Warehouse warehouse = null;
          ItemLog logType = null;
          if (activeChar.getUsingWarehouseType() == WarehouseType.PRIVATE) {
            warehouse = activeChar.getWarehouse();
            logType = ItemLog.WarehouseWithdraw;
          } else if (activeChar.getUsingWarehouseType() == WarehouseType.CLAN) {
            logType = ItemLog.ClanWarehouseWithdraw;
            if (activeChar.getClan() == null || activeChar.getClan().getLevel() == 0) {
              activeChar.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
              return;
            }

            boolean canWithdrawCWH = false;
            if (activeChar.getClan() != null && (activeChar.getClanPrivileges() & 8) == 8 && (Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || activeChar.isClanLeader() || activeChar.getVarB("canWhWithdraw"))) {
              canWithdrawCWH = true;
            }

            if (!canWithdrawCWH) {
              activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
              return;
            }

            warehouse = activeChar.getClan().getWarehouse();
          } else {
            if (activeChar.getUsingWarehouseType() != WarehouseType.FREIGHT) {
              _log.warn("Error retrieving a warehouse object for char " + activeChar.getName() + " - using warehouse type: " + activeChar.getUsingWarehouseType());
              return;
            }

            warehouse = activeChar.getFreight();
            logType = ItemLog.FreightWithdraw;
          }

          PcInventory inventory = activeChar.getInventory();
          inventory.writeLock();
          ((Warehouse)warehouse).writeLock();

          try {
            long weight = 0L;
            int slots = 0;
            int i = 0;

            while(true) {
              ItemInstance item;
              if (i >= this._count) {
                if (!activeChar.getInventory().validateCapacity((long)slots)) {
                  activeChar.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                  return;
                }

                if (!activeChar.getInventory().validateWeight(weight)) {
                  activeChar.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                  return;
                }

                for(i = 0; i < this._count; ++i) {
                  item = ((Warehouse)warehouse).removeItemByObjectId(this._items[i], this._itemQ[i]);
                  Log.LogItem(activeChar, logType, item);
                  activeChar.getInventory().addItem(item);
                }
                break;
              }

              item = ((Warehouse)warehouse).getItemByObjectId(this._items[i]);
              if (item == null || item.getCount() < this._itemQ[i]) {
                activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                return;
              }

              weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck((long)item.getTemplate().getWeight(), this._itemQ[i]));
              if (!item.isStackable() || inventory.getItemByItemId(item.getItemId()) == null) {
                ++slots;
              }

              ++i;
            }
          } catch (ArithmeticException var14) {
            this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
          } finally {
            ((Warehouse)warehouse).writeUnlock();
            inventory.writeUnlock();
          }

          activeChar.sendChanges();
          activeChar.sendPacket(Msg.THE_TRANSACTION_IS_COMPLETE);
        }
      }
    }
  }
}
