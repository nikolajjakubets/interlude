//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.JoinedIterator;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.CrestCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.items.ClanWarehouse;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeReceiveSubPledgeCreated;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListAll;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.PledgeSkillList;
import l2.gameserver.network.l2.s2c.PledgeSkillListAdd;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Log;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clan implements Iterable<UnitMember> {
  private static final Logger _log = LoggerFactory.getLogger(Clan.class);
  private final int _clanId;
  private int _allyId;
  private int _level;
  private int _hasCastle;
  private int _hasHideout;
  private int _crestId;
  private int _crestLargeId;
  private long _expelledMemberTime;
  private long _leavedAllyTime;
  private long _dissolvedAllyTime;
  private long _disbandEndTime;
  private long _disbandPenaltyTime;
  private final ClanWarehouse _warehouse;
  private int _whBonus = -1;
  private String _notice = null;
  private List<Clan> _atWarWith = new CopyOnWriteArrayList();
  private List<Clan> _underAttackFrom = new CopyOnWriteArrayList();
  protected IntObjectMap<Skill> _skills = new CTreeIntObjectMap();
  protected IntObjectMap<RankPrivs> _privs = new CTreeIntObjectMap();
  protected IntObjectMap<SubUnit> _subUnits = new CTreeIntObjectMap();
  public static long DISBAND_PENALTY = 604800000L;
  public static long DISBAND_TIME = 172800000L;
  private int _reputation = 0;
  public static final int CP_NOTHING = 0;
  public static final int CP_CL_INVITE_CLAN = 2;
  public static final int CP_CL_MANAGE_TITLES = 4;
  public static final int CP_CL_WAREHOUSE_SEARCH = 8;
  public static final int CP_CL_MANAGE_RANKS = 16;
  public static final int CP_CL_CLAN_WAR = 32;
  public static final int CP_CL_DISMISS = 64;
  public static final int CP_CL_EDIT_CREST = 128;
  public static final int CP_CL_APPRENTICE = 256;
  public static final int CP_CL_TROOPS_FAME = 512;
  public static final int CP_CH_ENTRY_EXIT = 1024;
  public static final int CP_CH_USE_FUNCTIONS = 2048;
  public static final int CP_CH_AUCTION = 4096;
  public static final int CP_CH_DISMISS = 8192;
  public static final int CP_CH_SET_FUNCTIONS = 16384;
  public static final int CP_CS_ENTRY_EXIT = 32768;
  public static final int CP_CS_MANOR_ADMIN = 65536;
  public static final int CP_CS_MANAGE_SIEGE = 131072;
  public static final int CP_CS_USE_FUNCTIONS = 262144;
  public static final int CP_CS_DISMISS = 524288;
  public static final int CP_CS_TAXES = 1048576;
  public static final int CP_CS_MERCENARIES = 2097152;
  public static final int CP_CS_SET_FUNCTIONS = 4194304;
  public static final int CP_ALL = 8388606;
  public static final int RANK_FIRST = 1;
  public static final int RANK_LAST = 9;
  public static final int SUBUNIT_NONE = -128;
  public static final int SUBUNIT_ACADEMY = -1;
  public static final int SUBUNIT_MAIN_CLAN = 0;
  public static final int SUBUNIT_ROYAL1 = 100;
  public static final int SUBUNIT_ROYAL2 = 200;
  public static final int SUBUNIT_KNIGHT1 = 1001;
  public static final int SUBUNIT_KNIGHT2 = 1002;
  public static final int SUBUNIT_KNIGHT3 = 2001;
  public static final int SUBUNIT_KNIGHT4 = 2002;
  private static final Clan.ClanReputationComparator REPUTATION_COMPARATOR = new Clan.ClanReputationComparator();
  private static final int REPUTATION_PLACES = 100;

  public Clan(int clanId) {
    this._clanId = clanId;
    this.InitializePrivs();
    this._warehouse = new ClanWarehouse(this);
    this._warehouse.restore();
  }

  public int getClanId() {
    return this._clanId;
  }

  public int getLeaderId() {
    return this.getLeaderId(0);
  }

  public UnitMember getLeader() {
    return this.getLeader(0);
  }

  public String getLeaderName() {
    return this.getLeaderName(0);
  }

  public String getName() {
    return this.getUnitName(0);
  }

  public UnitMember getAnyMember(int id) {
    Iterator var2 = this.getAllSubUnits().iterator();

    UnitMember m;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      SubUnit unit = (SubUnit)var2.next();
      m = unit.getUnitMember(id);
    } while(m == null);

    return m;
  }

  public UnitMember getAnyMember(String name) {
    Iterator var2 = this.getAllSubUnits().iterator();

    UnitMember m;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      SubUnit unit = (SubUnit)var2.next();
      m = unit.getUnitMember(name);
    } while(m == null);

    return m;
  }

  public int getAllSize() {
    int size = 0;

    SubUnit unit;
    for(Iterator var2 = this.getAllSubUnits().iterator(); var2.hasNext(); size += unit.size()) {
      unit = (SubUnit)var2.next();
    }

    return size;
  }

  public String getUnitName(int unitType) {
    return unitType != -128 && this._subUnits.containsKey(unitType) ? this.getSubUnit(unitType).getName() : "";
  }

  public String getLeaderName(int unitType) {
    return unitType != -128 && this._subUnits.containsKey(unitType) ? this.getSubUnit(unitType).getLeaderName() : "";
  }

  public int getLeaderId(int unitType) {
    return unitType != -128 && this._subUnits.containsKey(unitType) ? this.getSubUnit(unitType).getLeaderObjectId() : 0;
  }

  public UnitMember getLeader(int unitType) {
    return unitType != -128 && this._subUnits.containsKey(unitType) ? this.getSubUnit(unitType).getLeader() : null;
  }

  public void flush() {
    Iterator var1 = this.iterator();

    while(var1.hasNext()) {
      UnitMember member = (UnitMember)var1.next();
      this.removeClanMember(member.getObjectId());
    }

    this._warehouse.writeLock();

    try {
      ItemInstance[] var8 = this._warehouse.getItems();
      int var9 = var8.length;

      for(int var3 = 0; var3 < var9; ++var3) {
        ItemInstance item = var8[var3];
        this._warehouse.destroyItem(item);
      }
    } finally {
      this._warehouse.writeUnlock();
    }

    if (this._hasCastle != 0) {
      ((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._hasCastle)).changeOwner((Clan)null);
    }

  }

  public void removeClanMember(int id) {
    if (id != this.getLeaderId(0)) {
      Iterator var2 = this.getAllSubUnits().iterator();

      while(var2.hasNext()) {
        SubUnit unit = (SubUnit)var2.next();
        if (unit.isUnitMember(id)) {
          this.removeClanMember(unit.getType(), id);
          break;
        }
      }

    }
  }

  public void removeClanMember(int subUnitId, int objectId) {
    SubUnit subUnit = this.getSubUnit(subUnitId);
    if (subUnit != null) {
      subUnit.removeUnitMember(objectId);
    }
  }

  public List<UnitMember> getAllMembers() {
    Collection<SubUnit> units = this.getAllSubUnits();
    int size = 0;

    SubUnit unit;
    for(Iterator var3 = units.iterator(); var3.hasNext(); size += unit.size()) {
      unit = (SubUnit)var3.next();
    }

    List<UnitMember> members = new ArrayList<>(size);

    for (SubUnit next : units) {
      members.addAll(next.getUnitMembers());
    }

    return members;
  }

  public List<Player> getOnlineMembers(int exclude) {
    List<Player> result = new ArrayList(this.getAllSize() - 1);

    for (UnitMember temp : this) {
      if (temp != null && temp.isOnline() && temp.getObjectId() != exclude) {
        result.add(temp.getPlayer());
      }
    }

    return result;
  }

  public int getAllyId() {
    return this._allyId;
  }

  public int getLevel() {
    return this._level;
  }

  public int getCastle() {
    return this._hasCastle;
  }

  public int getHasHideout() {
    return this._hasHideout;
  }

  public int getResidenceId(ResidenceType r) {
    switch(r) {
      case Castle:
        return this._hasCastle;
      case ClanHall:
        return this._hasHideout;
      default:
        return 0;
    }
  }

  public void setAllyId(int allyId) {
    this._allyId = allyId;
  }

  public void setHasCastle(int castle) {
    this._hasCastle = castle;
  }

  public void setHasHideout(int hasHideout) {
    this._hasHideout = hasHideout;
  }

  public void setLevel(int level) {
    this._level = level;
  }

  public boolean isAnyMember(int id) {
    Iterator var2 = this.getAllSubUnits().iterator();

    SubUnit unit;
    do {
      if (!var2.hasNext()) {
        return false;
      }

      unit = (SubUnit)var2.next();
    } while(!unit.isUnitMember(id));

    return true;
  }

  public void updateClanInDB() {
    if (this.getLeaderId() == 0) {
      _log.warn("updateClanInDB with empty LeaderId");
      Thread.dumpStack();
    } else if (this.getClanId() == 0) {
      _log.warn("updateClanInDB with empty ClanId");
      Thread.dumpStack();
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE clan_data SET ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,disband_end=?,disband_penalty=? WHERE clan_id=?");
        statement.setInt(1, this.getAllyId());
        statement.setInt(2, this.getReputationScore());
        statement.setLong(3, this.getExpelledMemberTime() / 1000L);
        statement.setLong(4, this.getLeavedAllyTime() / 1000L);
        statement.setLong(5, this.getDissolvedAllyTime() / 1000L);
        statement.setInt(6, this._level);
        statement.setInt(7, this.getWhBonus());
        statement.setInt(8, (int)(this.getDisbandEndTime() / 1000L));
        statement.setInt(9, (int)(this.getDisbandPenaltyTime() / 1000L));
        statement.setInt(10, this.getClanId());
        statement.execute();
      } catch (Exception var7) {
        _log.warn("error while updating clan '" + this.getClanId() + "' data in db");
        _log.error("", var7);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  public void store() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("INSERT INTO clan_data (clan_id,clan_level,hasCastle,hasHideout,ally_id,expelled_member,leaved_ally,dissolved_ally) values (?,?,?,?,?,?,?,?)");
      statement.setInt(1, this._clanId);
      statement.setInt(2, this._level);
      statement.setInt(3, this._hasCastle);
      statement.setInt(4, this._hasHideout);
      statement.setInt(5, this._allyId);
      statement.setLong(6, this.getExpelledMemberTime() / 1000L);
      statement.setLong(7, this.getLeavedAllyTime() / 1000L);
      statement.setLong(8, this.getDissolvedAllyTime() / 1000L);
      statement.execute();
      DbUtils.close(statement);
      SubUnit mainSubUnit = (SubUnit)this._subUnits.get(0);
      statement = con.prepareStatement("INSERT INTO clan_subpledges (clan_id, type, leader_id, name) VALUES (?,?,?,?)");
      statement.setInt(1, this._clanId);
      statement.setInt(2, mainSubUnit.getType());
      statement.setInt(3, mainSubUnit.getLeaderObjectId());
      statement.setString(4, mainSubUnit.getName());
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE characters SET clanid=?,pledge_type=? WHERE obj_Id=?");
      statement.setInt(1, this.getClanId());
      statement.setInt(2, mainSubUnit.getType());
      statement.setInt(3, this.getLeaderId());
      statement.execute();
    } catch (Exception var7) {
      _log.warn("Exception: " + var7, var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public static Clan restore(int clanId) {
    if (clanId == 0) {
      return null;
    } else {
      Clan clan = null;
      Connection con1 = null;
      PreparedStatement statement1 = null;
      ResultSet clanData = null;

      Iterator var5;
      try {
        con1 = DatabaseFactory.getInstance().getConnection();
        statement1 = con1.prepareStatement("SELECT clan_level,hasCastle,hasHideout,ally_id,reputation_score,expelled_member,leaved_ally,dissolved_ally,warehouse,disband_end,disband_penalty FROM clan_data where clan_id=?");
        statement1.setInt(1, clanId);
        clanData = statement1.executeQuery();
        if (!clanData.next()) {
          _log.warn("Clan " + clanId + " doesnt exists!");
//          var5 = null;
          return null;
        }

        clan = new Clan(clanId);
        clan.setLevel(clanData.getInt("clan_level"));
        clan.setHasCastle(clanData.getInt("hasCastle"));
        clan.setHasHideout(clanData.getInt("hasHideout"));
        clan.setAllyId(clanData.getInt("ally_id"));
        clan._reputation = clanData.getInt("reputation_score");
        clan.setExpelledMemberTime(clanData.getLong("expelled_member") * 1000L);
        clan.setLeavedAllyTime(clanData.getLong("leaved_ally") * 1000L);
        clan.setDissolvedAllyTime(clanData.getLong("dissolved_ally") * 1000L);
        clan.setWhBonus(clanData.getInt("warehouse"));
        clan.setDisbandEndTime(clanData.getLong("disband_end") * 1000L);
        clan.setDisbandPenaltyTime(clanData.getLong("disband_penalty") * 1000L);
      } catch (Exception var9) {
        _log.error("Error while restoring clan!", var9);
      } finally {
        DbUtils.closeQuietly(con1, statement1, clanData);
      }

      if (clan == null) {
        _log.warn("Clan " + clanId + " does't exist");
        return null;
      } else {
        clan.restoreSkills();
        clan.restoreSubPledges();
        var5 = clan.getAllSubUnits().iterator();

        while(var5.hasNext()) {
          SubUnit unit = (SubUnit)var5.next();
          unit.restore();
          unit.restoreSkills();
        }

        clan.restoreRankPrivs();
        clan.setCrestId(CrestCache.getInstance().getPledgeCrestId(clanId));
        clan.setCrestLargeId(CrestCache.getInstance().getPledgeCrestLargeId(clanId));
        return clan;
      }
    }
  }

  public void broadcastToOnlineMembers(IStaticPacket... packets) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      UnitMember member = (UnitMember)var2.next();
      if (member.isOnline()) {
        member.getPlayer().sendPacket(packets);
      }
    }

  }

  public void broadcastToOnlineMembers(L2GameServerPacket... packets) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      UnitMember member = (UnitMember)var2.next();
      if (member.isOnline()) {
        member.getPlayer().sendPacket(packets);
      }
    }

  }

  public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player) {
    Iterator var3 = this.iterator();

    while(var3.hasNext()) {
      UnitMember member = (UnitMember)var3.next();
      if (member.isOnline() && member.getPlayer() != player) {
        member.getPlayer().sendPacket(packet);
      }
    }

  }

  public String toString() {
    return this.getName();
  }

  public void setCrestId(int newcrest) {
    this._crestId = newcrest;
  }

  public int getCrestId() {
    return this._crestId;
  }

  public boolean hasCrest() {
    return this._crestId > 0;
  }

  public int getCrestLargeId() {
    return this._crestLargeId;
  }

  public void setCrestLargeId(int newcrest) {
    this._crestLargeId = newcrest;
  }

  public boolean hasCrestLarge() {
    return this._crestLargeId > 0;
  }

  public long getAdenaCount() {
    return this._warehouse.getCountOfAdena();
  }

  public ClanWarehouse getWarehouse() {
    return this._warehouse;
  }

  public int isAtWar() {
    return this._atWarWith != null && !this._atWarWith.isEmpty() ? 1 : 0;
  }

  public int isAtWarOrUnderAttack() {
    return this._atWarWith.isEmpty() && this._underAttackFrom.isEmpty() ? 0 : 1;
  }

  public boolean isAtWarWith(int id) {
    Clan clan = ClanTable.getInstance().getClan(id);
    return this._atWarWith != null && !this._atWarWith.isEmpty() && this._atWarWith.contains(clan);
  }

  public boolean isUnderAttackFrom(int id) {
    Clan clan = ClanTable.getInstance().getClan(id);
    return this._underAttackFrom.contains(clan);
  }

  public void setEnemyClan(Clan clan) {
    this._atWarWith.add(clan);
  }

  public void deleteEnemyClan(Clan clan) {
    this._atWarWith.remove(clan);
  }

  public void setAttackerClan(Clan clan) {
    this._underAttackFrom.add(clan);
  }

  public void deleteAttackerClan(Clan clan) {
    this._underAttackFrom.remove(clan);
  }

  public List<Clan> getEnemyClans() {
    return this._atWarWith;
  }

  public int getWarsCount() {
    return this._atWarWith.size();
  }

  public List<Clan> getAttackerClans() {
    return Collections.unmodifiableList(this._underAttackFrom);
  }

  public void broadcastClanStatus(boolean updateList, boolean needUserInfo, boolean relation) {
    List<L2GameServerPacket> listAll = updateList ? this.listAll() : null;
    PledgeShowInfoUpdate update = new PledgeShowInfoUpdate(this);
    Iterator var6 = this.iterator();

    while(var6.hasNext()) {
      UnitMember member = (UnitMember)var6.next();
      if (member.isOnline()) {
        if (updateList) {
          member.getPlayer().sendPacket(PledgeShowMemberListDeleteAll.STATIC);
          member.getPlayer().sendPacket(listAll);
        }

        member.getPlayer().sendPacket(update);
        if (needUserInfo) {
          member.getPlayer().broadcastCharInfo();
        }

        if (relation) {
          member.getPlayer().broadcastRelationChanged();
        }
      }
    }

  }

  public Alliance getAlliance() {
    return this._allyId == 0 ? null : ClanTable.getInstance().getAlliance(this._allyId);
  }

  public void setExpelledMemberTime(long time) {
    this._expelledMemberTime = time;
  }

  public long getExpelledMemberTime() {
    return this._expelledMemberTime;
  }

  public void setExpelledMember() {
    this._expelledMemberTime = System.currentTimeMillis();
    this.updateClanInDB();
  }

  public void setLeavedAllyTime(long time) {
    this._leavedAllyTime = time;
  }

  public long getLeavedAllyTime() {
    return this._leavedAllyTime;
  }

  public void setLeavedAlly() {
    this._leavedAllyTime = System.currentTimeMillis();
    this.updateClanInDB();
  }

  public void setDissolvedAllyTime(long time) {
    this._dissolvedAllyTime = time;
  }

  public long getDissolvedAllyTime() {
    return this._dissolvedAllyTime;
  }

  public void setDissolvedAlly() {
    this._dissolvedAllyTime = System.currentTimeMillis();
    this.updateClanInDB();
  }

  public boolean canInvite() {
    return System.currentTimeMillis() - this._expelledMemberTime >= Config.EXPELLED_MEMBER_PENALTY;
  }

  public boolean canJoinAlly() {
    return System.currentTimeMillis() - this._leavedAllyTime >= Config.LEAVED_ALLY_PENALTY;
  }

  public boolean canCreateAlly() {
    return System.currentTimeMillis() - this._dissolvedAllyTime >= Config.DISSOLVED_ALLY_PENALTY;
  }

  public boolean canDisband() {
    return System.currentTimeMillis() > this._disbandEndTime;
  }

  public int getRank() {
    Clan[] clans = ClanTable.getInstance().getClans();
    Arrays.sort(clans, REPUTATION_COMPARATOR);
    int place = 1;

    for(int i = 0; i < clans.length; ++i) {
      if (i == 100) {
        return 0;
      }

      Clan clan = clans[i];
      if (clan == this) {
        return place + i;
      }
    }

    return 0;
  }

  public int getReputationScore() {
    return this._reputation;
  }

  private void setReputationScore(int rep) {
    Iterator var2;
    UnitMember member;
    if (this._reputation >= 0 && rep < 0) {
      this.broadcastToOnlineMembers(Msg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED);
      var2 = this.iterator();

      while(var2.hasNext()) {
        member = (UnitMember)var2.next();
        if (member.isOnline() && member.getPlayer() != null) {
          this.disableSkills(member.getPlayer());
        }
      }
    } else if (this._reputation < 0 && rep >= 0) {
      this.broadcastToOnlineMembers(Msg.THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER);
      var2 = this.iterator();

      while(var2.hasNext()) {
        member = (UnitMember)var2.next();
        if (member.isOnline() && member.getPlayer() != null) {
          this.enableSkills(member.getPlayer());
        }
      }
    }

    if (this._reputation != rep) {
      this._reputation = rep;
      this.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
    }

    this.updateClanInDB();
  }

  public int incReputation(int inc, boolean rate, String source) {
    if (this._level < Config.MIN_CLAN_LEVEL_FOR_REPUTATION) {
      return 0;
    } else {
      if (rate && Math.abs(inc) <= Config.RATE_CLAN_REP_SCORE_MAX_AFFECTED) {
        inc = (int)Math.round((double)inc * Config.RATE_CLAN_REP_SCORE);
      }

      this.setReputationScore(this._reputation + inc);
      Log.add(this.getName() + "|" + inc + "|" + this._reputation + "|" + source, "clan_reputation");
      return inc;
    }
  }

  private void restoreSkills() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?");
      statement.setInt(1, this.getClanId());
      rset = statement.executeQuery();

      while(rset.next()) {
        int id = rset.getInt("skill_id");
        int level = rset.getInt("skill_level");
        Skill skill = SkillTable.getInstance().getInfo(id, level);
        this._skills.put(skill.getId(), skill);
      }
    } catch (Exception var10) {
      _log.warn("Could not restore clan skills: " + var10);
      _log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public Collection<Skill> getSkills() {
    return this._skills.values();
  }

  public final Skill[] getAllSkills() {
    return this._reputation < 0 ? Skill.EMPTY_ARRAY : (Skill[])this._skills.values().toArray(new Skill[this._skills.values().size()]);
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
            statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
            statement.setInt(1, newSkill.getLevel());
            statement.setInt(2, oldSkill.getId());
            statement.setInt(3, this.getClanId());
            statement.execute();
          } else {
            statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?)");
            statement.setInt(1, this.getClanId());
            statement.setInt(2, newSkill.getId());
            statement.setInt(3, newSkill.getLevel());
            statement.execute();
          }
        } catch (Exception var11) {
          _log.warn("Error could not store char skills: " + var11);
          _log.error("", var11);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      }

      PledgeSkillListAdd p = new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel());
      PledgeSkillList p2 = new PledgeSkillList(this);
      Iterator var6 = this.iterator();

      while(var6.hasNext()) {
        UnitMember temp = (UnitMember)var6.next();
        if (temp.isOnline()) {
          Player player = temp.getPlayer();
          if (player != null) {
            this.addSkill(player, newSkill);
            player.sendPacket(new IStaticPacket[]{p, p2, new SkillList(player)});
          }
        }
      }
    }

    return oldSkill;
  }

  public void addSkillsQuietly(Player player) {
    Iterator var2 = this._skills.values().iterator();

    while(var2.hasNext()) {
      Skill skill = (Skill)var2.next();
      this.addSkill(player, skill);
    }

    SubUnit subUnit = this.getSubUnit(player.getPledgeType());
    if (subUnit != null) {
      subUnit.addSkillsQuietly(player);
    }

  }

  public void enableSkills(Player player) {
    if (!player.isOlyParticipant()) {
      Iterator var2 = this._skills.values().iterator();

      while(var2.hasNext()) {
        Skill skill = (Skill)var2.next();
        if (skill.getMinPledgeClass() <= player.getPledgeClass()) {
          player.removeUnActiveSkill(skill);
        }
      }

      SubUnit subUnit = this.getSubUnit(player.getPledgeType());
      if (subUnit != null) {
        subUnit.enableSkills(player);
      }

    }
  }

  public void disableSkills(Player player) {
    Iterator var2 = this._skills.values().iterator();

    while(var2.hasNext()) {
      Skill skill = (Skill)var2.next();
      player.addUnActiveSkill(skill);
    }

    SubUnit subUnit = this.getSubUnit(player.getPledgeType());
    if (subUnit != null) {
      subUnit.disableSkills(player);
    }

  }

  private void addSkill(Player player, Skill skill) {
    if (skill.getMinPledgeClass() <= player.getPledgeClass()) {
      player.addSkill(skill, false);
      if (this._reputation < 0 || player.isOlyParticipant()) {
        player.addUnActiveSkill(skill);
      }
    }

  }

  public void removeSkill(int skill) {
    this._skills.remove(skill);
    PledgeSkillListAdd p = new PledgeSkillListAdd(skill, 0);
    Iterator var3 = this.iterator();

    while(var3.hasNext()) {
      UnitMember temp = (UnitMember)var3.next();
      Player player = temp.getPlayer();
      if (player != null && player.isOnline()) {
        player.removeSkillById(skill);
        player.sendPacket(new IStaticPacket[]{p, new SkillList(player)});
      }
    }

  }

  public void broadcastSkillListToOnlineMembers() {
    Iterator var1 = this.iterator();

    while(var1.hasNext()) {
      UnitMember temp = (UnitMember)var1.next();
      Player player = temp.getPlayer();
      if (player != null && player.isOnline()) {
        player.sendPacket(new PledgeSkillList(this));
        player.sendPacket(new SkillList(player));
      }
    }

  }

  public static boolean isAcademy(int pledgeType) {
    return pledgeType == -1;
  }

  public static boolean isRoyalGuard(int pledgeType) {
    return pledgeType == 100 || pledgeType == 200;
  }

  public static boolean isOrderOfKnights(int pledgeType) {
    return pledgeType == 1001 || pledgeType == 1002 || pledgeType == 2001 || pledgeType == 2002;
  }

  public int getAffiliationRank(int pledgeType) {
    if (isAcademy(pledgeType)) {
      return 9;
    } else if (isOrderOfKnights(pledgeType)) {
      return 8;
    } else {
      return isRoyalGuard(pledgeType) ? 7 : 6;
    }
  }

  public final SubUnit getSubUnit(int pledgeType) {
    return (SubUnit)this._subUnits.get(pledgeType);
  }

  public final void addSubUnit(SubUnit sp, boolean updateDb) {
    this._subUnits.put(sp.getType(), sp);
    if (updateDb) {
      this.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(sp));
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)");
        statement.setInt(1, this.getClanId());
        statement.setInt(2, sp.getType());
        statement.setInt(3, sp.getLeaderObjectId());
        statement.setString(4, sp.getName());
        statement.execute();
      } catch (Exception var9) {
        _log.warn("Could not store clan Sub pledges: " + var9);
        _log.error("", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }
    }

  }

  public int createSubPledge(Player player, int pledgeType, UnitMember leader, String name) {
    int temp = pledgeType;
    pledgeType = this.getAvailablePledgeTypes(pledgeType);
    if (pledgeType == -128) {
      if (temp == -1) {
        player.sendPacket(Msg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
      } else {
        player.sendMessage(new CustomMessage("Clan.CantCreateSubUnit", player, new Object[0]));
      }

      return -128;
    } else {
      switch(pledgeType) {
        case -1:
        default:
          break;
        case 100:
        case 200:
          if (this.getReputationScore() < 5000) {
            player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
            return -128;
          }

          this.incReputation(-5000, false, "SubunitCreate");
          break;
        case 1001:
        case 1002:
        case 2001:
        case 2002:
          if (this.getReputationScore() < 10000) {
            player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
            return -128;
          }

          this.incReputation(-10000, false, "SubunitCreate");
      }

      this.addSubUnit(new SubUnit(this, pledgeType, leader, name), true);
      return pledgeType;
    }
  }

  public int getAvailablePledgeTypes(int pledgeType) {
    if (pledgeType == 0) {
      return -128;
    } else {
      if (this._subUnits.get(pledgeType) != null) {
        switch(pledgeType) {
          case -1:
            return -128;
          case 100:
            pledgeType = this.getAvailablePledgeTypes(200);
            break;
          case 200:
            return -128;
          case 1001:
            pledgeType = this.getAvailablePledgeTypes(1002);
            break;
          case 1002:
            pledgeType = this.getAvailablePledgeTypes(2001);
            break;
          case 2001:
            pledgeType = this.getAvailablePledgeTypes(2002);
            break;
          case 2002:
            return -128;
        }
      }

      return pledgeType;
    }
  }

  private void restoreSubPledges() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM clan_subpledges WHERE clan_id=?");
      statement.setInt(1, this.getClanId());
      rset = statement.executeQuery();

      while(rset.next()) {
        int type = rset.getInt("type");
        int leaderId = rset.getInt("leader_id");
        String name = rset.getString("name");
        SubUnit pledge = new SubUnit(this, type, leaderId, name);
        this.addSubUnit(pledge, false);
      }
    } catch (Exception var11) {
      _log.warn("Could not restore clan SubPledges: " + var11, var11);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public int getSubPledgeLimit(int pledgeType) {
    int limit;
    switch(this._level) {
      case 0:
        limit = Config.LIMIT_CLAN_LEVEL0;
        break;
      case 1:
        limit = Config.LIMIT_CLAN_LEVEL1;
        break;
      case 2:
        limit = Config.LIMIT_CLAN_LEVEL2;
        break;
      case 3:
        limit = Config.LIMIT_CLAN_LEVEL3;
        break;
      default:
        limit = Config.LIMIT_CLAN_LEVEL_4_AND_HIGH;
    }

    switch(pledgeType) {
      case -1:
        limit = Config.LIMIT_CLAN_ACADEMY;
        break;
      case 100:
      case 200:
        limit = Config.LIMIT_CLAN_HIGH_UNITS;
        break;
      case 1001:
      case 1002:
      case 2001:
      case 2002:
        limit = Config.LIMIT_CLAN_LOW_UNITS;
    }

    return limit;
  }

  public int getUnitMembersSize(int pledgeType) {
    return pledgeType != -128 && this._subUnits.containsKey(pledgeType) ? this.getSubUnit(pledgeType).size() : 0;
  }

  private void restoreRankPrivs() {
    if (this._privs == null) {
      this.InitializePrivs();
    }

    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT privilleges,rank FROM clan_privs WHERE clan_id=?");
      statement.setInt(1, this.getClanId());
      rset = statement.executeQuery();

      while(rset.next()) {
        int rank = rset.getInt("rank");
        int privileges = rset.getInt("privilleges");
        RankPrivs p = (RankPrivs)this._privs.get(rank);
        if (p != null) {
          p.setPrivs(privileges);
        } else {
          _log.warn("Invalid rank value (" + rank + "), please check clan_privs table");
        }
      }
    } catch (Exception var10) {
      _log.warn("Could not restore clan privs by rank: " + var10);
      _log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public void InitializePrivs() {
    for(int i = 1; i <= 9; ++i) {
      this._privs.put(i, new RankPrivs(i, 0, 0));
    }

  }

  public void updatePrivsForRank(int rank) {
    Iterator var2 = this.iterator();

    while(var2.hasNext()) {
      UnitMember member = (UnitMember)var2.next();
      if (member.isOnline() && member.getPlayer() != null && member.getPlayer().getPowerGrade() == rank && !member.getPlayer().isClanLeader()) {
        member.getPlayer().sendUserInfo();
      }
    }

  }

  public RankPrivs getRankPrivs(int rank) {
    if (rank >= 1 && rank <= 9) {
      if (this._privs.get(rank) == null) {
        _log.warn("Request of rank before init: " + rank);
        Thread.dumpStack();
        this.setRankPrivs(rank, 0);
      }

      return (RankPrivs)this._privs.get(rank);
    } else {
      _log.warn("Requested invalid rank value: " + rank);
      Thread.dumpStack();
      return null;
    }
  }

  public int countMembersByRank(int rank) {
    int ret = 0;
    Iterator var3 = this.iterator();

    while(var3.hasNext()) {
      UnitMember m = (UnitMember)var3.next();
      if (m.getPowerGrade() == rank) {
        ++ret;
      }
    }

    return ret;
  }

  public void setRankPrivs(int rank, int privs) {
    if (rank >= 1 && rank <= 9) {
      if (this._privs.get(rank) != null) {
        ((RankPrivs)this._privs.get(rank)).setPrivs(privs);
      } else {
        this._privs.put(rank, new RankPrivs(rank, this.countMembersByRank(rank), privs));
      }

      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)");
        statement.setInt(1, this.getClanId());
        statement.setInt(2, rank);
        statement.setInt(3, privs);
        statement.execute();
      } catch (Exception var9) {
        _log.warn("Could not store clan privs for rank: " + var9);
        _log.error("", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    } else {
      _log.warn("Requested set of invalid rank value: " + rank);
      Thread.dumpStack();
    }
  }

  public final RankPrivs[] getAllRankPrivs() {
    return this._privs == null ? new RankPrivs[0] : (RankPrivs[])this._privs.values().toArray(new RankPrivs[this._privs.values().size()]);
  }

  public int getWhBonus() {
    return this._whBonus;
  }

  public void setWhBonus(int i) {
    if (this._whBonus != -1) {
      mysql.set("UPDATE `clan_data` SET `warehouse`=? WHERE `clan_id`=?", new Object[]{i, this.getClanId()});
    }

    this._whBonus = i;
  }

  public final Collection<SubUnit> getAllSubUnits() {
    return this._subUnits.values();
  }

  public List<L2GameServerPacket> listAll() {
    List<L2GameServerPacket> p = new ArrayList(this._subUnits.size());
    Iterator var2 = this.getAllSubUnits().iterator();

    while(var2.hasNext()) {
      SubUnit unit = (SubUnit)var2.next();
      p.add(new PledgeShowMemberListAll(this, unit));
    }

    return p;
  }

  public String getNotice() {
    return this._notice;
  }

  public void setNotice(String notice) {
    this._notice = notice;
  }

  public int getSkillLevel(int id, int def) {
    Skill skill = (Skill)this._skills.get(id);
    return skill == null ? def : skill.getLevel();
  }

  public int getSkillLevel(int id) {
    return this.getSkillLevel(id, -1);
  }

  public Iterator<UnitMember> iterator() {
    List<Iterator<UnitMember>> iterators = new ArrayList(this._subUnits.size());
    Iterator var2 = this._subUnits.values().iterator();

    while(var2.hasNext()) {
      SubUnit subUnit = (SubUnit)var2.next();
      iterators.add(subUnit.getUnitMembers().iterator());
    }

    return new JoinedIterator(iterators);
  }

  public boolean isPlacedForDisband() {
    return this._disbandEndTime != 0L;
  }

  public void placeForDisband() {
    this._disbandEndTime = System.currentTimeMillis() + Config.CLAN_DISBAND_TIME;
    this.updateClanInDB();
  }

  public void unPlaceDisband() {
    this._disbandEndTime = 0L;
    this._disbandPenaltyTime = System.currentTimeMillis() + Config.CLAN_DISBAND_PENALTY;
    this.updateClanInDB();
  }

  public long getDisbandEndTime() {
    return this._disbandEndTime;
  }

  public void setDisbandEndTime(long disbandEndTime) {
    this._disbandEndTime = disbandEndTime;
  }

  public long getDisbandPenaltyTime() {
    return this._disbandPenaltyTime;
  }

  public void setDisbandPenaltyTime(long disbandPenaltyTime) {
    this._disbandPenaltyTime = disbandPenaltyTime;
  }

  private static class ClanReputationComparator implements Comparator<Clan> {
    private ClanReputationComparator() {
    }

    public int compare(Clan o1, Clan o2) {
      return o1 != null && o2 != null ? o2.getReputationScore() - o1.getReputationScore() : 0;
    }
  }
}
