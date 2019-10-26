//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AskJoinAlliance;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestJoinAlly extends L2GameClientPacket {
  private int _objectId;

  public RequestJoinAlly() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && activeChar.getClan() != null && activeChar.getAlliance() != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isProcessingRequest()) {
        activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY);
      } else if (activeChar.getAlliance().getMembersCount() >= Config.ALT_MAX_ALLY_SIZE) {
        activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
      } else {
        GameObject obj = activeChar.getVisibleObject(this._objectId);
        if (obj != null && obj.isPlayer() && obj != activeChar) {
          Player target = (Player)obj;
          if (!activeChar.isAllyLeader()) {
            activeChar.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
          } else {
            SystemMessage sm;
            if (target.getAlliance() == null && !activeChar.getAlliance().isMember(target.getClan().getClanId())) {
              if (!target.isClanLeader()) {
                activeChar.sendPacket((new SystemMessage(9)).addString(target.getName()));
              } else if (activeChar.isAtWarWith(target.getClanId()) > 0) {
                activeChar.sendPacket(Msg.YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_AT_BATTLE_WITH);
              } else if (!target.getClan().canJoinAlly()) {
                sm = new SystemMessage(761);
                sm.addString(target.getClan().getName());
                activeChar.sendPacket(sm);
              } else if (!activeChar.getAlliance().canInvite()) {
                activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestJoinAlly.InvitePenalty", activeChar, new Object[0]));
              } else if (target.isBusy()) {
                activeChar.sendPacket((new SystemMessage(153)).addString(target.getName()));
              } else {
                (new Request(L2RequestType.ALLY, activeChar, target)).setTimeout(10000L);
                target.sendPacket((new SystemMessage(527)).addString(activeChar.getAlliance().getAllyName()).addName(activeChar));
                target.sendPacket(new AskJoinAlliance(activeChar.getObjectId(), activeChar.getName(), activeChar.getAlliance().getAllyName()));
              }
            } else {
              sm = new SystemMessage(691);
              sm.addString(target.getClan().getName());
              sm.addString(target.getAlliance().getAllyName());
              activeChar.sendPacket(sm);
            }
          }
        } else {
          activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
        }
      }
    }
  }
}
