package com.lineage2.interlude.network.l2.s2c;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import l2.authserver.GameServerManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.commons.net.utils.NetUtils;

public final class ServerList extends L2LoginServerPacket {
    private static final Comparator<ServerList.ServerData> SERVER_DATA_COMPARATOR = new Comparator<ServerList.ServerData>() {
        public int compare(ServerList.ServerData o1, ServerList.ServerData o2) {
            return o1.serverId - o2.serverId;
        }
    };
    private List<ServerList.ServerData> _servers = new ArrayList();
    private int _lastServer;

    public ServerList(Account account) {
        this._lastServer = account.getLastServer();
        GameServer[] var2 = GameServerManager.getInstance().getGameServers();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            GameServer gs = var2[var4];

            InetAddress ip;
            try {
                ip = NetUtils.isInternalIP(account.getLastIP()) ? gs.getInternalHost() : gs.getExternalHost();
            } catch (UnknownHostException var10) {
                continue;
            }

            this._servers.add(new ServerList.ServerData(gs.getId(), ip, gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), gs.getAgeLimit()));
            List<ProxyServer> proxyServers = GameServerManager.getInstance().getProxyServersList(gs.getId());
            Iterator var8 = proxyServers.iterator();

            while(var8.hasNext()) {
                ProxyServer ps = (ProxyServer)var8.next();
                this._servers.add(new ServerList.ServerData(ps.getProxyServerId(), ps.getProxyAddr(), ps.getProxyPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), gs.getAgeLimit()));
            }
        }

        Collections.sort(this._servers, SERVER_DATA_COMPARATOR);
    }

    protected void writeImpl() {
        this.writeC(4);
        this.writeC(this._servers.size());
        this.writeC(this._lastServer);
        Iterator var1 = this._servers.iterator();

        while(var1.hasNext()) {
            ServerList.ServerData server = (ServerList.ServerData)var1.next();
            this.writeC(server.serverId);
            InetAddress i4 = server.ip;
            byte[] raw = i4.getAddress();
            this.writeC(raw[0] & 255);
            this.writeC(raw[1] & 255);
            this.writeC(raw[2] & 255);
            this.writeC(raw[3] & 255);
            this.writeD(server.port);
            this.writeC(server.ageLimit);
            this.writeC(server.pvp ? 1 : 0);
            this.writeH(server.online);
            this.writeH(server.maxPlayers);
            this.writeC(server.status ? 1 : 0);
            this.writeD(server.type);
            this.writeC(server.brackets ? 1 : 0);
        }

    }

    private static class ServerData {
        int serverId;
        InetAddress ip;
        int port;
        int online;
        int maxPlayers;
        boolean status;
        boolean pvp;
        boolean brackets;
        int type;
        int ageLimit;

        ServerData(int serverId, InetAddress ip, int port, boolean pvp, boolean brackets, int type, int online, int maxPlayers, boolean status, int ageLimit) {
            this.serverId = serverId;
            this.ip = ip;
            this.port = port;
            this.pvp = pvp;
            this.brackets = brackets;
            this.type = type;
            this.online = online;
            this.maxPlayers = maxPlayers;
            this.status = status;
            this.ageLimit = ageLimit;
        }
    }
}
