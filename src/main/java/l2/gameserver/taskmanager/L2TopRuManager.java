//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class L2TopRuManager {
  private static final String USERAGENT = "Mozilla/5.0 (SunOS; 5.10; amd64; U) Java HotSpot(TM) 64-Bit Server VM/16.2-b04";

  private static L2TopRuManager _instance;
  private Pattern _webPattern;
  private Pattern _smsPattern;
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static Map<Integer, Long> _voteDateCache = new ConcurrentHashMap<>();

  public static L2TopRuManager getInstance() {
    if (_instance == null) {
      _instance = new L2TopRuManager();
    }

    return _instance;
  }

  private L2TopRuManager() {
    if (Config.L2TOPRU_DELAY >= 1L) {
      log.info("L2TopRuManager: Initializing.");
      this._webPattern = Pattern.compile("^([\\d-]+\\s[\\d:]+)\\s+(?:" + Config.L2TOPRU_PREFIX + "-)*([^\\s]+)$", 8);
      this._smsPattern = Pattern.compile("^([\\d-]+\\s[\\d:]+)\\s+(?:" + Config.L2TOPRU_PREFIX + "-)*([^\\s]+)\\s+x(\\d{1,2})$", 8);
      ThreadPoolManager.getInstance().scheduleAtFixedRate(new L2TopRuManager.L2TopRuTask(), Config.L2TOPRU_DELAY, Config.L2TOPRU_DELAY);
    }
  }

  protected ArrayList<L2TopRuManager.L2TopRuVote> filterVotes(ArrayList<L2TopRuManager.L2TopRuVote> votes) {
    ArrayList<L2TopRuManager.L2TopRuVote> result = new ArrayList<>();
    HashMap<String, Integer> chars = new HashMap<>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      stmt = con.createStatement();
      rset = stmt.executeQuery("SELECT `obj_Id`,`char_name` FROM `characters`");

      while (rset.next()) {
        chars.put(rset.getString("char_name"), rset.getInt("obj_Id"));
      }
    } catch (Exception var12) {
      var12.printStackTrace();
    } finally {
      DbUtils.closeQuietly(con, stmt, rset);
    }

