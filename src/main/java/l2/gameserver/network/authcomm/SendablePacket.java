//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends l2.commons.net.nio.SendablePacket<AuthServerCommunication> {
  private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

  public SendablePacket() {
  }

  public AuthServerCommunication getClient() {
    return AuthServerCommunication.getInstance();
  }

  protected ByteBuffer getByteBuffer() {
    return this.getClient().getWriteBuffer();
  }

  public boolean write() {
    try {
      this.writeImpl();
    } catch (Exception var2) {
      _log.error("", var2);
    }

    return true;
  }

  protected abstract void writeImpl();
}
