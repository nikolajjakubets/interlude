//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListSell;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgSell;
import l2.gameserver.utils.TradeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SetPrivateStoreSellList extends L2GameClientPacket {
  private int _count;
  private boolean _package;
  private int[] _items;
  private int[] _itemQ;
  private int[] _itemP;

  public SetPrivateStoreSellList() {
  }

  protected void readImpl() {
    this._package = this.readD() == 1;
    this._count = this.readD();
    if (this._count * 12 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new int[this._count];
      this._itemP = new int[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = this.readD();
        this._itemP[i] = this.readD();
        if (this._itemQ[i] < 1 || this._itemP[i] < 0 || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
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
      if (!TradeHelper.checksIfCanOpenStore(seller, this._package ? 8 : 1)) {
        seller.sendActionFailed();
      } else {
        List<TradeItem> sellList = new CopyOnWriteArrayList();
        int totalCost = 0;
        seller.getInventory().writeLock();

        label127: {
          try {
            int i = 0;

            while(true) {
              if (i >= this._count) {
                break label127;
              }

              int objectId = this._items[i];
              int count = this._itemQ[i];
              int price = this._itemP[i];
              ItemInstance item = seller.getInventory().getItemByObjectId(objectId);
              if (item != null && item.getCount() >= (long)count && item.canBeTraded(seller) && item.getItemId() != 57) {
                TradeItem temp = new TradeItem(item);
                temp.setCount((long)count);
                temp.setOwnersPrice((long)price);
                totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
                sellList.add(temp);
              }

              ++i;
            }
          } catch (ArithmeticException e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            sellList.clear();
            this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
          } finally {
            seller.getInventory().writeUnlock();
          }

          return;
        }

        if (sellList.size() <= seller.getTradeLimit() && SafeMath.addAndCheck((long)totalCost, seller.getAdena()) < 2147483647L) {
          if (!sellList.isEmpty()) {
            seller.setSellList(this._package, sellList);
            seller.saveTradeList();
            seller.setPrivateStoreType(this._package ? 8 : 1);
            seller.broadcastPacket(new L2GameServerPacket[]{new PrivateStoreMsgSell(seller)});
          }

          seller.sendActionFailed();
        } else {
          seller.sendPacket(new IStaticPacket[]{Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED, new PrivateStoreManageListSell(seller, this._package)});
        }
      }
    }
  }
}
