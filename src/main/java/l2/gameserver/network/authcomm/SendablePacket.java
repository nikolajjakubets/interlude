//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public abstract class SendablePacket extends l2.commons.net.nio.SendablePacket<AuthServerCommunication> {

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
    } catch (Exception e) {
      log.error("write: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    }

    return true;
  }

  protected abstract void writeImpl();
}
