//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExAskJoinMPCC;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestExMPCCAskJoin extends L2GameClientPacket {
  private String _name;

  public RequestExMPCCAskJoin() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isProcessingRequest()) {
        activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
      } else if (!activeChar.isInParty()) {
        activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
      } else {
        Player target = World.getPlayer(this._name);
        if (target == null) {
          activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
        } else if (activeChar != target && target.isInParty() && activeChar.getParty() != target.getParty()) {
          if (target.isInParty() && !target.getParty().isLeader(target)) {
            target = target.getParty().getPartyLeader();
          }

          if (target == null) {
            activeChar.sendPacket(Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
          } else if (target.getParty().isInCommandChannel()) {
            activeChar.sendPacket((new SystemMessage(1594)).addString(target.getName()));
          } else if (target.isBusy()) {
            activeChar.sendPacket((new SystemMessage(153)).addString(target.getName()));
          } else {
            Party activeParty = activeChar.getParty();
            if (activeParty.isInCommandChannel()) {
              if (activeParty.getCommandChannel().getChannelLeader() != activeChar) {
                activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
                return;
              }

              this.sendInvite(activeChar, target);
            } else if (CommandChannel.checkAuthority(activeChar)) {
              this.sendInvite(activeChar, target);
            }

          }
        } else {
          activeChar.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
        }
      }
    }
  }

  private void sendInvite(Player requestor, Player target) {
    (new Request(L2RequestType.CHANNEL, requestor, target)).setTimeout(10000L);
    target.sendPacket(new ExAskJoinMPCC(requestor.getName()));
    requestor.sendMessage((new CustomMessage("l2p.gameserver.clientpackets.RequestExMPCCAskJoin.InviteToCommandChannel", requestor, new Object[0])).addString(target.getName()));
  }
}
