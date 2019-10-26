//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExRestartClient extends L2GameServerPacket {
  public ExRestartClient() {
  }

  protected final void writeImpl() {
    this.writeEx(70);
  }
}
