package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingResponse extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(PingResponse.class);
    private long _serverTime;

    public PingResponse() {
    }

    protected void readImpl() {
        this._serverTime = this.readQ();
    }

    protected void runImpl() {
        GameServer gameServer = this.getGameServer();
        if (gameServer.isAuthed()) {
            gameServer.getConnection().onPingResponse();
            long diff = System.currentTimeMillis() - this._serverTime;
            if (Math.abs(diff) > 999L) {
                _log.warn("Gameserver " + gameServer.getId() + " [" + gameServer.getName() + "] : time offset " + diff + " ms.");
            }

        }
    }
}
