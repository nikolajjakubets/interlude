//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class GMHide extends L2GameServerPacket {
  private final int obj_id;

  public GMHide(int id) {
    this.obj_id = id;
  }

  protected void writeImpl() {
    this.writeC(147);
    this.writeD(this.obj_id);
  }
}
