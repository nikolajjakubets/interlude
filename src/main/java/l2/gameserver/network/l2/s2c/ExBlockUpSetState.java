//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExBlockUpSetState extends L2GameServerPacket {
  private int BlockUpStateType = 0;

  public ExBlockUpSetState() {
  }

  protected void writeImpl() {
    this.writeEx(152);
    this.writeD(this.BlockUpStateType);
    switch(this.BlockUpStateType) {
      case 0:
      case 1:
      case 2:
      default:
    }
  }
}
