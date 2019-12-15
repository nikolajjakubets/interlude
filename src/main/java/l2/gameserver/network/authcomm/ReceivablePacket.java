//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public abstract class ReceivablePacket extends l2.commons.net.nio.ReceivablePacket<AuthServerCommunication> {

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
    } catch (Exception e) {
      log.error("read: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    }

    return true;
  }

  public final void run() {
    try {
      this.runImpl();
    } catch (Exception e) {
      log.error("run: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());

    }

  }

  protected abstract void readImpl();

  protected abstract void runImpl();

  protected void sendPacket(SendablePacket sp) {
    this.getClient().sendPacket(sp);
  }
}
