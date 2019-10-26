//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAll extends L2GameServerPacket {
  private int _clanObjectId;
  private int _clanCrestId;
  private int _level;
  private int _rank;
  private int _reputation;
  private int _allianceObjectId;
  private int _allianceCrestId;
  private int _hasCastle;
  private int _hasClanHall;
  private int _atClanWar;
  private String _unitName;
  private String _leaderName;
  private String _allianceName;
  private int _pledgeType;
  private List<PledgeShowMemberListAll.PledgePacketMember> _members;
  private boolean _isDisbanded;

  public PledgeShowMemberListAll(Clan clan, SubUnit sub) {
    this._pledgeType = sub.getType();
    this._clanObjectId = clan.getClanId();
    this._unitName = sub.getName();
    this._leaderName = sub.getLeaderName();
    this._clanCrestId = clan.getCrestId();
    this._level = clan.getLevel();
    this._hasCastle = clan.getCastle();
    this._hasClanHall = clan.getHasHideout();
    this._rank = clan.getRank();
    this._reputation = clan.getReputationScore();
    this._atClanWar = clan.isAtWarOrUnderAttack();
    this._isDisbanded = clan.isPlacedForDisband();
    Alliance ally = clan.getAlliance();
    if (ally != null) {
      this._allianceObjectId = ally.getAllyId();
      this._allianceName = ally.getAllyName();
      this._allianceCrestId = ally.getAllyCrestId();
    }

    this._members = new ArrayList(sub.size());
    Iterator var4 = sub.getUnitMembers().iterator();

    while(var4.hasNext()) {
      UnitMember m = (UnitMember)var4.next();
      this._members.add(new PledgeShowMemberListAll.PledgePacketMember(m));
    }

  }

  protected final void writeImpl() {
    this.writeC(83);
    this.writeD(this._pledgeType == 0 ? 0 : 1);
    this.writeD(this._clanObjectId);
    this.writeD(this._pledgeType);
    this.writeS(this._unitName);
    this.writeS(this._leaderName);
    this.writeD(this._clanCrestId);
    this.writeD(this._level);
    this.writeD(this._hasCastle);
    this.writeD(this._hasClanHall);
    this.writeD(this._rank);
    this.writeD(this._reputation);
    this.writeD(this._isDisbanded ? 3 : 0);
    this.writeD(0);
    this.writeD(this._allianceObjectId);
    this.writeS(this._allianceName);
    this.writeD(this._allianceCrestId);
    this.writeD(this._atClanWar);
    this.writeD(this._members.size());
    Iterator var1 = this._members.iterator();

    while(var1.hasNext()) {
      PledgeShowMemberListAll.PledgePacketMember m = (PledgeShowMemberListAll.PledgePacketMember)var1.next();
      this.writeS(m._name);
      this.writeD(m._level);
      this.writeD(m._classId);
      this.writeD(m._sex);
      this.writeD(m._race);
      this.writeD(m._online);
      this.writeD(m._hasSponsor ? 1 : 0);
    }

  }

  private class PledgePacketMember {
    private String _name;
    private int _level;
    private int _classId;
    private int _sex;
    private int _race;
    private int _online;
    private boolean _hasSponsor;

    public PledgePacketMember(UnitMember m) {
      this._name = m.getName();
      this._level = m.getLevel();
      this._classId = m.getClassId();
      this._sex = m.getSex();
      this._race = 0;
      this._online = m.isOnline() ? m.getObjectId() : 0;
      this._hasSponsor = m.getSponsor() != 0;
    }
  }
}
