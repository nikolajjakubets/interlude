package l2.authserver.network.gamecomm;

import l2.authserver.GameServerManager;

import java.net.InetAddress;

public class ProxyServer {
    private final int origServerId;
    private final int proxyServerId;
    private InetAddress proxyAddr;
    private int proxyport;

    public ProxyServer(int origServerId, int proxyServerId) {
        this.origServerId = origServerId;
        this.proxyServerId = proxyServerId;
    }

    public int getOrigServerId() {
        return this.origServerId;
    }

    public int getProxyServerId() {
        return this.proxyServerId;
    }

    public InetAddress getProxyAddr() {
        return this.proxyAddr;
    }

    public void setProxyAddr(InetAddress proxyAddr) {
        this.proxyAddr = proxyAddr;
    }

    public int getProxyPort() {
        return this.proxyport;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyport = proxyPort;
    }

    public GameServer getGameServer() {
        return GameServerManager.getInstance().getGameServerById(this.getOrigServerId());
    }
}
