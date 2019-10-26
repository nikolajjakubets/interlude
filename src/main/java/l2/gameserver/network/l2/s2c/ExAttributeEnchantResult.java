//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExAttributeEnchantResult extends L2GameServerPacket {
  private int _result;

  public ExAttributeEnchantResult(int unknown) {
    this._result = unknown;
  }

  protected final void writeImpl() {
    this.writeEx(97);
    this.writeD(this._result);
  }
}
