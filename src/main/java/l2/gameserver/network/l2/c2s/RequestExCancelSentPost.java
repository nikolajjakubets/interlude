//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.Set;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowSentPostList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestExCancelSentPost extends L2GameClientPacket {
  private int postId;

  public RequestExCancelSentPost() {
  }

  protected void readImpl() {
    this.postId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
      } else if (activeChar.isInTrade()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
      } else if (activeChar.getEnchantScroll() != null) {
        activeChar.sendPacket(Msg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
      } else if (!activeChar.isInPeaceZone()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else {
        Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), this.postId);
        if (mail != null) {
          if (mail.getAttachments().isEmpty()) {
            activeChar.sendActionFailed();
            return;
          }

          activeChar.getInventory().writeLock();

          try {
            int slots = 0;
            long weight = 0L;
            Iterator var6 = mail.getAttachments().iterator();

            label193:
            while(true) {
              ItemInstance item;
              do {
                if (!var6.hasNext()) {
                  if (!activeChar.getInventory().validateWeight(weight)) {
                    this.sendPacket(Msg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  if (!activeChar.getInventory().validateCapacity((long)slots)) {
                    this.sendPacket(Msg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  Set<ItemInstance> attachments = mail.getAttachments();
                  ItemInstance[] items;
                  synchronized(attachments) {
                    items = (ItemInstance[])mail.getAttachments().toArray(new ItemInstance[attachments.size()]);
                    attachments.clear();
                  }

                  ItemInstance[] var8 = items;
                  int var9 = items.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                    ItemInstance item = var8[var10];
                    activeChar.sendPacket((new SystemMessage(3073)).addItemName(item.getItemId()).addNumber(item.getCount()));
                    Log.LogItem(activeChar, ItemLog.PostCancel, item);
                    activeChar.getInventory().addItem(item);
                  }

                  mail.update();
                  MailDAO.getInstance().deleteSentMailByMailId(activeChar.getObserverMode(), this.postId);
                  MailDAO.getInstance().deleteReceivedMailByMailId(activeChar.getObserverMode(), this.postId);
                  mail.delete();
                  activeChar.sendPacket(Msg.MAIL_SUCCESSFULLY_CANCELLED);
                  break label193;
                }

                item = (ItemInstance)var6.next();
                weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), (long)item.getTemplate().getWeight()));
              } while(item.getTemplate().isStackable() && activeChar.getInventory().getItemByItemId(item.getItemId()) != null);

              ++slots;
            }
          } catch (ArithmeticException var17) {
          } finally {
            activeChar.getInventory().writeUnlock();
          }
        }

        activeChar.sendPacket(new ExShowSentPostList(activeChar));
      }
    }
  }
}
