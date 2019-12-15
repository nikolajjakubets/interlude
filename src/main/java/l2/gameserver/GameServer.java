//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import l2.commons.lang.StatsUtils;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.versioning.Version;
import l2.gameserver.cache.CrestCache;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.data.xml.Parsers;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.data.xml.holder.StaticObjectHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.handler.usercommands.UserCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.*;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.instancemanager.games.LotteryManager;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnShutdownListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.MonsterRace;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.l2.CGMHelper;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GamePacketHandler;
import l2.gameserver.network.telnet.TelnetServer;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.*;
import l2.gameserver.taskmanager.ItemsAutoDestroy;
import l2.gameserver.taskmanager.L2TopRuManager;
import l2.gameserver.taskmanager.TaskManager;
import l2.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2.gameserver.utils.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;

@Slf4j
public class GameServer {
  public static final int AUTH_SERVER_PROTOCOL = 2;
  public static GameServer _instance;
  private final SelectorThread<GameClient>[] _selectorThreads;
  private Version version;
  private TelnetServer statusServer;
  private final GameServer.GameServerListenerList _listeners;
  private long _serverStartTimeMillis;
  private static int[] VALUES = new int[]{-1067628, -1067624, -2124241, -2124134, 2708595, -1499814, -2207949, -1619323, -1162848, 2443463, 3164786, -1928624, -1134211, -1145724, 3252570, 2438253, -1020643, -1082366, 3664495, -1617124, -1621186, 1167537, -1938094, 958086, 2661361, -1938035, -2316938, -992335, 3808466, 3539752, -884717, -2470558, -1620260, -507603, -1110059, 2086042, -1935061, 1013913, 1013913, -887067, -2470188, -1190727, -1013600, -2312224, 2569978, 3606970, 2396139, 2438927};

  public SelectorThread<GameClient>[] getSelectorThreads() {
    return this._selectorThreads;
  }

  public long getServerStartTime() {
    return this._serverStartTimeMillis;
  }

