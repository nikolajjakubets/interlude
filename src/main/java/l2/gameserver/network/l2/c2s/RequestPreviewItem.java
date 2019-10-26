//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.HashMap;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ShopPreviewInfo;
import l2.gameserver.network.l2.s2c.ShopPreviewList;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestPreviewItem extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestPreviewItem.class);
  private int _unknow;
  private int _listId;
  private int _count;
  private int[] _items;

  public RequestPreviewItem() {
  }

  protected void readImpl() {
    this._unknow = this.readD();
    this._listId = this.readD();
    this._count = this.readD();
    if (this._count * 4 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isInTrade()) {
        activeChar.sendActionFailed();
      } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
        activeChar.sendActionFailed();
      } else {
        NpcInstance merchant = activeChar.getLastNpc();
        boolean isValidMerchant = merchant != null && merchant.isMerchantNpc();
        if (!activeChar.isGM() && (merchant == null || !isValidMerchant || !merchant.isInActingRange(activeChar))) {
          activeChar.sendActionFailed();
        } else {
          NpcTradeList list = BuyListHolder.getInstance().getBuyList(this._listId);
          if (list == null) {
            activeChar.sendActionFailed();
          } else {
            long totalPrice = 0L;
            HashMap itemList = new HashMap();

            try {
              for(int i = 0; i < this._count; ++i) {
                int itemId = this._items[i];
                if (list.getItemByItemId(itemId) == null) {
                  activeChar.sendActionFailed();
                  return;
                }

                ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
                if (template != null && template.isEquipable()) {
                  int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
                  if (paperdoll >= 0) {
                    if (itemList.containsKey(paperdoll)) {
                      activeChar.sendPacket(Msg.THOSE_ITEMS_MAY_NOT_BE_TRIED_ON_SIMULTANEOUSLY);
                      return;
                    }

                    itemList.put(paperdoll, itemId);
                    totalPrice += (long)ShopPreviewList.getWearPrice(template);
                  }
                }
              }

              if (!activeChar.reduceAdena(totalPrice)) {
                activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
              }
            } catch (ArithmeticException var13) {
              this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
              return;
            }

            if (!itemList.isEmpty()) {
              activeChar.sendPacket(new ShopPreviewInfo(itemList));
              ThreadPoolManager.getInstance().schedule(new RequestPreviewItem.RemoveWearItemsTask(activeChar), (long)(Config.WEAR_DELAY * 1000));
            }

          }
        }
      }
    }
  }

  private static class RemoveWearItemsTask extends RunnableImpl {
    private Player _activeChar;

    public RemoveWearItemsTask(Player activeChar) {
      this._activeChar = activeChar;
    }

    public void runImpl() throws Exception {
      this._activeChar.sendPacket(Msg.TRYING_ON_MODE_HAS_ENDED);
      this._activeChar.sendUserInfo(true);
    }
  }
}
