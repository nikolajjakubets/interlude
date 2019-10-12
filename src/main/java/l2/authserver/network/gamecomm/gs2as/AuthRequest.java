package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.GameServerManager;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.AuthResponse;
import l2.authserver.network.gamecomm.as2gs.LoginServerFail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRequest extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(AuthRequest.class);
    private int _protocolVersion;
    private int requestId;
    private boolean acceptAlternateID;
    private String externalIp;
    private String internalIp;
    private int maxOnline;
    private int _serverType;
    private int _ageLimit;
    private boolean _gmOnly;
    private boolean _brackets;
    private boolean _pvp;
    private int[] ports;

    public AuthRequest() {
    }

    protected void readImpl() {
        this._protocolVersion = this.readD();
        this.requestId = this.readC();
        this.acceptAlternateID = this.readC() == 1;
        this._serverType = this.readD();
        this._ageLimit = this.readD();
        this._gmOnly = this.readC() == 1;
        this._brackets = this.readC() == 1;
        this._pvp = this.readC() == 1;
        this.externalIp = this.readS();
        this.internalIp = this.readS();
        this.ports = new int[this.readH()];

        for(int i = 0; i < this.ports.length; ++i) {
            this.ports[i] = this.readH();
        }

        this.maxOnline = this.readD();
    }

    protected void runImpl() {
        _log.info("Trying to register gameserver: " + this.requestId + " [" + this.getGameServer().getConnection().getIpAddress() + "]");
        int failReason = 0;
        GameServer gs = this.getGameServer();
        if (GameServerManager.getInstance().registerGameServer(this.requestId, gs)) {
            gs.setPorts(this.ports);
            gs.setExternalHost(this.externalIp);
            gs.setInternalHost(this.internalIp);
            gs.setMaxPlayers(this.maxOnline);
            gs.setPvp(this._pvp);
            gs.setServerType(this._serverType);
            gs.setShowingBrackets(this._brackets);
            gs.setGmOnly(this._gmOnly);
            gs.setAgeLimit(this._ageLimit);
            gs.setProtocol(this._protocolVersion);
            gs.setAuthed(true);
            gs.getConnection().startPingTask();
        } else if (this.acceptAlternateID) {
            if (GameServerManager.getInstance().registerGameServer(gs = this.getGameServer())) {
                gs.setPorts(this.ports);
                gs.setExternalHost(this.externalIp);
                gs.setInternalHost(this.internalIp);
                gs.setMaxPlayers(this.maxOnline);
                gs.setPvp(this._pvp);
                gs.setServerType(this._serverType);
                gs.setShowingBrackets(this._brackets);
                gs.setGmOnly(this._gmOnly);
                gs.setAgeLimit(this._ageLimit);
                gs.setProtocol(this._protocolVersion);
                gs.setAuthed(true);
                gs.getConnection().startPingTask();
            } else {
                failReason = 5;
            }
        } else {
            failReason = 4;
        }

        if (failReason != 0) {
            _log.info("Gameserver registration failed.");
            this.sendPacket(new LoginServerFail(failReason));
        } else {
            _log.info("Gameserver registration successful.");
            this.sendPacket(new AuthResponse(gs));
        }
    }
}
