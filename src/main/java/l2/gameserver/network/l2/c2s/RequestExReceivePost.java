//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Set;
import l2.commons.dao.JdbcEntityState;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestExReceivePost extends L2GameClientPacket {
  private int postId;

  public RequestExReceivePost() {
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
        activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
      } else if (activeChar.isInTrade()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else if (activeChar.getEnchantScroll() != null) {
        activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
      } else {
        Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), this.postId);
        if (mail != null) {
          activeChar.getInventory().writeLock();

          try {
            Set<ItemInstance> attachments = mail.getAttachments();
            if (attachments.size() > 0 && !activeChar.isInPeaceZone()) {
              activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
              return;
            }

            ItemInstance[] items;
            int slots;
            synchronized(attachments) {
              if (mail.getAttachments().isEmpty()) {
                return;
              }

              items = (ItemInstance[])mail.getAttachments().toArray(new ItemInstance[attachments.size()]);
              slots = 0;
              long weight = 0L;
              ItemInstance[] var9 = items;
              int expireTime = items.length;
              int var11 = 0;

              while(true) {
                ItemInstance item;
                if (var11 >= expireTime) {
                  if (!activeChar.getInventory().validateWeight(weight)) {
                    this.sendPacket(Msg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  if (!activeChar.getInventory().validateCapacity((long)slots)) {
                    this.sendPacket(Msg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                  }

                  if (mail.getPrice() > 0L) {
                    if (!activeChar.reduceAdena(mail.getPrice(), true)) {
                      activeChar.sendPacket(Msg.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                      return;
                    }

                    Player sender = World.getPlayer(mail.getSenderId());
                    if (sender != null) {
                      sender.addAdena(mail.getPrice(), true);
                      sender.sendPacket((new SystemMessage(3072)).addName(activeChar));
                    } else {
                      expireTime = 1296000 + (int)(System.currentTimeMillis() / 1000L);
                      Mail reply = mail.reply();
                      reply.setExpireTime(expireTime);
                      item = ItemFunctions.createItem(57);
                      item.setOwnerId(reply.getReceiverId());
                      item.setCount(mail.getPrice());
                      item.setLocation(ItemLocation.MAIL);
                      item.save();
                      Log.LogItem(activeChar, ItemLog.PostSend, item);
                      reply.addAttachment(item);
                      reply.save();
                    }
                  }

                  attachments.clear();
                  break;
                }

                item = var9[var11];
                weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), (long)item.getTemplate().getWeight()));
                if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
                  ++slots;
                }

                ++var11;
              }
            }

            mail.setJdbcState(JdbcEntityState.UPDATED);
            mail.update();
            ItemInstance[] var5 = items;
            slots = items.length;

            for(int var21 = 0; var21 < slots; ++var21) {
              ItemInstance item = var5[var21];
              activeChar.sendPacket((new SystemMessage(3073)).addItemName(item.getItemId()).addNumber(item.getCount()));
              Log.LogItem(activeChar, ItemLog.PostRecieve, item);
              activeChar.getInventory().addItem(item);
            }

            activeChar.sendPacket(Msg.MAIL_SUCCESSFULLY_RECEIVED);
          } catch (ArithmeticException var19) {
          } finally {
            activeChar.getInventory().writeUnlock();
          }
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
      }
    }
  }
}
