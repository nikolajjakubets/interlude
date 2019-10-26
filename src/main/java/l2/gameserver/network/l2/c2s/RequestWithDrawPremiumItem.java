//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.PremiumItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExGetPremiumItemList;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public final class RequestWithDrawPremiumItem extends L2GameClientPacket {
  private int _itemNum;
  private int _charId;
  private long _itemcount;

  public RequestWithDrawPremiumItem() {
  }

  protected void readImpl() {
    this._itemNum = this.readD();
    this._charId = this.readD();
    this._itemcount = (long)this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._itemcount > 0L) {
        if (activeChar.getObjectId() == this._charId) {
          if (!activeChar.getPremiumItemList().isEmpty()) {
            if (activeChar.getWeightPenalty() < 3 && (double)activeChar.getInventoryLimit() * 0.8D > (double)activeChar.getInventory().getSize()) {
              if (activeChar.isProcessingRequest()) {
                activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
              } else {
                PremiumItem _item = (PremiumItem)activeChar.getPremiumItemList().get(this._itemNum);
                if (_item != null) {
                  boolean stackable = ItemHolder.getInstance().getTemplate(_item.getItemId()).isStackable();
                  if (_item.getCount() >= this._itemcount) {
                    if (!stackable) {
                      for(int i = 0; (long)i < this._itemcount; ++i) {
                        this.addItem(activeChar, _item.getItemId(), 1L);
                      }
                    } else {
                      this.addItem(activeChar, _item.getItemId(), this._itemcount);
                    }

                    if (this._itemcount < _item.getCount()) {
                      ((PremiumItem)activeChar.getPremiumItemList().get(this._itemNum)).updateCount(_item.getCount() - this._itemcount);
                      activeChar.updatePremiumItem(this._itemNum, _item.getCount() - this._itemcount);
                    } else {
                      activeChar.getPremiumItemList().remove(this._itemNum);
                      activeChar.deletePremiumItem(this._itemNum);
                    }

                    if (activeChar.getPremiumItemList().isEmpty()) {
                      activeChar.sendPacket(Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
                    } else {
                      activeChar.sendPacket(new ExGetPremiumItemList(activeChar));
                    }

                  }
                }
              }
            } else {
              activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
            }
          }
        }
      }
    }
  }

  private void addItem(Player player, int itemId, long count) {
    player.getInventory().addItem(itemId, count);
    player.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
  }
}
