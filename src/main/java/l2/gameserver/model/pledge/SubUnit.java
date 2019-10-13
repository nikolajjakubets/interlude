//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.ExSubPledgeSkillAdd;
import l2.gameserver.tables.SkillTable;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubUnit {
  private static final Logger _log = LoggerFactory.getLogger(SubUnit.class);
  private IntObjectMap<Skill> _skills = new CTreeIntObjectMap();
  private IntObjectMap<UnitMember> _members = new CHashIntObjectMap();
  private int _type;
  private int _leaderObjectId;
  private UnitMember _leader;
  private int _nextLeaderObjectId;
  private String _name;
  private Clan _clan;

  public SubUnit(Clan c, int type, UnitMember leader, String name) {
    this._clan = c;
    this._type = type;
    this._name = name;
    this.setLeader(leader, false);
  }

  public SubUnit(Clan c, int type, int leader, String name) {
    this._clan = c;
    this._type = type;
    this._leaderObjectId = leader;
    this._name = name;
  }

  public int getType() {
    return this._type;
  }

  public String getName() {
    return this._name;
  }

  public UnitMember getLeader() {
    return this._leader;
  }

  public boolean isUnitMember(int obj) {
    return this._members.containsKey(obj);
  }

  public void addUnitMember(UnitMember member) {
    this._members.put(member.getObjectId(), member);
  }

  public UnitMember getUnitMember(int obj) {
    return obj == 0 ? null : (UnitMember)this._members.get(obj);
  }

  public UnitMember getUnitMember(String obj) {
    Iterator var2 = this.getUnitMembers().iterator();

    UnitMember m;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      m = (UnitMember)var2.next();
    } while(!m.getName().equalsIgnoreCase(obj));

    return m;
  }

  public void removeUnitMember(int objectId) {
    UnitMember m = (UnitMember)this._members.remove(objectId);
    if (m != null) {
      if (objectId == this.getLeaderObjectId()) {
        this.setLeader((UnitMember)null, true);
      }

      if (m.hasSponsor()) {
        this._clan.getAnyMember(m.getSponsor()).setApprentice(0);
      }

      removeMemberInDatabase(m);
      m.setPlayerInstance((Player)null, true);
    }
  }

  public void replace(int objectId, int newUnitId) {
    SubUnit newUnit = this._clan.getSubUnit(newUnitId);
    if (newUnit != null) {
      UnitMember m = (UnitMember)this._members.remove(objectId);
      if (m != null) {
        m.setPledgeType(newUnitId);
        newUnit.addUnitMember(m);
        if (m.getPowerGrade() > 5) {
          m.setPowerGrade(this._clan.getAffiliationRank(m.getPledgeType()));
        }

      }
    }
  }

  public int getLeaderObjectId() {
    return this._leader == null ? 0 : this._leader.getObjectId();
  }

  public int size() {
    return this._members.size();
  }

  public Collection<UnitMember> getUnitMembers() {
    return this._members.values();
  }

  public void updateDbLeader(UnitMember leaderUnitMember) {
    if (this.getType() == 0) {
      if (leaderUnitMember != this._leader) {
        this._nextLeaderObjectId = leaderUnitMember.getObjectId();
      } else {
        this._nextLeaderObjectId = 0;
      }
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?");
      statement.setInt(1, leaderUnitMember.getObjectId());
      statement.setInt(2, this._clan.getClanId());
      statement.setInt(3, this._type);
      statement.execute();
    } catch (Exception var8) {
      _log.error("Exception: " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void setLeader(UnitMember newLeader, boolean updateDB) {
    UnitMember old = this._leader;
    if (old != null) {
      old.setLeaderOf(-128);
    }

    this._leader = newLeader;
    this._leaderObjectId = newLeader == null ? 0 : newLeader.getObjectId();
    if (newLeader != null) {
      newLeader.setLeaderOf(this._type);
    }

    if (updateDB) {
      this.updateDbLeader(this._leader);
    }

  }

  public void setName(String name, boolean updateDB) {
    this._name = name;
    if (updateDB) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? and type=?");
        statement.setString(1, this._name);
        statement.setInt(2, this._clan.getClanId());
        statement.setInt(3, this._type);
        statement.execute();
      } catch (Exception var9) {
        _log.error("Exception: " + var9, var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }
    }

  }

  public String getLeaderName() {
    return this._leader == null ? "" : this._leader.getName();
  }

  public Skill addSkill(Skill newSkill, boolean store) {
    Skill oldSkill = null;
    if (newSkill != null) {
      oldSkill = (Skill)this._skills.put(newSkill.getId(), newSkill);
      if (store) {
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          if (oldSkill != null) {
            statement = con.prepareStatement("UPDATE clan_subpledges_skills SET skill_level=? WHERE skill_id=? AND clan_id=? AND type=?");
            statement.setInt(1, newSkill.getLevel());
            statement.setInt(2, oldSkill.getId());
            statement.setInt(3, this._clan.getClanId());
            statement.setInt(4, this._type);
            statement.execute();
          } else {
            statement = con.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id,type,skill_id,skill_level) VALUES (?,?,?,?)");
            statement.setInt(1, this._clan.getClanId());
            statement.setInt(2, this._type);
            statement.setInt(3, newSkill.getId());
            statement.setInt(4, newSkill.getLevel());
            statement.execute();
          }
        } catch (Exception var10) {
          _log.warn("Exception: " + var10, var10);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      }

      ExSubPledgeSkillAdd packet = new ExSubPledgeSkillAdd(this._type, newSkill.getId(), newSkill.getLevel());
      Iterator var13 = this._clan.iterator();

      while(var13.hasNext()) {
        UnitMember temp = (UnitMember)var13.next();
        if (temp.isOnline()) {
          Player player = temp.getPlayer();
          if (player != null) {
            player.sendPacket(packet);
            if (player.getPledgeType() == this._type) {
              this.addSkill(player, newSkill);
            }
          }
        }
      }
    }

    return oldSkill;
  }

  public int getNextLeaderObjectId() {
    return this._nextLeaderObjectId;
  }

  public void addSkillsQuietly(Player player) {
    Iterator var2 = this._skills.values().iterator();

    while(var2.hasNext()) {
      Skill skill = (Skill)var2.next();
      this.addSkill(player, skill);
    }

  }

  public void enableSkills(Player player) {
    Iterator var2 = this._skills.values().iterator();

    while(var2.hasNext()) {
      Skill skill = (Skill)var2.next();
      if (skill.getMinRank() <= player.getPledgeClass()) {
        player.removeUnActiveSkill(skill);
      }
    }

  }

  public void disableSkills(Player player) {
    Iterator var2 = this._skills.values().iterator();

    while(var2.hasNext()) {
      Skill skill = (Skill)var2.next();
      player.addUnActiveSkill(skill);
    }

  }

  private void addSkill(Player player, Skill skill) {
    if (skill.getMinRank() <= player.getPledgeClass()) {
      player.addSkill(skill, false);
      if (this._clan.getReputationScore() < 0 || player.isOlyParticipant()) {
        player.addUnActiveSkill(skill);
      }
    }

  }

  public Collection<Skill> getSkills() {
    return this._skills.values();
  }

  private static void removeMemberInDatabase(UnitMember member) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET clanid=0, pledge_type=?, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?");
      statement.setInt(1, -128);
      statement.setLong(2, System.currentTimeMillis() / 1000L);
      statement.setInt(3, member.getObjectId());
      statement.execute();
    } catch (Exception var7) {
      _log.warn("Exception: " + var7, var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void restore() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT `c`.`char_name` AS `char_name`,`s`.`level` AS `level`,`s`.`class_id` AS `classid`,`c`.`obj_Id` AS `obj_id`,`c`.`title` AS `title`,`c`.`pledge_rank` AS `pledge_rank`,`c`.`apprentice` AS `apprentice`, `c`.`sex` AS `sex` FROM `characters` `c` LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC");
      statement.setInt(1, this._clan.getClanId());
      statement.setInt(2, this._type);
      rset = statement.executeQuery();

      while(rset.next()) {
        UnitMember member = new UnitMember(this._clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("classid"), rset.getInt("obj_Id"), this._type, rset.getInt("pledge_rank"), rset.getInt("apprentice"), rset.getInt("sex"), -128);
        this.addUnitMember(member);
      }

      if (this._type != -1) {
        SubUnit mainClan = this._clan.getSubUnit(0);
        UnitMember leader = mainClan.getUnitMember(this._leaderObjectId);
        if (leader != null) {
          this.setLeader(leader, false);
        } else if (this._type == 0) {
          _log.error("Clan " + this._name + " have no leader!");
        }
      }
    } catch (Exception var9) {
      _log.warn("Error while restoring clan members for clan: " + this._clan.getClanId() + " " + var9, var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public void restoreSkills() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_subpledges_skills WHERE clan_id=? AND type=?");
      statement.setInt(1, this._clan.getClanId());
      statement.setInt(2, this._type);
      rset = statement.executeQuery();

      while(rset.next()) {
        int id = rset.getInt("skill_id");
        int level = rset.getInt("skill_level");
        Skill skill = SkillTable.getInstance().getInfo(id, level);
        this._skills.put(skill.getId(), skill);
      }
    } catch (Exception var10) {
      _log.warn("Exception: " + var10, var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public int getSkillLevel(int id, int def) {
    Skill skill = (Skill)this._skills.get(id);
    return skill == null ? def : skill.getLevel();
  }

  public int getSkillLevel(int id) {
    return this.getSkillLevel(id, -1);
  }
}
