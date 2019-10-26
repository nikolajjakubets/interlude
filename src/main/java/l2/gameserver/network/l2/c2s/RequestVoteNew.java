//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestVoteNew extends L2GameClientPacket {
  private int _targetObjectId;

  public RequestVoteNew() {
  }

  protected void readImpl() {
    this._targetObjectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getTarget() == null) {
        activeChar.sendPacket(Msg.SELECT_TARGET);
      } else if (activeChar.getTarget().isPlayer() && activeChar.getTarget().getObjectId() == this._targetObjectId) {
        Player target = activeChar.getTarget().getPlayer();
        if (target == activeChar) {
          activeChar.sendPacket(Msg.SELECT_TARGET);
        } else if (activeChar.getLevel() < 10) {
          activeChar.sendPacket(Msg.ONLY_LEVEL_SUP_10_CAN_RECOMMEND);
        } else if (activeChar.getGivableRec() <= 0) {
          activeChar.sendPacket(Msg.NO_MORE_RECOMMENDATIONS_TO_HAVE);
        } else if (activeChar.isRecommended(target)) {
          activeChar.sendPacket(Msg.THAT_CHARACTER_HAS_ALREADY_BEEN_RECOMMENDED);
        } else if (target.getReceivedRec() >= 255) {
          activeChar.sendPacket(Msg.YOU_NO_LONGER_RECIVE_A_RECOMMENDATION);
        } else {
          activeChar.giveRecommendation(target);
          activeChar.sendPacket((new SystemMessage(830)).addName(target).addNumber(activeChar.getGivableRec()));
          target.sendPacket((new SystemMessage(831)).addName(activeChar));
          activeChar.sendUserInfo(false);
          target.broadcastUserInfo(true);
        }
      } else {
        activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
      }
    }
  }
}
