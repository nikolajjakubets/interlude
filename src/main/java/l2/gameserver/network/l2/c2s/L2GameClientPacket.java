//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.GameServer;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import lombok.extern.slf4j.Slf4j;

import java.nio.BufferUnderflowException;
import java.util.List;

@Slf4j
public abstract class L2GameClientPacket extends ReceivablePacket<GameClient> {

  public L2GameClientPacket() {
  }

  public final boolean read() {
    try {
      this.readImpl();
      return true;
    } catch (BufferUnderflowException var2) {
      this._client.onPacketReadFail();
      log.error("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), var2);
    } catch (Exception var3) {
      log.error("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), var3);
    }

    return false;
  }

  protected abstract void readImpl() throws Exception;

  public final void run() {
    GameClient client = this.getClient();

    try {
      this.runImpl();
    } catch (Exception var3) {
      log.error("Client: " + client + " - Failed running: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), var3);
    }

  }

  protected abstract void runImpl() throws Exception;

  protected String readS(int len) {
    String ret = this.readS();
    return ret.length() > len ? ret.substring(0, len) : ret;
  }

  protected void sendPacket(L2GameServerPacket packet) {
    this.getClient().sendPacket(packet);
  }

  protected void sendPacket(L2GameServerPacket... packets) {
    this.getClient().sendPacket(packets);
  }

  protected void sendPackets(List<L2GameServerPacket> packets) {
    this.getClient().sendPackets(packets);
  }

  public String getType() {
    return "[C] " + this.getClass().getSimpleName();
  }
}
