//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class RequestTimeCheck extends L2GameServerPacket {
  public RequestTimeCheck() {
  }

  protected void writeImpl() {
    this.writeC(193);
  }
}
