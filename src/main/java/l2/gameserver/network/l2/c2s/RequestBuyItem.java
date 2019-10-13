//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBuyItem extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestBuyItem.class);
  private int _listId;
  private int _count;
  private int[] _items;
  private long[] _itemQ;

  public RequestBuyItem() {
  }

  protected void readImpl() {
    this._listId = this.readD();
    this._count = this.readD();
    if (this._count * 8 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        if (this._itemQ[i] < 1L) {
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
      if (activeChar.getBuyListId() != this._listId) {
        activeChar.sendActionFailed();
      } else if (activeChar.isActionsDisabled()) {
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
        if (activeChar.isGM() || merchant != null && isValidMerchant && merchant.isInActingRange(activeChar)) {
          NpcTradeList list = BuyListHolder.getInstance().getBuyList(this._listId);
          if (list == null) {
            activeChar.sendActionFailed();
          } else {
            int slots = 0;
            long weight = 0L;
            long totalPrice = 0L;
            long tax = 0L;
            double taxRate = 0.0D;
            Castle castle = null;
            if (merchant != null) {
              castle = merchant.getCastle(activeChar);
              if (castle != null) {
                taxRate = castle.getTaxRate();
              }
            }

            List<TradeItem> buyList = new ArrayList(this._count);
            List tradeList = list.getItems();

            try {
              int i = 0;

              while(true) {
                if (i >= this._count) {
                  tax = (long)((double)totalPrice * taxRate);
                  totalPrice = SafeMath.addAndCheck(totalPrice, tax);
                  if (!activeChar.getInventory().validateWeight(weight)) {
                    this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    return;
                  }

                  if (!activeChar.getInventory().validateCapacity((long)slots)) {
                    this.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  if (!activeChar.reduceAdena(totalPrice)) {
                    activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                  }

                  Iterator var26 = buyList.iterator();

                  while(var26.hasNext()) {
                    TradeItem ti = (TradeItem)var26.next();
                    activeChar.getInventory().addItem(ti.getItemId(), ti.getCount());
                  }

                  list.updateItems(buyList);
                  if (castle != null && tax > 0L && castle.getOwnerId() > 0 && activeChar.getReflection() == ReflectionManager.DEFAULT) {
                    castle.addToTreasury(tax, true, false);
                  }
                  break;
                }

                int itemId = this._items[i];
                long count = this._itemQ[i];
                long price = 0L;
                Iterator var23 = tradeList.iterator();

                while(true) {
                  if (!var23.hasNext()) {
                    if (price == 0L && (!activeChar.isGM() || !activeChar.getPlayerAccess().UseGMShop)) {
                      activeChar.sendActionFailed();
                      return;
                    }

                    totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));
                    TradeItem ti = new TradeItem();
                    ti.setItemId(itemId);
                    ti.setCount(count);
                    ti.setOwnersPrice(price);
                    weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)ti.getItem().getWeight()));
                    if (!ti.getItem().isStackable() || activeChar.getInventory().getItemByItemId(itemId) == null) {
                      ++slots;
                    }

                    buyList.add(ti);
                    break;
                  }

                  TradeItem ti = (TradeItem)var23.next();
                  if (ti.getItemId() == itemId) {
                    if (ti.isCountLimited() && ti.getCurrentValue() < count) {
                      break;
                    }

                    price = ti.getOwnersPrice();
                  }
                }

                ++i;
              }
            } catch (ArithmeticException var25) {
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            }

            activeChar.sendChanges();
          }
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
