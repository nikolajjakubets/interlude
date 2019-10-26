//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

/** @deprecated */
@Deprecated
public class TradePressOwnOk extends L2GameServerPacket {
  public TradePressOwnOk() {
  }

  protected final void writeImpl() {
    this.writeC(34);
    this.writeD(0);
  }
}
