//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TradeStart;

public class AnswerTradeRequest extends L2GameClientPacket {
  private int _response;

  public AnswerTradeRequest() {
  }

  protected void readImpl() {
    this._response = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Request request = activeChar.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.TRADE_REQUEST)) {
        if (!request.isInProgress()) {
          request.cancel();
          activeChar.sendActionFailed();
        } else if (activeChar.isOutOfControl()) {
          request.cancel();
          activeChar.sendActionFailed();
        } else {
          Player requestor = request.getRequestor();
          if (requestor == null) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            activeChar.sendActionFailed();
          } else if (requestor.getRequest() != request) {
            request.cancel();
            activeChar.sendActionFailed();
          } else if (this._response == 0) {
            request.cancel();
            requestor.sendPacket((new SystemMessage2(SystemMsg.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE)).addString(activeChar.getName()));
          } else if (!requestor.isInActingRange(activeChar)) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
          } else if (requestor.isActionsDisabled()) {
            request.cancel();
            activeChar.sendPacket((new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK)).addString(requestor.getName()));
            activeChar.sendActionFailed();
          } else {
            try {
              new Request(L2RequestType.TRADE, activeChar, requestor);
              requestor.setTradeList(new CopyOnWriteArrayList());
              requestor.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1)).addString(activeChar.getName()), new TradeStart(requestor, activeChar)});
              activeChar.setTradeList(new CopyOnWriteArrayList());
              activeChar.sendPacket(new IStaticPacket[]{(new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1)).addString(requestor.getName()), new TradeStart(activeChar, requestor)});
            } finally {
              request.done();
            }

          }
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
