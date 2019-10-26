//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2Friend;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestFriendAddReply extends L2GameClientPacket {
  private int _response;

  public RequestFriendAddReply() {
  }

  protected void readImpl() {
    this._response = this._buf.hasRemaining() ? this.readD() : 0;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Request request = activeChar.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.FRIEND)) {
        if (activeChar.isOutOfControl()) {
          request.cancel();
          activeChar.sendActionFailed();
        } else if (!request.isInProgress()) {
          request.cancel();
          activeChar.sendActionFailed();
        } else if (activeChar.isOutOfControl()) {
          request.cancel();
          activeChar.sendActionFailed();
        } else {
          Player requestor = request.getRequestor();
          if (requestor == null) {
            request.cancel();
            activeChar.sendPacket(Msg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            activeChar.sendActionFailed();
          } else if (requestor.getRequest() != request) {
            request.cancel();
            activeChar.sendActionFailed();
          } else if (this._response == 0) {
            request.cancel();
            requestor.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_FRIEND);
            activeChar.sendActionFailed();
          } else {
            requestor.getFriendList().addFriend(activeChar);
            activeChar.getFriendList().addFriend(requestor);
            requestor.sendPacket(new IStaticPacket[]{Msg.YOU_HAVE_SUCCEEDED_IN_INVITING_A_FRIEND, (new SystemMessage(132)).addString(activeChar.getName()), new L2Friend(activeChar, true)});
            activeChar.sendPacket(new IStaticPacket[]{(new SystemMessage(479)).addString(requestor.getName()), new L2Friend(requestor, true)});
          }
        }
      }
    }
  }
}
