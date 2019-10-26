//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestPledgeReorganizeMember extends L2GameClientPacket {
  int _replace;
  String _subjectName;
  int _targetUnit;
  String _replaceName;

  public RequestPledgeReorganizeMember() {
  }

  protected void readImpl() {
    this._replace = this.readD();
    this._subjectName = this.readS(16);
    this._targetUnit = this.readD();
    if (this._replace > 0) {
      this._replaceName = this.readS();
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan == null) {
        activeChar.sendActionFailed();
      } else if (!activeChar.isClanLeader()) {
        activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.ChangeAffiliations", activeChar, new Object[0]));
        activeChar.sendActionFailed();
      } else {
        UnitMember subject = clan.getAnyMember(this._subjectName);
        if (subject == null) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.NotInYourClan", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else if (subject.getPledgeType() == this._targetUnit) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.AlreadyInThatCombatUnit", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else if (this._targetUnit != 0 && clan.getSubUnit(this._targetUnit) == null) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.NoSuchCombatUnit", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else if (Clan.isAcademy(this._targetUnit)) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.AcademyViaInvitation", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else if (Clan.isAcademy(subject.getPledgeType())) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CantMoveAcademyMember", activeChar, new Object[0]));
          activeChar.sendActionFailed();
        } else {
          UnitMember replacement = null;
          if (this._replace > 0) {
            replacement = clan.getAnyMember(this._replaceName);
            if (replacement == null) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongClan", activeChar, new Object[0]));
              activeChar.sendActionFailed();
              return;
            }

            if (replacement.getPledgeType() != this._targetUnit) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongCombatUnit", activeChar, new Object[0]));
              activeChar.sendActionFailed();
              return;
            }

            if (replacement.isSubLeader() != 0) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterLeaderAnotherCombatUnit", activeChar, new Object[0]));
              activeChar.sendActionFailed();
              return;
            }
          } else {
            if (clan.getUnitMembersSize(this._targetUnit) >= clan.getSubPledgeLimit(this._targetUnit)) {
              if (this._targetUnit == 0) {
                activeChar.sendPacket((new SystemMessage(1835)).addString(clan.getName()));
              } else {
                activeChar.sendPacket(Msg.THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
              }

              activeChar.sendActionFailed();
              return;
            }

            if (subject.isSubLeader() != 0) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.MemberLeaderAnotherUnit", activeChar, new Object[0]));
              activeChar.sendActionFailed();
              return;
            }
          }

          SubUnit oldUnit = null;
          if (replacement != null) {
            oldUnit = replacement.getSubUnit();
            oldUnit.replace(replacement.getObjectId(), subject.getPledgeType());
            clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(replacement)});
            if (replacement.isOnline()) {
              replacement.getPlayer().updatePledgeClass();
              replacement.getPlayer().broadcastCharInfo();
            }
          }

          oldUnit = subject.getSubUnit();
          oldUnit.replace(subject.getObjectId(), this._targetUnit);
          clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new PledgeShowMemberListUpdate(subject)});
          if (subject.isOnline()) {
            subject.getPlayer().updatePledgeClass();
            subject.getPlayer().broadcastCharInfo();
          }

        }
      }
    }
  }
}
