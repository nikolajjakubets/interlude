//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.time.cron.SchedulingPattern.InvalidPatternException;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.oly.StadiumPool;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shutdown extends Thread {
  private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);
  public static final int SHUTDOWN = 0;
  public static final int RESTART = 2;
  public static final int NONE = -1;
  private static final Shutdown _instance = new Shutdown();
  private Timer counter;
  private int shutdownMode;
  private int shutdownCounter;

  public static final Shutdown getInstance() {
    return _instance;
  }

  private Shutdown() {
    this.setName(this.getClass().getSimpleName());
    this.setDaemon(true);
    this.shutdownMode = -1;
  }

  public int getSeconds() {
    return this.shutdownMode == -1 ? -1 : this.shutdownCounter;
  }

  public int getMode() {
    return this.shutdownMode;
  }

  public synchronized void schedule(int seconds, int shutdownMode) {
    if (seconds >= 0) {
      if (this.counter != null) {
        this.counter.cancel();
      }

      this.shutdownMode = shutdownMode;
      this.shutdownCounter = seconds;
      _log.info("Scheduled server " + (shutdownMode == 0 ? "shutdown" : "restart") + " in " + Util.formatTime(seconds) + ".");
      this.counter = new Timer("ShutdownCounter", true);
      this.counter.scheduleAtFixedRate(new Shutdown.ShutdownCounter(), 0L, 1000L);
    }
  }

  public void schedule(String time, int shutdownMode) {
    SchedulingPattern cronTime;
    try {
      cronTime = new SchedulingPattern(time);
    } catch (InvalidPatternException var5) {
      return;
    }

    int seconds = (int)(cronTime.next(System.currentTimeMillis()) / 1000L - System.currentTimeMillis() / 1000L);
    this.schedule(seconds, shutdownMode);
  }

  public synchronized void cancel() {
    this.shutdownMode = -1;
    if (this.counter != null) {
      this.counter.cancel();
    }

    this.counter = null;
  }

  public void run() {
    System.out.println("Shutting down LS/GS communication...");
    AuthServerCommunication.getInstance().shutdown();
    System.out.println("Shutting down scripts...");
    Scripts.getInstance().shutdown();
    System.out.println("Disconnecting players...");
    this.disconnectAllPlayers();
    System.out.println("Saving data...");
    this.saveData();
    NoblesController.getInstance().SaveNobleses();
    if (Config.OLY_ENABLED) {
      try {
        ParticipantPool.getInstance().FreePools();
        StadiumPool.getInstance().FreeStadiums();
        OlyController.getInstance().shutdown();
        System.out.println("Olympiad System: Oly cleaned and data saved!");
      } catch (Exception var9) {
        var9.printStackTrace();
      }
    }

    try {
      System.out.println("Shutting down thread pool...");
      ThreadPoolManager.getInstance().shutdown();
    } catch (Exception var8) {
      var8.printStackTrace();
    }

    System.out.println("Shutting down selector...");
    if (GameServer.getInstance() != null) {
      SelectorThread[] var1 = GameServer.getInstance().getSelectorThreads();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        SelectorThread st = var1[var3];

        try {
          st.shutdown();
        } catch (Exception var7) {
          var7.printStackTrace();
        }
      }
    }

    try {
      System.out.println("Shutting down database communication...");
      DatabaseFactory.getInstance().shutdown();
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    System.out.println("Shutdown finished.");
  }

  private void saveData() {
    try {
      if (!SevenSigns.getInstance().isSealValidationPeriod()) {
        SevenSignsFestival.getInstance().saveFestivalData(false);
        System.out.println("SevenSignsFestival: Data saved.");
      }
    } catch (Exception var9) {
      var9.printStackTrace();
    }

    try {
      SevenSigns.getInstance().saveSevenSignsData(0, true);
      System.out.println("SevenSigns: Data saved.");
    } catch (Exception var8) {
      var8.printStackTrace();
    }

    if (Config.ALLOW_WEDDING) {
      try {
        CoupleManager.getInstance().store();
        System.out.println("CoupleManager: Data saved.");
      } catch (Exception var7) {
        var7.printStackTrace();
      }
    }

    try {
      FishingChampionShipManager.getInstance().shutdown();
      System.out.println("FishingChampionShipManager: Data saved.");
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    try {
      HeroController.getInstance().saveHeroes();
      System.out.println("Hero: Data saved.");
    } catch (Exception var5) {
      var5.printStackTrace();
    }

    try {
      Collection<Residence> residences = ResidenceHolder.getInstance().getResidences();
      Iterator var2 = residences.iterator();

      while(var2.hasNext()) {
        Residence residence = (Residence)var2.next();
        residence.update();
      }

      System.out.println("Residences: Data saved.");
    } catch (Exception var10) {
      var10.printStackTrace();
    }

    if (Config.ALLOW_CURSED_WEAPONS) {
      try {
        CursedWeaponsManager.getInstance().saveData();
        System.out.println("CursedWeaponsManager: Data saved,");
      } catch (Exception var4) {
        var4.printStackTrace();
      }
    }

  }

  private void disconnectAllPlayers() {
    Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var1.hasNext()) {
      Player player = (Player)var1.next();

      try {
        player.logout();
      } catch (Exception var4) {
        System.out.println("Error while disconnecting: " + player + "!");
        var4.printStackTrace();
      }
    }

  }

  private class ShutdownCounter extends TimerTask {
    private ShutdownCounter() {
    }

    public void run() {
      switch(Shutdown.this.shutdownCounter) {
        case 0:
          switch(Shutdown.this.shutdownMode) {
            case 0:
              Runtime.getRuntime().exit(0);
              break;
            case 2:
              Runtime.getRuntime().exit(2);
          }

          this.cancel();
          return;
        case 5:
        case 10:
        case 20:
        case 30:
          Announcements.getInstance().announceToAll((new SystemMessage(1)).addNumber(Shutdown.this.shutdownCounter));
          break;
        case 60:
        case 120:
        case 180:
        case 240:
        case 300:
        case 600:
        case 900:
        case 1800:
          switch(Shutdown.this.shutdownMode) {
            case 0:
              Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(Shutdown.this.shutdownCounter / 60)});
              break;
            case 2:
              Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_RESTARTED_IN_S1_MINUTES", new String[]{String.valueOf(Shutdown.this.shutdownCounter / 60)});
          }
      }

      Shutdown.this.shutdownCounter--;
    }
  }
}
