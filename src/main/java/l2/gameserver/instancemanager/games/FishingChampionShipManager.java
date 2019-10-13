//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingChampionShipManager {
  private static final Logger _log = LoggerFactory.getLogger(FishingChampionShipManager.class);
  private static final FishingChampionShipManager _instance = new FishingChampionShipManager();
  private long _enddate = 0L;
  private List<String> _playersName = new ArrayList();
  private List<String> _fishLength = new ArrayList();
  private List<String> _winPlayersName = new ArrayList();
  private List<String> _winFishLength = new ArrayList();
  private List<FishingChampionShipManager.Fisher> _tmpPlayers = new ArrayList();
  private List<FishingChampionShipManager.Fisher> _winPlayers = new ArrayList();
  private double _minFishLength = 0.0D;
  private boolean _needRefresh = true;

  public static final FishingChampionShipManager getInstance() {
    return _instance;
  }

  private FishingChampionShipManager() {
    this.restoreData();
    this.refreshWinResult();
    this.recalculateMinLength();
    if (this._enddate <= System.currentTimeMillis()) {
      this._enddate = System.currentTimeMillis();
      (new FishingChampionShipManager.finishChamp()).run();
    } else {
      ThreadPoolManager.getInstance().schedule(new FishingChampionShipManager.finishChamp(), this._enddate - System.currentTimeMillis());
    }

  }

  private void setEndOfChamp() {
    Calendar finishtime = Calendar.getInstance();
    finishtime.setTimeInMillis(this._enddate);
    finishtime.set(12, 0);
    finishtime.set(13, 0);
    finishtime.add(5, 6);
    finishtime.set(7, 3);
    finishtime.set(11, 19);
    this._enddate = finishtime.getTimeInMillis();
  }

  private void restoreData() {
    this._enddate = ServerVariables.getLong("fishChampionshipEnd", 0L);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT `PlayerName`, `fishLength`, `rewarded` FROM fishing_championship");
      ResultSet rs = statement.executeQuery();

      while(rs.next()) {
        int rewarded = rs.getInt("rewarded");
        if (rewarded == 0) {
          this._tmpPlayers.add(new FishingChampionShipManager.Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), 0));
        }

        if (rewarded > 0) {
          this._winPlayers.add(new FishingChampionShipManager.Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), rewarded));
        }
      }

      rs.close();
    } catch (SQLException var8) {
      _log.warn("Exception: can't get fishing championship info: " + var8.getMessage());
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public synchronized void newFish(Player pl, int lureId) {
    if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
      double p1 = (double)Rnd.get(60, 80);
      if (p1 < 90.0D && lureId > 8484 && lureId < 8486) {
        long diff = Math.round(90.0D - p1);
        if (diff > 1L) {
          p1 += (double)Rnd.get(1L, diff);
        }
      }

      double len = (double)Rnd.get(100, 999) / 1000.0D + p1;
      Iterator var7;
      FishingChampionShipManager.Fisher fisher;
      if (this._tmpPlayers.size() < 5) {
        var7 = this._tmpPlayers.iterator();

        while(var7.hasNext()) {
          fisher = (FishingChampionShipManager.Fisher)var7.next();
          if (fisher.getName().equalsIgnoreCase(pl.getName())) {
            if (fisher.getLength() < len) {
              fisher.setLength(len);
              pl.sendMessage(new CustomMessage("l2p.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn", pl, new Object[0]));
              this.recalculateMinLength();
            }

            return;
          }
        }

        this._tmpPlayers.add(new FishingChampionShipManager.Fisher(pl.getName(), len, 0));
        pl.sendMessage(new CustomMessage("l2p.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList", pl, new Object[0]));
        this.recalculateMinLength();
      } else if (this._minFishLength < len) {
        var7 = this._tmpPlayers.iterator();

        while(var7.hasNext()) {
          fisher = (FishingChampionShipManager.Fisher)var7.next();
          if (fisher.getName().equalsIgnoreCase(pl.getName())) {
            if (fisher.getLength() < len) {
              fisher.setLength(len);
              pl.sendMessage(new CustomMessage("l2p.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn", pl, new Object[0]));
              this.recalculateMinLength();
            }

            return;
          }
        }

        FishingChampionShipManager.Fisher minFisher = null;
        double minLen = 99999.0D;
        Iterator var10 = this._tmpPlayers.iterator();

        while(var10.hasNext()) {
          FishingChampionShipManager.Fisher fisher = (FishingChampionShipManager.Fisher)var10.next();
          if (fisher.getLength() < minLen) {
            minFisher = fisher;
            minLen = fisher.getLength();
          }
        }

        this._tmpPlayers.remove(minFisher);
        this._tmpPlayers.add(new FishingChampionShipManager.Fisher(pl.getName(), len, 0));
        pl.sendMessage(new CustomMessage("l2p.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList", pl, new Object[0]));
        this.recalculateMinLength();
      }

    }
  }

  private void recalculateMinLength() {
    double minLen = 99999.0D;
    Iterator var3 = this._tmpPlayers.iterator();

    while(var3.hasNext()) {
      FishingChampionShipManager.Fisher fisher = (FishingChampionShipManager.Fisher)var3.next();
      if (fisher.getLength() < minLen) {
        minLen = fisher.getLength();
      }
    }

    this._minFishLength = minLen;
  }

  public long getTimeRemaining() {
    return (this._enddate - System.currentTimeMillis()) / 60000L;
  }

  public String getWinnerName(int par) {
    return this._winPlayersName.size() >= par ? (String)this._winPlayersName.get(par - 1) : "—";
  }

  public String getCurrentName(int par) {
    return this._playersName.size() >= par ? (String)this._playersName.get(par - 1) : "—";
  }

  public String getFishLength(int par) {
    return this._winFishLength.size() >= par ? (String)this._winFishLength.get(par - 1) : "0";
  }

  public String getCurrentFishLength(int par) {
    return this._fishLength.size() >= par ? (String)this._fishLength.get(par - 1) : "0";
  }

  public void getReward(Player pl) {
    String filename = "fisherman/championship/getReward.htm";
    NpcHtmlMessage html = new NpcHtmlMessage(pl.getObjectId());
    html.setFile(filename);
    pl.sendPacket(html);
    Iterator var4 = this._winPlayers.iterator();

    while(true) {
      FishingChampionShipManager.Fisher fisher;
      do {
        do {
          if (!var4.hasNext()) {
            return;
          }

          fisher = (FishingChampionShipManager.Fisher)var4.next();
        } while(!fisher._name.equalsIgnoreCase(pl.getName()));
      } while(fisher.getRewardType() == 2);

      int rewardCnt = 0;

      for(int x = 0; x < this._winPlayersName.size(); ++x) {
        if (((String)this._winPlayersName.get(x)).equalsIgnoreCase(pl.getName())) {
          switch(x) {
            case 0:
              rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_1;
              break;
            case 1:
              rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_2;
              break;
            case 2:
              rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_3;
              break;
            case 3:
              rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_4;
              break;
            case 4:
              rewardCnt = Config.ALT_FISH_CHAMPIONSHIP_REWARD_5;
          }
        }
      }

      fisher.setRewardType(2);
      if (rewardCnt > 0) {
        SystemMessage smsg = (new SystemMessage(53)).addItemName(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).addNumber(rewardCnt);
        pl.sendPacket(smsg);
        pl.getInventory().addItem(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM, (long)rewardCnt);
        pl.sendItemList(false);
      }
    }
  }

  public void showMidResult(Player pl) {
    if (this._needRefresh) {
      this.refreshResult();
      ThreadPoolManager.getInstance().schedule(new FishingChampionShipManager.needRefresh(), 60000L);
    }

    NpcHtmlMessage html = new NpcHtmlMessage(pl.getObjectId());
    String filename = "fisherman/championship/MidResult.htm";
    html.setFile(filename);
    String str = null;

    for(int x = 1; x <= 5; ++x) {
      str = str + "<tr><td width=70 align=center>" + x + (pl.isLangRus() ? " Место:" : " Position:") + "</td>";
      str = str + "<td width=110 align=center>" + this.getCurrentName(x) + "</td>";
      str = str + "<td width=80 align=center>" + this.getCurrentFishLength(x) + "</td></tr>";
    }

    html.replace("%TABLE%", str);
    html.replace("%prizeItem%", ItemHolder.getInstance().getTemplate(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
    html.replace("%prizeFirst%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_1));
    html.replace("%prizeTwo%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_2));
    html.replace("%prizeThree%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_3));
    html.replace("%prizeFour%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_4));
    html.replace("%prizeFive%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_5));
    pl.sendPacket(html);
  }

  public void showChampScreen(Player pl, NpcInstance npc) {
    NpcHtmlMessage html = new NpcHtmlMessage(pl.getObjectId());
    String filename = "fisherman/championship/champScreen.htm";
    html.setFile(filename);
    String str = null;

    for(int x = 1; x <= 5; ++x) {
      str = str + "<tr><td width=70 align=center>" + x + (pl.isLangRus() ? " Место:" : " Position:") + "</td>";
      str = str + "<td width=110 align=center>" + this.getWinnerName(x) + "</td>";
      str = str + "<td width=80 align=center>" + this.getFishLength(x) + "</td></tr>";
    }

    html.replace("%TABLE%", str);
    html.replace("%prizeItem%", ItemHolder.getInstance().getTemplate(Config.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
    html.replace("%prizeFirst%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_1));
    html.replace("%prizeTwo%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_2));
    html.replace("%prizeThree%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_3));
    html.replace("%prizeFour%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_4));
    html.replace("%prizeFive%", String.valueOf(Config.ALT_FISH_CHAMPIONSHIP_REWARD_5));
    html.replace("%refresh%", String.valueOf(this.getTimeRemaining()));
    html.replace("%objectId%", String.valueOf(npc.getObjectId()));
    pl.sendPacket(html);
  }

  public void shutdown() {
    ServerVariables.set("fishChampionshipEnd", this._enddate);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM fishing_championship");
      statement.execute();
      statement.close();
      Iterator var3 = this._winPlayers.iterator();

      FishingChampionShipManager.Fisher fisher;
      while(var3.hasNext()) {
        fisher = (FishingChampionShipManager.Fisher)var3.next();
        statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
        statement.setString(1, fisher.getName());
        statement.setDouble(2, fisher.getLength());
        statement.setInt(3, fisher.getRewardType());
        statement.execute();
        statement.close();
      }

      var3 = this._tmpPlayers.iterator();

      while(var3.hasNext()) {
        fisher = (FishingChampionShipManager.Fisher)var3.next();
        statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
        statement.setString(1, fisher.getName());
        statement.setDouble(2, fisher.getLength());
        statement.setInt(3, 0);
        statement.execute();
        statement.close();
      }
    } catch (SQLException var8) {
      _log.warn("Exception: can't update player vitality: " + var8.getMessage());
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private synchronized void refreshResult() {
    this._needRefresh = false;
    this._playersName.clear();
    this._fishLength.clear();
    FishingChampionShipManager.Fisher fisher1 = null;
    FishingChampionShipManager.Fisher fisher2 = null;

    int x;
    for(x = 0; x <= this._tmpPlayers.size() - 1; ++x) {
      for(int y = 0; y <= this._tmpPlayers.size() - 2; ++y) {
        fisher1 = (FishingChampionShipManager.Fisher)this._tmpPlayers.get(y);
        fisher2 = (FishingChampionShipManager.Fisher)this._tmpPlayers.get(y + 1);
        if (fisher1.getLength() < fisher2.getLength()) {
          this._tmpPlayers.set(y, fisher2);
          this._tmpPlayers.set(y + 1, fisher1);
        }
      }
    }

    for(x = 0; x <= this._tmpPlayers.size() - 1; ++x) {
      this._playersName.add(((FishingChampionShipManager.Fisher)this._tmpPlayers.get(x))._name);
      this._fishLength.add(String.valueOf(((FishingChampionShipManager.Fisher)this._tmpPlayers.get(x)).getLength()));
    }

  }

  private void refreshWinResult() {
    this._winPlayersName.clear();
    this._winFishLength.clear();
    FishingChampionShipManager.Fisher fisher1 = null;
    FishingChampionShipManager.Fisher fisher2 = null;

    int x;
    for(x = 0; x <= this._winPlayers.size() - 1; ++x) {
      for(int y = 0; y <= this._winPlayers.size() - 2; ++y) {
        fisher1 = (FishingChampionShipManager.Fisher)this._winPlayers.get(y);
        fisher2 = (FishingChampionShipManager.Fisher)this._winPlayers.get(y + 1);
        if (fisher1.getLength() < fisher2.getLength()) {
          this._winPlayers.set(y, fisher2);
          this._winPlayers.set(y + 1, fisher1);
        }
      }
    }

    for(x = 0; x <= this._winPlayers.size() - 1; ++x) {
      this._winPlayersName.add(((FishingChampionShipManager.Fisher)this._winPlayers.get(x))._name);
      this._winFishLength.add(String.valueOf(((FishingChampionShipManager.Fisher)this._winPlayers.get(x)).getLength()));
    }

  }

  private class Fisher {
    private double _length = 0.0D;
    private String _name;
    private int _reward = 0;

    public Fisher(String name, double length, int rewardType) {
      this.setName(name);
      this.setLength(length);
      this.setRewardType(rewardType);
    }

    public void setLength(double value) {
      this._length = value;
    }

    public void setName(String value) {
      this._name = value;
    }

    public void setRewardType(int value) {
      this._reward = value;
    }

    public String getName() {
      return this._name;
    }

    public int getRewardType() {
      return this._reward;
    }

    public double getLength() {
      return this._length;
    }
  }

  private class needRefresh extends RunnableImpl {
    private needRefresh() {
    }

    public void runImpl() throws Exception {
      FishingChampionShipManager.this._needRefresh = true;
    }
  }

  private class finishChamp extends RunnableImpl {
    private finishChamp() {
    }

    public void runImpl() throws Exception {
      FishingChampionShipManager.this._winPlayers.clear();
      Iterator var1 = FishingChampionShipManager.this._tmpPlayers.iterator();

      while(var1.hasNext()) {
        FishingChampionShipManager.Fisher fisher = (FishingChampionShipManager.Fisher)var1.next();
        fisher.setRewardType(1);
        FishingChampionShipManager.this._winPlayers.add(fisher);
      }

      FishingChampionShipManager.this._tmpPlayers.clear();
      FishingChampionShipManager.this.refreshWinResult();
      FishingChampionShipManager.this.setEndOfChamp();
      FishingChampionShipManager.this.shutdown();
      _log.info("Fishing Championship Manager : start new event period.");
      ThreadPoolManager.getInstance().schedule(FishingChampionShipManager.this.new finishChamp(), FishingChampionShipManager.this._enddate - System.currentTimeMillis());
    }
  }
}
