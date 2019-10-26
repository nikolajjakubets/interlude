//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExEventMatchCreate extends L2GameServerPacket {
  public ExEventMatchCreate() {
  }

  protected void writeImpl() {
    this.writeEx(29);
  }
}
