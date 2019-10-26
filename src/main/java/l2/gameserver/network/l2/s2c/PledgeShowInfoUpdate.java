//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket {
  private int clan_id;
  private int clan_level;
  private int clan_rank;
  private int clan_rep;
  private int crest_id;
  private int ally_id;
  private int ally_crest;
  private int atwar;
  private String ally_name = "";
  private int HasCastle;
  private int HasHideout;
  private boolean _isDisbanded;

  public PledgeShowInfoUpdate(Clan clan) {
    this.clan_id = clan.getClanId();
    this.clan_level = clan.getLevel();
    this.HasCastle = clan.getCastle();
    this.HasHideout = clan.getHasHideout();
    this.clan_rank = clan.getRank();
    this.clan_rep = clan.getReputationScore();
    this.crest_id = clan.getCrestId();
    this.ally_id = clan.getAllyId();
    this.atwar = clan.isAtWar();
    this._isDisbanded = clan.isPlacedForDisband();
    Alliance ally = clan.getAlliance();
    if (ally != null) {
      this.ally_name = ally.getAllyName();
      this.ally_crest = ally.getAllyCrestId();
    }

  }

  protected final void writeImpl() {
    this.writeC(136);
    this.writeD(this.clan_id);
    this.writeD(this.crest_id);
    this.writeD(this.clan_level);
    this.writeD(this.HasCastle);
    this.writeD(this.HasHideout);
    this.writeD(this.clan_rank);
    this.writeD(this.clan_rep);
    this.writeD(this._isDisbanded ? 3 : 0);
    this.writeD(0);
    this.writeD(this.ally_id);
    this.writeS(this.ally_name);
    this.writeD(this.ally_crest);
    this.writeD(this.atwar);
  }
}
