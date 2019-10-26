//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class WareHouseDone extends L2GameServerPacket {
  public WareHouseDone() {
  }

  protected void writeImpl() {
    this.writeC(67);
    this.writeD(0);
  }
}
