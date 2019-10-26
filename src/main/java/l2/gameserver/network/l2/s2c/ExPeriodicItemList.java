//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPeriodicItemList extends L2GameServerPacket {
  public ExPeriodicItemList() {
  }

  protected void writeImpl() {
    this.writeEx(135);
    this.writeD(0);
  }
}
