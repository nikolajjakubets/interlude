//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.TradeHelper;

public class SetPrivateStoreBuyList extends L2GameClientPacket {
  private int _count;
  private List<SetPrivateStoreBuyList.SetPrivateStoreBuyListEntry> privateBuyItemsList = Collections.emptyList();

  public SetPrivateStoreBuyList() {
  }

  protected void readImpl() {
    this._count = this.readD();
    if (this._count >= 1 && this._count * 16 == this._buf.remaining() && this._count <= 1024) {
      List<SetPrivateStoreBuyList.SetPrivateStoreBuyListEntry> buyItems = new ArrayList(this._count);

      for(int i = 0; i < this._count; ++i) {
        int itemId = this.readD();
        int enchant = this.readH();
        int damage = this.readH();
        long count = (long)this.readD();
        long price = (long)this.readD();
        if (count < 1L || price < 1L) {
          buyItems.clear();
          return;
        }

        buyItems.add(new SetPrivateStoreBuyList.SetPrivateStoreBuyListEntry(itemId, enchant, damage, count, price));
      }

      this.privateBuyItemsList = buyItems;
    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player buyer = ((GameClient)this.getClient()).getActiveChar();
    if (buyer != null && this._count != 0 && this._count == this.privateBuyItemsList.size()) {
      if (!TradeHelper.checksIfCanOpenStore(buyer, 3)) {
        buyer.sendActionFailed();
      } else {
        buyer.getInventory().readLock();
        List<TradeItem> buyList = new CopyOnWriteArrayList();
        long totalCost = 0L;
        int weight = 0;

        label223: {
          try {
            Iterator var6 = this.privateBuyItemsList.iterator();

            while(true) {
              if (!var6.hasNext()) {
                break label223;
              }

              SetPrivateStoreBuyList.SetPrivateStoreBuyListEntry entry = (SetPrivateStoreBuyList.SetPrivateStoreBuyListEntry)var6.next();
              if (entry.getItemId() != 57) {
                ItemTemplate item = ItemHolder.getInstance().getTemplate(entry.getItemId());
                if (item != null) {
                  totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(entry.getCount(), entry.getPrice()));
                  weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck((int)entry.getCount(), item.getWeight()));
                  TradeItem tradeItem = new TradeItem();
                  tradeItem.setItemId(entry.getItemId());
                  tradeItem.setEnchantLevel(entry.getEnchant());
                  tradeItem.setCount(entry.getCount());
                  tradeItem.setOwnersPrice(entry.getPrice());
                  tradeItem.setReferencePrice((long)item.getReferencePrice());
                  if (tradeItem.getStorePrice() > entry.getPrice()) {
                    buyer.sendMessage((new CustomMessage("l2p.gameserver.clientpackets.SetPrivateStoreBuyList.TooLowPrice", buyer, new Object[0])).addItemName(item).addNumber((long)(item.getReferencePrice() / 2)));
                  } else {
                    buyList.add(tradeItem);
                  }
                }
              }
            }
          } catch (ArithmeticException var19) {
            buyList.clear();
            this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
          } finally {
            buyer.getInventory().readUnlock();
          }

          return;
        }

        if (!buyer.getInventory().validateWeight((long)weight)) {
          this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
        } else if (!buyer.getInventory().validateCapacity((long)buyList.size())) {
          this.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
        } else if (buyList.size() <= buyer.getTradeLimit() && totalCost <= 2147483647L && buyList.size() == this._count) {
          if (totalCost > buyer.getAdena()) {
            buyer.sendPacket(Msg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
            buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
          } else {
            if (!buyList.isEmpty()) {
              buyer.getInventory().writeLock();

              try {
                buyer.setBuyList(buyList);
                buyer.setPrivateStoreType(3);
                buyer.saveTradeList();
                buyer.broadcastPacket(new L2GameServerPacket[]{new PrivateStoreMsgBuy(buyer)});
              } finally {
                buyer.getInventory().writeUnlock();
              }
            } else {
              buyer.setBuyList(Collections.emptyList());
              buyer.setPrivateStoreType(0);
            }

            buyer.sendActionFailed();
          }
        } else {
          buyer.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
          buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
        }
      }
    }
  }

  private static class SetPrivateStoreBuyListEntry {
    public static final int IN_PACKET_SIZE = 16;
    private final int itemId;
    private final int enchant;
    private final int damage;
    private final long count;
    private final long price;

    private SetPrivateStoreBuyListEntry(int itemId, int enchant, int damage, long count, long price) {
      this.itemId = itemId;
      this.enchant = enchant;
      this.damage = damage;
      this.count = count;
      this.price = price;
    }

    public int getItemId() {
      return this.itemId;
    }

    public int getEnchant() {
      return this.enchant;
    }

    public int getDamage() {
      return this.damage;
    }

    public long getCount() {
      return this.count;
    }

    public long getPrice() {
      return this.price;
    }
  }
}
