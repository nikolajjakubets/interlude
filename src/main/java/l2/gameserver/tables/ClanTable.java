//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.SiegeUtils;
import l2.gameserver.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClanTable {
  private static ClanTable _instance;
  private final Map<Integer, Clan> _clans = new ConcurrentHashMap<>();
  private final Map<Integer, Alliance> _alliances = new ConcurrentHashMap<>();
  private Clan _npcClan;
  private static final List<Skill> FULL_CLAN_SKILLS = Arrays.asList(SkillTable.getInstance().getInfo(370, 3), SkillTable.getInstance().getInfo(373, 3), SkillTable.getInstance().getInfo(379, 3), SkillTable.getInstance().getInfo(391, 1), SkillTable.getInstance().getInfo(371, 3), SkillTable.getInstance().getInfo(374, 3), SkillTable.getInstance().getInfo(376, 3), SkillTable.getInstance().getInfo(377, 3), SkillTable.getInstance().getInfo(383, 3), SkillTable.getInstance().getInfo(380, 3), SkillTable.getInstance().getInfo(382, 3), SkillTable.getInstance().getInfo(384, 3), SkillTable.getInstance().getInfo(385, 3), SkillTable.getInstance().getInfo(386, 3), SkillTable.getInstance().getInfo(387, 3), SkillTable.getInstance().getInfo(388, 3), SkillTable.getInstance().getInfo(390, 3), SkillTable.getInstance().getInfo(372, 3), SkillTable.getInstance().getInfo(375, 3), SkillTable.getInstance().getInfo(378, 3), SkillTable.getInstance().getInfo(381, 3), SkillTable.getInstance().getInfo(389, 3));

  public static ClanTable getInstance() {
    if (_instance == null) {
      new ClanTable();
    }

    return _instance;
  }

  public Clan[] getClans() {
    return this._clans.values().toArray(new Clan[0]);
  }

  public Alliance[] getAlliances() {
    return this._alliances.values().toArray(new Alliance[0]);
  }

  private ClanTable() {
    _instance = this;
    this.restoreClans();
    this.restoreAllies();
    this.restoreWars();
  }

  public Clan getClan(int clanId) {
    if (clanId <= 0) {
      return null;
    } else {
      return Config.ALT_NPC_CLAN == clanId ? this._npcClan : this._clans.get(clanId);
    }
  }

  public String getClanName(int clanId) {
    Clan c = this.getClan(clanId);
    return c != null ? c.getName() : "";
  }

  public Clan getClanByCharId(int charId) {
    if (charId <= 0) {
      return null;
    } else {
      Clan[] var2 = this.getClans();
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; ++var4) {
        Clan clan = var2[var4];
        if (clan != null && clan.isAnyMember(charId)) {
          return clan;
        }
      }

      return null;
    }
  }

  public Alliance getAlliance(int allyId) {
    return allyId <= 0 ? null : this._alliances.get(allyId);
  }

  public Alliance getAllianceByCharId(int charId) {
    if (charId <= 0) {
      return null;
    } else {
      Clan charClan = this.getClanByCharId(charId);
      return charClan == null ? null : charClan.getAlliance();
    }
  }

  public Entry<Clan, Alliance> getClanAndAllianceByCharId(int charId) {
    Player player = GameObjectsStorage.getPlayer(charId);
    Clan charClan = player != null ? player.getClan() : this.getClanByCharId(charId);
    return new SimpleEntry(charClan, charClan == null ? null : charClan.getAlliance());
  }

  public void restoreClans() {
    List<Integer> clanIds = new ArrayList<>();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT clan_id FROM clan_data");
      result = statement.executeQuery();

      while (result.next()) {
        clanIds.add(result.getInt("clan_id"));
      }
    } catch (Exception var10) {
      log.warn("Error while restoring clans!!! " + var10);
    } finally {
      DbUtils.closeQuietly(con, statement, result);
    }

    Iterator var5 = clanIds.iterator();

    while (var5.hasNext()) {
      int clanId = (Integer) var5.next();
      Clan clan = Clan.restore(clanId);
      if (clan == null) {
        log.warn("Error while restoring clanId: " + clanId);
      } else if (clan.getAllSize() <= 0) {
        log.warn("membersCount = 0 for clanId: " + clanId);
      } else if (clan.getLeader() == null) {
        log.warn("Not found leader for clanId: " + clanId);
      } else {
        this._clans.put(clan.getClanId(), clan);
        if (Config.ALT_NPC_CLAN > 0) {
          this._npcClan = this._clans.get(Config.ALT_NPC_CLAN);
        }
      }
    }

  }

  public void restoreAllies() {
    List<Integer> allyIds = new ArrayList<>();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT ally_id FROM ally_data");
      result = statement.executeQuery();

      while (result.next()) {
        allyIds.add(result.getInt("ally_id"));
      }
    } catch (Exception var10) {
      log.warn("Error while restoring allies!!! " + var10);
    } finally {
      DbUtils.closeQuietly(con, statement, result);
    }

    for (int allyId : allyIds) {
      Alliance ally = new Alliance(allyId);
      if (ally.getMembersCount() <= 0) {
        log.warn("membersCount = 0 for allyId: " + allyId);
      } else if (ally.getLeader() == null) {
        log.warn("Not found leader for allyId: " + allyId);
      } else {
        this._alliances.put(ally.getAllyId(), ally);
      }
    }

  }

  public Clan getClanByName(String clanName) {
    if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE)) {
      return null;
    } else {
      Iterator var2 = this._clans.values().iterator();

      Clan clan;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        clan = (Clan) var2.next();
      } while (!clan.getName().equalsIgnoreCase(clanName));

      return clan;
    }
  }

  public Alliance getAllyByName(String allyName) {
    if (!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE)) {
      return null;
    } else {
      Iterator var2 = this._alliances.values().iterator();

      Alliance ally;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        ally = (Alliance) var2.next();
      } while (!ally.getAllyName().equalsIgnoreCase(allyName));

      return ally;
    }
  }

  public Clan createClan(Player player, String clanName) {
    if (this.getClanByName(clanName) == null) {
      UnitMember leader = new UnitMember(player);
      leader.setLeaderOf(0);
      Clan clan = new Clan(IdFactory.getInstance().getNextId());
      clan.setLevel(Config.CLAN_INIT_LEVEL);
      SubUnit unit = new SubUnit(clan, 0, leader, clanName);
      unit.addUnitMember(leader);
      clan.addSubUnit(unit, false);
      clan.store();
      player.setPledgeType(0);
      player.setClan(clan);
      player.setPowerGrade(6);
      leader.setPlayerInstance(player, false);
      this._clans.put(clan.getClanId(), clan);
      if (Config.CLAN_REPUTATION_BONUS_ON_CREATE > 0) {
        clan.incReputation(Config.CLAN_REPUTATION_BONUS_ON_CREATE, false, "ClanReputationOnCreateBonusAdd");
      }

      if (Config.FULL_CLAN_SKILLS_ON_CREATE) {
        Iterator var6 = FULL_CLAN_SKILLS.iterator();

        while (var6.hasNext()) {
          Skill aNewClanSkill = (Skill) var6.next();
          clan.addSkill(aNewClanSkill, true);
          clan.broadcastToOnlineMembers((new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED)).addSkillName(aNewClanSkill));
        }
      }

      return clan;
    } else {
      return null;
    }
  }

  public void dissolveClan(Clan clan) {
    int leaderId = clan.getLeaderId();
    Player leaderPlayer = clan.getLeader() != null ? clan.getLeader().getPlayer() : null;
    long curtime = System.currentTimeMillis();
    if (leaderPlayer != null) {
      SiegeUtils.removeSiegeSkills(leaderPlayer);
    }

    Iterator var6 = clan.getOnlineMembers(0).iterator();

    while (var6.hasNext()) {
      Player clanMember = (Player) var6.next();
      clanMember.setClan(null);
      clanMember.setTitle("");
      clanMember.sendPacket(PledgeShowMemberListDeleteAll.STATIC, Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
      clanMember.broadcastCharInfo();
      clanMember.setLeaveClanTime(curtime);
    }

    clan.flush();
    this.deleteClanFromDb(clan.getClanId(), leaderId);
    this._clans.remove(clan.getClanId());
    if (leaderPlayer != null) {
      leaderPlayer.sendPacket(Msg.CLAN_HAS_DISPERSED);
      leaderPlayer.setDeleteClanTime(curtime);
    }

  }

  public void deleteClanFromDb(int clanId, int leaderId) {
    long curtime = System.currentTimeMillis();
    Connection con = null;
    PreparedStatement statement = null;
    boolean deleted = false;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET clanid=0,title='',pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0,leaveclan=? WHERE clanid=?");
      statement.setLong(1, curtime / 1000L);
      statement.setInt(2, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE characters SET deleteclan=? WHERE obj_Id=?");
      statement.setLong(1, curtime / 1000L);
      statement.setInt(2, leaderId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM siege_players WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?");
      statement.setInt(1, clanId);
      statement.execute();
      deleted = true;
    } catch (Exception var12) {
      log.warn("could not dissolve clan:" + var12);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    if (deleted) {
      CharacterVariablesDAO.getInstance().deleteVars(clanId);
    }

  }

  public Alliance createAlliance(Player player, String allyName) {
    Alliance alliance = null;
    if (this.getAllyByName(allyName) == null) {
      Clan leader = player.getClan();
      alliance = new Alliance(IdFactory.getInstance().getNextId(), allyName, leader);
      alliance.store();
      this._alliances.put(alliance.getAllyId(), alliance);
      player.getClan().setAllyId(alliance.getAllyId());
      Iterator var5 = player.getClan().getOnlineMembers(0).iterator();

      while (var5.hasNext()) {
        Player temp = (Player) var5.next();
        temp.broadcastCharInfo();
      }
    }

    return alliance;
  }

  public void dissolveAlly(Player player) {
    int allyId = player.getAllyId();
    Clan[] var3 = player.getAlliance().getMembers();
    int var4 = var3.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      Clan member = var3[var5];
      member.setAllyId(0);
      member.broadcastClanStatus(false, true, false);
      member.broadcastToOnlineMembers(Msg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
      member.setLeavedAlly();
    }

    this.deleteAllyFromDb(allyId);
    this._alliances.remove(allyId);
    player.sendPacket(Msg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
    player.getClan().setDissolvedAlly();
  }

  public void deleteAllyFromDb(int allyId) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE ally_id=?");
      statement.setInt(1, allyId);
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM ally_data WHERE ally_id=?");
      statement.setInt(1, allyId);
      statement.execute();
    } catch (Exception var8) {
      log.warn("could not dissolve clan:" + var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void startClanWar(Clan clan1, Clan clan2) {
    clan1.setEnemyClan(clan2);
    clan2.setAttackerClan(clan1);
    clan1.broadcastClanStatus(false, false, true);
    clan2.broadcastClanStatus(false, false, true);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("REPLACE INTO clan_wars (clan1, clan2) VALUES(?,?)");
      statement.setInt(1, clan1.getClanId());
      statement.setInt(2, clan2.getClanId());
      statement.execute();
    } catch (Exception var9) {
      log.warn("could not store clan war data:" + var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    clan1.broadcastToOnlineMembers((new SystemMessage(1562)).addString(clan2.getName()));
    clan2.broadcastToOnlineMembers((new SystemMessage(1561)).addString(clan1.getName()));
  }

  public void stopClanWar(Clan clan1, Clan clan2) {
    clan1.deleteEnemyClan(clan2);
    clan2.deleteAttackerClan(clan1);
    clan1.broadcastClanStatus(false, false, true);
    clan2.broadcastClanStatus(false, false, true);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? AND clan2=?");
      statement.setInt(1, clan1.getClanId());
      statement.setInt(2, clan2.getClanId());
      statement.execute();
    } catch (Exception var9) {
      log.warn("could not delete war data:" + var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    clan1.broadcastToOnlineMembers((new SystemMessage(1567)).addString(clan2.getName()));
    clan2.broadcastToOnlineMembers((new SystemMessage(1566)).addString(clan1.getName()));
  }

  private void restoreWars() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT clan1, clan2 FROM clan_wars");
      rset = statement.executeQuery();

      while (rset.next()) {
        Clan clan1 = this.getClan(rset.getInt("clan1"));
        Clan clan2 = this.getClan(rset.getInt("clan2"));
        if (clan1 != null && clan2 != null) {
          clan1.setEnemyClan(clan2);
          clan2.setAttackerClan(clan1);
        }
      }
    } catch (Exception var9) {
      log.warn("could not restore clan wars data:");
      log.error("", var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public void checkClans() {
    long currentTime = System.currentTimeMillis();
    Clan[] var3 = this.getClans();
    int var4 = var3.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      Clan clan = var3[var5];
      if (clan.getDisbandEndTime() > 0L && clan.getDisbandEndTime() < currentTime) {
        this.dissolveClan(clan);
      }
    }

  }

  public static void unload() {
    if (_instance != null) {
      try {
        _instance.finalize();
      } catch (Throwable var1) {
      }
    }

  }
}
