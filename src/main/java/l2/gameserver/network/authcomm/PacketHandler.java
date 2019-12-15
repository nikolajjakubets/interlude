//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import l2.gameserver.network.authcomm.as2gs.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class PacketHandler {

  public PacketHandler() {
  }

  public static ReceivablePacket handlePacket(ByteBuffer buf) {
    ReceivablePacket packet = null;
    int id = buf.get() & 255;
    switch (id) {
      case 0:
        packet = new AuthResponse();
        break;
      case 1:
        packet = new LoginServerFail();
        break;
      case 2:
        packet = new PlayerAuthResponse();
        break;
      case 3:
        packet = new KickPlayer();
        break;
      case 161:
        packet = new NotifyPwdCngResult();
        break;
      case 255:
        packet = new PingRequest();
        break;
      default:
        log.error("Received unknown packet: " + Integer.toHexString(id));
    }

    return packet;
  }
}
