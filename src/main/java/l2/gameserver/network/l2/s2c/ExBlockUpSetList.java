//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExBlockUpSetList extends L2GameServerPacket {
  private int BlockUpType = 0;

  public ExBlockUpSetList() {
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(this.BlockUpType);
    switch(this.BlockUpType) {
      case -1:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      default:
    }
  }
}
