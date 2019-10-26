//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExRaidCharSelected extends L2GameServerPacket {
  public ExRaidCharSelected() {
  }

  protected void writeImpl() {
    this.writeEx(181);
  }
}
