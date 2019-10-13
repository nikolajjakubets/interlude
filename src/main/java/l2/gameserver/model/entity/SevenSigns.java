//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.dbutils.DbUtils;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnSSPeriodListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.s2c.SSQInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SevenSigns {
  private static final Logger _log = LoggerFactory.getLogger(SevenSigns.class);
  private static SevenSigns _instance;
  private ScheduledFuture<?> _periodChange;
  public static final String SEVEN_SIGNS_HTML_PATH = "seven_signs/";
  public static final int CABAL_NULL = 0;
  public static final int CABAL_DUSK = 1;
  public static final int CABAL_DAWN = 2;
  public static final int SEAL_NULL = 0;
  public static final int SEAL_AVARICE = 1;
  public static final int SEAL_GNOSIS = 2;
  public static final int SEAL_STRIFE = 3;
  public static final int PERIOD_COMP_RECRUITING = 0;
  public static final int PERIOD_COMPETITION = 1;
  public static final int PERIOD_COMP_RESULTS = 2;
  public static final int PERIOD_SEAL_VALIDATION = 3;
  public static final int PERIOD_START_HOUR = 18;
  public static final int PERIOD_START_MINS = 0;
  public static final int PERIOD_START_DAY = 2;
  public static final int PERIOD_MINOR_LENGTH = 900000;
  public static final int PERIOD_MAJOR_LENGTH = 603900000;
  public static final int ANCIENT_ADENA_ID = 5575;
  public static final int RECORD_SEVEN_SIGNS_ID = 5707;
  public static final int CERTIFICATE_OF_APPROVAL_ID = 6388;
  public static final int RECORD_SEVEN_SIGNS_COST = 500;
  public static final int ADENA_JOIN_DAWN_COST = 50000;
  public static final Set<Integer> ORATOR_NPC_IDS = new HashSet(Arrays.asList(31093, 31172, 31174, 31176, 31178, 31180, 31182, 31184, 31186, 31188, 31190, 31192, 31194, 31196, 31198, 31200, 31231, 31232, 31233, 31234, 31235, 31236, 31237, 31238, 31239, 31240, 31241, 31242, 31243, 31244, 31245, 31246, 31713, 31714, 31715, 31716, 31717, 31718, 31719, 31720));
  public static final Set<Integer> PREACHER_NPC_IDS = new HashSet(Arrays.asList(31094, 31173, 31175, 31177, 31179, 31181, 31183, 31185, 31187, 31189, 31191, 31193, 31195, 31197, 31199, 31201, 31247, 31248, 31249, 31250, 31251, 31252, 31253, 31254, 31721, 31722, 31723, 31724, 31725, 31726, 31727, 31728, 32003, 32004, 32005, 32006));
  public static final int SEAL_STONE_BLUE_ID = 6360;
  public static final int SEAL_STONE_GREEN_ID = 6361;
  public static final int SEAL_STONE_RED_ID = 6362;
  public static final int SEAL_STONE_BLUE_VALUE = 3;
  public static final int SEAL_STONE_GREEN_VALUE = 5;
  public static final int SEAL_STONE_RED_VALUE = 10;
  public static final int BLUE_CONTRIB_POINTS = 3;
  public static final int GREEN_CONTRIB_POINTS = 5;
  public static final int RED_CONTRIB_POINTS = 10;
  public static final long MAXIMUM_PLAYER_CONTRIB;
  private final Calendar _calendar = Calendar.getInstance();
  protected int _activePeriod;
  protected int _currentCycle;
  protected long _dawnStoneScore;
  protected long _duskStoneScore;
  protected long _dawnFestivalScore;
  protected long _duskFestivalScore;
  protected int _compWinner;
  protected int _previousWinner;
  private Map<Integer, StatsSet> _signsPlayerData;
  private Map<Integer, Integer> _signsSealOwners;
  private Map<Integer, Integer> _signsDuskSealTotals;
  private Map<Integer, Integer> _signsDawnSealTotals;
  private SevenSigns.SSListenerList _listenerList = new SevenSigns.SSListenerList();

  public SevenSigns() {
    GameServer.getInstance().addListener(new SevenSigns.OnStartListenerImpl());
    this._signsPlayerData = new ConcurrentHashMap();
    this._signsSealOwners = new ConcurrentHashMap();
    this._signsDuskSealTotals = new ConcurrentHashMap();
    this._signsDawnSealTotals = new ConcurrentHashMap();

    try {
      this.restoreSevenSignsData();
    } catch (Exception var10) {
      _log.error("SevenSigns: Failed to load configuration: " + var10);
      _log.error("", var10);
    }

    _log.info("SevenSigns: Currently in the " + this.getCurrentPeriodName() + " period!");
    this.initializeSeals();
    if (this.isSealValidationPeriod()) {
      if (this.getCabalHighestScore() == 0) {
        _log.info("SevenSigns: The Competition last week ended with a tie.");
      } else {
        _log.info("SevenSigns: The " + getCabalName(this.getCabalHighestScore()) + " were victorious last week.");
      }
    } else if (this.getCabalHighestScore() == 0) {
      _log.info("SevenSigns: The Competition this week, if the trend continue, will end with a tie.");
    } else {
      _log.info("SevenSigns: The " + getCabalName(this.getCabalHighestScore()) + " are in the lead this week.");
    }

    int numMins = false;
    int numHours = false;
    int numDays = false;
    this.setCalendarForNextPeriodChange();
    long milliToChange = this.getMilliToPeriodChange();
    if (milliToChange < 10L) {
      milliToChange = 10L;
    }

    this._periodChange = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), milliToChange);
    double numSecs = (double)(milliToChange / 1000L % 60L);
    double countDown = ((double)(milliToChange / 1000L) - numSecs) / 60.0D;
    int numMins = (int)Math.floor(countDown % 60.0D);
    countDown = (countDown - (double)numMins) / 60.0D;
    int numHours = (int)Math.floor(countDown % 24.0D);
    int numDays = (int)Math.floor((countDown - (double)numHours) / 24.0D);
    _log.info("SevenSigns: Next period begins in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
    if (Config.SS_ANNOUNCE_PERIOD > 0) {
      ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsAnnounce(), (long)Config.SS_ANNOUNCE_PERIOD * 1000L * 60L);
    }

  }

  public static SevenSigns getInstance() {
    if (_instance == null) {
      _instance = new SevenSigns();
    }

    return _instance;
  }

  public static long calcContributionScore(long blueCount, long greenCount, long redCount) {
    long contrib = blueCount * 3L;
    contrib += greenCount * 5L;
    contrib += redCount * 10L;
    return contrib;
  }

  public static long calcAncientAdenaReward(long blueCount, long greenCount, long redCount) {
    long reward = blueCount * 3L;
    reward += greenCount * 5L;
    reward += redCount * 10L;
    return reward;
  }

  public static int getCabalNumber(String cabal) {
    if (cabal.equalsIgnoreCase("dawn")) {
      return 2;
    } else {
      return cabal.equalsIgnoreCase("dusk") ? 1 : 0;
    }
  }

  public static String getCabalShortName(int cabal) {
    switch(cabal) {
      case 1:
        return "dusk";
      case 2:
        return "dawn";
      default:
        return "No Cabal";
    }
  }

  public static String getCabalName(int cabal) {
    switch(cabal) {
      case 1:
        return "Revolutionaries of Dusk";
      case 2:
        return "Lords of Dawn";
      default:
        return "No Cabal";
    }
  }

  public static String getSealName(int seal, boolean shortName) {
    String sealName = !shortName ? "Seal of " : "";
    switch(seal) {
      case 1:
        sealName = sealName + "Avarice";
        break;
      case 2:
        sealName = sealName + "Gnosis";
        break;
      case 3:
        sealName = sealName + "Strife";
    }

    return sealName;
  }

  public static String capitalizeWords(String str) {
    char[] charArray = str.toCharArray();
    StringBuilder buf = new StringBuilder();
    charArray[0] = Character.toUpperCase(charArray[0]);

    for(int i = 0; i < charArray.length; ++i) {
      if (Character.isWhitespace(charArray[i]) && i != charArray.length - 1) {
        charArray[i + 1] = Character.toUpperCase(charArray[i + 1]);
      }

      buf.append(Character.toString(charArray[i]));
    }

    return buf.toString();
  }

  public final int getCurrentCycle() {
    return this._currentCycle;
  }

  public final int getCurrentPeriod() {
    return this._activePeriod;
  }

  private int getDaysToPeriodChange() {
    int numDays = this._calendar.get(7) - 2;
    return numDays < 0 ? 0 - numDays : 7 - numDays;
  }

  public final long getMilliToPeriodChange() {
    return this._calendar.getTimeInMillis() - System.currentTimeMillis();
  }

  protected void setCalendarForNextPeriodChange() {
    switch(this.getCurrentPeriod()) {
      case 0:
      case 2:
        this._calendar.add(14, 900000);
        break;
      case 1:
      case 3:
        int daysToChange = this.getDaysToPeriodChange();
        if (daysToChange == 7) {
          if (this._calendar.get(11) < 18) {
            daysToChange = 0;
          } else if (this._calendar.get(11) == 18 && this._calendar.get(12) < 0) {
            daysToChange = 0;
          }
        }

        if (daysToChange > 0) {
          this._calendar.add(5, daysToChange);
        }

        this._calendar.set(11, 18);
        this._calendar.set(12, 0);
    }

  }

  public final String getCurrentPeriodName() {
    String periodName = null;
    switch(this._activePeriod) {
      case 0:
        periodName = "Quest Event Initialization";
        break;
      case 1:
        periodName = "Competition (Quest Event)";
        break;
      case 2:
        periodName = "Quest Event Results";
        break;
      case 3:
        periodName = "Seal Validation";
    }

    return periodName;
  }

  public final boolean isSealValidationPeriod() {
    return this._activePeriod == 3;
  }

  public final boolean isCompResultsPeriod() {
    return this._activePeriod == 2;
  }

  public final long getCurrentScore(int cabal) {
    double totalStoneScore = (double)(this._dawnStoneScore + this._duskStoneScore);
    switch(cabal) {
      case 0:
        return 0L;
      case 1:
        return Math.round((double)this._duskStoneScore / (totalStoneScore == 0.0D ? 1.0D : totalStoneScore) * 500.0D) + this._duskFestivalScore;
      case 2:
        return Math.round((double)this._dawnStoneScore / (totalStoneScore == 0.0D ? 1.0D : totalStoneScore) * 500.0D) + this._dawnFestivalScore;
      default:
        return 0L;
    }
  }

  public final long getCurrentStoneScore(int cabal) {
    switch(cabal) {
      case 0:
        return 0L;
      case 1:
        return this._duskStoneScore;
      case 2:
        return this._dawnStoneScore;
      default:
        return 0L;
    }
  }

  public final long getCurrentFestivalScore(int cabal) {
    switch(cabal) {
      case 0:
        return 0L;
      case 1:
        return this._duskFestivalScore;
      case 2:
        return this._dawnFestivalScore;
      default:
        return 0L;
    }
  }

  public final int getCabalHighestScore() {
    long diff = this.getCurrentScore(1) - this.getCurrentScore(2);
    if (diff == 0L) {
      return 0;
    } else {
      return diff > 0L ? 1 : 2;
    }
  }

  public final int getSealOwner(int seal) {
    return this._signsSealOwners != null && this._signsSealOwners.containsKey(seal) ? (Integer)this._signsSealOwners.get(seal) : 0;
  }

  public final int getSealProportion(int seal, int cabal) {
    if (cabal == 0) {
      return 0;
    } else {
      return cabal == 1 ? (Integer)this._signsDuskSealTotals.get(seal) : (Integer)this._signsDawnSealTotals.get(seal);
    }
  }

  public final int getTotalMembers(int cabal) {
    int cabalMembers = 0;
    Iterator var3 = this._signsPlayerData.values().iterator();

    while(var3.hasNext()) {
      StatsSet sevenDat = (StatsSet)var3.next();
      if (sevenDat.getInteger("cabal") == cabal) {
        ++cabalMembers;
      }
    }

    return cabalMembers;
  }

  public final StatsSet getPlayerStatsSet(Player player) {
    return !this.hasRegisteredBefore(player.getObjectId()) ? null : (StatsSet)this._signsPlayerData.get(player.getObjectId());
  }

  public long getPlayerStoneContrib(Player player) {
    if (!this.hasRegisteredBefore(player.getObjectId())) {
      return 0L;
    } else {
      long stoneCount = 0L;
      StatsSet currPlayer = (StatsSet)this._signsPlayerData.get(player.getObjectId());
      if (this.getPlayerCabal(player) == 2) {
        stoneCount += currPlayer.getLong("dawn_red_stones");
        stoneCount += currPlayer.getLong("dawn_green_stones");
        stoneCount += currPlayer.getLong("dawn_blue_stones");
      } else {
        stoneCount += currPlayer.getLong("dusk_red_stones");
        stoneCount += currPlayer.getLong("dusk_green_stones");
        stoneCount += currPlayer.getLong("dusk_blue_stones");
      }

      return stoneCount;
    }
  }

  public long getPlayerContribScore(Player player) {
    if (!this.hasRegisteredBefore(player.getObjectId())) {
      return 0L;
    } else {
      StatsSet currPlayer = (StatsSet)this._signsPlayerData.get(player.getObjectId());
      return this.getPlayerCabal(player) == 2 ? (long)currPlayer.getInteger("dawn_contribution_score") : (long)currPlayer.getInteger("dusk_contribution_score");
    }
  }

  public long getPlayerAdenaCollect(Player player) {
    return !this.hasRegisteredBefore(player.getObjectId()) ? 0L : ((StatsSet)this._signsPlayerData.get(player.getObjectId())).getLong(this.getPlayerCabal(player) == 2 ? "dawn_ancient_adena_amount" : "dusk_ancient_adena_amount");
  }

  public int getPlayerSeal(Player player) {
    return !this.hasRegisteredBefore(player.getObjectId()) ? 0 : ((StatsSet)this._signsPlayerData.get(player.getObjectId())).getInteger("seal");
  }

  public int getPlayerCabal(Player player) {
    return !this.hasRegisteredBefore(player.getObjectId()) ? 0 : ((StatsSet)this._signsPlayerData.get(player.getObjectId())).getInteger("cabal");
  }

  protected void restoreSevenSignsData() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT char_obj_id, cabal, seal, dawn_red_stones, dawn_green_stones, dawn_blue_stones, dawn_ancient_adena_amount, dawn_contribution_score, dusk_red_stones, dusk_green_stones, dusk_blue_stones, dusk_ancient_adena_amount, dusk_contribution_score FROM seven_signs");
      rset = statement.executeQuery();

      while(rset.next()) {
        int charObjId = rset.getInt("char_obj_id");
        StatsSet sevenDat = new StatsSet();
        sevenDat.set("char_obj_id", charObjId);
        sevenDat.set("cabal", getCabalNumber(rset.getString("cabal")));
        sevenDat.set("seal", rset.getInt("seal"));
        sevenDat.set("dawn_red_stones", rset.getInt("dawn_red_stones"));
        sevenDat.set("dawn_green_stones", rset.getInt("dawn_green_stones"));
        sevenDat.set("dawn_blue_stones", rset.getInt("dawn_blue_stones"));
        sevenDat.set("dawn_ancient_adena_amount", rset.getInt("dawn_ancient_adena_amount"));
        sevenDat.set("dawn_contribution_score", rset.getInt("dawn_contribution_score"));
        sevenDat.set("dusk_red_stones", rset.getInt("dusk_red_stones"));
        sevenDat.set("dusk_green_stones", rset.getInt("dusk_green_stones"));
        sevenDat.set("dusk_blue_stones", rset.getInt("dusk_blue_stones"));
        sevenDat.set("dusk_ancient_adena_amount", rset.getInt("dusk_ancient_adena_amount"));
        sevenDat.set("dusk_contribution_score", rset.getInt("dusk_contribution_score"));
        this._signsPlayerData.put(charObjId, sevenDat);
      }

      DbUtils.close(statement, rset);
      statement = con.prepareStatement("SELECT * FROM seven_signs_status");
      rset = statement.executeQuery();

      while(rset.next()) {
        this._currentCycle = rset.getInt("current_cycle");
        this._activePeriod = rset.getInt("active_period");
        this._previousWinner = rset.getInt("previous_winner");
        this._dawnStoneScore = rset.getLong("dawn_stone_score");
        this._dawnFestivalScore = rset.getLong("dawn_festival_score");
        this._duskStoneScore = rset.getLong("dusk_stone_score");
        this._duskFestivalScore = rset.getLong("dusk_festival_score");
        this._signsSealOwners.put(1, rset.getInt("avarice_owner"));
        this._signsSealOwners.put(2, rset.getInt("gnosis_owner"));
        this._signsSealOwners.put(3, rset.getInt("strife_owner"));
        this._signsDawnSealTotals.put(1, rset.getInt("avarice_dawn_score"));
        this._signsDawnSealTotals.put(2, rset.getInt("gnosis_dawn_score"));
        this._signsDawnSealTotals.put(3, rset.getInt("strife_dawn_score"));
        this._signsDuskSealTotals.put(1, rset.getInt("avarice_dusk_score"));
        this._signsDuskSealTotals.put(2, rset.getInt("gnosis_dusk_score"));
        this._signsDuskSealTotals.put(3, rset.getInt("strife_dusk_score"));
      }

      DbUtils.close(statement, rset);
      statement = con.prepareStatement("UPDATE seven_signs_status SET date=?");
      statement.setInt(1, Calendar.getInstance().get(7));
      statement.execute();
    } catch (SQLException var9) {
      _log.error("Unable to load Seven Signs Data: " + var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public synchronized void saveSevenSignsData(int playerId, boolean updateSettings) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE seven_signs SET cabal=?, seal=?, dawn_red_stones=?, dawn_green_stones=?, dawn_blue_stones=?, dawn_ancient_adena_amount=?, dawn_contribution_score=?, dusk_red_stones=?, dusk_green_stones=?, dusk_blue_stones=?, dusk_ancient_adena_amount=?, dusk_contribution_score=? WHERE char_obj_id=?");
      if (playerId > 0) {
        processStatement(statement, (StatsSet)this._signsPlayerData.get(playerId));
      } else {
        Iterator var5 = this._signsPlayerData.values().iterator();

        while(var5.hasNext()) {
          StatsSet sevenDat = (StatsSet)var5.next();
          processStatement(statement, sevenDat);
        }
      }

      DbUtils.close(statement);
      if (updateSettings) {
        StringBuilder buf = new StringBuilder();
        buf.append("UPDATE seven_signs_status SET current_cycle=?, active_period=?, previous_winner=?, dawn_stone_score=?, dawn_festival_score=?, dusk_stone_score=?, dusk_festival_score=?, avarice_owner=?, gnosis_owner=?, strife_owner=?, avarice_dawn_score=?, gnosis_dawn_score=?, strife_dawn_score=?, avarice_dusk_score=?, gnosis_dusk_score=?, strife_dusk_score=?, festival_cycle=?, ");

        int i;
        for(i = 0; i < 5; ++i) {
          buf.append("accumulated_bonus" + String.valueOf(i) + "=?, ");
        }

        buf.append("date=?");
        statement = con.prepareStatement(buf.toString());
        statement.setInt(1, this._currentCycle);
        statement.setInt(2, this._activePeriod);
        statement.setInt(3, this._previousWinner);
        statement.setLong(4, this._dawnStoneScore);
        statement.setLong(5, this._dawnFestivalScore);
        statement.setLong(6, this._duskStoneScore);
        statement.setLong(7, this._duskFestivalScore);
        statement.setInt(8, (Integer)this._signsSealOwners.get(1));
        statement.setInt(9, (Integer)this._signsSealOwners.get(2));
        statement.setInt(10, (Integer)this._signsSealOwners.get(3));
        statement.setInt(11, (Integer)this._signsDawnSealTotals.get(1));
        statement.setInt(12, (Integer)this._signsDawnSealTotals.get(2));
        statement.setInt(13, (Integer)this._signsDawnSealTotals.get(3));
        statement.setInt(14, (Integer)this._signsDuskSealTotals.get(1));
        statement.setInt(15, (Integer)this._signsDuskSealTotals.get(2));
        statement.setInt(16, (Integer)this._signsDuskSealTotals.get(3));
        statement.setInt(17, this.getCurrentCycle());

        for(i = 0; i < 5; ++i) {
          statement.setLong(18 + i, SevenSignsFestival.getInstance().getAccumulatedBonus(i));
        }

        statement.setInt(23, Calendar.getInstance().get(7));
        statement.executeUpdate();
      }
    } catch (SQLException var10) {
      _log.error("Unable to save Seven Signs data: " + var10);
      _log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private static void processStatement(PreparedStatement statement, StatsSet sevenDat) throws SQLException {
    statement.setString(1, getCabalShortName(sevenDat.getInteger("cabal")));
    statement.setInt(2, sevenDat.getInteger("seal"));
    statement.setInt(3, sevenDat.getInteger("dawn_red_stones"));
    statement.setInt(4, sevenDat.getInteger("dawn_green_stones"));
    statement.setInt(5, sevenDat.getInteger("dawn_blue_stones"));
    statement.setInt(6, sevenDat.getInteger("dawn_ancient_adena_amount"));
    statement.setInt(7, sevenDat.getInteger("dawn_contribution_score"));
    statement.setInt(8, sevenDat.getInteger("dusk_red_stones"));
    statement.setInt(9, sevenDat.getInteger("dusk_green_stones"));
    statement.setInt(10, sevenDat.getInteger("dusk_blue_stones"));
    statement.setInt(11, sevenDat.getInteger("dusk_ancient_adena_amount"));
    statement.setInt(12, sevenDat.getInteger("dusk_contribution_score"));
    statement.setInt(13, sevenDat.getInteger("char_obj_id"));
    statement.executeUpdate();
  }

  protected void resetPlayerData() {
    Iterator var1 = this._signsPlayerData.values().iterator();

    while(var1.hasNext()) {
      StatsSet sevenDat = (StatsSet)var1.next();
      int charObjId = sevenDat.getInteger("char_obj_id");
      if (sevenDat.getInteger("cabal") == this.getCabalHighestScore()) {
        switch(this.getCabalHighestScore()) {
          case 1:
            sevenDat.set("dusk_red_stones", 0);
            sevenDat.set("dusk_green_stones", 0);
            sevenDat.set("dusk_blue_stones", 0);
            sevenDat.set("dusk_contribution_score", 0);
            break;
          case 2:
            sevenDat.set("dawn_red_stones", 0);
            sevenDat.set("dawn_green_stones", 0);
            sevenDat.set("dawn_blue_stones", 0);
            sevenDat.set("dawn_contribution_score", 0);
        }
      } else if (sevenDat.getInteger("cabal") != 2 && sevenDat.getInteger("cabal") != 0) {
        if (sevenDat.getInteger("cabal") == 1 || sevenDat.getInteger("cabal") == 0) {
          sevenDat.set("dawn_red_stones", 0);
          sevenDat.set("dawn_green_stones", 0);
          sevenDat.set("dawn_blue_stones", 0);
          sevenDat.set("dawn_contribution_score", 0);
        }
      } else {
        sevenDat.set("dusk_red_stones", 0);
        sevenDat.set("dusk_green_stones", 0);
        sevenDat.set("dusk_blue_stones", 0);
        sevenDat.set("dusk_contribution_score", 0);
      }

      sevenDat.set("cabal", 0);
      sevenDat.set("seal", 0);
      this._signsPlayerData.put(charObjId, sevenDat);
    }

  }

  private boolean hasRegisteredBefore(int charObjId) {
    return this._signsPlayerData.containsKey(charObjId);
  }

  public int setPlayerInfo(int charObjId, int chosenCabal, int chosenSeal) {
    StatsSet currPlayer = null;
    if (this.hasRegisteredBefore(charObjId)) {
      currPlayer = (StatsSet)this._signsPlayerData.get(charObjId);
      currPlayer.set("cabal", chosenCabal);
      currPlayer.set("seal", chosenSeal);
      this._signsPlayerData.put(charObjId, currPlayer);
    } else {
      currPlayer = new StatsSet();
      currPlayer.set("char_obj_id", charObjId);
      currPlayer.set("cabal", chosenCabal);
      currPlayer.set("seal", chosenSeal);
      currPlayer.set("dawn_red_stones", 0);
      currPlayer.set("dawn_green_stones", 0);
      currPlayer.set("dawn_blue_stones", 0);
      currPlayer.set("dawn_ancient_adena_amount", 0);
      currPlayer.set("dawn_contribution_score", 0);
      currPlayer.set("dusk_red_stones", 0);
      currPlayer.set("dusk_green_stones", 0);
      currPlayer.set("dusk_blue_stones", 0);
      currPlayer.set("dusk_ancient_adena_amount", 0);
      currPlayer.set("dusk_contribution_score", 0);
      this._signsPlayerData.put(charObjId, currPlayer);
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("INSERT INTO seven_signs (char_obj_id, cabal, seal) VALUES (?,?,?)");
        statement.setInt(1, charObjId);
        statement.setString(2, getCabalShortName(chosenCabal));
        statement.setInt(3, chosenSeal);
        statement.execute();
      } catch (SQLException var11) {
        _log.error("SevenSigns: Failed to save data: " + var11);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }
    }

    long contribScore = 0L;
    switch(chosenCabal) {
      case 1:
        contribScore = calcContributionScore((long)currPlayer.getInteger("dusk_blue_stones"), (long)currPlayer.getInteger("dusk_green_stones"), (long)currPlayer.getInteger("dusk_red_stones"));
        this._duskStoneScore += contribScore;
        break;
      case 2:
        contribScore = calcContributionScore((long)currPlayer.getInteger("dawn_blue_stones"), (long)currPlayer.getInteger("dawn_green_stones"), (long)currPlayer.getInteger("dawn_red_stones"));
        this._dawnStoneScore += contribScore;
    }

    if (currPlayer.getInteger("cabal") == 2) {
      this._signsDawnSealTotals.put(chosenSeal, (Integer)this._signsDawnSealTotals.get(chosenSeal) + 1);
    } else {
      this._signsDuskSealTotals.put(chosenSeal, (Integer)this._signsDuskSealTotals.get(chosenSeal) + 1);
    }

    this.saveSevenSignsData(charObjId, true);
    return chosenCabal;
  }

  public int getAncientAdenaReward(Player player, boolean removeReward) {
    int charObjId = player.getObjectId();
    StatsSet currPlayer = (StatsSet)this._signsPlayerData.get(charObjId);
    int rewardAmount = false;
    int rewardAmount;
    if (currPlayer.getInteger("cabal") == 2) {
      rewardAmount = currPlayer.getInteger("dawn_ancient_adena_amount");
      currPlayer.set("dawn_ancient_adena_amount", 0);
    } else {
      rewardAmount = currPlayer.getInteger("dusk_ancient_adena_amount");
      currPlayer.set("dusk_ancient_adena_amount", 0);
    }

    if (removeReward) {
      this._signsPlayerData.put(charObjId, currPlayer);
      this.saveSevenSignsData(charObjId, false);
    }

    return rewardAmount;
  }

  public long addPlayerStoneContrib(Player player, long blueCount, long greenCount, long redCount) {
    return this.addPlayerStoneContrib(player.getObjectId(), blueCount, greenCount, redCount);
  }

  public long addPlayerStoneContrib(int charObjId, long blueCount, long greenCount, long redCount) {
    StatsSet currPlayer = (StatsSet)this._signsPlayerData.get(charObjId);
    long contribScore = calcContributionScore(blueCount, greenCount, redCount);
    long totalAncientAdena = 0L;
    long totalContribScore = 0L;
    if (currPlayer.getInteger("cabal") == 2) {
      totalAncientAdena = (long)currPlayer.getInteger("dawn_ancient_adena_amount") + calcAncientAdenaReward(blueCount, greenCount, redCount);
      totalContribScore = (long)currPlayer.getInteger("dawn_contribution_score") + contribScore;
      if (totalContribScore > MAXIMUM_PLAYER_CONTRIB) {
        return -1L;
      }

      currPlayer.set("dawn_red_stones", (long)currPlayer.getInteger("dawn_red_stones") + redCount);
      currPlayer.set("dawn_green_stones", (long)currPlayer.getInteger("dawn_green_stones") + greenCount);
      currPlayer.set("dawn_blue_stones", (long)currPlayer.getInteger("dawn_blue_stones") + blueCount);
      currPlayer.set("dawn_ancient_adena_amount", totalAncientAdena);
      currPlayer.set("dawn_contribution_score", totalContribScore);
      this._signsPlayerData.put(charObjId, currPlayer);
      this._dawnStoneScore += contribScore;
    } else {
      totalAncientAdena = (long)currPlayer.getInteger("dusk_ancient_adena_amount") + calcAncientAdenaReward(blueCount, greenCount, redCount);
      totalContribScore = (long)currPlayer.getInteger("dusk_contribution_score") + contribScore;
      if (totalContribScore > MAXIMUM_PLAYER_CONTRIB) {
        return -1L;
      }

      currPlayer.set("dusk_red_stones", (long)currPlayer.getInteger("dusk_red_stones") + redCount);
      currPlayer.set("dusk_green_stones", (long)currPlayer.getInteger("dusk_green_stones") + greenCount);
      currPlayer.set("dusk_blue_stones", (long)currPlayer.getInteger("dusk_blue_stones") + blueCount);
      currPlayer.set("dusk_ancient_adena_amount", totalAncientAdena);
      currPlayer.set("dusk_contribution_score", totalContribScore);
      this._signsPlayerData.put(charObjId, currPlayer);
      this._duskStoneScore += contribScore;
    }

    this.saveSevenSignsData(charObjId, true);
    return contribScore;
  }

  public synchronized void updateFestivalScore() {
    this._duskFestivalScore = 0L;
    this._dawnFestivalScore = 0L;

    for(int i = 0; i < 5; ++i) {
      long dusk = SevenSignsFestival.getInstance().getHighestScore(1, i);
      long dawn = SevenSignsFestival.getInstance().getHighestScore(2, i);
      if (dusk > dawn) {
        this._duskFestivalScore += (long)SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i];
      } else if (dusk < dawn) {
        this._dawnFestivalScore += (long)SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i];
      }
    }

  }

  public void sendCurrentPeriodMsg(Player player) {
    switch(this._activePeriod) {
      case 0:
        player.sendPacket(Msg.SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT);
        return;
      case 1:
        player.sendPacket(Msg.SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_SPEAK_WITH_A_PRIEST_OF_DAWN_OR_DUSK_PRIESTESS_IF_YOU_WISH_TO_PARTICIPATE_IN_THE_EVENT);
        return;
      case 2:
        player.sendPacket(Msg.SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED_RESULTS_ARE_BEING_TALLIED);
        return;
      case 3:
        player.sendPacket(Msg.SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD_A_NEW_QUEST_EVENT_PERIOD_BEGINS_NEXT_MONDAY);
        return;
      default:
    }
  }

  public void sendMessageToAll(int sysMsgId) {
    SystemMessage sm = new SystemMessage(sysMsgId);
    Iterator var3 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      player.sendPacket(sm);
    }

  }

  protected void initializeSeals() {
    Iterator var1 = this._signsSealOwners.keySet().iterator();

    while(var1.hasNext()) {
      Integer currSeal = (Integer)var1.next();
      int sealOwner = (Integer)this._signsSealOwners.get(currSeal);
      if (sealOwner != 0) {
        if (this.isSealValidationPeriod()) {
          _log.info("SevenSigns: The " + getCabalName(sealOwner) + " have won the " + getSealName(currSeal, false) + ".");
        } else {
          _log.info("SevenSigns: The " + getSealName(currSeal, false) + " is currently owned by " + getCabalName(sealOwner) + ".");
        }
      } else {
        _log.info("SevenSigns: The " + getSealName(currSeal, false) + " remains unclaimed.");
      }
    }

  }

  protected void resetSeals() {
    this._signsDawnSealTotals.put(1, 0);
    this._signsDawnSealTotals.put(2, 0);
    this._signsDawnSealTotals.put(3, 0);
    this._signsDuskSealTotals.put(1, 0);
    this._signsDuskSealTotals.put(2, 0);
    this._signsDuskSealTotals.put(3, 0);
  }

  protected void calcNewSealOwners() {
    Iterator var1 = this._signsDawnSealTotals.keySet().iterator();

    while(var1.hasNext()) {
      Integer currSeal;
      int newSealOwner;
      currSeal = (Integer)var1.next();
      int prevSealOwner = (Integer)this._signsSealOwners.get(currSeal);
      newSealOwner = 0;
      int dawnProportion = this.getSealProportion(currSeal, 2);
      int totalDawnMembers = this.getTotalMembers(2) == 0 ? 1 : this.getTotalMembers(2);
      int duskProportion = this.getSealProportion(currSeal, 1);
      int totalDuskMembers = this.getTotalMembers(1) == 0 ? 1 : this.getTotalMembers(1);
      label126:
      switch(prevSealOwner) {
        case 0:
          switch(this.getCabalHighestScore()) {
            case 0:
              if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers) && dawnProportion > duskProportion) {
                newSealOwner = 2;
              } else {
                if ((long)duskProportion >= Math.round(0.35D * (double)totalDuskMembers) && duskProportion > dawnProportion) {
                  newSealOwner = 1;
                  break label126;
                }

                newSealOwner = prevSealOwner;
              }
              break label126;
            case 1:
              if ((long)duskProportion >= Math.round(0.35D * (double)totalDuskMembers)) {
                newSealOwner = 1;
              } else if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = 2;
              } else {
                newSealOwner = prevSealOwner;
              }
              break label126;
            case 2:
              if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = 2;
              } else if ((long)duskProportion >= Math.round(0.35D * (double)totalDuskMembers)) {
                newSealOwner = 1;
              } else {
                newSealOwner = prevSealOwner;
              }
            default:
              break label126;
          }
        case 1:
          switch(this.getCabalHighestScore()) {
            case 0:
              if ((long)duskProportion >= Math.round(0.1D * (double)totalDuskMembers)) {
                newSealOwner = prevSealOwner;
              } else if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = 2;
              } else {
                newSealOwner = 0;
              }
              break label126;
            case 1:
              if ((long)duskProportion >= Math.round(0.1D * (double)totalDuskMembers)) {
                newSealOwner = prevSealOwner;
              } else if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = 2;
              } else {
                newSealOwner = 0;
              }
              break label126;
            case 2:
              if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = 2;
              } else if ((long)duskProportion >= Math.round(0.1D * (double)totalDuskMembers)) {
                newSealOwner = prevSealOwner;
              } else {
                newSealOwner = 0;
              }
            default:
              break label126;
          }
        case 2:
          switch(this.getCabalHighestScore()) {
            case 0:
              if ((long)dawnProportion >= Math.round(0.1D * (double)totalDawnMembers)) {
                newSealOwner = prevSealOwner;
              } else if ((long)duskProportion >= Math.round(0.35D * (double)totalDuskMembers)) {
                newSealOwner = 1;
              } else {
                newSealOwner = 0;
              }
              break;
            case 1:
              if ((long)duskProportion >= Math.round(0.1D * (double)totalDuskMembers)) {
                newSealOwner = 1;
              } else if ((long)dawnProportion >= Math.round(0.35D * (double)totalDawnMembers)) {
                newSealOwner = prevSealOwner;
              } else {
                newSealOwner = 0;
              }
              break;
            case 2:
              if ((long)dawnProportion >= Math.round(0.1D * (double)totalDawnMembers)) {
                newSealOwner = prevSealOwner;
              } else if ((long)duskProportion >= Math.round(0.35D * (double)totalDuskMembers)) {
                newSealOwner = 1;
              } else {
                newSealOwner = 0;
              }
          }
      }

      this._signsSealOwners.put(currSeal, newSealOwner);
      switch(currSeal) {
        case 1:
          if (newSealOwner == 2) {
            this.sendMessageToAll(1212);
          } else if (newSealOwner == 1) {
            this.sendMessageToAll(1215);
          }
          break;
        case 2:
          if (newSealOwner == 2) {
            this.sendMessageToAll(1213);
          } else if (newSealOwner == 1) {
            this.sendMessageToAll(1216);
          }
          break;
        case 3:
          if (newSealOwner == 2) {
            this.sendMessageToAll(1214);
          } else if (newSealOwner == 1) {
            this.sendMessageToAll(1217);
          }
      }
    }

  }

  public int getPriestCabal(int id) {
    switch(id) {
      case 31078:
      case 31079:
      case 31080:
      case 31081:
      case 31082:
      case 31083:
      case 31084:
      case 31168:
      case 31692:
      case 31694:
      case 31997:
        return 2;
      case 31085:
      case 31086:
      case 31087:
      case 31088:
      case 31089:
      case 31090:
      case 31091:
      case 31169:
      case 31693:
      case 31695:
      case 31998:
        return 1;
      default:
        return 0;
    }
  }

  public void changePeriod() {
    this._periodChange = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), 10L);
  }

  public void changePeriod(int period) {
    this.changePeriod(period, 1);
  }

  public void changePeriod(int period, int seconds) {
    this._activePeriod = period - 1;
    if (this._activePeriod < 0) {
      this._activePeriod += 4;
    }

    this._periodChange = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), (long)seconds * 1000L);
  }

  public void setTimeToNextPeriodChange(int time) {
    this._calendar.setTimeInMillis(System.currentTimeMillis() + (long)time * 1000L * 60L);
    if (this._periodChange != null) {
      this._periodChange.cancel(false);
    }

    this._periodChange = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), this.getMilliToPeriodChange());
  }

  public SevenSigns.SSListenerList getListenerEngine() {
    return this._listenerList;
  }

  public <T extends GameListener> boolean addListener(T listener) {
    return this._listenerList.add(listener);
  }

  public <T extends GameListener> boolean removeListener(T listener) {
    return this._listenerList.remove(listener);
  }

  static {
    MAXIMUM_PLAYER_CONTRIB = Math.round(1000000.0D * Config.RATE_DROP_ITEMS);
  }

  public class SevenSignsPeriodChange extends RunnableImpl {
    public SevenSignsPeriodChange() {
    }

    public void runImpl() throws Exception {
      _log.info("SevenSignsPeriodChange: old=" + SevenSigns.this._activePeriod);
      int periodEnded = SevenSigns.this._activePeriod++;
      switch(periodEnded) {
        case 0:
          SevenSigns.this.sendMessageToAll(1210);
          RaidBossSpawnManager.getInstance().distributeRewards();
          break;
        case 1:
          SevenSigns.this.sendMessageToAll(1211);
          int compWinner = SevenSigns.this.getCabalHighestScore();
          SevenSigns.this.calcNewSealOwners();
          if (compWinner == 1) {
            SevenSigns.this.sendMessageToAll(1240);
          } else {
            SevenSigns.this.sendMessageToAll(1241);
          }

          SevenSigns.this._previousWinner = compWinner;
          break;
        case 2:
          SevenSignsFestival.getInstance().distribAccumulatedBonus();
          SevenSignsFestival.getInstance().rewardHighestRanked();
          SevenSigns.this.initializeSeals();
          RaidBossSpawnManager.getInstance().distributeRewards();
          SevenSigns.this.sendMessageToAll(1218);
          _log.info("SevenSigns: The " + SevenSigns.getCabalName(SevenSigns.this._previousWinner) + " have won the competition with " + SevenSigns.this.getCurrentScore(SevenSigns.this._previousWinner) + " points!");
          break;
        case 3:
          SevenSigns.this._activePeriod = 0;
          SevenSigns.this.sendMessageToAll(1219);
          SevenSigns.this.resetPlayerData();
          SevenSigns.this.resetSeals();
          SevenSigns.this._dawnStoneScore = 0L;
          SevenSigns.this._duskStoneScore = 0L;
          SevenSigns.this._dawnFestivalScore = 0L;
          SevenSigns.this._duskFestivalScore = 0L;
          ++SevenSigns.this._currentCycle;
          SevenSignsFestival.getInstance().resetFestivalData(false);
      }

      SevenSigns.this.saveSevenSignsData(0, true);
      _log.info("SevenSignsPeriodChange: new=" + SevenSigns.this._activePeriod);

      try {
        _log.info("SevenSigns: Change Catacomb spawn...");
        SevenSigns.this.getListenerEngine().onPeriodChange();
        SSQInfo ss = new SSQInfo();
        Iterator var3 = GameObjectsStorage.getAllPlayersForIterate().iterator();

        while(var3.hasNext()) {
          Player player = (Player)var3.next();
          player.sendPacket(ss);
        }

        _log.info("SevenSigns: Spawning NPCs...");
        _log.info("SevenSigns: The " + SevenSigns.this.getCurrentPeriodName() + " period has begun!");
        _log.info("SevenSigns: Calculating next period change time...");
        SevenSigns.this.setCalendarForNextPeriodChange();
        _log.info("SevenSignsPeriodChange: time to next change=" + Util.formatTime((int)(SevenSigns.this.getMilliToPeriodChange() / 1000L)));
        SevenSigns.SevenSignsPeriodChange sspc = SevenSigns.this.new SevenSignsPeriodChange();
        SevenSigns.this._periodChange = ThreadPoolManager.getInstance().schedule(sspc, SevenSigns.this.getMilliToPeriodChange());
      } catch (Exception var5) {
        _log.error("", var5);
      }

    }
  }

  public class SevenSignsAnnounce extends RunnableImpl {
    public SevenSignsAnnounce() {
    }

    public void runImpl() throws Exception {
      if (Config.SEND_SSQ_WELCOME_MESSAGE) {
        Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

        while(var1.hasNext()) {
          Player player = (Player)var1.next();
          SevenSigns.this.sendCurrentPeriodMsg(player);
        }

        ThreadPoolManager.getInstance().schedule(SevenSigns.this.new SevenSignsAnnounce(), (long)Config.SS_ANNOUNCE_PERIOD * 1000L * 60L);
      }

    }
  }

  protected class SSListenerList extends ListenerList<GameServer> {
    protected SSListenerList() {
    }

    public void onPeriodChange() {
      if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
        SevenSigns.getInstance().getCabalHighestScore();
      }

      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener<GameServer> listener = (Listener)var1.next();
        if (listener instanceof OnSSPeriodListener) {
          ((OnSSPeriodListener)listener).onPeriodChange(SevenSigns.getInstance().getCurrentPeriod());
        }
      }

    }
  }

  private class OnStartListenerImpl implements OnStartListener {
    private OnStartListenerImpl() {
    }

    public void onStart() {
      SevenSigns.this.getListenerEngine().onPeriodChange();
    }
  }
}
