package l2.authserver.network.gamecomm.as2gs;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.SendablePacket;

public class AuthResponse extends SendablePacket {
    private int serverId;
    private String name;

    public AuthResponse(GameServer gs) {
        this.serverId = gs.getId();
        this.name = gs.getName();
    }

    protected void writeImpl() {
        this.writeC(0);
        this.writeC(this.serverId);
        this.writeS(this.name);
    }
}
