//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Iterator;
import l2.commons.lang.StatsUtils;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.net.nio.impl.IAcceptFilter;
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
import l2.gameserver.instancemanager.AutoAnnounce;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.instancemanager.SpawnManager;
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
import l2.gameserver.tables.CharTemplateTable;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.LevelUpTable;
import l2.gameserver.tables.PetSkillsTable;
import l2.gameserver.tables.SkillTreeTable;
import l2.gameserver.taskmanager.ItemsAutoDestroy;
import l2.gameserver.taskmanager.L2TopRuManager;
import l2.gameserver.taskmanager.TaskManager;
import l2.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2.gameserver.utils.Strings;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {
  public static final int AUTH_SERVER_PROTOCOL = 2;
  private static final Logger _log = LoggerFactory.getLogger(GameServer.class);
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
    this.version = new Version(GameServer.class);
    _log.info("=================================================");
    _log.info("Revision: ................ " + this.version.getRevisionNumber());
    _log.info("Build date: .............. " + this.version.getBuildDate());
    _log.info("Compiler version: ........ " + this.version.getBuildJdk());
    _log.info("=================================================");
    Config.load();
    checkFreePorts();
    DatabaseFactory.getInstance().getConnection().close();
    IdFactory _idFactory = IdFactory.getInstance();
    if (!_idFactory.isInitialized()) {
      _log.error("Could not read object IDs from DB. Please Check Your Data.");
      throw new Exception("Could not initialize the ID factory");
    } else {
      CacheManager.getInstance();
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
        _log.info("CoupleManager initialized");
      }

      ItemHandler.getInstance();
      AdminCommandHandler.getInstance().log();
      UserCommandHandler.getInstance().log();
      VoicedCommandHandler.getInstance().log();
      TaskManager.getInstance();
      ClanTable.getInstance().checkClans();
      _log.info("=[Events]=========================================");
      ResidenceHolder.getInstance().callInit();
      EventHolder.getInstance().callInit();
      _log.info("==================================================");
      CastleManorManager.getInstance();
      Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
      _log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
      CoupleManager.getInstance();
      CursedWeaponsManager.getInstance();
      if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
        FishingChampionShipManager.getInstance();
      }

      L2TopRuManager.getInstance();
      Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, 2);
      _log.info("GameServer Started");
      _log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
      if (Config.SERVICE_AUTO_ANNOUNCE) {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoAnnounce(), 60000L, 60000L);
      }

      GamePacketHandler gph = new GamePacketHandler();
      InetAddress serverAddr = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? null : InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
      this._selectorThreads = new SelectorThread[Config.PORTS_GAME.length];

      int var7;
      for(int i = 0; i < Config.PORTS_GAME.length; ++i) {
        this._selectorThreads[i] = new SelectorThread(Config.SELECTOR_CONFIG, gph, gph, gph, (IAcceptFilter)null);

        SelectorThread var10000;
        try {
          InetAddress[] addrs = InetAddress.getAllByName(Config.EXTERNAL_HOSTNAME);
          InetAddress[] var6 = addrs;
          var7 = addrs.length;
          int var8 = 0;

          label83:
          while(true) {
            if (var8 >= var7) {
              var10000 = this._selectorThreads[i];
              SelectorThread.MAX_CONNECTIONS = 10L;
              break;
            }

            InetAddress addr = var6[var8];
            int[] var10 = VALUES;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
              Integer a = var10[var12];
              if (a == Arrays.hashCode(addr.getAddress())) {
                break label83;
              }
            }

            ++var8;
          }
        } catch (Exception var14) {
          var10000 = this._selectorThreads[i];
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
        _log.info("Telnet server is currently disabled.");
      }

      CGMHelper.getInstance();
      _log.info("=================================================");
      String memUsage = (new StringBuilder()).append(StatsUtils.getMemUsage()).toString();
      String[] var16 = memUsage.split("\n");
      int var17 = var16.length;

      for(var7 = 0; var7 < var17; ++var7) {
        String line = var16[var7];
        _log.info(line);
      }

      _log.info("=================================================");
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

    while(!binded) {
      int[] var1 = Config.PORTS_GAME;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        int PORT_GAME = var1[var3];

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
          _log.warn("Port " + PORT_GAME + " is allready binded. Please free it and restart server.");
          binded = false;

          try {
            Thread.sleep(1000L);
          } catch (InterruptedException var7) {
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

  public class GameServerListenerList extends ListenerList<GameServer> {
    public GameServerListenerList() {
    }

    public void onStart() {
      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener<GameServer> listener = (Listener)var1.next();
        if (OnStartListener.class.isInstance(listener)) {
          ((OnStartListener)listener).onStart();
        }
      }

    }

    public void onShutdown() {
      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener<GameServer> listener = (Listener)var1.next();
        if (OnShutdownListener.class.isInstance(listener)) {
          ((OnShutdownListener)listener).onShutdown();
        }
      }

    }
  }
}
