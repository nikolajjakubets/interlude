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
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.TradeHelper;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestPrivateStoreBuySellList extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestPrivateStoreBuySellList.class);
  private int _buyerId;
  private int _count;
  private int[] _items;
  private long[] _itemQ;
  private long[] _itemP;

  public RequestPrivateStoreBuySellList() {
  }

  protected void readImpl() {
    this._buyerId = this.readD();
    this._count = this.readD();
    if (this._count * 20 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];
      this._itemP = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this.readD();
        this.readH();
        this.readH();
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
    Player seller = ((GameClient)this.getClient()).getActiveChar();
    if (seller != null && this._count != 0) {
      if (seller.isActionsDisabled()) {
        seller.sendActionFailed();
      } else if (seller.isInStoreMode()) {
        seller.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (seller.isInTrade()) {
        seller.sendActionFailed();
      } else if (seller.isFishing()) {
        seller.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
      } else if (!seller.getPlayerAccess().UseTrade) {
        seller.sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
      } else {
        Player buyer = (Player)seller.getVisibleObject(this._buyerId);
        if (buyer != null && buyer.getPrivateStoreType() == 3 && seller.isInActingRange(buyer)) {
          List<TradeItem> buyList = buyer.getBuyList();
          if (buyList.isEmpty()) {
            seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
            seller.sendActionFailed();
          } else {
            List<TradeItem> sellList = new ArrayList();
            long totalCost = 0L;
            int slots = 0;
            long weight = 0L;
            buyer.getInventory().writeLock();
            seller.getInventory().writeLock();
            boolean var50 = false;

            label1882: {
              try {
                var50 = true;
                ArrayList tradeItems = new ArrayList(buyList);

                label1775:
                for(int i = 0; i < this._count; ++i) {
                  int objectId = this._items[i];
                  long count = this._itemQ[i];
                  long price = this._itemP[i];
                  ItemInstance item = seller.getInventory().getItemByObjectId(objectId);
                  if (item == null) {
                    var50 = false;
                    break label1882;
                  }

                  if (item.getCount() < count) {
                    var50 = false;
                    break label1882;
                  }

                  if (!item.canBeTraded(seller)) {
                    var50 = false;
                    break label1882;
                  }

                  TradeItem si = null;
                  Iterator biIt = tradeItems.iterator();

                  TradeItem bi;
                  do {
                    do {
                      do {
                        do {
                          if (!biIt.hasNext()) {
                            continue label1775;
                          }

                          bi = (TradeItem)biIt.next();
                        } while(bi.getItemId() != item.getItemId());
                      } while(bi.getOwnersPrice() != price);
                    } while(Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() != bi.getEnchantLevel());
                  } while(!Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() < bi.getEnchantLevel());

                  if (count > bi.getCount()) {
                    var50 = false;
                    break label1882;
                  }

                  totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
                  weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, (long)item.getTemplate().getWeight()));
                  if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null) {
                    ++slots;
                  }

                  si = new TradeItem();
                  si.setObjectId(objectId);
                  si.setItemId(item.getItemId());
                  si.setEnchantLevel(item.getEnchantLevel());
                  si.setCount(count);
                  si.setOwnersPrice(price);
                  sellList.add(si);
                  biIt.remove();
                }

                var50 = false;
                break label1882;
              } catch (ArithmeticException var54) {
                sellList.clear();
                this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                var50 = false;
              } finally {
                if (var50) {
                  try {
                    if (sellList.size() != this._count) {
                      seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                      seller.sendActionFailed();
                      return;
                    }

                    if (buyer.getInventory().validateWeight(weight)) {
                      if (!buyer.getInventory().validateCapacity((long)slots)) {
                        buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                        seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                        seller.sendActionFailed();
                        return;
                      }

                      if (!buyer.reduceAdena(totalCost)) {
                        buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                        seller.sendActionFailed();
                        return;
                      }

                      Iterator var25 = sellList.iterator();

                      while(var25.hasNext()) {
                        TradeItem si = (TradeItem)var25.next();
                        ItemInstance item = seller.getInventory().removeItemByObjectId(si.getObjectId(), si.getCount());
                        Iterator var27 = buyList.iterator();

                        label1663: {
                          TradeItem bi;
                          do {
                            do {
                              do {
                                do {
                                  if (!var27.hasNext()) {
                                    break label1663;
                                  }

                                  bi = (TradeItem)var27.next();
                                } while(bi.getItemId() != si.getItemId());
                              } while(bi.getOwnersPrice() != si.getOwnersPrice());
                            } while(Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() != bi.getEnchantLevel());
                          } while(!Config.PRIVATE_BUY_MATCH_ENCHANT && item.getEnchantLevel() < bi.getEnchantLevel());

                          bi.setCount(bi.getCount() - si.getCount());
                          if (bi.getCount() < 1L) {
                            buyList.remove(bi);
                          }
                        }

                        Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                        Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                        buyer.getInventory().addItem(item);
                        TradeHelper.purchaseItem(buyer, seller, si);
                      }

                      long tax = TradeHelper.getTax(seller, totalCost);
                      if (tax > 0L) {
                        totalCost -= tax;
                        seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(tax));
                      }

                      seller.addAdena(totalCost);
                      buyer.saveTradeList();
                    }

                    buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                  } finally {
                    seller.getInventory().writeUnlock();
                    buyer.getInventory().writeUnlock();
                  }

                  return;
                }
              }

              try {
                if (sellList.size() == this._count) {
                  if (!buyer.getInventory().validateWeight(weight)) {
                    buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                  }

                  if (!buyer.getInventory().validateCapacity((long)slots)) {
                    buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                    seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                  }

                  if (!buyer.reduceAdena(totalCost)) {
                    buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                  }

                  Iterator var59 = sellList.iterator();

                  while(var59.hasNext()) {
                    TradeItem si = (TradeItem)var59.next();
                    ItemInstance item = seller.getInventory().removeItemByObjectId(si.getObjectId(), si.getCount());
                    Iterator var14 = buyList.iterator();

                    while(var14.hasNext()) {
                      TradeItem bi = (TradeItem)var14.next();
                      if (bi.getItemId() == si.getItemId() && bi.getOwnersPrice() == si.getOwnersPrice() && (!Config.PRIVATE_BUY_MATCH_ENCHANT || item.getEnchantLevel() == bi.getEnchantLevel()) && (Config.PRIVATE_BUY_MATCH_ENCHANT || item.getEnchantLevel() >= bi.getEnchantLevel())) {
                        bi.setCount(bi.getCount() - si.getCount());
                        if (bi.getCount() < 1L) {
                          buyList.remove(bi);
                        }
                        break;
                      }
                    }

                    Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                    Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                    buyer.getInventory().addItem(item);
                    TradeHelper.purchaseItem(buyer, seller, si);
                  }

                  long tax = TradeHelper.getTax(seller, totalCost);
                  if (tax > 0L) {
                    totalCost -= tax;
                    seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(tax));
                  }

                  seller.addAdena(totalCost);
                  buyer.saveTradeList();
                  return;
                }

                seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                seller.sendActionFailed();
              } finally {
                seller.getInventory().writeUnlock();
                buyer.getInventory().writeUnlock();
              }

              return;
            }

            try {
              if (sellList.size() != this._count) {
                seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                seller.sendActionFailed();
                return;
              }

              if (!buyer.getInventory().validateWeight(weight)) {
                buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                seller.sendActionFailed();
                return;
              }

              if (!buyer.getInventory().validateCapacity((long)slots)) {
                buyer.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                seller.sendActionFailed();
                return;
              }

              if (!buyer.reduceAdena(totalCost)) {
                buyer.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                seller.sendActionFailed();
                return;
              }

              Iterator var58 = sellList.iterator();

              while(true) {
                if (!var58.hasNext()) {
                  long tax = TradeHelper.getTax(seller, totalCost);
                  if (tax > 0L) {
                    totalCost -= tax;
                    seller.sendMessage((new CustomMessage("trade.HavePaidTax", seller, new Object[0])).addNumber(tax));
                  }

                  seller.addAdena(totalCost);
                  buyer.saveTradeList();
                  break;
                }

                TradeItem si = (TradeItem)var58.next();
                ItemInstance item = seller.getInventory().removeItemByObjectId(si.getObjectId(), si.getCount());
                Iterator var64 = buyList.iterator();

                while(var64.hasNext()) {
                  TradeItem bi = (TradeItem)var64.next();
                  if (bi.getItemId() == si.getItemId() && bi.getOwnersPrice() == si.getOwnersPrice() && (!Config.PRIVATE_BUY_MATCH_ENCHANT || item.getEnchantLevel() == bi.getEnchantLevel()) && (Config.PRIVATE_BUY_MATCH_ENCHANT || item.getEnchantLevel() >= bi.getEnchantLevel())) {
                    bi.setCount(bi.getCount() - si.getCount());
                    if (bi.getCount() < 1L) {
                      buyList.remove(bi);
                    }
                    break;
                  }
                }

                Log.LogItem(seller, ItemLog.PrivateStoreSell, item);
                Log.LogItem(buyer, ItemLog.PrivateStoreBuy, item);
                buyer.getInventory().addItem(item);
                TradeHelper.purchaseItem(buyer, seller, si);
              }
            } finally {
              seller.getInventory().writeUnlock();
              buyer.getInventory().writeUnlock();
            }

            if (buyList.isEmpty()) {
              TradeHelper.cancelStore(buyer);
            }

            seller.sendChanges();
            buyer.sendChanges();
            seller.sendActionFailed();
          }
        } else {
          seller.sendPacket(Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
          seller.sendActionFailed();
        }
      }
    }
  }
}
