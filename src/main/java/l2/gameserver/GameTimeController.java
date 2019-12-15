//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ClientSetTime;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Iterator;

public class GameTimeController {
  private static final Logger _log = LoggerFactory.getLogger(GameTimeController.class);
  public static final int TICKS_PER_SECOND = 10;
  public static final int MILLIS_IN_TICK = 100;
  private static final GameTimeController _instance = new GameTimeController();
  private long _gameStartTime = this.getDayStartTime();
  private GameTimeController.GameTimeListenerList listenerEngine = new GameTimeController.GameTimeListenerList();
  private Runnable _dayChangeNotify = new GameTimeController.CheckSunState();

  public static final GameTimeController getInstance() {
    return _instance;
  }

  private GameTimeController() {
    GameServer.getInstance().addListener(new GameTimeController.OnStartListenerImpl());
    StringBuilder msg = new StringBuilder();
    msg.append("GameTimeController: initialized.").append(" ");
    msg.append("Current time is ");
    msg.append(this.getGameHour()).append(":");
    if (this.getGameMin() < 10) {
      msg.append("0");
    }

    msg.append(this.getGameMin());
    msg.append(" in the ");
    if (this.isNowNight()) {
      msg.append("night");
    } else {
      msg.append("day");
    }

    msg.append(".");
    _log.info(msg.toString());
    long nightStart = 0L;

    long dayStart;
    for(dayStart = 3600000L; this._gameStartTime + nightStart < System.currentTimeMillis(); nightStart += 14400000L) {
    }

    while(this._gameStartTime + dayStart < System.currentTimeMillis()) {
      dayStart += 14400000L;
    }

    dayStart -= System.currentTimeMillis() - this._gameStartTime;
    nightStart -= System.currentTimeMillis() - this._gameStartTime;
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this._dayChangeNotify, nightStart, 14400000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this._dayChangeNotify, dayStart, 14400000L);
  }

  private long getDayStartTime() {
    Calendar dayStart = Calendar.getInstance();
    int HOUR_OF_DAY = dayStart.get(11);
    dayStart.add(11, -(HOUR_OF_DAY + 1) % 4);
    dayStart.set(12, 0);
    dayStart.set(13, 0);
    dayStart.set(14, 0);
    return dayStart.getTimeInMillis();
  }

  public boolean isNowNight() {
    return this.getGameHour() < 6;
  }

  public int getGameTime() {
    return this.getGameTicks() / 100;
  }

  public int getGameHour() {
    return this.getGameTime() / 60 % 24;
  }

  public int getGameMin() {
    return this.getGameTime() % 60;
  }

  public int getGameTicks() {
    return (int)((System.currentTimeMillis() - this._gameStartTime) / 100L);
  }

  public GameTimeController.GameTimeListenerList getListenerEngine() {
    return this.listenerEngine;
  }

  public <T extends GameListener> boolean addListener(T listener) {
    return this.listenerEngine.add(listener);
  }

  public <T extends GameListener> boolean removeListener(T listener) {
    return this.listenerEngine.remove(listener);
  }

  @Slf4j
  protected static class GameTimeListenerList extends ListenerList<GameServer> {
    protected GameTimeListenerList() {
    }

    public void onDay() {
      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener listener = (Listener)var1.next();

        try {
          if (listener instanceof OnDayNightChangeListener) {
            ((OnDayNightChangeListener)listener).onDay();
          }
        } catch (Exception e) {
          log.error("onDay: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
          GameTimeController._log.warn("Exception during day change", e);
        }
      }

    }

    public void onNight() {

      for (Listener<GameServer> gameServerListener : this.getListeners()) {

        try {
          if (gameServerListener instanceof OnDayNightChangeListener) {
            ((OnDayNightChangeListener) gameServerListener).onNight();
          }
        } catch (Exception e) {
          log.error("onNight: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
          GameTimeController._log.warn("Exception during night change", e);
        }
      }

    }
  }

  public class CheckSunState extends RunnableImpl {
    public CheckSunState() {
    }

    public void runImpl() throws Exception {
      if (GameTimeController.this.isNowNight()) {
        GameTimeController.getInstance().getListenerEngine().onNight();
      } else {
        GameTimeController.getInstance().getListenerEngine().onDay();
      }

      ThreadPoolManager.getInstance().execute(new RunnableImpl() {
        public void runImpl() throws Exception {

          for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            player.checkDayNightMessages();
            player.sendPacket(new ClientSetTime());
          }

        }
      });
    }
  }

  private class OnStartListenerImpl implements OnStartListener {
    private OnStartListenerImpl() {
    }

    public void onStart() {
      ThreadPoolManager.getInstance().execute(GameTimeController.this._dayChangeNotify);
    }
  }
}
