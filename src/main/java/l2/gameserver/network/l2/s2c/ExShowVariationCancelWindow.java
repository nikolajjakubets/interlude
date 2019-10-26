//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExShowVariationCancelWindow extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC = new ExShowVariationCancelWindow();

  public ExShowVariationCancelWindow() {
  }

  protected final void writeImpl() {
    this.writeEx(81);
  }
}
