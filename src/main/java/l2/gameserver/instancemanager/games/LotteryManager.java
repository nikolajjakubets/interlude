//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager.games;

import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

@Slf4j
public class LotteryManager {
  public static final long SECOND = 1000L;
  public static final long MINUTE = 60000L;
  private static LotteryManager _instance;
  private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
  private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
  private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
  private static final String SELECT_LOTTERY_ITEM = "SELECT items.enchant AS `enchant`, items_options.damaged AS `damaged` FROM items, items_options WHERE items_options.blessed = ? AND items.item_id = items_options.item_id AND items.item_type = 4442";
  private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?";
  protected int _number = 1;
  protected int _prize;
  protected boolean _isSellingTickets;
  protected boolean _isStarted;
  protected long _enddate;

  public LotteryManager() {
    this._prize = Config.SERVICES_LOTTERY_PRIZE;
    this._isSellingTickets = false;
    this._isStarted = false;
    this._enddate = System.currentTimeMillis();
    if (Config.SERVICES_ALLOW_LOTTERY) {
      (new LotteryManager.startLottery()).run();
    }

  }

  public void increasePrize(int count) {
    this._prize += count;
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?");
      statement.setInt(1, this.getPrize());
      statement.setInt(2, this.getPrize());
      statement.setInt(3, this.getId());
      statement.execute();
    } catch (SQLException var8) {
      log.error("Lottery: Could not increase current lottery prize: " + var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private boolean restoreLotteryData() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    boolean var4;
    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1");
      rset = statement.executeQuery();
      if (!rset.next()) {
        return true;
      }

      this._number = rset.getInt("idnr");
      if (rset.getInt("finished") == 1) {
        ++this._number;
        this._prize = rset.getInt("newprize");
        return true;
      }

      this._prize = rset.getInt("prize");
      this._enddate = rset.getLong("enddate");
      if (this._enddate <= System.currentTimeMillis() + 120000L) {
        (new LotteryManager.finishLottery()).run();
        return false;
      }

      if (this._enddate <= System.currentTimeMillis()) {
        return true;
      }

      this._isStarted = true;
      ThreadPoolManager.getInstance().schedule(new LotteryManager.finishLottery(), this._enddate - System.currentTimeMillis());
      if (this._enddate > System.currentTimeMillis() + 720000L) {
        this._isSellingTickets = true;
        ThreadPoolManager.getInstance().schedule(new LotteryManager.stopSellingTickets(), this._enddate - System.currentTimeMillis() - 600000L);
      }

      var4 = false;
    } catch (SQLException var8) {
      log.error("Lottery: Could not restore lottery data: " + var8);
      return true;
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return var4;
  }

  private void announceLottery() {
    if (Config.SERVICES_ALLOW_LOTTERY) {
      log.info("Lottery: Starting ticket sell for lottery #" + this.getId() + ".");
    }

    this._isSellingTickets = true;
    this._isStarted = true;
    Announcements.getInstance().announceToAll("Lottery tickets are now available for Lucky Lottery #" + this.getId() + ".");
  }

  private void scheduleEndOfLottery() {
    Calendar finishtime = Calendar.getInstance();
    finishtime.setTimeInMillis(this._enddate);
    finishtime.set(12, 0);
    finishtime.set(13, 0);
    if (finishtime.get(7) == 1) {
      finishtime.set(11, 19);
      this._enddate = finishtime.getTimeInMillis();
      this._enddate += 604800000L;
    } else {
      finishtime.set(7, 1);
      finishtime.set(11, 19);
      this._enddate = finishtime.getTimeInMillis();
    }

    ThreadPoolManager.getInstance().schedule(new LotteryManager.stopSellingTickets(), this._enddate - System.currentTimeMillis() - 600000L);
    ThreadPoolManager.getInstance().schedule(new LotteryManager.finishLottery(), this._enddate - System.currentTimeMillis());
  }

  private void createNewLottery() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)");
      statement.setInt(1, 1);
      statement.setInt(2, this.getId());
      statement.setLong(3, this.getEndDate());
      statement.setInt(4, this.getPrize());
      statement.setInt(5, this.getPrize());
      statement.execute();
    } catch (SQLException var7) {
      log.error("Lottery: Could not store new lottery data: " + var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public int[] decodeNumbers(int enchant, int type2) {
    int[] res = new int[5];
    int id = 0;

    int nr;
    int val;
    for (nr = 1; enchant > 0; ++nr) {
      val = enchant / 2;
      if ((double) val != (double) enchant / 2.0D) {
        res[id++] = nr;
      }

      enchant /= 2;
    }

    for (nr = 17; type2 > 0; ++nr) {
      val = type2 / 2;
      if ((double) val != (double) type2 / 2.0D) {
        res[id++] = nr;
      }

      type2 /= 2;
    }

    return res;
  }

  public int[] checkTicket(int id, int enchant, int type2) {
    int[] res = new int[]{0, 0};
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?");
      statement.setInt(1, id);
      rset = statement.executeQuery();
      if (rset.next()) {
        int curenchant = rset.getInt("number1") & enchant;
        int curtype2 = rset.getInt("number2") & type2;
        if (curenchant == 0 && curtype2 == 0) {
          int[] var19 = res;
          return var19;
        }

        int count = 0;

        for (int i = 1; i <= 16; ++i) {
          int val = curenchant / 2;
          if ((double) val != (double) curenchant / 2.0D) {
            ++count;
          }

          int val2 = curtype2 / 2;
          if ((double) val2 != (double) curtype2 / 2.0D) {
            ++count;
          }

          curenchant = val;
          curtype2 = val2;
        }

        switch (count) {
          case 0:
            break;
          case 1:
          case 2:
          default:
            res[0] = 4;
            res[1] = 200;
            break;
          case 3:
            res[0] = 3;
            res[1] = rset.getInt("prize3");
            break;
          case 4:
            res[0] = 2;
            res[1] = rset.getInt("prize2");
            break;
          case 5:
            res[0] = 1;
            res[1] = rset.getInt("prize1");
        }
      }
    } catch (SQLException var17) {
      log.error("Lottery: Could not check lottery ticket #" + id + ": " + var17);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return res;
  }

  public int[] checkTicket(ItemInstance item) {
    return this.checkTicket(item.getBlessed(), item.getEnchantLevel(), item.getDamaged());
  }

  public boolean isSellableTickets() {
    return this._isSellingTickets;
  }

  public boolean isStarted() {
    return this._isStarted;
  }

  public static LotteryManager getInstance() {
    if (_instance == null) {
      _instance = new LotteryManager();
    }

    return _instance;
  }

  public int getId() {
    return this._number;
  }

  public int getPrize() {
    return this._prize;
  }

  public long getEndDate() {
    return this._enddate;
  }

  private class finishLottery extends RunnableImpl {
    protected finishLottery() {
    }

    public void runImpl() throws Exception {
      if (Config.SERVICES_ALLOW_LOTTERY) {
        log.info("Lottery: Ending lottery #" + LotteryManager.this.getId() + ".");
      }

      int[] luckynums = new int[5];
      int luckynum = 0;

      int enchant;
      int count1;
      for (enchant = 0; enchant < 5; ++enchant) {
        boolean found = true;

        while (found) {
          luckynum = Rnd.get(20) + 1;
          found = false;

          for (count1 = 0; count1 < enchant; ++count1) {
            if (luckynums[count1] == luckynum) {
              found = true;
            }
          }
        }

        luckynums[enchant] = luckynum;
      }

      if (Config.SERVICES_ALLOW_LOTTERY) {
        log.info("Lottery: The lucky numbers are " + luckynums[0] + ", " + luckynums[1] + ", " + luckynums[2] + ", " + luckynums[3] + ", " + luckynums[4] + ".");
      }

      enchant = 0;
      int type2 = 0;

      for (count1 = 0; count1 < 5; ++count1) {
        if (luckynums[count1] < 17) {
          enchant = (int) ((double) enchant + Math.pow(2.0D, (double) (luckynums[count1] - 1)));
        } else {
          type2 = (int) ((double) type2 + Math.pow(2.0D, (double) (luckynums[count1] - 17)));
        }
      }

      if (Config.SERVICES_ALLOW_LOTTERY) {
        log.info("Lottery: Encoded lucky numbers are " + enchant + ", " + type2);
      }

      count1 = 0;
      int count2 = 0;
      int count3 = 0;
      int count4 = 0;
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      int curenchant;
      int prize1;
      int prize2;
      int prize3;
      int newprize;
      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("SELECT items.enchant AS `enchant`, items_options.damaged AS `damaged` FROM items, items_options WHERE items_options.blessed = ? AND items.item_id = items_options.item_id AND items.item_type = 4442");
        statement.setInt(1, LotteryManager.this.getId());
        rset = statement.executeQuery();

        label453:
        while (true) {
          do {
            if (!rset.next()) {
              break label453;
            }

            curenchant = rset.getInt("enchant") & enchant;
            prize1 = rset.getInt("damaged") & type2;
          } while (curenchant == 0 && prize1 == 0);

          prize2 = 0;

          for (prize3 = 1; prize3 <= 16; ++prize3) {
            newprize = curenchant / 2;
            if ((double) newprize != (double) curenchant / 2.0D) {
              ++prize2;
            }

            int val2 = prize1 / 2;
            if ((double) val2 != (double) prize1 / 2.0D) {
              ++prize2;
            }

            curenchant = newprize;
            prize1 = val2;
          }

          if (prize2 == 5) {
            ++count1;
          } else if (prize2 == 4) {
            ++count2;
          } else if (prize2 == 3) {
            ++count3;
          } else if (prize2 > 0) {
            ++count4;
          }
        }
      } catch (SQLException var30) {
        log.warn("Lottery: Could restore lottery data: " + var30);
      } finally {
        DbUtils.closeQuietly(con, statement, rset);
      }

      curenchant = count4 * Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
      prize1 = 0;
      prize2 = 0;
      prize3 = 0;
      if (count1 > 0) {
        prize1 = (int) ((double) (LotteryManager.this.getPrize() - curenchant) * Config.SERVICES_LOTTERY_5_NUMBER_RATE / (double) count1);
      }

      if (count2 > 0) {
        prize2 = (int) ((double) (LotteryManager.this.getPrize() - curenchant) * Config.SERVICES_LOTTERY_4_NUMBER_RATE / (double) count2);
      }

      if (count3 > 0) {
        prize3 = (int) ((double) (LotteryManager.this.getPrize() - curenchant) * Config.SERVICES_LOTTERY_3_NUMBER_RATE / (double) count3);
      }

      if (prize1 == 0 && prize2 == 0 && prize3 == 0) {
        newprize = LotteryManager.this.getPrize();
      } else {
        newprize = LotteryManager.this.getPrize() + prize1 + prize2 + prize3;
      }

      if (Config.SERVICES_ALLOW_LOTTERY) {
        log.info("Lottery: Jackpot for next lottery is " + newprize + ".");
      }

      SystemMessage sm;
      if (count1 > 0) {
        sm = new SystemMessage(1112);
        sm.addNumber(LotteryManager.this.getId());
        sm.addNumber(LotteryManager.this.getPrize());
        sm.addNumber(count1);
        Announcements.getInstance().announceToAll(sm);
      } else {
        sm = new SystemMessage(1113);
        sm.addNumber(LotteryManager.this.getId());
        sm.addNumber(LotteryManager.this.getPrize());
        Announcements.getInstance().announceToAll(sm);
      }

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?");
        statement.setInt(1, LotteryManager.this.getPrize());
        statement.setInt(2, newprize);
        statement.setInt(3, enchant);
        statement.setInt(4, type2);
        statement.setInt(5, prize1);
        statement.setInt(6, prize2);
        statement.setInt(7, prize3);
        statement.setInt(8, LotteryManager.this.getId());
        statement.execute();
      } catch (SQLException var28) {
        log.warn("Lottery: Could not store finished lottery data: " + var28);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      ThreadPoolManager.getInstance().schedule(LotteryManager.this.new startLottery(), 60000L);
      ++LotteryManager.this._number;
      LotteryManager.this._isStarted = false;
    }
  }

  private class stopSellingTickets extends RunnableImpl {
    protected stopSellingTickets() {
    }

    public void runImpl() throws Exception {
      if (Config.SERVICES_ALLOW_LOTTERY) {
        log.info("Lottery: Stopping ticket sell for lottery #" + LotteryManager.this.getId() + ".");
      }

      LotteryManager.this._isSellingTickets = false;
      Announcements.getInstance().announceToAll(Msg.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED);
    }
  }

  private class startLottery extends RunnableImpl {
    protected startLottery() {
    }

    public void runImpl() throws Exception {
      if (LotteryManager.this.restoreLotteryData()) {
        LotteryManager.this.announceLottery();
        LotteryManager.this.scheduleEndOfLottery();
        LotteryManager.this.createNewLottery();
      }

    }
  }
}