//    int charObjId = false;

    for (L2TopRuVote vote : votes) {
      if (chars.containsKey(vote.charname)) {
        int charObjId = chars.get(vote.charname);
        if (this.isRewardReq(charObjId, vote.datetime)) {
          vote.char_obj_id = charObjId;
          result.add(vote);
        }
      }
    }

    return result;
  }

  private boolean isRewardReq(int charObjId, long date) {
    long lastDate;
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rset;
    if (_voteDateCache.containsKey(charObjId)) {
      lastDate = _voteDateCache.get(charObjId);
      if (date > lastDate) {
        _voteDateCache.put(charObjId, date);

        try {
          con = DatabaseFactory.getInstance().getConnection();
          pstmt = con.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
          pstmt.setInt(1, charObjId);
          pstmt.setLong(2, date);
          pstmt.executeUpdate();
          DbUtils.closeQuietly(con, pstmt);
        } catch (Exception var16) {
          var16.printStackTrace();
        }

        return true;
      }
    } else {
      try {
        con = DatabaseFactory.getInstance().getConnection();
        pstmt = con.prepareStatement("SELECT `obj_Id`,`last_vote` FROM `l2topru_votes` WHERE `obj_Id` = ?");
        pstmt.setInt(1, charObjId);
        rset = pstmt.executeQuery();
        boolean var9;
        if (!rset.next()) {
          DbUtils.closeQuietly(pstmt, rset);
          _voteDateCache.put(charObjId, date);

          try {
            pstmt = con.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
            pstmt.setInt(1, charObjId);
            pstmt.setLong(2, date);
            pstmt.executeUpdate();
            DbUtils.closeQuietly(con, pstmt);
          } catch (Exception var18) {
            var18.printStackTrace();
          }

          return true;
        }

        lastDate = rset.getLong("last_vote");
        DbUtils.closeQuietly(pstmt, rset);
        if (date > lastDate) {
          _voteDateCache.put(charObjId, date);

          try {
            pstmt = con.prepareStatement("REPLACE DELAYED INTO `l2topru_votes`(`obj_Id`,`last_vote`) VALUES (?,?)");
            pstmt.setInt(1, charObjId);
            pstmt.setLong(2, date);
            pstmt.executeUpdate();
            DbUtils.closeQuietly(con, pstmt);
          } catch (Exception var17) {
            var17.printStackTrace();
          }

          var9 = true;
          return var9;
        }

        _voteDateCache.put(charObjId, lastDate);
      } catch (Exception var19) {
        var19.printStackTrace();
      } finally {
        DbUtils.closeQuietly(con);
      }
    }

    return false;
  }

  private void giveItem(int charObjId, int itemId, int itemCount) {
    if (charObjId >= 1) {
      Player player = GameObjectsStorage.getPlayer(charObjId);
      if (player != null) {
        player.sendMessage((new CustomMessage("l2.gameserver.taskmanager.L2TopRuManager", player)).addItemName(player.getInventory().addItem(itemId, itemCount)));
      } else {
        ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
        newItem.setCount(itemCount);
        newItem.setOwnerId(charObjId);
        newItem.setLocation(ItemLocation.INVENTORY);
        newItem.save();
      }

    }
  }

  private void rewardVotes(ArrayList<L2TopRuManager.L2TopRuVote> votes) {

    for (L2TopRuVote vote : votes) {
      switch (vote.type) {
        case WEB:
          log.info("L2TopRuManager: Rewarding " + vote.toString());
          this.giveItem(vote.char_obj_id, Config.L2TOPRU_WEB_REWARD_ITEMID, Config.L2TOPRU_WEB_REWARD_ITEMCOUNT);
          break;
        case SMS:
          log.info("L2TopRuManager: Rewarding " + vote.toString());
          if (Config.L2TOPRU_SMS_REWARD_VOTE_MULTI) {
            this.giveItem(vote.char_obj_id, Config.L2TOPRU_SMS_REWARD_ITEMID, Config.L2TOPRU_SMS_REWARD_ITEMCOUNT * vote.count);
          }
      }
    }

  }

  private ArrayList<L2TopRuManager.L2TopRuVote> getAllVotes() {
    ArrayList<L2TopRuManager.L2TopRuVote> result = new ArrayList<>();

    try {
      Matcher m = this._webPattern.matcher(this.getPage(Config.L2TOPRU_WEB_VOTE_URL));

      L2TopRuManager.L2TopRuVote vote;
      String dateTimeStr;
      String nameStr;
      while (m.find()) {
        dateTimeStr = m.group(1);
        nameStr = m.group(2);
        if (Util.isMatchingRegexp(nameStr, Config.CNAME_TEMPLATE)) {
          vote = new L2TopRuManager.L2TopRuVote(dateTimeStr, nameStr);
          result.add(vote);
        }
      }

      m = this._smsPattern.matcher(this.getPage(Config.L2TOPRU_SMS_VOTE_URL));

      while (m.find()) {
        dateTimeStr = m.group(1);
        nameStr = m.group(2);
        String mulStr = m.group(3);
        if (Util.isMatchingRegexp(nameStr, Config.CNAME_TEMPLATE)) {
          vote = new L2TopRuManager.L2TopRuVote(dateTimeStr, nameStr, mulStr);
          result.add(vote);
        }
      }

      result.sort(new L2TopRuVoteComparator<>());
    } catch (Exception var7) {
      var7.printStackTrace();
    }

    return result;
  }

  private void tick() {
    this.rewardVotes(this.filterVotes(this.getAllVotes()));
  }

  private String getPage(String uri) throws Exception {
    try {
      URL url = new URL(uri);
      URLConnection conn = url.openConnection();
      conn.addRequestProperty("Host", url.getHost());
      conn.addRequestProperty("Accept", "*/*");
      conn.addRequestProperty("Connection", "close");
      conn.addRequestProperty("User-Agent", USERAGENT);
      conn.setConnectTimeout(30000);
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "cp1251"));
      StringBuilder builder = new StringBuilder();
      String line;

      while ((line = in.readLine()) != null) {
        builder.append(line).append("\n");
      }

      return builder.toString();
    } catch (Exception var7) {
      return "";
    }
  }

  private static class L2TopRuVoteComparator<T> implements Comparator<L2TopRuManager.L2TopRuVote> {
    private L2TopRuVoteComparator() {
    }

    public int compare(L2TopRuManager.L2TopRuVote o, L2TopRuManager.L2TopRuVote o1) {
      if (o.datetime == o1.datetime) {
        return 0;
      } else if (o.datetime < o1.datetime) {
        return -2147483648;
      } else {
        return 2147483647;
      }
    }
  }

  private static class L2TopRuVote {
    public long datetime;
    public String charname;
    public int count;
    public int char_obj_id = -1;
    public L2TopRuManager.L2TopRuVoteType type;

    public L2TopRuVote(String date, String charName, String itemcount) throws Exception {
      this.datetime = L2TopRuManager.DATE_FORMAT.parse(date).getTime() / 1000L;
      this.count = Byte.parseByte(itemcount);
      this.charname = charName;
      this.type = L2TopRuManager.L2TopRuVoteType.SMS;
    }

    public L2TopRuVote(String date, String charName) throws Exception {
      this.datetime = L2TopRuManager.DATE_FORMAT.parse(date).getTime() / 1000L;
      this.charname = charName;
      this.count = 1;
      this.type = L2TopRuManager.L2TopRuVoteType.WEB;
    }

    public String toString() {
      return this.charname + "-" + this.count + "[" + this.char_obj_id + "(" + this.datetime + "|" + this.type.name() + ")]";
    }
  }

  private enum L2TopRuVoteType {
    WEB,
    SMS;

    L2TopRuVoteType() {
    }
  }

  private static class L2TopRuTask implements Runnable {
    private L2TopRuTask() {
    }

    public void run() {
      L2TopRuManager.getInstance().tick();
    }
  }
}
