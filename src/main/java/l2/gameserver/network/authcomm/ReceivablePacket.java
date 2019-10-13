//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket extends l2.commons.net.nio.ReceivablePacket<AuthServerCommunication> {
  private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);

  public ReceivablePacket() {
  }

  public AuthServerCommunication getClient() {
    return AuthServerCommunication.getInstance();
  }

  protected ByteBuffer getByteBuffer() {
    return this.getClient().getReadBuffer();
  }

  public final boolean read() {
    try {
      this.readImpl();
    } catch (Exception var2) {
      _log.error("", var2);
    }

    return true;
  }

  public final void run() {
    try {
      this.runImpl();
    } catch (Exception var2) {
      _log.error("", var2);
    }

  }

  protected abstract void readImpl();

  protected abstract void runImpl();

  protected void sendPacket(SendablePacket sp) {
    this.getClient().sendPacket(sp);
  }
}
