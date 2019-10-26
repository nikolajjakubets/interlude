//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SendTradeRequest;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Util;

public class TradeRequest extends L2GameClientPacket {
  private int _objectId;

  public TradeRequest() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (!activeChar.getPlayerAccess().UseTrade) {
        activeChar.sendPacket(Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else if (activeChar.isInTrade()) {
        activeChar.sendPacket(Msg.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
      } else if (activeChar.isProcessingRequest()) {
        activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
      } else {
        String tradeBan = activeChar.getVar("tradeBan");
        if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
          if (tradeBan.equals("-1")) {
            activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently", activeChar, new Object[0]));
          } else {
            activeChar.sendMessage((new CustomMessage("common.TradeBanned", activeChar, new Object[0])).addString(Util.formatTime((int)(Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
          }

        } else {
          GameObject target = activeChar.getVisibleObject(this._objectId);
          if (target != null && target.isPlayer() && target != activeChar) {
            if (!target.isInActingRange(activeChar)) {
              activeChar.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            } else {
              Player reciever = (Player)target;
              if (!reciever.getPlayerAccess().UseTrade) {
                activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
              } else {
                tradeBan = reciever.getVar("tradeBan");
                if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
                  activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                } else if (reciever.isInBlockList(activeChar)) {
                  activeChar.sendPacket(Msg.YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED);
                } else if (!reciever.getTradeRefusal() && !reciever.isBusy()) {
                  (new Request(L2RequestType.TRADE_REQUEST, activeChar, reciever)).setTimeout(10000L);
                  reciever.sendPacket(new SendTradeRequest(activeChar.getObjectId()));
                  activeChar.sendPacket((new SystemMessage(118)).addString(reciever.getName()));
                } else {
                  activeChar.sendPacket((new SystemMessage(153)).addString(reciever.getName()));
                }
              }
            }
          } else {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
          }
        }
      }
    }
  }
}
