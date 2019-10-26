//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class NormalCamera extends L2GameServerPacket {
  public NormalCamera() {
  }

  protected final void writeImpl() {
    this.writeC(215);
  }
}
