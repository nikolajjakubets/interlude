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

public class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket {
  private int _powerGrade;
  private String _name;

  public RequestPledgeSetMemberPowerGrade() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
    this._powerGrade = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._powerGrade >= 1 && this._powerGrade <= 9) {
        Clan clan = activeChar.getClan();
        if (clan != null) {
          if ((activeChar.getClanPrivileges() & 16) == 16) {
            UnitMember member = activeChar.getClan().getAnyMember(this._name);
            if (member != null) {
              if (Clan.isAcademy(member.getPledgeType())) {
                activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.CantChangeAcademyGrade", activeChar, new Object[0]));
                return;
              }

              if (this._powerGrade > 5) {
                member.setPowerGrade(clan.getAffiliationRank(member.getPledgeType()));
              } else {
                member.setPowerGrade(this._powerGrade);
              }

              if (member.isOnline()) {
                member.getPlayer().sendUserInfo();
              }
            } else {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.NotBelongClan", activeChar, new Object[0]));
            }
          } else {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.HaveNotAuthority", activeChar, new Object[0]));
          }

        }
      }
    }
  }
}
