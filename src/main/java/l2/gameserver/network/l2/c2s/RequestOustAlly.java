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
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;

public class RequestOustAlly extends L2GameClientPacket {
  private String _clanName;

  public RequestOustAlly() {
  }

  protected void readImpl() {
    this._clanName = this.readS(32);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan leaderClan = activeChar.getClan();
      if (leaderClan == null) {
        activeChar.sendActionFailed();
      } else {
        Alliance alliance = leaderClan.getAlliance();
        if (alliance == null) {
          activeChar.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
        } else if (!activeChar.isAllyLeader()) {
          activeChar.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
        } else if (this._clanName != null) {
          Clan clan = ClanTable.getInstance().getClanByName(this._clanName);
          if (clan != null) {
            if (!alliance.isMember(clan.getClanId())) {
              activeChar.sendActionFailed();
              return;
            }

            if (alliance.getLeader().equals(clan)) {
              activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
              return;
            }

            clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new SystemMessage("Your clan has been expelled from " + alliance.getAllyName() + " alliance."), new SystemMessage(468)});
            clan.setAllyId(0);
            clan.setLeavedAlly();
            alliance.broadcastAllyStatus();
            alliance.removeAllyMember(clan.getClanId());
            alliance.setExpelledMember();
            activeChar.sendMessage((new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.ClanDismissed", activeChar, new Object[0])).addString(clan.getName()).addString(alliance.getAllyName()));
          }

        }
      }
    }
  }
}
