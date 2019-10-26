//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.util.concurrent.atomic.AtomicLong;
import l2.gameserver.instancemanager.ServerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStats {
  private static final Logger _log = LoggerFactory.getLogger(GameStats.class);
  private static AtomicLong _updatePlayerBase = new AtomicLong(0L);
  private static AtomicLong _playerEnterGameCounter = new AtomicLong(0L);
  private static AtomicLong _taxSum = new AtomicLong(0L);
  private static long _taxLastUpdate;
  private static AtomicLong _rouletteSum = new AtomicLong(0L);
  private static long _rouletteLastUpdate;
  private static AtomicLong _adenaSum = new AtomicLong(0L);

  public GameStats() {
  }

  public static void increaseUpdatePlayerBase() {
    _updatePlayerBase.incrementAndGet();
  }

  public static long getUpdatePlayerBase() {
    return _updatePlayerBase.get();
  }

  public static void incrementPlayerEnterGame() {
    _playerEnterGameCounter.incrementAndGet();
  }

  public static long getPlayerEnterGame() {
    return _playerEnterGameCounter.get();
  }

  public static void addTax(long sum) {
    long taxSum = _taxSum.addAndGet(sum);
    if (System.currentTimeMillis() - _taxLastUpdate >= 10000L) {
      _taxLastUpdate = System.currentTimeMillis();
      ServerVariables.set("taxsum", taxSum);
    }
  }

  public static void addRoulette(long sum) {
    long rouletteSum = _rouletteSum.addAndGet(sum);
    if (System.currentTimeMillis() - _rouletteLastUpdate >= 10000L) {
      _rouletteLastUpdate = System.currentTimeMillis();
      ServerVariables.set("rouletteSum", rouletteSum);
    }
  }

  public static long getTaxSum() {
    return _taxSum.get();
  }

  public static long getRouletteSum() {
    return _rouletteSum.get();
  }

  public static void addAdena(long sum) {
    _adenaSum.addAndGet(sum);
  }

  public static long getAdena() {
    return _adenaSum.get();
  }

  static {
    _taxSum.set(ServerVariables.getLong("taxsum", 0L));
    _rouletteSum.set(ServerVariables.getLong("rouletteSum", 0L));
  }
}
