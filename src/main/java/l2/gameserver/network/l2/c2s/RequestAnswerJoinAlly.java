//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.network.l2.GameClient;

public class RequestAnswerJoinAlly extends L2GameClientPacket {
  private int _response;

  public RequestAnswerJoinAlly() {
  }

  protected void readImpl() {
    this._response = this._buf.remaining() >= 4 ? this.readD() : 0;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Request request = activeChar.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.ALLY)) {
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
            activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_ONLINE);
            activeChar.sendActionFailed();
          } else if (requestor.getRequest() != request) {
            request.cancel();
            activeChar.sendActionFailed();
          } else if (requestor.getAlliance() == null) {
            request.cancel();
            activeChar.sendActionFailed();
          } else if (this._response == 0) {
            request.cancel();
            requestor.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
          } else {
            try {
              Alliance ally = requestor.getAlliance();
              activeChar.sendPacket(Msg.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
              activeChar.getClan().setAllyId(requestor.getAllyId());
              activeChar.getClan().updateClanInDB();
              ally.addAllyMember(activeChar.getClan(), true);
              ally.broadcastAllyStatus();
            } finally {
              request.done();
            }

          }
        }
      }
    }
  }
}
