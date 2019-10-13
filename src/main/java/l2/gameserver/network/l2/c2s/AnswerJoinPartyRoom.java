//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;

public class AnswerJoinPartyRoom extends L2GameClientPacket {
  private int _response;

  public AnswerJoinPartyRoom() {
  }

  protected void readImpl() {
    if (this._buf.hasRemaining()) {
      this._response = this.readD();
    } else {
      this._response = 0;
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Request request = activeChar.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.PARTY_ROOM)) {
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
            requestor.sendPacket(SystemMsg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);
          } else if (activeChar.getMatchingRoom() != null) {
            request.cancel();
            activeChar.sendActionFailed();
          } else {
            try {
              MatchingRoom room = requestor.getMatchingRoom();
              if (room == null || room.getType() != MatchingRoom.PARTY_MATCHING) {
                return;
              }

              room.addMember(activeChar);
            } finally {
              request.done();
            }

          }
        }
      }
    }
  }
}
