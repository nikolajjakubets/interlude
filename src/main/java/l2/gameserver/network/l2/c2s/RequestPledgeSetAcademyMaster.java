//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeReceiveMemberInfo;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestPledgeSetAcademyMaster extends L2GameClientPacket {
  private int _mode;
  private String _sponsorName;
  private String _apprenticeName;

  public RequestPledgeSetAcademyMaster() {
  }

  protected void readImpl() {
    this._mode = this.readD();
    this._sponsorName = this.readS(16);
    this._apprenticeName = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        if ((activeChar.getClanPrivileges() & 256) == 256) {
          UnitMember sponsor = activeChar.getClan().getAnyMember(this._sponsorName);
          UnitMember apprentice = activeChar.getClan().getAnyMember(this._apprenticeName);
          if (sponsor != null && apprentice != null) {
            if (apprentice.getPledgeType() != -1 || sponsor.getPledgeType() == -1) {
              return;
            }

            if (this._mode == 1) {
              if (sponsor.hasApprentice()) {
                activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.MemberAlreadyHasApprentice", activeChar, new Object[0]));
                return;
              }

              if (apprentice.hasSponsor()) {
                activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.ApprenticeAlreadyHasSponsor", activeChar, new Object[0]));
                return;
              }

              sponsor.setApprentice(apprentice.getObjectId());
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(apprentice)});
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage(1755)).addString(sponsor.getName()).addString(apprentice.getName())});
            } else {
              if (!sponsor.hasApprentice()) {
                activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.MemberHasNoApprentice", activeChar, new Object[0]));
                return;
              }

              sponsor.setApprentice(0);
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(apprentice)});
              clan.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage(1763)).addString(sponsor.getName()).addString(apprentice.getName())});
            }

            if (apprentice.isOnline()) {
              apprentice.getPlayer().broadcastCharInfo();
            }

            activeChar.sendPacket(new PledgeReceiveMemberInfo(sponsor));
          }
        } else {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.NoMasterRights", activeChar, new Object[0]));
        }

      }
    }
  }
}
