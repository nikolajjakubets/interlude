//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2.gameserver.network.l2.s2c.ExReplyWritePost;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;

public class RequestExSendPost extends L2GameClientPacket {
  private int _messageType;
  private String _recieverName;
  private String _topic;
  private String _body;
  private int _count;
  private int[] _items;
  private long[] _itemQ;
  private long _price;

  public RequestExSendPost() {
  }

  protected void readImpl() {
    this._recieverName = this.readS(35);
    this._messageType = this.readD();
    this._topic = this.readS(127);
    this._body = this.readS(32767);
    this._count = this.readD();
    if (this._count * 8 + 4 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._items = new int[this._count];
      this._itemQ = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._items[i] = this.readD();
        this._itemQ[i] = (long)this.readD();
        if (this._itemQ[i] < 1L || ArrayUtils.indexOf(this._items, this._items[i]) < i) {
          this._count = 0;
          return;
        }
      }

      this._price = this.readQ();
      if (this._price < 0L) {
        this._count = 0;
        this._price = 0L;
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isGM() && this._recieverName.equalsIgnoreCase("ONLINE_ALL")) {
        Map<Integer, Long> map = new HashMap<>();
        if (this._items != null && this._items.length > 0) {
          for(int i = 0; i < this._items.length; ++i) {
            ItemInstance item = activeChar.getInventory().getItemByObjectId(this._items[i]);
            map.put(item.getItemId(), this._itemQ[i]);
          }
        }

        Iterator var16 = GameObjectsStorage.getAllPlayersForIterate().iterator();

        while(var16.hasNext()) {
          Player p = (Player)var16.next();
          if (p != null && p.isOnline()) {
            Functions.sendSystemMail(p, this._topic, this._body, map);
          }
        }

        activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
        activeChar.sendPacket(Msg.MAIL_SUCCESSFULLY_SENT);
      } else if (!Config.ALLOW_MAIL) {
        activeChar.sendMessage(new CustomMessage("mail.Disabled", activeChar, new Object[0]));
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
      } else if (activeChar.isInTrade()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
      } else if (activeChar.getEnchantScroll() != null) {
        activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
      } else if (activeChar.getName().equalsIgnoreCase(this._recieverName)) {
        activeChar.sendPacket(Msg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
      } else if (this._count > 0 && !activeChar.isInPeaceZone()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else {
        if (this._price > 0L) {
          if (!activeChar.getPlayerAccess().UseTrade) {
            activeChar.sendPacket(Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            activeChar.sendActionFailed();
            return;
          }

          String tradeBan = activeChar.getVar("tradeBan");
          if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
            if (tradeBan.equals("-1")) {
              activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently", activeChar, new Object[0]));
            } else {
              activeChar.sendMessage((new CustomMessage("common.TradeBanned", activeChar, new Object[0])).addString(Util.formatTime((int)(Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
            }

            return;
          }
        }

        if (activeChar.isInBlockList(this._recieverName)) {
          activeChar.sendPacket((new SystemMessage(2057)).addString(this._recieverName));
        } else {
          Player target = World.getPlayer(this._recieverName);
          int recieverId;
          if (target != null) {
            recieverId = target.getObjectId();
            this._recieverName = target.getName();
            if (target.isInBlockList(activeChar)) {
              activeChar.sendPacket((new SystemMessage(1228)).addString(this._recieverName));
              return;
            }
          } else {
            recieverId = CharacterDAO.getInstance().getObjectIdByName(this._recieverName);
            if (recieverId > 0 && mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) > 0) {
              activeChar.sendPacket((new SystemMessage(1228)).addString(this._recieverName));
              return;
            }
          }

          if (recieverId == 0) {
            activeChar.sendPacket(Msg.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
          } else {
            int expireTime = (this._messageType == 1 ? 12 : 360) * 3600 + (int)(System.currentTimeMillis() / 1000L);
            if (this._count > 8) {
              activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            } else {
              long serviceCost = (long)(100 + this._count * 1000);
              List<ItemInstance> attachments = new ArrayList<>();
              activeChar.getInventory().writeLock();

              label393: {
                try {
                  if (activeChar.getAdena() >= serviceCost) {
                    int i;
                    ItemInstance item;
                    if (this._count > 0) {
                      for(i = 0; i < this._count; ++i) {
                        item = activeChar.getInventory().getItemByObjectId(this._items[i]);
                        if (item == null || item.getCount() < this._itemQ[i] || item.getItemId() == 57 && item.getCount() < this._itemQ[i] + serviceCost || !item.canBeTraded(activeChar)) {
                          activeChar.sendPacket(Msg.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
                          return;
                        }
                      }
                    }

                    if (!activeChar.reduceAdena(serviceCost, true)) {
                      activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                      return;
                    }

                    if (this._count <= 0) {
                      break label393;
                    }

                    i = 0;

                    while(true) {
                      if (i >= this._count) {
                        break label393;
                      }

                      item = activeChar.getInventory().removeItemByObjectId(this._items[i], this._itemQ[i]);
                      Log.LogItem(activeChar, ItemLog.PostSend, item);
                      item.setOwnerId(activeChar.getObjectId());
                      item.setLocation(ItemLocation.MAIL);
                      item.save();
                      attachments.add(item);
                      ++i;
                    }
                  }

                  activeChar.sendPacket(Msg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                } finally {
                  activeChar.getInventory().writeUnlock();
                }

                return;
              }

              Mail mail = new Mail();
              mail.setSenderId(activeChar.getObjectId());
              mail.setSenderName(activeChar.getName());
              mail.setReceiverId(recieverId);
              mail.setReceiverName(this._recieverName);
              mail.setTopic(this._topic);
              mail.setBody(this._body);
              mail.setPrice(this._messageType > 0 ? this._price : 0L);
              mail.setUnread(true);
              mail.setType(SenderType.NORMAL);
              mail.setExpireTime(expireTime);
              Iterator var20 = attachments.iterator();

              while(var20.hasNext()) {
                ItemInstance item = (ItemInstance)var20.next();
                mail.addAttachment(item);
              }

              mail.save();
              activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
              activeChar.sendPacket(Msg.MAIL_SUCCESSFULLY_SENT);
              if (target != null) {
                target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
                target.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
              }

            }
          }
        }
      }
    }
  }
}
