//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestWithdrawAlly extends L2GameClientPacket {
  public RequestWithdrawAlly() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan == null) {
        activeChar.sendActionFailed();
      } else if (!activeChar.isClanLeader()) {
        activeChar.sendPacket(Msg.ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE);
      } else if (clan.getAlliance() == null) {
        activeChar.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
      } else if (clan.equals(clan.getAlliance().getLeader())) {
        activeChar.sendPacket(Msg.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
      } else {
        clan.broadcastToOnlineMembers(new L2GameServerPacket[]{Msg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE, Msg.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION});
        Alliance alliance = clan.getAlliance();
        clan.setAllyId(0);
        clan.setLeavedAlly();
        alliance.broadcastAllyStatus();
        alliance.removeAllyMember(clan.getClanId());
      }
    }
  }
}
