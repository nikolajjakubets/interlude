//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.TradeHelper;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;

public class RequestPrivateStoreManageBuy extends L2GameClientPacket {
  private int _sellerId;
  private int _count;
  private int[] _items;
  private long[] _itemQ;
  private long[] _itemP;

  public RequestPrivateStoreManageBuy() {
  }

  protected void readImpl() {
    this._sellerId = this.readD();
    this._count = this.readD();
    if (this._count * 12 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];
      this._itemP = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        this._itemP[i] = (long)this.readD();
        if (this._itemQ[i] < 1L || this._itemP[i] < 1L || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
          this._count = 0;
          break;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player buyer = ((GameClient)this.getClient()).getActiveChar();
    if (buyer != null && this._count != 0) {
      if (buyer.isActionsDisabled()) {
        buyer.sendActionFailed();
      } else if (buyer.isInStoreMode()) {
        buyer.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (buyer.isInTrade()) {
        buyer.sendActionFailed();
      } else if (buyer.isFishing()) {
        buyer.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
      } else if (!buyer.getPlayerAccess().UseTrade) {
        buyer.sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
      } else {
        Player seller = (Player)buyer.getVisibleObject(this._sellerId);
        if (seller != null && (seller.getPrivateStoreType() == 1 || seller.getPrivateStoreType() == 8) && seller.isInActingRange(buyer)) {
          List<TradeItem> sellList = seller.getSellList();
          if (sellList.isEmpty()) {
            buyer.sendPacket(Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
            buyer.sendActionFailed();
          } else {
            List<TradeItem> buyList = new ArrayList();
            long totalCost = 0L;
            int slots = 0;
            long weight = 0L;
            buyer.getInventory().writeLock();
            seller.getInventory().writeLock();
            boolean var49 = false;

            label1687: {
              long count;
              try {
                var49 = true;

                for(int i = 0; i < this._count; ++i) {
                  int objectId = this._items[i];
                  count = this._itemQ[i];
                  long price = this._itemP[i];
                  TradeItem bi = null;
                  Iterator var17 = sellList.iterator();

                  while(var17.hasNext()) {
                    TradeItem si = (TradeItem)var17.next();
                    if (si.getObjectId() == objectId && si.getOwnersPrice() == price) {
                      if (count > si.getCount()) {
                        var49 = false;
                        break label1687;
                      }

                      ItemInstance item = seller.getInventory().getItemByObjectId(objectId);
                      if (item == null) {
                        var49 = false;
                        break label1687;
                      }

                      if (item.getCount() < count) {
                        var49 = false;
                        break label1687;
                      }

                      if (!item.canBeTraded(seller)) {
                        var49 = false;
                        break label1687;
                      }

                      totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
                      weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)item.getTemplate().getWeight()));
                      if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null) {
                        ++slots;
                      }

                      bi = new TradeItem();
                      bi.setObjectId(objectId);
                      bi.setItemId(item.getItemId());
                      bi.setCount(count);
                      bi.setOwnersPrice(price);
                      buyList.add(bi);
                      break;
                    }
                  }
                }

                var49 = false;
                break label1687;
              } catch (ArithmeticException var53) {
                buyList.clear();
                this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                var49 = false;
              } finally {
                if (var49) {
                  try {
                    if (buyList.size() == this._count && (seller.getPrivateStoreType() != 8 || buyList.size() == sellList.size())) {
                      if (!buyer.getInventory().validateWeight(weight)) {
                        buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        buyer.sendActionFailed();
                        return;
                      }

                      if (!buyer.getInventory().validateCapacity((long)slots)) {
                        buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                        buyer.sendActionFailed();
                        return;
                      }

                      if (!buyer.reduceAdena(totalCost)) {
                        buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        buyer.sendActionFailed();
                        return;
                      }

                      Iterator var24 = buyList.iterator();

                      while(var24.hasNext()) {
                        TradeItem bi = (TradeItem)var24.next();
                        ItemInstance item = seller.getInventory().removeItemByObjectId(bi.getObjectId(), bi.getCount());
                        Iterator var26 = sellList.iterator();

                        while(var26.hasNext()) {
                          TradeItem si = (TradeItem)var26.next();
                          if (si.getObjectId() == bi.getObjectId()) {
                            si.setCount(si.getCount() - bi.getCount());
                            if (si.getCount() < 1L) {
                              sellList.remove(si);
                            }
                            break;
                          }
                        }

                        Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                        Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                        buyer.getInventory().addItem(item);
                        TradeHelper.purchaseItem(buyer, seller, bi);
                      }

                      long tax = TradeHelper.getTax(seller, totalCost);
                      if (tax > 0L) {
                        totalCost -= tax;
                        seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(tax));
                      }

                      seller.addAdena(totalCost);
                      seller.saveTradeList();
                    }

                    buyer.sendPacket(Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                    buyer.sendActionFailed();
                  } finally {
                    seller.getInventory().writeUnlock();
                    buyer.getInventory().writeUnlock();
                  }

                  return;
                }
              }

              try {
                if (buyList.size() != this._count || seller.getPrivateStoreType() == 8 && buyList.size() != sellList.size()) {
                  buyer.sendPacket(Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                  buyer.sendActionFailed();
                  return;
                }

                if (buyer.getInventory().validateWeight(weight)) {
                  if (!buyer.getInventory().validateCapacity((long)slots)) {
                    buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    buyer.sendActionFailed();
                    return;
                  }

                  if (!buyer.reduceAdena(totalCost)) {
                    buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    buyer.sendActionFailed();
                    return;
                  }

                  Iterator var59 = buyList.iterator();

                  while(var59.hasNext()) {
                    TradeItem bi = (TradeItem)var59.next();
                    ItemInstance item = seller.getInventory().removeItemByObjectId(bi.getObjectId(), bi.getCount());
                    Iterator var62 = sellList.iterator();

                    while(true) {
                      if (var62.hasNext()) {
                        TradeItem si = (TradeItem)var62.next();
                        if (si.getObjectId() != bi.getObjectId()) {
                          continue;
                        }

                        si.setCount(si.getCount() - bi.getCount());
                        if (si.getCount() < 1L) {
                          sellList.remove(si);
                        }
                      }

                      Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                      Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                      buyer.getInventory().addItem(item);
                      TradeHelper.purchaseItem(buyer, seller, bi);
                      break;
                    }
                  }

                  count = TradeHelper.getTax(seller, totalCost);
                  if (count > 0L) {
                    totalCost -= count;
                    seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(count));
                  }

                  seller.addAdena(totalCost);
                  seller.saveTradeList();
                  return;
                }

                buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                buyer.sendActionFailed();
              } finally {
                seller.getInventory().writeUnlock();
                buyer.getInventory().writeUnlock();
              }

              return;
            }

            label1689: {
              try {
                if (buyList.size() == this._count && (seller.getPrivateStoreType() != 8 || buyList.size() == sellList.size())) {
                  if (!buyer.getInventory().validateWeight(weight)) {
                    buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    buyer.sendActionFailed();
                    return;
                  }

                  if (!buyer.getInventory().validateCapacity((long)slots)) {
                    buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    buyer.sendActionFailed();
                    return;
                  }

                  if (!buyer.reduceAdena(totalCost)) {
                    buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    buyer.sendActionFailed();
                    return;
                  }

                  Iterator var57 = buyList.iterator();

                  while(var57.hasNext()) {
                    TradeItem bi = (TradeItem)var57.next();
                    ItemInstance item = seller.getInventory().removeItemByObjectId(bi.getObjectId(), bi.getCount());
                    Iterator var61 = sellList.iterator();

                    while(true) {
                      if (var61.hasNext()) {
                        TradeItem si = (TradeItem)var61.next();
                        if (si.getObjectId() != bi.getObjectId()) {
                          continue;
                        }

                        si.setCount(si.getCount() - bi.getCount());
                        if (si.getCount() < 1L) {
                          sellList.remove(si);
                        }
                      }

                      Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                      Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                      buyer.getInventory().addItem(item);
                      TradeHelper.purchaseItem(buyer, seller, bi);
                      break;
                    }
                  }

                  long tax = TradeHelper.getTax(seller, totalCost);
                  if (tax > 0L) {
                    totalCost -= tax;
                    seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(tax));
                  }

                  seller.addAdena(totalCost);
                  seller.saveTradeList();
                  break label1689;
                }

                buyer.sendPacket(Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                buyer.sendActionFailed();
              } finally {
                seller.getInventory().writeUnlock();
                buyer.getInventory().writeUnlock();
              }

              return;
            }

            if (sellList.isEmpty()) {
              TradeHelper.cancelStore(seller);
            }

            seller.sendChanges();
            buyer.sendChanges();
            buyer.sendActionFailed();
          }
        } else {
          buyer.sendPacket(Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
          buyer.sendActionFailed();
        }
      }
    }
  }
}
