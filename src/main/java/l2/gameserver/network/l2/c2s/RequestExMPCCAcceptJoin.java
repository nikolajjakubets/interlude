//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestExMPCCAcceptJoin extends L2GameClientPacket {
  private int _response;
  private int _unk;

  public RequestExMPCCAcceptJoin() {
  }

  protected void readImpl() {
    this._response = this._buf.hasRemaining() ? this.readD() : 0;
    this._unk = this._buf.hasRemaining() ? this.readD() : 0;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Request request = activeChar.getRequest();
      if (request != null && request.isTypeOf(L2RequestType.CHANNEL)) {
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
          } else if (this._response == 0) {
            request.cancel();
            requestor.sendPacket((new SystemMessage(1680)).addString(activeChar.getName()));
          } else if (requestor.isInParty() && activeChar.isInParty() && !activeChar.getParty().isInCommandChannel()) {
            if (activeChar.isTeleporting()) {
              request.cancel();
              activeChar.sendPacket(Msg.YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING);
              requestor.sendPacket(Msg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
            } else {
              try {
                if (requestor.getParty().isInCommandChannel()) {
                  requestor.getParty().getCommandChannel().addParty(activeChar.getParty());
                  return;
                }

                if (!CommandChannel.checkAuthority(requestor)) {
                  return;
                }

                boolean haveSkill = requestor.getSkillLevel(391) > 0;
                boolean haveItem = false;
                if (!haveSkill && (haveItem = requestor.getInventory().destroyItemByItemId(8871, 1L))) {
                  requestor.sendPacket(SystemMessage2.removeItems(8871, 1L));
                }

                if (haveSkill || haveItem) {
                  CommandChannel channel = new CommandChannel(requestor);
                  requestor.sendPacket(Msg.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
                  channel.addParty(activeChar.getParty());
                  return;
                }
              } finally {
                request.done();
              }

            }
          } else {
            request.cancel();
            requestor.sendPacket(Msg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
          }
        }
      }
    }
  }
}
