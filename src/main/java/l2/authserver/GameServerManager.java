//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.authserver;

import l2.authserver.Config.ProxyServerConfig;
import l2.authserver.database.L2DatabaseFactory;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.commons.dbutils.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class GameServerManager {
    private static final GameServerManager INSTANCE = new GameServerManager();
    private final Map<Integer, GameServer> gameServers = new TreeMap<Integer, GameServer>();
    private final Map<Integer, List<ProxyServer>> gameServerProxys = new TreeMap<Integer, List<ProxyServer>>();
    private final Map<Integer, ProxyServer> _proxyServers = new TreeMap<Integer, ProxyServer>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock _readLock;
    private final Lock _writeLock;

    public static GameServerManager getInstance() {
        return INSTANCE;
    }

    public GameServerManager() {
        this._readLock = this.lock.readLock();
        this._writeLock = this.lock.writeLock();
        this.loadGameServers();
        log.info("Init: Loaded " + this.gameServers.size() + " registered GameServer(s).");
        this.loadProxyServers();
        log.info("Init: Loaded " + this._proxyServers.size() + " proxy server(s).");
    }

    private void loadGameServers() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT server_id FROM gameservers");
            rset = statement.executeQuery();

            while (rset.next()) {
                int id = rset.getInt("server_id");
                ProxyServerConfig[] var5 = Config.PROXY_SERVERS_CONFIGS;

                for (ProxyServerConfig psc : var5) {
                    if (psc.getProxyServerId() == id) {
                        log.warn("loadGameServers: Server with id " + id + " collides with proxy server.");
                    }
                }

                GameServer gs = new GameServer(id);
                this.gameServers.put(id, gs);
            }
        } catch (Exception e) {
            log.error("restore: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

    }

    private void loadProxyServers() {
        ProxyServerConfig[] var1 = Config.PROXY_SERVERS_CONFIGS;

        for (ProxyServerConfig psc : var1) {
            if (this.gameServers.containsKey(psc.getProxyServerId())) {
                log.warn("loadProxyServers: Won't load collided proxy with id " + psc.getProxyServerId() + ".");
            } else {
                ProxyServer ps = new ProxyServer(psc.getOrigServerId(), psc.getProxyServerId());
                try {
                    InetAddress inetAddress = InetAddress.getByName(psc.getPorxyHost());
                    ps.setProxyAddr(inetAddress);
                } catch (UnknownHostException e) {
                    log.error("loadProxyServers: eMessage={}, eClass={}", e.getMessage(), e.getClass());
                    log.error("loadProxyServers: Can't load proxy", e);
                    continue;
                }

                ps.setProxyPort(psc.getProxyPort());
                List<ProxyServer> proxyList = this.gameServerProxys.get(ps.getOrigServerId());
                if (proxyList == null) {
                    this.gameServerProxys.put(ps.getOrigServerId(), proxyList = new LinkedList<ProxyServer>());
                }

                proxyList.add(ps);
                this._proxyServers.put(ps.getProxyServerId(), ps);
            }
        }

    }

    public List<ProxyServer> getProxyServersList(int gameServerId) {
        List<ProxyServer> result = this.gameServerProxys.get(gameServerId);
        return result != null ? result : new ArrayList<ProxyServer>();
    }

    public ProxyServer getProxyServerById(int proxyServerId) {
        return this._proxyServers.get(proxyServerId);
    }

    public GameServer[] getGameServers() {
        this._readLock.lock();

        GameServer[] var1;
        try {
            var1 = this.gameServers.values().toArray(new GameServer[0]);
        } finally {
            this._readLock.unlock();
        }

        return var1;
    }

    public GameServer getGameServerById(int id) {
        this._readLock.lock();

        GameServer var2;
        try {
            var2 = this.gameServers.get(id);
        } finally {
            this._readLock.unlock();
        }

        return var2;
    }

    public boolean registerGameServer(GameServer gs) {
        if (!Config.ACCEPT_NEW_GAMESERVER) {
            return false;
        } else {
            this._writeLock.lock();

            try {
                int id = 1;

                GameServer pgs;
                do {
                    if (id++ >= 127) {
                        return false;
                    }

                    pgs = this.gameServers.get(id);
                } while (!this._proxyServers.containsKey(id) && pgs != null);

                this.gameServers.put(id, gs);
                gs.setId(id);
                return true;
            } finally {
                this._writeLock.unlock();
            }
        }
    }

    public boolean registerGameServer(int id, GameServer gs) {
        this._writeLock.lock();

        boolean var4;
        try {
            GameServer pgs = this.gameServers.get(id);
            if (!Config.ACCEPT_NEW_GAMESERVER && pgs == null) {
                var4 = false;
                return var4;
            }

            if (pgs != null && pgs.isAuthed()) {
                return false;
            }

            this.gameServers.put(id, gs);
            gs.setId(id);
            var4 = true;
        } finally {
            this._writeLock.unlock();
        }

        return true;
    }
}
