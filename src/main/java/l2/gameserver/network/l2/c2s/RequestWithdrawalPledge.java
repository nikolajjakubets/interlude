//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDelete;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestWithdrawalPledge extends L2GameClientPacket {
  public RequestWithdrawalPledge() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getClanId() == 0) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInCombat()) {
        activeChar.sendPacket(Msg.ONE_CANNOT_LEAVE_ONES_CLAN_DURING_COMBAT);
      } else {
        Clan clan = activeChar.getClan();
        if (clan != null) {
          UnitMember member = clan.getAnyMember(activeChar.getObjectId());
          if (member == null) {
            activeChar.sendActionFailed();
          } else {
            SubUnit mainUnit = clan.getSubUnit(0);
            if (!member.isClanLeader() && mainUnit.getNextLeaderObjectId() != member.getObjectId()) {
              activeChar.removeEventsByClass(SiegeEvent.class);
              int subUnitType = activeChar.getPledgeType();
              clan.removeClanMember(subUnitType, activeChar.getObjectId());
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage(SystemMsg.S1_HAS_WITHDRAWN_FROM_THE_CLAN)).addString(activeChar.getName()), new PledgeShowMemberListDelete(activeChar.getName())});
              if (subUnitType == -1) {
                activeChar.setLvlJoinedAcademy(0);
              }

              activeChar.setClan((Clan)null);
              if (!activeChar.isNoble()) {
                activeChar.setTitle("");
              }

              activeChar.setLeaveClanCurTime();
              activeChar.broadcastCharInfo();
              activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC});
            } else {
              activeChar.sendPacket(SystemMsg.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            }
          }
        }
      }
    }
  }
}
