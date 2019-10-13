//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoblesController {
  private static final Logger _log = LoggerFactory.getLogger(NoblesController.class);
  private static NoblesController _instance;
  private static final String GET_ALL_NOBLES = "SELECT `char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done` FROM `oly_nobles`";
  private static final String SAVE_NOBLE = "REPLACE INTO `oly_nobles`(`char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String REMOVE_NOBLE = "DELETE FROM `oly_nobles` WHERE `char_id` = ?";
  private List<NoblesController.NobleRecord> _nobleses = new CopyOnWriteArrayList();
  private static final NoblesController.NobleRecordCmp NRCmp = new NoblesController.NobleRecordCmp();

  public static final NoblesController getInstance() {
    if (_instance == null) {
      _instance = new NoblesController();
    }

    return _instance;
  }

  private NoblesController() {
    this.LoadNobleses();
    this.ComputeRanks();
  }

  public boolean isNobles(int object_id) {
    return null != this.getNobleRecord(object_id);
  }

  public boolean isNobles(Player player) {
    return this.getNobleRecord(player.getObjectId()) != null;
  }

  public NoblesController.NobleRecord getNobleRecord(int object_id) {
    Iterator var2 = this._nobleses.iterator();

    NoblesController.NobleRecord nr;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      nr = (NoblesController.NobleRecord)var2.next();
    } while(nr == null || nr.char_id != object_id);

    return nr;
  }

  public void renameNoble(int object_id, String newname) {
    Iterator var3 = this._nobleses.iterator();

    NoblesController.NobleRecord nr;
    do {
      if (!var3.hasNext()) {
        return;
      }

      nr = (NoblesController.NobleRecord)var3.next();
    } while(nr == null || nr.char_id != object_id);

    nr.char_name = newname;
    this.SaveNobleRecord(nr);
  }

  protected Collection<NoblesController.NobleRecord> getNoblesRecords() {
    return this._nobleses;
  }

  public int getPointsOf(int object_id) {
    Iterator var2 = this._nobleses.iterator();

    NoblesController.NobleRecord nr;
    do {
      if (!var2.hasNext()) {
        return -1;
      }

      nr = (NoblesController.NobleRecord)var2.next();
    } while(nr == null || nr.char_id != object_id);

    return nr.points_current;
  }

  public void setPointsOf(int object_id, int points) {
    Iterator var3 = this._nobleses.iterator();

    while(var3.hasNext()) {
      NoblesController.NobleRecord nr = (NoblesController.NobleRecord)var3.next();
      if (nr != null && nr.char_id == object_id) {
        nr.points_current = points;
        this.SaveNobleRecord(nr);
      }
    }

  }

  public synchronized void removeNoble(Player noble) {
    NoblesController.NobleRecord nobleRecord = null;
    Iterator var3 = this._nobleses.iterator();

    while(var3.hasNext()) {
      NoblesController.NobleRecord nobleRecord2 = (NoblesController.NobleRecord)var3.next();
      if (nobleRecord2.char_id == noble.getObjectId()) {
        nobleRecord = nobleRecord2;
      }
    }

    if (nobleRecord != null) {
      Connection conn = null;
      PreparedStatement pstmt = null;
      Object rset = null;

      try {
        conn = DatabaseFactory.getInstance().getConnection();
        pstmt = conn.prepareStatement("DELETE FROM `oly_nobles` WHERE `char_id` = ?");
        pstmt.setInt(1, nobleRecord.char_id);
        pstmt.executeUpdate();
        this._nobleses.remove(nobleRecord);
      } catch (SQLException var10) {
        _log.warn("NoblesController: Can't remove nobleses ", var10);
      } finally {
        DbUtils.closeQuietly(conn, pstmt, (ResultSet)rset);
      }

    }
  }

  public synchronized void addNoble(Player noble) {
    synchronized(this._nobleses) {
      NoblesController.NobleRecord nr = null;
      Iterator var4 = this._nobleses.iterator();

      while(var4.hasNext()) {
        NoblesController.NobleRecord nr2 = (NoblesController.NobleRecord)var4.next();
        if (nr2.char_id == noble.getObjectId()) {
          nr = nr2;
        }
      }

      this._nobleses.remove(nr);
      if (nr == null) {
        int classId = noble.getBaseClassId();
        if (classId < 88) {
          ClassId[] var12 = ClassId.values();
          int var6 = var12.length;

          for(int var7 = 0; var7 < var6; ++var7) {
            ClassId id = var12[var7];
            if (id.level() == 3 && id.getParent(0).getId() == classId) {
              classId = id.getId();
              break;
            }
          }
        }

        nr = new NoblesController.NobleRecord(noble.getObjectId(), classId, noble.getName(), Config.OLY_SEASON_START_POINTS, 0, 0, 0, 0, 0, 0, 0, 0);
      }

      if (!noble.getName().equals(nr.char_name)) {
        nr.char_name = noble.getName();
      }

      if (noble.getBaseClassId() != nr.class_id) {
        _log.warn("OlympiadController: " + noble.getName() + " got base class " + noble.getBaseClassId() + " but " + nr.class_id + " class nobless");
        nr.class_id = nr.class_id;
      }

      this._nobleses.add(nr);
      this.setNobleRecord(nr);
    }
  }

  private void setNobleRecord(NoblesController.NobleRecord noble) {
    this.SaveNobleRecord(noble);
  }

  public void LoadNobleses() {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rset = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      stmt = conn.createStatement();
      rset = stmt.executeQuery("SELECT `char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done` FROM `oly_nobles`");

      while(rset.next()) {
        NoblesController.NobleRecord nr = new NoblesController.NobleRecord(rset.getInt("char_id"), rset.getInt("class_id"), rset.getString("char_name"), rset.getInt("points_current"), rset.getInt("points_past"), rset.getInt("points_pre_past"), rset.getInt("class_free_cnt"), rset.getInt("class_based_cnt"), rset.getInt("team_cnt"), rset.getInt("comp_win"), rset.getInt("comp_loose"), rset.getInt("comp_done"));
        this._nobleses.add(nr);
      }
    } catch (SQLException var8) {
      _log.warn("NoblesController: Can't load nobleses ", var8);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rset);
    }

    _log.info("NoblesController: loaded " + this._nobleses.size() + " nobleses.");
  }

  public void SaveNobleses() {
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      pstmt = con.prepareStatement("REPLACE INTO `oly_nobles`(`char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
      Iterator var3 = this._nobleses.iterator();

      while(var3.hasNext()) {
        NoblesController.NobleRecord noble = (NoblesController.NobleRecord)var3.next();
        pstmt.setInt(1, noble.char_id);
        pstmt.setInt(2, noble.class_id);
        pstmt.setString(3, noble.char_name);
        pstmt.setInt(4, noble.points_current);
        pstmt.setInt(5, noble.points_past);
        pstmt.setInt(6, noble.points_pre_past);
        pstmt.setInt(7, noble.class_free_cnt);
        pstmt.setInt(8, noble.class_based_cnt);
        pstmt.setInt(9, noble.team_cnt);
        pstmt.setInt(10, noble.comp_win);
        pstmt.setInt(11, noble.comp_loose);
        pstmt.setInt(12, noble.comp_done);
        pstmt.executeUpdate();
      }
    } catch (Exception var8) {
      _log.warn("Oly: can't save nobleses : ", var8);
      var8.printStackTrace();
    } finally {
      DbUtils.closeQuietly(con, pstmt);
    }

  }

  public void SaveNobleRecord(NoblesController.NobleRecord noble) {
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      pstmt = con.prepareStatement("REPLACE INTO `oly_nobles`(`char_id`,`class_id`,`char_name`,`points_current`,`points_past`,`points_pre_past`,`class_free_cnt`,`class_based_cnt`,`team_cnt`,`comp_win`,`comp_loose`,`comp_done`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
      pstmt.setInt(1, noble.char_id);
      pstmt.setInt(2, noble.class_id);
      pstmt.setString(3, noble.char_name);
      pstmt.setInt(4, noble.points_current);
      pstmt.setInt(5, noble.points_past);
      pstmt.setInt(6, noble.points_pre_past);
      pstmt.setInt(7, noble.class_free_cnt);
      pstmt.setInt(8, noble.class_based_cnt);
      pstmt.setInt(9, noble.team_cnt);
      pstmt.setInt(10, noble.comp_win);
      pstmt.setInt(11, noble.comp_loose);
      pstmt.setInt(12, noble.comp_done);
      pstmt.executeUpdate();
    } catch (Exception var8) {
      _log.warn("Oly: can't save noble " + noble.char_name, var8);
      var8.printStackTrace();
    } finally {
      DbUtils.closeQuietly(con, pstmt);
    }

  }

  public void TransactNewSeason() {
    _log.info("NoblesController: Cleanuping last period.");

    NoblesController.NobleRecord nr;
    for(Iterator var1 = this._nobleses.iterator(); var1.hasNext(); nr.points_current = Config.OLY_DEFAULT_POINTS) {
      nr = (NoblesController.NobleRecord)var1.next();
      Log.add(String.format("NoblesController: %s(%d) new season clean. points_current=%d|points_past=%d|points_pre_past=%d|comp_done=%d", nr.char_name, nr.char_id, nr.points_current, nr.points_past, nr.points_pre_past, nr.comp_done), "olympiad");
      if (nr.comp_done >= Config.OLY_MIN_NOBLE_COMPS) {
        nr.points_past = nr.points_current;
        nr.points_pre_past = nr.points_current;
      } else {
        nr.points_past = 0;
        nr.points_pre_past = 0;
      }

      nr.comp_done = 0;
      nr.comp_win = 0;
      nr.comp_loose = 0;
      nr.class_based_cnt = 0;
      nr.class_free_cnt = 0;
      nr.team_cnt = 0;
    }

    this.SaveNobleses();
  }

  public void AddWeaklyBonus() {
    NoblesController.NobleRecord nr;
    for(Iterator var1 = this._nobleses.iterator(); var1.hasNext(); nr.team_cnt = 0) {
      nr = (NoblesController.NobleRecord)var1.next();
      nr.points_current += Config.OLY_WBONUS_POINTS;
      nr.class_based_cnt = 0;
      nr.class_free_cnt = 0;
    }

    this.SaveNobleses();
  }

  public String[] getClassLeaders(int cid) {
    ArrayList<NoblesController.NobleRecord> tmp = new ArrayList();
    ArrayList<String> result = new ArrayList();
    Iterator var4 = this._nobleses.iterator();

    while(var4.hasNext()) {
      NoblesController.NobleRecord nr = (NoblesController.NobleRecord)var4.next();
      if (nr.class_id == cid && nr.points_pre_past > 0) {
        tmp.add(nr);
      }
    }

    NoblesController.NobleRecord[] leader = (NoblesController.NobleRecord[])tmp.toArray(new NoblesController.NobleRecord[tmp.size()]);
    Arrays.sort(leader, NRCmp);

    for(int i = 0; i < leader.length && i < 15; ++i) {
      if (leader[i] != null) {
        result.add(leader[i].char_name);
      }
    }

    return (String[])result.toArray(new String[result.size()]);
  }

  public int getPlayerClassRank(int cid, int playerId) {
    ArrayList<NoblesController.NobleRecord> tmp = new ArrayList();
    Iterator var4 = this._nobleses.iterator();

    while(var4.hasNext()) {
      NoblesController.NobleRecord nr = (NoblesController.NobleRecord)var4.next();
      if (nr.class_id == cid) {
        tmp.add(nr);
      }
    }

    Collections.sort(tmp, NRCmp);

    for(int i = 0; i < tmp.size(); ++i) {
      if (tmp.get(i) != null && ((NoblesController.NobleRecord)tmp.get(i)).char_id == playerId) {
        return i;
      }
    }

    return -1;
  }

  public synchronized void ComputeRanks() {
    _log.info("NoblesController: Computing ranks.");
    NoblesController.NobleRecord[] rank_nobleses = (NoblesController.NobleRecord[])this._nobleses.toArray(new NoblesController.NobleRecord[this._nobleses.size()]);
    Arrays.sort(rank_nobleses, NRCmp);
    int rank0 = (int)Math.round((double)rank_nobleses.length * 0.01D);
    int rank1 = (int)Math.round((double)rank_nobleses.length * 0.1D);
    int rank2 = (int)Math.round((double)rank_nobleses.length * 0.25D);
    int rank3 = (int)Math.round((double)rank_nobleses.length * 0.5D);
    if (rank0 == 0) {
      rank0 = 1;
      ++rank1;
      ++rank2;
      ++rank3;
    }

    int i;
    for(i = 0; i <= rank0 && i < rank_nobleses.length; ++i) {
      rank_nobleses[i].rank = 0;
    }

    while(i <= rank1 && i < rank_nobleses.length) {
      rank_nobleses[i].rank = 1;
      ++i;
    }

    while(i <= rank2 && i < rank_nobleses.length) {
      rank_nobleses[i].rank = 2;
      ++i;
    }

    while(i <= rank3 && i < rank_nobleses.length) {
      rank_nobleses[i].rank = 3;
      ++i;
    }

    while(i < rank_nobleses.length) {
      rank_nobleses[i].rank = 4;
      ++i;
    }

  }

  public synchronized int getNoblessePasses(Player player) {
    int coid = player.getObjectId();
    NoblesController.NobleRecord nr = getInstance().getNobleRecord(coid);
    if (nr == null) {
      return 0;
    } else if (nr.points_past == 0) {
      return 0;
    } else {
      int points = 0;
      if (nr.rank >= 0 && nr.rank < Config.OLY_POINTS_SETTLEMENT.length) {
        points = Config.OLY_POINTS_SETTLEMENT[nr.rank];
      }

      if (HeroController.getInstance().isCurrentHero(coid) || HeroController.getInstance().isInactiveHero(coid)) {
        points += Config.OLY_HERO_POINT_BONUS;
      }

      nr.points_past = 0;
      getInstance().SaveNobleRecord(nr);
      return points * Config.OLY_ITEMS_SETTLEMENT_PER_POINT;
    }
  }

  private static class NobleRecordCmp implements Comparator<NoblesController.NobleRecord> {
    private NobleRecordCmp() {
    }

    public int compare(NoblesController.NobleRecord o1, NoblesController.NobleRecord o2) {
      if (o1 == null && o2 == null) {
        return 0;
      } else if (o1 == null && o2 != null) {
        return 1;
      } else {
        return o2 == null && o1 != null ? -1 : o2.points_pre_past - o1.points_pre_past;
      }
    }
  }

  public class NobleRecord {
    public int char_id;
    public int class_id;
    public String char_name;
    public int points_current;
    public int points_past;
    public int points_pre_past;
    public int class_free_cnt;
    public int class_based_cnt;
    public int team_cnt;
    public int comp_win;
    public int comp_loose;
    public int comp_done;
    public int rank;

    private NobleRecord(int _char_id, int _class_id, String _char_name, int _points_current, int _points_past, int _points_pre_past, int _class_free_cnt, int _class_based_cnt, int _team_cnt, int _comp_win, int _comp_loose, int _comp_done) {
      this.char_id = _char_id;
      this.class_id = _class_id;
      this.char_name = _char_name;
      this.points_current = _points_current;
      this.points_past = _points_past;
      this.points_pre_past = _points_pre_past;
      this.class_free_cnt = _class_free_cnt;
      this.class_based_cnt = _class_based_cnt;
      this.team_cnt = _team_cnt;
      this.comp_win = _comp_win;
      this.comp_loose = _comp_loose;
      this.comp_done = _comp_done;
      this.rank = 0;
    }
  }
}
