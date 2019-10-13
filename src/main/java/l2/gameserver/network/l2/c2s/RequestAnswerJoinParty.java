//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.JoinParty;

public class RequestAnswerJoinParty extends L2GameClientPacket {
  private int _response;

  public RequestAnswerJoinParty() {
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
      if (request != null && request.isTypeOf(L2RequestType.PARTY)) {
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
          } else if (this._response <= 0) {
            request.cancel();
            requestor.sendPacket(JoinParty.FAIL);
          } else if (activeChar.isOlyParticipant()) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
            requestor.sendPacket(JoinParty.FAIL);
          } else if (requestor.isOlyParticipant()) {
            request.cancel();
            requestor.sendPacket(JoinParty.FAIL);
          } else {
            Party party = requestor.getParty();
            if (party != null && party.getMemberCount() >= Config.ALT_MAX_PARTY_SIZE) {
              request.cancel();
              activeChar.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
              requestor.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
              requestor.sendPacket(JoinParty.FAIL);
            } else {
              IStaticPacket problem = activeChar.canJoinParty(requestor);
              if (problem != null) {
                request.cancel();
                activeChar.sendPacket(new IStaticPacket[]{problem, ActionFail.STATIC});
                requestor.sendPacket(JoinParty.FAIL);
              } else {
                if (party == null) {
                  int itemDistribution = request.getInteger("itemDistribution");
                  requestor.setParty(party = new Party(requestor, itemDistribution));
                }

                try {
                  activeChar.joinParty(party);
                  requestor.sendPacket(JoinParty.SUCCESS);
                } finally {
                  request.done();
                }

              }
            }
          }
        }
      }
    }
  }
}
