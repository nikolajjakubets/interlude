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
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SellRefundList;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
public class RequestSellItem extends L2GameClientPacket {
  private int _count;
  private int[] _items;
  private long[] _itemQ;

  public RequestSellItem() {
  }

  protected void readImpl() {
    int _listId = this.readD();
    this._count = this.readD();
    if (this._count * 12 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for (int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this.readD();
        this._itemQ[i] = (long) this.readD();
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
    Player activeChar = ((GameClient) this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isInTrade()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
        activeChar.sendActionFailed();
      } else {
        NpcInstance merchant = activeChar.getLastNpc();
        boolean isValidMerchant = merchant != null && merchant.isMerchantNpc();
        if (Config.ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP || activeChar.isGM() || isValidMerchant && activeChar.isInActingRange(merchant)) {
          activeChar.getInventory().writeLock();

          label156:
          {
            try {
              int i = 0;

              while (true) {
                if (i >= this._count) {
                  break label156;
                }

                int objectId = this._items[i];
                long count = this._itemQ[i];
                ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
                if (item != null && item.getCount() >= count && item.canBeSold(activeChar)) {
                  long price = SafeMath.mulAndCheck(Math.max(1L, (long) item.getReferencePrice() / Config.ALT_SHOP_REFUND_SELL_DIVISOR), count);
                  ItemInstance refund = activeChar.getInventory().removeItemByObjectId(objectId, count);
                  Log.LogItem(activeChar, ItemLog.RefundSell, refund);
                  activeChar.addAdena(price);
                  activeChar.getRefund().addItem(refund);
                }

                ++i;
              }
            } catch (ArithmeticException e) {
              log.error("runImpl: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            } finally {
              activeChar.getInventory().writeUnlock();
            }

            return;
          }

          activeChar.sendPacket(new SellRefundList(activeChar, true));
          activeChar.sendChanges();
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
