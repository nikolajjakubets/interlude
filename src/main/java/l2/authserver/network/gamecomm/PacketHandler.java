package l2.authserver.network.gamecomm;


import l2.authserver.network.gamecomm.gs2as.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class PacketHandler {

    public PacketHandler() {
    }

    public static ReceivablePacket handlePacket(GameServer gs, ByteBuffer buf) {
        ReceivablePacket packet = null;
        int id = buf.get() & 255;
        if (!gs.isAuthed()) {
            if (id == 0) {
                packet = new AuthRequest();
            } else {
                log.error("handlePacket: Received unknown packet={}", Integer.toHexString(id));
            }
        } else {
            switch(id) {
                case 1:
                    packet = new OnlineStatus();
                    break;
                case 2:
                    packet = new PlayerAuthRequest();
                    break;
                case 3:
                    packet = new PlayerInGame();
                    break;
                case 4:
                    packet = new PlayerLogout();
                    break;
                case 17:
                    packet = new ChangeAccessLevel();
                    break;
                case 160:
                    packet = new IGPwdCng();
                    break;
                case 255:
                    packet = new PingResponse();
                    break;
                default:
                    log.error("handlePacket: Received unknown packet={} ", Integer.toHexString(id));
            }
        }

        return packet;
    }
}
