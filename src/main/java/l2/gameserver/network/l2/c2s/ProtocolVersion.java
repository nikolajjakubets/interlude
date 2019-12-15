//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.network.l2.s2c.KeyPacket;
import l2.gameserver.network.l2.s2c.SendStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtocolVersion extends L2GameClientPacket {
  private int _version;

  public ProtocolVersion() {
  }

  protected void readImpl() {
    this._version = this.readD();
  }

  protected void runImpl() {
    if (this._version == -2) {
      this._client.closeNow(false);
    } else if (this._version == -3) {
      log.info("Status request from IP : " + this.getClient().getIpAddr());
      this.getClient().close(new SendStatus());
    } else if (this._version >= Config.MIN_PROTOCOL_REVISION && this._version <= Config.MAX_PROTOCOL_REVISION) {
      this.getClient().setRevision(this._version);
      this.sendPacket(new KeyPacket(this._client.enableCrypt()));
    } else {
      log.warn("Unknown protocol revision : " + this._version + ", client : " + this._client);
      this.getClient().close(new KeyPacket(null));
    }
  }
}
