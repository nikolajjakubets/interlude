//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.authserver.network.gamecomm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import l2.authserver.Config;
import org.apache.log4j.Logger;

public class GameServer {
    private static final Logger _log = Logger.getLogger(GameServer.class);
    private int _id;
    private String _internalHost;
    private String _externalHost;
    private InetAddress _internalAddr;
    private InetAddress _externalAddr;
    private volatile int[] _ports = new int[]{7777};
    private int _serverType;
    private int _ageLimit;
    private int _protocol;
    private boolean _isOnline;
    private boolean _isPvp;
    private boolean _isShowingBrackets;
    private boolean _isGmOnly;
    private int _maxPlayers;
    private GameServerConnection _conn;
    private boolean _isAuthed;
    private AtomicInteger _port = new AtomicInteger(0);
    private volatile int _playersIngame;

    public GameServer(GameServerConnection conn) {
        this._conn = conn;
    }

    public GameServer(int id) {
        this._id = id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getId() {
        return this._id;
    }

    public void setAuthed(boolean isAuthed) {
        this._isAuthed = isAuthed;
    }

    public boolean isAuthed() {
        return this._isAuthed;
    }

    public void setConnection(GameServerConnection conn) {
        this._conn = conn;
    }

    public GameServerConnection getConnection() {
        return this._conn;
    }

    public InetAddress getInternalHost() throws UnknownHostException {
        return this._internalAddr != null ? this._internalAddr : (this._internalAddr = InetAddress.getByName(this._internalHost));
    }

    public void setInternalHost(String internalHost) {
        if (internalHost.equals("*")) {
            internalHost = this.getConnection().getIpAddress();
        }

        this._internalHost = internalHost;
        this._internalAddr = null;
    }

    public void setExternalHost(String externalHost) {
        if (externalHost.equals("*")) {
            externalHost = this.getConnection().getIpAddress();
        }

        this._externalHost = externalHost;
        this._externalAddr = null;
    }

    public InetAddress getExternalHost() throws UnknownHostException {
        return this._externalAddr != null ? this._externalAddr : (this._externalAddr = InetAddress.getByName(this._externalHost));
    }

    public int getPort() {
        int[] ports = this._ports;
        return ports[(this._port.incrementAndGet() & 2147483647) % ports.length];
    }

    public void setPorts(int[] ports) {
        this._ports = ports;
    }

    public void setMaxPlayers(int maxPlayers) {
        this._maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return this._maxPlayers;
    }

    public int getOnline() {
        return this._playersIngame;
    }

    public void addAccount(String account) {
        ++this._playersIngame;
    }

    public void removeAccount(String account) {
        --this._playersIngame;
    }

    public void setDown() {
        this.setAuthed(false);
        this.setConnection((GameServerConnection)null);
        this.setOnline(false);
    }

    public String getName() {
        return (String)Config.SERVER_NAMES.get(this.getId());
    }

    public void sendPacket(SendablePacket packet) {
        GameServerConnection conn = this.getConnection();
        if (conn != null) {
            conn.sendPacket(packet);
        }

    }

    public int getServerType() {
        return this._serverType;
    }

    public boolean isOnline() {
        return this._isOnline;
    }

    public void setOnline(boolean online) {
        this._isOnline = online;
    }

    public void setServerType(int serverType) {
        this._serverType = serverType;
    }

    public boolean isPvp() {
        return this._isPvp;
    }

    public void setPvp(boolean pvp) {
        this._isPvp = pvp;
    }

    public boolean isShowingBrackets() {
        return this._isShowingBrackets;
    }

    public void setShowingBrackets(boolean showingBrackets) {
        this._isShowingBrackets = showingBrackets;
    }

    public boolean isGmOnly() {
        return this._isGmOnly;
    }

    public void setGmOnly(boolean gmOnly) {
        this._isGmOnly = gmOnly;
    }

    public int getAgeLimit() {
        return this._ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this._ageLimit = ageLimit;
    }

    public void setProtocol(int protocol) {
        this._protocol = protocol;
    }
}
