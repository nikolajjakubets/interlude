package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingResponse extends ReceivablePacket {
    private long serverTime;

    public PingResponse() {
    }

    protected void readImpl() {
        this.serverTime = this.readQ();
    }

    protected void runImpl() {
        GameServer gameServer = this.getGameServer();
        if (gameServer.isAuthed()) {
            gameServer.getConnection().onPingResponse();
            long diff = System.currentTimeMillis() - this.serverTime;
            if (Math.abs(diff) > 999L) {
              log.warn("runImpl: GameServerId={}  serverName={}", gameServer.getId(), gameServer.getServerName());
                log.warn("runImpl: time offset ={} ms ] ", diff);
            }

        }
    }
}
