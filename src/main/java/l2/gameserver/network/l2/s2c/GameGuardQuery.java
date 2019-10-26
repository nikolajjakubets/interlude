//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class GameGuardQuery extends L2GameServerPacket {
  public GameGuardQuery() {
  }

  protected final void writeImpl() {
    this.writeC(249);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
  }
}
