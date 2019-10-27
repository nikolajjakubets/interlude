//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SendTradeDone;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.TradePressOtherOk;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class TradeDone extends L2GameClientPacket {
  private int _response;

  public TradeDone() {
  }

  protected void readImpl() {
    this._response = this.readD();
  }

  protected void runImpl() {
    Player parthner1 = this.getClient().getActiveChar();
    if (parthner1 != null) {
      Request request = parthner1.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.TRADE)) {
        if (!request.isInProgress()) {
          request.cancel();
          parthner1.sendPacket(SendTradeDone.FAIL);
          parthner1.sendActionFailed();
        } else if (parthner1.isOutOfControl()) {
          request.cancel();
          parthner1.sendPacket(SendTradeDone.FAIL);
          parthner1.sendActionFailed();
        } else {
          Player parthner2 = request.getOtherPlayer(parthner1);
          if (parthner2 == null) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendPacket(Msg.THAT_PLAYER_IS_NOT_ONLINE);
            parthner1.sendActionFailed();
          } else if (parthner2.getRequest() != request) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendActionFailed();
          } else if (this._response == 0) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner2.sendPacket(SendTradeDone.FAIL, (new SystemMessage(124)).addString(parthner1.getName()));
          } else if (!parthner2.isInActingRange(parthner1)) {
            parthner1.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
          } else {
            request.confirm(parthner1);
            parthner2.sendPacket((new SystemMessage(121)).addString(parthner1.getName()), TradePressOtherOk.STATIC);
            if (!request.isConfirmed(parthner2)) {
              parthner1.sendActionFailed();
            } else {
              List<TradeItem> tradeList1 = parthner1.getTradeList();
              List<TradeItem> tradeList2 = parthner2.getTradeList();
//              int slots = false;
              long weight = 0L;
              boolean success = false;
              parthner1.getInventory().writeLock();
              parthner2.getInventory().writeLock();

              try {
                int slots = 0;
                Iterator var10 = tradeList1.iterator();

                TradeItem ti;
                ItemInstance item;
                while(var10.hasNext()) {
                  ti = (TradeItem)var10.next();
                  item = parthner1.getInventory().getItemByObjectId(ti.getObjectId());
                  if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner1)) {
                    return;
                  }

                  weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(ti.getCount(), ti.getItem().getWeight()));
                  if (!ti.getItem().isStackable() || parthner2.getInventory().getItemByItemId(ti.getItemId()) == null) {
                    ++slots;
                  }
                }

                if (!parthner2.getInventory().validateWeight(weight)) {
                  parthner2.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                } else if (!parthner2.getInventory().validateCapacity(slots)) {
                  parthner2.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                } else {
                  slots = 0;
                  weight = 0L;
                  var10 = tradeList2.iterator();

                  while(var10.hasNext()) {
                    ti = (TradeItem)var10.next();
                    item = parthner2.getInventory().getItemByObjectId(ti.getObjectId());
                    if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner2)) {
                      return;
                    }

                    weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(ti.getCount(), ti.getItem().getWeight()));
                    if (!ti.getItem().isStackable() || parthner1.getInventory().getItemByItemId(ti.getItemId()) == null) {
                      ++slots;
                    }
                  }

                  if (!parthner1.getInventory().validateWeight(weight)) {
                    parthner1.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                  } else if (!parthner1.getInventory().validateCapacity(slots)) {
                    parthner1.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                  } else {
                    var10 = tradeList1.iterator();

                    while(var10.hasNext()) {
                      ti = (TradeItem)var10.next();
                      item = parthner1.getInventory().removeItemByObjectId(ti.getObjectId(), ti.getCount());
                      Log.LogItem(parthner1, ItemLog.TradeSell, item);
                      Log.LogItem(parthner2, ItemLog.TradeBuy, item);
                      parthner2.getInventory().addItem(item);
                    }

                    var10 = tradeList2.iterator();

                    while(var10.hasNext()) {
                      ti = (TradeItem)var10.next();
                      item = parthner2.getInventory().removeItemByObjectId(ti.getObjectId(), ti.getCount());
                      Log.LogItem(parthner2, ItemLog.TradeSell, item);
                      Log.LogItem(parthner1, ItemLog.TradeBuy, item);
                      parthner1.getInventory().addItem(item);
                    }

                    parthner1.sendPacket(Msg.YOUR_TRADE_IS_SUCCESSFUL);
                    parthner2.sendPacket(Msg.YOUR_TRADE_IS_SUCCESSFUL);
                    success = true;
                  }
                }
              } finally {
                parthner2.getInventory().writeUnlock();
                parthner1.getInventory().writeUnlock();
                request.done();
                parthner1.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
                parthner2.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
              }
            }
          }
        }
      } else {
        parthner1.sendActionFailed();
      }
    }
  }
}
