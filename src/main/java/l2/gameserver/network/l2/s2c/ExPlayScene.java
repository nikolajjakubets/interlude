//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPlayScene extends L2GameServerPacket {
  public static final ExPlayScene STATIC = new ExPlayScene();

  public ExPlayScene() {
  }

  protected void writeImpl() {
    this.writeEx(91);
    this.writeD(0);
  }
}
