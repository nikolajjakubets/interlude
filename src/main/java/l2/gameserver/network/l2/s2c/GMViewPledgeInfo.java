//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;

public class GMViewPledgeInfo extends L2GameServerPacket {
  private String char_name;
  private String clan_name;
  private String leader_name;
  private String ally_name;
  private int clan_id;
  private int clan_crest_id;
  private int clan_level;
  private int rank;
  private int rep;
  private int ally_id;
  private int ally_crest_id;
  private int hasCastle;
  private int hasHideout;
  private int atWar;
  private List<GMViewPledgeInfo.PledgeMemberInfo> infos = new ArrayList<>();

  public GMViewPledgeInfo(Player activeChar) {
    Clan clan = activeChar.getClan();
    Iterator var3 = clan.iterator();

    while(var3.hasNext()) {
      UnitMember member = (UnitMember)var3.next();
      if (member != null) {
        this.char_name = member.getName();
        this.clan_level = member.getLevel();
        this.clan_id = member.getClassId();
        this.clan_crest_id = member.isOnline() ? member.getObjectId() : 0;
        this.rep = member.getSponsor() != 0 ? 1 : 0;
        this.infos.add(new GMViewPledgeInfo.PledgeMemberInfo(this.char_name, this.clan_level, this.clan_id, this.clan_crest_id, member.getSex(), 1, this.rep));
      }
    }

    this.char_name = activeChar.getName();
    this.clan_id = clan.getClanId();
    this.clan_name = clan.getName();
    this.leader_name = clan.getLeaderName();
    this.clan_crest_id = clan.getCrestId();
    this.clan_level = clan.getLevel();
    this.hasCastle = clan.getCastle();
    this.hasHideout = clan.getHasHideout();
    this.rank = clan.getRank();
    this.rep = clan.getReputationScore();
    this.ally_id = clan.getAllyId();
    if (clan.getAlliance() != null) {
      this.ally_name = clan.getAlliance().getAllyName();
      this.ally_crest_id = clan.getAlliance().getAllyCrestId();
    } else {
      this.ally_name = "";
      this.ally_crest_id = 0;
    }

    this.atWar = clan.isAtWar();
  }

  protected final void writeImpl() {
    this.writeC(144);
    this.writeS(this.char_name);
    this.writeD(this.clan_id);
    this.writeD(0);
    this.writeS(this.clan_name);
    this.writeS(this.leader_name);
    this.writeD(this.clan_crest_id);
    this.writeD(this.clan_level);
    this.writeD(this.hasCastle);
    this.writeD(this.hasHideout);
    this.writeD(this.rank);
    this.writeD(this.rep);
    this.writeD(0);
    this.writeD(0);
    this.writeD(this.ally_id);
    this.writeS(this.ally_name);
    this.writeD(this.ally_crest_id);
    this.writeD(this.atWar);
    this.writeD(this.infos.size());
    Iterator var1 = this.infos.iterator();

    while(var1.hasNext()) {
      GMViewPledgeInfo.PledgeMemberInfo _info = (GMViewPledgeInfo.PledgeMemberInfo)var1.next();
      this.writeS(_info._name);
      this.writeD(_info.level);
      this.writeD(_info.class_id);
      this.writeD(_info.sex);
      this.writeD(_info.race);
      this.writeD(_info.online);
      this.writeD(_info.sponsor);
    }

    this.infos.clear();
  }

  static class PledgeMemberInfo {
    public String _name;
    public int level;
    public int class_id;
    public int online;
    public int sex;
    public int race;
    public int sponsor;

    public PledgeMemberInfo(String __name, int _level, int _class_id, int _online, int _sex, int _race, int _sponsor) {
      this._name = __name;
      this.level = _level;
      this.class_id = _class_id;
      this.online = _online;
      this.sex = _sex;
      this.race = _race;
      this.sponsor = _sponsor;
    }
  }
}
