//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ShowRadar extends L2GameServerPacket {
  public ShowRadar() {
  }

  protected final void writeImpl() {
    this.writeC(170);
  }
}
