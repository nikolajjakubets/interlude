//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.Config.OlySeasonTimeCalcMode;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlyController {
  private static final Logger _log = LoggerFactory.getLogger(OlyController.class);
  private static final SimpleDateFormat _dtformat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
  private static OlyController _instance;
  private int _season_id;
  private boolean _season_calculation = false;
  private boolean _is_comp_active = false;
  private ScheduledFuture<?> _season_start_task;
  private ScheduledFuture<?> _season_end_task;
  private ScheduledFuture<?> _nominate_task;
  private ScheduledFuture<?>[][] _comps_start_tasks;
  private ScheduledFuture<?>[] _bonus_tasks;
  private long _season_start_time;
  private long _season_end_time;
  private long _nominate_time;
  private long[] _bonus_time = new long[4];
  private int _bonus_idx;
  private long[][] _comps_time = new long[31][];
  private int _comp_idx;
  private int _part_count;
  private static final String VAR_SEASON_ID = "oly_season_id";
  private static final String VAR_SEASON_CALC = "oly_season_calc";
  private static final String SQL_LOAD_SEASON_TIME = "SELECT `season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30` FROM `oly_season` WHERE `season_id` = ?";
  private static final String SQL_SAVE_SEASON_TIME = "REPLACE INTO `oly_season`(`season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String PART_CNT_VAR = "@OlyPartCnt";
  private static final String OLY_HERO_SEASON_VAR = "oly_chero_season";
  private int _active_comp_idx = -1;

  public static final OlyController getInstance() {
    if (_instance == null) {
      _instance = new OlyController();
    }

    return _instance;
  }

  private OlyController() {
    this._comps_start_tasks = new ScheduledFuture[this._comps_time.length][];
    this._bonus_tasks = new ScheduledFuture[this._bonus_time.length];
    this.load();
    this.schedule();
  }

  public synchronized void load() {
    this._season_id = ServerVariables.getInt("oly_season_id", 0);
    this._season_calculation = ServerVariables.getBool("oly_season_calc", false);
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("SELECT `season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30` FROM `oly_season` WHERE `season_id` = ?");
      pstmt.setInt(1, this._season_id);
      rset = pstmt.executeQuery();
      if (rset.next()) {
        this._season_start_time = rset.getLong("season_start_time");
        this._season_end_time = rset.getLong("season_end_time");
        this._nominate_time = rset.getLong("nominate_start");
        this._bonus_idx = rset.getInt("b_idx");
        this._bonus_time[0] = rset.getLong("b_s0");
        this._bonus_time[1] = rset.getLong("b_s1");
        this._bonus_time[2] = rset.getLong("b_s2");
        this._bonus_time[3] = rset.getLong("b_s3");
        this._comp_idx = rset.getInt("c_idx");

        for(int i = 0; i < this._comps_time.length; ++i) {
          long[] comp_time = new long[]{rset.getLong("c_s" + i), rset.getLong("c_e" + i)};
          this._comps_time[i] = comp_time;
        }
      } else {
        _log.info("Oly: Generating a new season " + this._season_id);
        this.calcNewSeason();
        this.save();
      }
    } catch (Exception var9) {
      var9.printStackTrace();
    } finally {
      DbUtils.closeQuietly(conn, pstmt, rset);
    }

    this._part_count = ServerVariables.getInt("@OlyPartCnt", 0);
  }

  public synchronized void save() {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("REPLACE INTO `oly_season`(`season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      pstmt.setInt(1, this._season_id);
      pstmt.setLong(2, this._season_start_time);
      pstmt.setLong(3, this._season_end_time);
      pstmt.setLong(4, this._nominate_time);
      pstmt.setInt(5, this._bonus_idx);

      int j;
      for(j = 0; j < this._bonus_time.length; ++j) {
        pstmt.setLong(6 + j, this._bonus_time[j]);
      }

      pstmt.setInt(10, this._comp_idx);

      for(j = 0; j < this._comps_time.length; ++j) {
        pstmt.setLong(11 + j * 2, this._comps_time[j][0]);
        pstmt.setLong(12 + j * 2, this._comps_time[j][1]);
      }

      pstmt.executeUpdate();
    } catch (Exception var7) {
      var7.printStackTrace();
    } finally {
      DbUtils.closeQuietly(conn, pstmt);
    }

    ServerVariables.set("oly_season_id", this._season_id);
    ServerVariables.set("oly_season_calc", this._season_calculation);
    ServerVariables.set("@OlyPartCnt", this._part_count);
  }

  private void schedule() {
    long now = System.currentTimeMillis() / 1000L;
    int curr_season = this._season_id;
    long seasonStartRemaining = Math.max(0L, this._season_start_time - now);
    if (seasonStartRemaining == 0L) {
      this._season_start_task = null;
      this.SeasonStart(curr_season);
    } else {
      this._season_start_task = ThreadPoolManager.getInstance().schedule(new OlyController.SeasonStartTask(curr_season), seasonStartRemaining * 1000L);
      _log.info("OlyController: Season " + curr_season + " start schedule at " + ScheduledFutureTime(this._season_start_task));
    }

    long seasonEndRemaining = Math.max(0L, this._season_end_time - now);
    if (seasonEndRemaining == 0L) {
      this._season_end_task = null;
      this.SeasonEnd(curr_season);
    } else {
      this._season_end_task = ThreadPoolManager.getInstance().schedule(new OlyController.SeasonEndTask(curr_season), seasonEndRemaining * 1000L);
      _log.info("OlyController: Season " + curr_season + " end schedule at " + ScheduledFutureTime(this._season_end_task));
    }

    long seasonNominateRemaining = Math.max(0L, this._nominate_time - now);
    if (seasonNominateRemaining == 0L) {
      this._nominate_task = null;
      this.Nomination(curr_season);
    } else {
      this._nominate_task = ThreadPoolManager.getInstance().schedule(new OlyController.NominationTask(curr_season), seasonNominateRemaining * 1000L);
      _log.info("OlyController: Season " + curr_season + " nomination schedule at " + ScheduledFutureTime(this._nominate_task));
    }

    StringBuilder sb = new StringBuilder();

    int j;
    for(j = this._comp_idx; j < this._comps_time.length; ++j) {
      if (this._comps_time[j] != null && (this._comps_time[j][0] >= now || this._comps_time[j][1] >= now)) {
        if (j != this._comp_idx) {
          sb.append(';');
        }

        this._comps_start_tasks[j] = new ScheduledFuture[]{ThreadPoolManager.getInstance().schedule(new OlyController.CompetitionStartTask(curr_season, j), Math.max(60L, this._comps_time[j][0] - now) * 1000L), ThreadPoolManager.getInstance().schedule(new OlyController.CompetitionEndTask(curr_season, j), Math.max(60L, this._comps_time[j][1] - now) * 1000L)};
        sb.append(ScheduledFutureTime(this._comps_start_tasks[j][0]) + "-" + ScheduledFutureTime(this._comps_start_tasks[j][1]));
      }
    }

    _log.info("OlyController: Season " + curr_season + " competitions schedule at [" + sb.toString() + "]");
    sb.delete(0, sb.length());

    for(j = this._bonus_idx; j < this._bonus_time.length; ++j) {
      if (this._bonus_time[j] > now) {
        if (j != this._bonus_idx) {
          sb.append(';');
        }

        this._bonus_tasks[j] = ThreadPoolManager.getInstance().schedule(new OlyController.BonusTask(curr_season, j), Math.max((long)((3 + j - this._bonus_idx) * 60), this._bonus_time[j] - now) * 1000L);
        sb.append(ScheduledFutureTime(this._bonus_tasks[j]));
      }
    }

    _log.info("OlyController: Season " + curr_season + " bonuses schedule at [" + sb.toString() + "]");
    sb = null;
  }

  private synchronized void SeasonStart(int season_id) {
    try {
      this._season_calculation = false;
      Announcements.getInstance().announceToAll((new SystemMessage(1639)).addNumber(season_id));
      _log.info("OlyController: Season " + season_id + " started.");
    } catch (Exception var3) {
      _log.warn("Exception while starting of " + season_id + " season", var3);
    }

  }

  private synchronized void SeasonEnd(int season_id) {
    try {
      if (!this._season_calculation) {
        this._season_calculation = true;
        if (ServerVariables.getInt("oly_chero_season", -1) != season_id) {
          _log.info("OlyController: calculation heroes for " + season_id + " season");
          HeroController.getInstance().ComputeNewHeroNobleses();
          ServerVariables.set("oly_chero_season", season_id);
        }

        this.save();
        Announcements.getInstance().announceToAll((new SystemMessage(1640)).addNumber(season_id));
      } else {
        _log.warn("OlyController: Unexpected season calculated. Canceling computation.");
      }

      _log.info("OlyController: Season " + season_id + " ended.");
    } catch (Exception var3) {
      _log.warn("Exception while ending of " + season_id + " season", var3);
    }

  }

  private synchronized void Nomination(int season_id) {
    try {
      if (this._season_calculation) {
        this._season_calculation = false;
        this.save();
      } else {
        _log.warn("OlyController: Season not calculated. Run calculation manualy.");
      }

      _log.info("OlyController: Season " + season_id + " nomination started.");
      ThreadPoolManager.getInstance().execute(new OlyController.NewSeasonCalcTask(season_id + 1));
    } catch (Exception var3) {
      _log.warn("Exception while nominating in " + season_id + " season", var3);
    }

  }

  private synchronized void CompetitionStart(int season_id, int comp_id) {
    try {
      if (!this._is_comp_active) {
        this._is_comp_active = true;
        StadiumPool.getInstance().AllocateStadiums();
        ParticipantPool.getInstance().AllocatePools();
        CompetitionController.getInstance();
        CompetitionController.getInstance().scheduleStartTask();
        Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
        _log.info("OlyController: Season " + season_id + " comp " + comp_id + " started.");
        this._active_comp_idx = comp_id;
      } else {
        _log.warn("OlyController: Can't start new competitions. Old comps in progress.");
      }
    } catch (Exception var4) {
      _log.warn("Exception while start comp " + comp_id + " in " + season_id + " season", var4);
    }

  }

  private synchronized void CompetitionEnd(int season_id, int comp_id) {
    try {
      if (this._is_comp_active) {
        CompetitionController.getInstance().cancelStartTask();
        this._is_comp_active = false;
        StadiumPool.getInstance().FreeStadiums();
        ParticipantPool.getInstance().FreePools();
        this._active_comp_idx = -1;
        ++this._comp_idx;
        Announcements.getInstance().announceToAll(new SystemMessage(1919));
        Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
        _log.info("OlyController: Season " + season_id + " comp " + comp_id + " ended.");
        this.save();
      } else {
        _log.warn("OlyController: Can't stop competitions. Competitions not in progress.");
      }
    } catch (Exception var4) {
      _log.warn("Exception while end comp " + comp_id + " in " + season_id + " season", var4);
    }

  }

  private synchronized void Bonus(int season_id, int bonus_id) {
    try {
      NoblesController.getInstance().AddWeaklyBonus();
      ++this._bonus_idx;
      _log.info("OlyController: Season " + season_id + " bonus " + bonus_id + " applied.");
      this.save();
    } catch (Exception var4) {
      _log.warn("Exception while bonus " + bonus_id + " in " + season_id + " season", var4);
    }

  }

  private synchronized void NewSeasonCalc(int season_id) {
    try {
      this.save();
      this._season_id = season_id;
      if (Config.OLY_RECALC_NEW_SEASON) {
        this.calcNewSeason();
        this.save();
      } else {
        ServerVariables.set("oly_season_id", this._season_id);
        this.load();
      }

      this.schedule();
    } catch (Exception var3) {
      _log.warn("Exception while calculating new " + season_id + " season", var3);
    }

  }

  public boolean isCompetitionsActive() {
    return this._is_comp_active;
  }

  public boolean isRegAllowed() {
    if (this._is_comp_active && this._active_comp_idx >= 0) {
      return System.currentTimeMillis() < (this._comps_time[this._active_comp_idx][1] - 300L) * 1000L;
    } else {
      return this._is_comp_active;
    }
  }

  public boolean isCalculationPeriod() {
    return this._season_calculation;
  }

  public int getCurrentSeason() {
    return this._season_id;
  }

  public int getCurrentPeriod() {
    return this._bonus_idx;
  }

  public void shutdown() {
    if (this._is_comp_active) {
      CompetitionController.getInstance().cancelStartTask();
      StadiumPool.getInstance().FreeStadiums();
      ParticipantPool.getInstance().FreePools();
    }

  }

  public void announceCompetition(CompetitionType type, int stad_id) {
    Iterator var3 = GameObjectsStorage.getAllByNpcId(31688, false).iterator();

    while(var3.hasNext()) {
      NpcInstance npc = (NpcInstance)var3.next();
      if (Config.NPC_OLYMPIAD_GAME_ANNOUNCE) {
        switch(type) {
          case CLASS_FREE:
            Functions.npcShoutCustomMessage(npc, "l2p.gameserver.model.entity.OlympiadGame.OlympiadNonClassed", new Object[]{String.valueOf(stad_id + 1)});
            break;
          case CLASS_INDIVIDUAL:
            Functions.npcShoutCustomMessage(npc, "l2p.gameserver.model.entity.OlympiadGame.OlympiadClassed", new Object[]{String.valueOf(stad_id + 1)});
            break;
          case TEAM_CLASS_FREE:
            Functions.npcShoutCustomMessage(npc, "l2p.gameserver.model.entity.OlympiadGame.OlympiadTeam", new Object[]{String.valueOf(stad_id + 1)});
        }
      }
    }

  }

  public int getPartCount() {
    return this._part_count;
  }

  public int getCurrentPartCount() {
    return CompetitionController.getInstance().getCompetitions().size();
  }

  public void incPartCount() {
    ++this._part_count;
  }

  private final synchronized void calcNewSeason() {
    Calendar base = Calendar.getInstance();
    if (Config.OLY_SEASON_TIME_CALC_MODE == OlySeasonTimeCalcMode.NORMAL) {
      base.set(5, 1);
    } else {
      base.set(5, base.get(5));
    }

    base.set(11, 0);
    base.set(12, 0);
    base.set(13, 0);
    base.set(14, 0);
    long base_mills = base.getTimeInMillis();
    this._season_start_time = this.getDateSeconds(base_mills, Config.OLY_SEASON_START_TIME);
    this._season_end_time = this.getDateSeconds(base_mills, Config.OLY_SEASON_END_TIME);
    this._nominate_time = this.getDateSeconds(base_mills, Config.OLY_NOMINATE_TIME);
    base_mills = this._season_start_time * 1000L;
    Calendar c_bonus = Calendar.getInstance();
    c_bonus.setTimeInMillis(base_mills);

    for(int i = 0; i < this._bonus_time.length; ++i) {
      this._bonus_time[i] = this.getDateSeconds(c_bonus, Config.OLY_BONUS_TIME);
    }

    this._bonus_idx = 0;
    Calendar c_comp_start = Calendar.getInstance();
    c_comp_start.setTimeInMillis(base_mills);

    for(int j = 0; j < this._comps_time.length; ++j) {
      this._comps_time[j] = new long[2];
      this._comps_time[j][0] = this.getDateSeconds(c_comp_start, Config.OLY_COMPETITION_START_TIME);
      this._comps_time[j][1] = this.getDateSeconds(c_comp_start, Config.OLY_COMPETITION_END_TIME);
      if (this._comps_time[j][0] > this._season_end_time) {
        this._comps_time[j][0] = -1L;
        this._comps_time[j][1] = -1L;
      }

      if (this._comps_time[j][1] >= this._season_end_time - 300L) {
        long[] var10000 = this._comps_time[j];
        var10000[1] -= 300L;
      }
    }

    this._comp_idx = 0;
  }

  private long getDateSeconds(long mills, String rule) {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date(mills));
    return this.getDateSeconds(c, rule);
  }

  private long getDateSeconds(Calendar c, String rule) {
    String[] parts = rule.split("\\s+");
    if (parts.length == 2) {
      String datepartsstr = parts[0];
      String[] dateparts = datepartsstr.split(":");
      if (dateparts.length == 2) {
        if (dateparts[0].startsWith("+")) {
          c.add(2, Integer.parseInt(dateparts[0].substring(1)));
        } else {
          c.set(2, Integer.parseInt(dateparts[0]) - 1);
        }
      }

      String datemodstr = dateparts[dateparts.length - 1];
      if (datemodstr.startsWith("+")) {
        c.add(5, Integer.parseInt(datemodstr.substring(1)));
      } else {
        c.set(5, Integer.parseInt(datemodstr));
      }
    }

    String[] timeparts = parts[parts.length - 1].split(":");
    if (timeparts[0].startsWith("+")) {
      c.add(11, Integer.parseInt(timeparts[0].substring(1)));
    } else {
      c.set(11, Integer.parseInt(timeparts[0]));
    }

    if (timeparts[1].startsWith("+")) {
      c.add(12, Integer.parseInt(timeparts[1].substring(1)));
    } else {
      c.set(12, Integer.parseInt(timeparts[1]));
    }

    return c.getTimeInMillis() / 1000L;
  }

  public long getSeasonStartTime() {
    return this._season_start_time;
  }

  public long getSeasonEndTime() {
    return this._season_end_time;
  }

  private static String ScheduledFutureTime(ScheduledFuture<?> future) {
    return UnixTimeStampToString(System.currentTimeMillis() / 1000L + future.getDelay(TimeUnit.SECONDS));
  }

  private static String UnixTimeStampToString(long dt) {
    return _dtformat.format(new Date((dt + 1L) * 1000L));
  }

  static {
    _dtformat.setTimeZone(TimeZone.getDefault());
    _instance = null;
  }

  private class NewSeasonCalcTask implements Runnable {
    private int season_id;

    public NewSeasonCalcTask(int season_id_) {
      this.season_id = season_id_;
    }

    public void run() {
      OlyController.getInstance().NewSeasonCalc(this.season_id);
    }
  }

  private class BonusTask implements Runnable {
    private int season_id;
    private int bonus_id;

    public BonusTask(int season_id_, int bonus_id_) {
      this.season_id = season_id_;
      this.bonus_id = bonus_id_;
    }

    public void run() {
      OlyController.getInstance().Bonus(this.season_id, this.bonus_id);
    }
  }

  private class CompetitionEndTask implements Runnable {
    private int season_id;
    private int comp_id;

    public CompetitionEndTask(int season_id_, int comp_id_) {
      this.season_id = season_id_;
      this.comp_id = comp_id_;
    }

    public void run() {
      OlyController.getInstance().CompetitionEnd(this.season_id, this.comp_id);
    }
  }

  private class CompetitionStartTask implements Runnable {
    private int season_id;
    private int comp_id;

    public CompetitionStartTask(int season_id_, int comp_id_) {
      this.season_id = season_id_;
      this.comp_id = comp_id_;
    }

    public void run() {
      OlyController.getInstance().CompetitionStart(this.season_id, this.comp_id);
    }
  }

  private class NominationTask implements Runnable {
    private int season_id;

    public NominationTask(int season_id_) {
      this.season_id = season_id_;
    }

    public void run() {
      OlyController.getInstance().Nomination(this.season_id);
    }
  }

  private class SeasonEndTask implements Runnable {
    private int season_id;

    public SeasonEndTask(int season_id_) {
      this.season_id = season_id_;
    }

    public void run() {
      OlyController.getInstance().SeasonEnd(this.season_id);
    }
  }

  private class SeasonStartTask implements Runnable {
    private int season_id;

    public SeasonStartTask(int season_id_) {
      this.season_id = season_id_;
    }

    public void run() {
      OlyController.getInstance().SeasonStart(this.season_id);
    }
  }
}
