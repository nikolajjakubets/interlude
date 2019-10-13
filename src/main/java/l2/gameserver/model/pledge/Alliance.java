//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.cache.CrestCache;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alliance {
  private static final Logger _log = LoggerFactory.getLogger(Alliance.class);
  private String _allyName;
  private int _allyId;
  private Clan _leader = null;
  private Map<Integer, Clan> _members = new ConcurrentHashMap();
  private int _allyCrestId;
  private long _expelledMemberTime;
  public static long EXPELLED_MEMBER_PENALTY = 86400000L;

  public Alliance(int allyId) {
    this._allyId = allyId;
    this.restore();
  }

  public Alliance(int allyId, String allyName, Clan leader) {
    this._allyId = allyId;
    this._allyName = allyName;
    this.setLeader(leader);
  }

  public int getLeaderId() {
    return this._leader != null ? this._leader.getClanId() : 0;
  }

  public Clan getLeader() {
    return this._leader;
  }

  public void setLeader(Clan leader) {
    this._leader = leader;
    this._members.put(leader.getClanId(), leader);
  }

  public String getAllyLeaderName() {
    return this._leader != null ? this._leader.getLeaderName() : "";
  }

  public void addAllyMember(Clan member, boolean storeInDb) {
    this._members.put(member.getClanId(), member);
    if (storeInDb) {
      this.storeNewMemberInDatabase(member);
    }

  }

  public Clan getAllyMember(int id) {
    return (Clan)this._members.get(id);
  }

  public void removeAllyMember(int id) {
    if (this._leader == null || this._leader.getClanId() != id) {
      Clan exMember = (Clan)this._members.remove(id);
      if (exMember == null) {
        _log.warn("Clan " + id + " not found in alliance while trying to remove");
      } else {
        this.removeMemberInDatabase(exMember);
      }
    }
  }

  public Clan[] getMembers() {
    return (Clan[])this._members.values().toArray(new Clan[this._members.size()]);
  }

  public int getMembersCount() {
    return this._members.size();
  }

  public int getAllyId() {
    return this._allyId;
  }

  public String getAllyName() {
    return this._allyName;
  }

  public void setAllyCrestId(int allyCrestId) {
    this._allyCrestId = allyCrestId;
  }

  public int getAllyCrestId() {
    return this._allyCrestId;
  }

  public void setAllyId(int allyId) {
    this._allyId = allyId;
  }

  public void setAllyName(String allyName) {
    this._allyName = allyName;
  }

  public boolean isMember(int id) {
    return this._members.containsKey(id);
  }

  public void setExpelledMemberTime(long time) {
    this._expelledMemberTime = time;
  }

  public long getExpelledMemberTime() {
    return this._expelledMemberTime;
  }

  public void setExpelledMember() {
    this._expelledMemberTime = System.currentTimeMillis();
    this.updateAllyInDB();
  }

  public boolean canInvite() {
    return System.currentTimeMillis() - this._expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
  }

  public void updateAllyInDB() {
    if (this.getLeaderId() == 0) {
      _log.warn("updateAllyInDB with empty LeaderId");
      Thread.dumpStack();
    } else if (this.getAllyId() == 0) {
      _log.warn("updateAllyInDB with empty AllyId");
      Thread.dumpStack();
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE ally_data SET leader_id=?,expelled_member=? WHERE ally_id=?");
        statement.setInt(1, this.getLeaderId());
        statement.setLong(2, this.getExpelledMemberTime() / 1000L);
        statement.setInt(3, this.getAllyId());
        statement.execute();
      } catch (Exception var7) {
        _log.warn("error while updating ally '" + this.getAllyId() + "' data in db: " + var7);
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
      statement = con.prepareStatement("INSERT INTO ally_data (ally_id,ally_name,leader_id) values (?,?,?)");
      statement.setInt(1, this.getAllyId());
      statement.setString(2, this.getAllyName());
      statement.setInt(3, this.getLeaderId());
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
      statement.setInt(1, this.getAllyId());
      statement.setInt(2, this.getLeaderId());
      statement.execute();
    } catch (Exception var7) {
      _log.warn("error while saving new ally to db " + var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private void storeNewMemberInDatabase(Clan member) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
      statement.setInt(1, this.getAllyId());
      statement.setInt(2, member.getClanId());
      statement.execute();
    } catch (Exception var8) {
      _log.warn("error while saving new alliance member to db " + var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private void removeMemberInDatabase(Clan member) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE clan_id=?");
      statement.setInt(1, member.getClanId());
      statement.execute();
    } catch (Exception var8) {
      _log.warn("error while removing ally member in db " + var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private void restore() {
    if (this.getAllyId() != 0) {
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("SELECT ally_name,leader_id FROM ally_data where ally_id=?");
        statement.setInt(1, this.getAllyId());
        rset = statement.executeQuery();
        if (rset.next()) {
          this.setAllyName(rset.getString("ally_name"));
          int leaderId = rset.getInt("leader_id");
          DbUtils.close(statement, rset);
          statement = con.prepareStatement("SELECT clan_id FROM clan_data WHERE ally_id=?");
          statement.setInt(1, this.getAllyId());
          rset = statement.executeQuery();

          while(rset.next()) {
            Clan member = ClanTable.getInstance().getClan(rset.getInt("clan_id"));
            if (member != null) {
              if (member.getClanId() == leaderId) {
                this.setLeader(member);
              } else {
                this.addAllyMember(member, false);
              }
            }
          }
        }

        this.setAllyCrestId(CrestCache.getInstance().getAllyCrestId(this.getAllyId()));
      } catch (Exception var9) {
        _log.warn("error while restoring ally");
        _log.error("", var9);
      } finally {
        DbUtils.closeQuietly(con, statement, rset);
      }

    }
  }

  public void broadcastToOnlineMembers(L2GameServerPacket packet) {
    Iterator var2 = this._members.values().iterator();

    while(var2.hasNext()) {
      Clan member = (Clan)var2.next();
      if (member != null) {
        member.broadcastToOnlineMembers(new L2GameServerPacket[]{packet});
      }
    }

  }

  public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player) {
    Iterator var3 = this._members.values().iterator();

    while(var3.hasNext()) {
      Clan member = (Clan)var3.next();
      if (member != null) {
        member.broadcastToOtherOnlineMembers(packet, player);
      }
    }

  }

  public String toString() {
    return this.getAllyName();
  }

  public boolean hasAllyCrest() {
    return this._allyCrestId > 0;
  }

  public void broadcastAllyStatus() {
    Clan[] var1 = this.getMembers();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Clan member = var1[var3];
      member.broadcastClanStatus(false, true, false);
    }

  }
}