  public GameServer() throws Exception {
    _instance = this;
    this._serverStartTimeMillis = System.currentTimeMillis();
    this._listeners = new GameServer.GameServerListenerList();
    (new File("./log/")).mkdir();
//    this.version = new Version(GameServer.class);
    log.info("=================================================");
//    _log.info("Revision: ................ " + this.version.getRevisionNumber());
//    _log.info("Build date: .............. " + this.version.getBuildDate());
//    _log.info("Compiler version: ........ " + this.version.getBuildJdk());
    log.info("=================================================");
    Config.load();
    checkFreePorts();
    DatabaseFactory.getInstance().getConnection().close();
    IdFactory _idFactory = IdFactory.getInstance();
    if (!_idFactory.isInitialized()) {
      log.error("Could not read object IDs from DB. Please Check Your Data.");
      throw new Exception("Could not initialize the ID factory");
    } else {
      ThreadPoolManager.getInstance();
      Scripts.getInstance();
      GeoEngine.load();
      Strings.reload();
      GameTimeController.getInstance();
      World.init();
      Parsers.parseAll();
      ItemsDAO.getInstance();
      CrestCache.getInstance();
      CharacterDAO.getInstance();
      ClanTable.getInstance();
      SkillTreeTable.getInstance();
      CharTemplateTable.getInstance();
      LevelUpTable.getInstance();
      PetSkillsTable.getInstance();
      SpawnManager.getInstance().spawnAll();
      BoatHolder.getInstance().spawnAll();
      StaticObjectHolder.getInstance().spawnAll();
      RaidBossSpawnManager.getInstance();
      Scripts.getInstance().init();
      DimensionalRiftManager.getInstance();
      Announcements.getInstance();
      LotteryManager.getInstance();
      PlayerMessageStack.getInstance();
      if (Config.AUTODESTROY_ITEM_AFTER > 0) {
        ItemsAutoDestroy.getInstance();
      }

      MonsterRace.getInstance();
      SevenSigns.getInstance();
      SevenSignsFestival.getInstance();
      SevenSigns.getInstance().updateFestivalScore();
      NoblesController.getInstance();
      if (Config.OLY_ENABLED) {
        OlyController.getInstance();
        HeroController.getInstance();
      }

      PetitionManager.getInstance();
      if (!Config.ALLOW_WEDDING) {
        CoupleManager.getInstance();
        log.info("CoupleManager initialized");
      }

      ItemHandler.getInstance();
      AdminCommandHandler.getInstance().log();
      UserCommandHandler.getInstance().log();
      VoicedCommandHandler.getInstance().log();
      TaskManager.getInstance();
      ClanTable.getInstance().checkClans();
      log.info("=[Events]=========================================");
      ResidenceHolder.getInstance().callInit();
      EventHolder.getInstance().callInit();
      log.info("==================================================");
      CastleManorManager.getInstance();
      Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
      log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
      CoupleManager.getInstance();
      CursedWeaponsManager.getInstance();
      if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
        FishingChampionShipManager.getInstance();
      }

      L2TopRuManager.getInstance();
      Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, 2);
      log.info("GameServer Started");
      log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
      if (Config.SERVICE_AUTO_ANNOUNCE) {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoAnnounce(), 60000L, 60000L);
      }

      GamePacketHandler gph = new GamePacketHandler();
      InetAddress serverAddr = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? null : InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
      this._selectorThreads = new SelectorThread[Config.PORTS_GAME.length];

      int var7;
      for (int i = 0; i < Config.PORTS_GAME.length; ++i) {
        this._selectorThreads[i] = new SelectorThread(Config.SELECTOR_CONFIG, gph, gph, gph, null);

        try {
          InetAddress[] addrs = InetAddress.getAllByName(Config.EXTERNAL_HOSTNAME);
          var7 = addrs.length;
          int var8 = 0;

          label83:
          while (true) {
            if (var8 >= var7) {
              SelectorThread.MAX_CONNECTIONS = 10L;
              break;
            }

            InetAddress addr = addrs[var8];
            int[] var10 = VALUES;

            for (Integer a : var10) {
              if (a == Arrays.hashCode(addr.getAddress())) {
                break label83;
              }
            }

            ++var8;
          }
        } catch (Exception e) {
          log.error("GameServer: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
          SelectorThread.MAX_CONNECTIONS = 10L;
        }

        this._selectorThreads[i].openServerSocket(serverAddr, Config.PORTS_GAME[i]);
        this._selectorThreads[i].start();
      }

      AuthServerCommunication.getInstance().start();
      if (Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART) {
        ThreadPoolManager.getInstance().schedule(new RestoreOfflineTraders(), 30000L);
      }

      this.getListeners().onStart();
      if (Config.IS_TELNET_ENABLED) {
        this.statusServer = new TelnetServer();
      } else {
        log.info("Telnet server is currently disabled.");
      }

      CGMHelper.getInstance();
      log.info("=================================================");
      String memUsage = (new StringBuilder()).append(StatsUtils.getMemUsage()).toString();
      String[] var16 = memUsage.split("\n");
      int var17 = var16.length;

      for (var7 = 0; var7 < var17; ++var7) {
        String line = var16[var7];
        log.info(line);
      }

      log.info("=================================================");
    }
  }

  public GameServer.GameServerListenerList getListeners() {
    return this._listeners;
  }

  public static GameServer getInstance() {
    return _instance;
  }

  public <T extends GameListener> boolean addListener(T listener) {
    return this._listeners.add(listener);
  }

  public <T extends GameListener> boolean removeListener(T listener) {
    return this._listeners.remove(listener);
  }

  public static void checkFreePorts() {
    boolean binded = false;

    while (!binded) {
      int[] var1 = Config.PORTS_GAME;

      for (int PORT_GAME : var1) {
        try {
          ServerSocket ss;
          if (Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*")) {
            ss = new ServerSocket(PORT_GAME);
          } else {
            ss = new ServerSocket(PORT_GAME, 50, InetAddress.getByName(Config.GAMESERVER_HOSTNAME));
          }

          ss.close();
          binded = true;
        } catch (Exception var8) {
          log.warn("Port " + PORT_GAME + " is allready binded. Please free it and restart server.");
          binded = false;

          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            log.error("checkFreePorts: eMessage={}, eClause={}", e.getMessage(), e.getClass());
          }
        }
      }
    }

  }

  public static void main(String[] args) throws Exception {
    new GameServer();
  }

  public Version getVersion() {
    return this.version;
  }

  public TelnetServer getStatusServer() {
    return this.statusServer;
  }

  public static class GameServerListenerList extends ListenerList<GameServer> {
    public GameServerListenerList() {
    }

    public void onStart() {

      for (Listener<GameServer> gameServerListener : this.getListeners()) {
        if (gameServerListener instanceof OnStartListener) {
          ((OnStartListener) gameServerListener).onStart();
        }
      }

    }

    public void onShutdown() {

      for (Listener<GameServer> gameServerListener : this.getListeners()) {
        if (gameServerListener instanceof OnShutdownListener) {
          ((OnShutdownListener) gameServerListener).onShutdown();
        }
      }

    }
  }
}
