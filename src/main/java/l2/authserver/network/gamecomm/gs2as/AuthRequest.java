package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.GameServerManager;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.AuthResponse;
import l2.authserver.network.gamecomm.as2gs.LoginServerFail;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthRequest extends ReceivablePacket {
    private int protocolVersion;
    private int requestId;
    private boolean acceptAlternateID;
    private String externalIp;
    private String internalIp;
    private int maxOnline;
    private int serverType;
    private int ageLimit;
    private boolean gmOnly;
    private boolean brackets;
    private boolean pvp;
    private int[] ports;

    public AuthRequest() {
    }

    protected void readImpl() {
        this.protocolVersion = this.readD();
        this.requestId = this.readC();
        this.acceptAlternateID = this.readC() == 1;
        this.serverType = this.readD();
        this.ageLimit = this.readD();
        this.gmOnly = this.readC() == 1;
        this.brackets = this.readC() == 1;
        this.pvp = this.readC() == 1;
        this.externalIp = this.readS();
        this.internalIp = this.readS();
        this.ports = new int[this.readH()];

        for(int i = 0; i < this.ports.length; ++i) {
            this.ports[i] = this.readH();
        }

        this.maxOnline = this.readD();
    }

    protected void runImpl() {
        log.info("Trying to register gameserver: " + this.requestId + " [" + this.getGameServer().getConnection().getIpAddress() + "]");
        int failReason = 0;
        GameServer gs = this.getGameServer();
        if (GameServerManager.getInstance().registerGameServer(this.requestId, gs)) {
            gs.setPorts(this.ports);
            gs.setExternalHost(this.externalIp);
            gs.setInternalHost(this.internalIp);
            gs.setMaxPlayers(this.maxOnline);
            gs.setPvp(this.pvp);
            gs.setServerType(this.serverType);
            gs.setShowingBrackets(this.brackets);
            gs.setGmOnly(this.gmOnly);
            gs.setAgeLimit(this.ageLimit);
            gs.setProtocol(this.protocolVersion);
            gs.setAuthed(true);
            gs.getConnection().startPingTask();
        } else if (this.acceptAlternateID) {
            if (GameServerManager.getInstance().registerGameServer(gs = this.getGameServer())) {
                gs.setPorts(this.ports);
                gs.setExternalHost(this.externalIp);
                gs.setInternalHost(this.internalIp);
                gs.setMaxPlayers(this.maxOnline);
                gs.setPvp(this.pvp);
                gs.setServerType(this.serverType);
                gs.setShowingBrackets(this.brackets);
                gs.setGmOnly(this.gmOnly);
                gs.setAgeLimit(this.ageLimit);
                gs.setProtocol(this.protocolVersion);
                gs.setAuthed(true);
                gs.getConnection().startPingTask();
            } else {
                failReason = 5;
            }
        } else {
            failReason = 4;
        }

        if (failReason != 0) {
            log.info("Gameserver registration failed.");
            this.sendPacket(new LoginServerFail(failReason));
        } else {
            log.info("Gameserver registration successful.");
            this.sendPacket(new AuthResponse(gs));
        }
    }
}
