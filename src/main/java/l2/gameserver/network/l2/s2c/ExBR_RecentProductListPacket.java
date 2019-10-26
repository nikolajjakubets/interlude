//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExBR_RecentProductListPacket extends L2GameServerPacket {
  public ExBR_RecentProductListPacket() {
  }

  protected void writeImpl() {
    this.writeEx(220);
  }
}
