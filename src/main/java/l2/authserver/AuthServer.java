package l2.authserver;

import l2.authserver.database.L2DatabaseFactory;
import l2.authserver.network.gamecomm.GameServerCommunication;
import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.L2LoginPacketHandler;
import l2.authserver.network.l2.SelectorHelper;
import l2.commons.net.nio.impl.SelectorConfig;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.time.cron.SchedulingPattern.InvalidPatternException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;

import static l2.authserver.Config.RESTART_AT_TIME;

@Slf4j
public class AuthServer {
    public static final int SHUTDOWN = 0;
    public static final int RESTART = 2;
    public static final int NONE = -1;
    private static AuthServer authServer;
    private GameServerCommunication gameServerListener;
    private SelectorThread<L2LoginClient> selectorThread;

    public static AuthServer getInstance() {
        return authServer;
    }

    private AuthServer() throws Throwable {
        Config.initCrypt();
        GameServerManager.getInstance();
        L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
        SelectorHelper sh = new SelectorHelper();
        SelectorConfig sc = new SelectorConfig();
        this.selectorThread = new SelectorThread<L2LoginClient>(sc, loginPacketHandler, sh, sh, sh);
        this.gameServerListener = GameServerCommunication.getInstance();
        this.gameServerListener.openServerSocket(Config.GAME_SERVER_LOGIN_HOST.equals("*") ? null : InetAddress.getByName(Config.GAME_SERVER_LOGIN_HOST), Config.GAME_SERVER_LOGIN_PORT);
        this.gameServerListener.start();

        log.info("Listening for gameservers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);

        this.selectorThread.openServerSocket(Config.LOGIN_HOST.equals("*") ? null : InetAddress.getByName(Config.LOGIN_HOST), Config.PORT_LOGIN);
        this.selectorThread.start();

        log.info("Listening for clients on " + Config.LOGIN_HOST + ":" + Config.PORT_LOGIN);
    }

    private static void checkFreePorts() throws Throwable {
        ServerSocket ss = null;

        try {
            if (Config.LOGIN_HOST.equalsIgnoreCase("*")) {
                ss = new ServerSocket(Config.PORT_LOGIN);
            } else {
                ss = new ServerSocket(Config.PORT_LOGIN, 50, InetAddress.getByName(Config.LOGIN_HOST));
            }
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (Exception e) {
                    log.error("checkFreePorts: eMessage={}, eClass={}", e.getMessage(), e.getClass());
                }
            }

        }

    }

    private void scheduleRestartByCron(String cronPattern) {
        SchedulingPattern cronTime;
        try {
            cronTime = new SchedulingPattern(cronPattern);
        } catch (InvalidPatternException e) {
            log.error("scheduleRestartByCron: eMessage={}, eClass={}", e.getMessage(), e.getClass());
            return;
        }

        long now = System.currentTimeMillis();
        long remaining = cronTime.next(now) - now;
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() throws Exception {
                Runtime.getRuntime().exit(2);
            }
        }, remaining);
    }

    public static void main(String[] args) throws Throwable {
        (new File("./log/")).mkdir();
        Config.load();
        checkFreePorts();
        L2DatabaseFactory.getInstance().getConnection().close();
        authServer = new AuthServer();
        if (RESTART_AT_TIME != null && !RESTART_AT_TIME.equals("")) {
            authServer.scheduleRestartByCron(RESTART_AT_TIME);
        }

    }
}
