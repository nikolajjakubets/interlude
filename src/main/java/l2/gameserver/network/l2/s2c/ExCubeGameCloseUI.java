//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExCubeGameCloseUI extends L2GameServerPacket {
  int _seconds;

  public ExCubeGameCloseUI() {
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(-1);
  }
}
