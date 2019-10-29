package l2.authserver.network.l2.s2c;

import l2.authserver.GameServerManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.commons.net.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public final class ServerList extends L2LoginServerPacket {
  private static final Comparator<ServerData> SERVER_DATA_COMPARATOR = Comparator.comparingInt(o -> o.serverId);

    private List<ServerData> _servers = new ArrayList<ServerData>();
    private int _lastServer;

    public ServerList(Account account) {
        this._lastServer = account.getLastServer();
        GameServer[] var2 = GameServerManager.getInstance().getGameServers();

      for (GameServer gs : var2) {
            InetAddress ip;
            try {
                ip = NetUtils.isInternalIP(account.getLastIP()) ? gs.getInternalHost() : gs.getExternalHost();
            } catch (UnknownHostException e) {
              log.error("ServerList: eMessage={}, eClass={}", e.getMessage(), e.getClass());
              log.error("ServerList: Client: " + this.getClient() + " - Failed writing: " + this.getClass().getSimpleName() + "!", e);
                continue;
            }

            this._servers.add(new ServerData(gs.getId(), ip, gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), gs.getAgeLimit()));
            List<ProxyServer> proxyServers = GameServerManager.getInstance().getProxyServersList(gs.getId());

            for (ProxyServer ps : proxyServers) {
                this._servers.add(new ServerData(ps.getProxyServerId(), ps.getProxyAddr(), ps.getProxyPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), gs.getAgeLimit()));
            }
        }

      this._servers.sort(SERVER_DATA_COMPARATOR);
    }

    protected void writeImpl() {
        this.writeC(4);
        this.writeC(this._servers.size());
        this.writeC(this._lastServer);

        for (ServerData server : this._servers) {
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
