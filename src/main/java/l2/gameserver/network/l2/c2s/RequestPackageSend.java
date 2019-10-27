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
import l2.gameserver.model.items.PcFreight;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;

public class RequestPackageSend extends L2GameClientPacket {
  private static final long _FREIGHT_FEE = 1000L;
  private int _objectId;
  private int _count;
  private int[] _items;
  private int[] _itemQ;

  public RequestPackageSend() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._count = this.readD();
    if (this._count * 8 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new int[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = this.readD();
        if (this._itemQ[i] < 1 || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
          this._count = 0;
          return;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() throws Exception {
    Player player = this.getClient().getActiveChar();
    if (player != null && this._count != 0) {
      if (!player.getPlayerAccess().UseWarehouse) {
        player.sendActionFailed();
      } else if (player.isActionsDisabled()) {
        player.sendActionFailed();
      } else if (player.isInStoreMode()) {
        player.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (player.isInTrade()) {
        player.sendActionFailed();
      } else {
        NpcInstance whkeeper = player.getLastNpc();
        if ((whkeeper == null || !whkeeper.isInActingRange(whkeeper)) && !Config.ALT_ALLOW_REMOTE_USE_CARGO_BOX) {
          player.sendPacket(Msg.WAREHOUSE_IS_TOO_FAR);
        } else if (player.getAccountChars().containsKey(this._objectId)) {
          PcInventory inventory = player.getInventory();
          PcFreight freight = new PcFreight(this._objectId);
          freight.restore();
          inventory.writeLock();
          freight.writeLock();

          label300: {
            try {
//              int slotsleft = false;
              long adenaDeposit = 0L;
              int slotsleft = Config.FREIGHT_SLOTS - freight.getSize();
              int items = 0;

              for(int i = 0; i < this._count; ++i) {
                ItemInstance item = inventory.getItemByObjectId(this._items[i]);
                if (item != null && item.getCount() >= (long)this._itemQ[i] && item.getTemplate().isTradeable() && CanSendItem(item)) {
                  if (!item.isStackable() || freight.getItemByItemId(item.getItemId()) == null) {
                    if (slotsleft <= 0) {
                      this._items[i] = 0;
                      this._itemQ[i] = 0;
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
                  this._itemQ[i] = 0;
                }
              }

              if (slotsleft <= 0) {
                player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              }

              if (items == 0) {
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                return;
              }

              long fee = SafeMath.mulAndCheck(items, 1000L);
              if (fee + adenaDeposit <= player.getAdena()) {
                if (!player.reduceAdena(fee, true)) {
                  player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                  return;
                }

                int i = 0;

                while(true) {
                  if (i >= this._count) {
                    break label300;
                  }

                  if (this._items[i] != 0) {
                    ItemInstance item = inventory.removeItemByObjectId(this._items[i], this._itemQ[i]);
                    Log.LogItem(player, ItemLog.FreightDeposit, item);
                    freight.addItem(item);
                  }

                  ++i;
                }
              }

              player.sendPacket(SystemMsg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
            } catch (ArithmeticException var16) {
              player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            } finally {
              freight.writeUnlock();
              inventory.writeUnlock();
            }

            return;
          }

          player.sendChanges();
          player.sendPacket(SystemMsg.THE_TRANSACTION_IS_COMPLETE);
        }
      }
    }
  }

  public static boolean CanSendItem(ItemInstance item) {
    if (!item.getTemplate().isTradeable()) {
      return false;
    } else {
      return !item.isEquipped() && !item.getTemplate().isQuest() && !item.isAugmented();
    }
  }
}
