//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.KeyPacket;
import l2.gameserver.network.l2.s2c.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersion extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);
  private int _version;

  public ProtocolVersion() {
  }

  protected void readImpl() {
    this._version = this.readD();
  }

  protected void runImpl() {
    if (this._version == -2) {
      ((GameClient)this._client).closeNow(false);
    } else if (this._version == -3) {
      _log.info("Status request from IP : " + ((GameClient)this.getClient()).getIpAddr());
      ((GameClient)this.getClient()).close(new SendStatus());
    } else if (this._version >= Config.MIN_PROTOCOL_REVISION && this._version <= Config.MAX_PROTOCOL_REVISION) {
      ((GameClient)this.getClient()).setRevision(this._version);
      this.sendPacket(new KeyPacket(((GameClient)this._client).enableCrypt()));
    } else {
      _log.warn("Unknown protocol revision : " + this._version + ", client : " + this._client);
      ((GameClient)this.getClient()).close(new KeyPacket((byte[])null));
    }
  }
}
