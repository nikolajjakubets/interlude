//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMember {
  private static final Logger _log = LoggerFactory.getLogger(UnitMember.class);
  private Player _player;
  private Clan _clan;
  private String _name;
  private String _title;
  private int _objectId;
  private int _level;
  private int _classId;
  private int _sex;
  private int _pledgeType;
  private int _powerGrade;
  private int _apprentice;
  private int _leaderOf = -128;

  public UnitMember(Clan clan, String name, String title, int level, int classId, int objectId, int pledgeType, int powerGrade, int apprentice, int sex, int leaderOf) {
    this._clan = clan;
    this._objectId = objectId;
    this._name = name;
    this._title = title;
    this._level = level;
    this._classId = classId;
    this._pledgeType = pledgeType;
    this._powerGrade = powerGrade;
    this._apprentice = apprentice;
    this._sex = sex;
    this._leaderOf = leaderOf;
    if (powerGrade != 0) {
      RankPrivs r = clan.getRankPrivs(powerGrade);
      r.setParty(clan.countMembersByRank(powerGrade));
    }

  }

  public UnitMember(Player player) {
    this._objectId = player.getObjectId();
    this._player = player;
  }

  public void setPlayerInstance(Player player, boolean exit) {
    this._player = exit ? null : player;
    if (player != null) {
      this._clan = player.getClan();
      this._name = player.getName();
      this._title = player.getTitle();
      this._level = player.getLevel();
      this._classId = player.getClassId().getId();
      this._pledgeType = player.getPledgeType();
      this._powerGrade = player.getPowerGrade();
      this._apprentice = player.getApprentice();
      this._sex = player.getSex();
    }
  }

  public Player getPlayer() {
    return this._player;
  }

  public boolean isOnline() {
    Player player = this.getPlayer();
    return player != null && !player.isInOfflineMode();
  }

  public Clan getClan() {
    Player player = this.getPlayer();
    return player == null ? this._clan : player.getClan();
  }

  public int getClassId() {
    Player player = this.getPlayer();
    return player == null ? this._classId : player.getClassId().getId();
  }

  public int getSex() {
    Player player = this.getPlayer();
    return player == null ? this._sex : player.getSex();
  }

  public int getLevel() {
    Player player = this.getPlayer();
    return player == null ? this._level : player.getLevel();
  }

  public String getName() {
    Player player = this.getPlayer();
    return player == null ? this._name : player.getName();
  }

  public int getObjectId() {
    return this._objectId;
  }

  public String getTitle() {
    Player player = this.getPlayer();
    return player == null ? this._title : player.getTitle();
  }

  public void setTitle(String title) {
    Player player = this.getPlayer();
    this._title = title;
    if (player != null) {
      player.setTitle(title);
      player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?");
        statement.setString(1, title);
        statement.setInt(2, this.getObjectId());
        statement.execute();
      } catch (Exception var9) {
        _log.error("", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }
    }

  }

  public SubUnit getSubUnit() {
    return this._clan.getSubUnit(this._pledgeType);
  }

  public int getPledgeType() {
    Player player = this.getPlayer();
    return player == null ? this._pledgeType : player.getPledgeType();
  }

  public void setPledgeType(int pledgeType) {
    Player player = this.getPlayer();
    this._pledgeType = pledgeType;
    if (player != null) {
      player.setPledgeType(pledgeType);
    } else {
      this.updatePledgeType();
    }

  }

  private void updatePledgeType() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET pledge_type=? WHERE obj_Id=?");
      statement.setInt(1, this._pledgeType);
      statement.setInt(2, this.getObjectId());
      statement.execute();
    } catch (Exception var7) {
      _log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public int getPowerGrade() {
    Player player = this.getPlayer();
    return player == null ? this._powerGrade : player.getPowerGrade();
  }

  public void setPowerGrade(int newPowerGrade) {
    Player player = this.getPlayer();
    int oldPowerGrade = this.getPowerGrade();
    this._powerGrade = newPowerGrade;
    if (player != null) {
      player.setPowerGrade(newPowerGrade);
    } else {
      this.updatePowerGrade();
    }

    this.updatePowerGradeParty(oldPowerGrade, newPowerGrade);
  }

  private void updatePowerGradeParty(int oldGrade, int newGrade) {
    RankPrivs r2;
    if (oldGrade != 0) {
      r2 = this.getClan().getRankPrivs(oldGrade);
      r2.setParty(this.getClan().countMembersByRank(oldGrade));
    }

    if (newGrade != 0) {
      r2 = this.getClan().getRankPrivs(newGrade);
      r2.setParty(this.getClan().countMembersByRank(newGrade));
    }

  }

  private void updatePowerGrade() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET pledge_rank=? WHERE obj_Id=?");
      statement.setInt(1, this._powerGrade);
      statement.setInt(2, this.getObjectId());
      statement.execute();
    } catch (Exception var7) {
      _log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private int getApprentice() {
    Player player = this.getPlayer();
    return player == null ? this._apprentice : player.getApprentice();
  }

  public void setApprentice(int apprentice) {
    Player player = this.getPlayer();
    this._apprentice = apprentice;
    if (player != null) {
      player.setApprentice(apprentice);
    } else {
      this.updateApprentice();
    }

  }

  private void updateApprentice() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET apprentice=? WHERE obj_Id=?");
      statement.setInt(1, this._apprentice);
      statement.setInt(2, this.getObjectId());
      statement.execute();
    } catch (Exception var7) {
      _log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public String getApprenticeName() {
    return this.getApprentice() != 0 && this.getClan().getAnyMember(this.getApprentice()) != null ? this.getClan().getAnyMember(this.getApprentice()).getName() : "";
  }

  public boolean hasApprentice() {
    return this.getApprentice() != 0;
  }

  public int getSponsor() {
    if (this.getPledgeType() != -1) {
      return 0;
    } else {
      int id = this.getObjectId();
      Iterator var2 = this.getClan().iterator();

      UnitMember element;
      do {
        if (!var2.hasNext()) {
          return 0;
        }

        element = (UnitMember)var2.next();
      } while(element.getApprentice() != id);

      return element.getObjectId();
    }
  }

  private String getSponsorName() {
    int sponsorId = this.getSponsor();
    if (sponsorId == 0) {
      return "";
    } else {
      return this.getClan().getAnyMember(sponsorId) != null ? this.getClan().getAnyMember(sponsorId).getName() : "";
    }
  }

  public boolean hasSponsor() {
    return this.getSponsor() != 0;
  }

  public String getRelatedName() {
    return this.getPledgeType() == -1 ? this.getSponsorName() : this.getApprenticeName();
  }

  public boolean isClanLeader() {
    Player player = this.getPlayer();
    return player == null ? this._leaderOf == 0 : player.isClanLeader();
  }

  public int isSubLeader() {
    Iterator var1 = this.getClan().getAllSubUnits().iterator();

    SubUnit pledge;
    do {
      if (!var1.hasNext()) {
        return 0;
      }

      pledge = (SubUnit)var1.next();
    } while(pledge.getLeaderObjectId() != this.getObjectId());

    return pledge.getType();
  }

  public void setLeaderOf(int leaderOf) {
    this._leaderOf = leaderOf;
  }

  public int getLeaderOf() {
    return this._leaderOf;
  }
}
