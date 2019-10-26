//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExMPCCClose extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC = new ExMPCCClose();

  public ExMPCCClose() {
  }

  protected void writeImpl() {
    this.writeEx(38);
  }
}
