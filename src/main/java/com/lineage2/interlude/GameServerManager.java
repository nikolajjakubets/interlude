package com.lineage2.interlude;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.authserver.Config.ProxyServerConfig;
import l2.authserver.database.L2DatabaseFactory;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerManager {
    private static final Logger LOG = LoggerFactory.getLogger(GameServerManager.class);
    private static final GameServerManager INSTANCE = new GameServerManager();
    private final Map<Integer, GameServer> _gameServers = new TreeMap();
    private final Map<Integer, List<ProxyServer>> _gameServerProxys = new TreeMap();
    private final Map<Integer, ProxyServer> _proxyServers = new TreeMap();
    private final ReadWriteLock _lock = new ReentrantReadWriteLock();
    private final Lock _readLock;
    private final Lock _writeLock;

    public static final GameServerManager getInstance() {
        return INSTANCE;
    }

    public GameServerManager() {
        this._readLock = this._lock.readLock();
        this._writeLock = this._lock.writeLock();
        this.loadGameServers();
        LOG.info("Loaded " + this._gameServers.size() + " registered GameServer(s).");
        this.loadProxyServers();
        LOG.info("Loaded " + this._proxyServers.size() + " proxy server(s).");
    }

    private void loadGameServers() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT server_id FROM gameservers");
            rset = statement.executeQuery();

            while(rset.next()) {
                int id = rset.getInt("server_id");
                ProxyServerConfig[] var5 = Config.PROXY_SERVERS_CONFIGS;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    ProxyServerConfig psc = var5[var7];
                    if (psc.getProxyId() == id) {
                        LOG.warn("Server with id " + id + " collides with proxy server.");
                    }
                }

                GameServer gs = new GameServer(id);
                this._gameServers.put(id, gs);
            }
        } catch (Exception var12) {
            LOG.error("", var12);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

    }

    private void loadProxyServers() {
        ProxyServerConfig[] var1 = Config.PROXY_SERVERS_CONFIGS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ProxyServerConfig psc = var1[var3];
            if (this._gameServers.containsKey(psc.getProxyId())) {
                LOG.warn("Won't load collided proxy with id " + psc.getProxyId() + ".");
            } else {
                ProxyServer ps = new ProxyServer(psc.getOrigServerId(), psc.getProxyId());

                try {
                    InetAddress inetAddress = InetAddress.getByName(psc.getPorxyHost());
                    ps.setProxyAddr(inetAddress);
                } catch (UnknownHostException var7) {
                    LOG.error("Can't load proxy", var7);
                    continue;
                }

                ps.setProxyPort(psc.getProxyPort());
                List<ProxyServer> proxyList = (List)this._gameServerProxys.get(ps.getOrigServerId());
                if (proxyList == null) {
                    this._gameServerProxys.put(ps.getOrigServerId(), proxyList = new LinkedList());
                }

                ((List)proxyList).add(ps);
                this._proxyServers.put(ps.getProxyServerId(), ps);
            }
        }

    }

    public List<ProxyServer> getProxyServersList(int gameServerId) {
        List<ProxyServer> result = (List)this._gameServerProxys.get(gameServerId);
        return result != null ? result : Collections.emptyList();
    }

    public ProxyServer getProxyServerById(int proxyServerId) {
        return (ProxyServer)this._proxyServers.get(proxyServerId);
    }

    public GameServer[] getGameServers() {
        this._readLock.lock();

        GameServer[] var1;
        try {
            var1 = (GameServer[])this._gameServers.values().toArray(new GameServer[this._gameServers.size()]);
        } finally {
            this._readLock.unlock();
        }

        return var1;
    }

    public GameServer getGameServerById(int id) {
        this._readLock.lock();

        GameServer var2;
        try {
            var2 = (GameServer)this._gameServers.get(id);
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

                    pgs = (GameServer)this._gameServers.get(id);
                } while(!this._proxyServers.containsKey(id) && pgs != null);

                this._gameServers.put(id, gs);
                gs.setId(id);
                boolean var4 = true;
                return var4;
            } finally {
                this._writeLock.unlock();
            }
        }
    }

    public boolean registerGameServer(int id, GameServer gs) {
        this._writeLock.lock();

        boolean var4;
        try {
            GameServer pgs = (GameServer)this._gameServers.get(id);
            if (!Config.ACCEPT_NEW_GAMESERVER && pgs == null) {
                var4 = false;
                return var4;
            }

            if (pgs != null && pgs.isAuthed()) {
                return false;
            }

            this._gameServers.put(id, gs);
            gs.setId(id);
            var4 = true;
        } finally {
            this._writeLock.unlock();
        }

        return var4;
    }