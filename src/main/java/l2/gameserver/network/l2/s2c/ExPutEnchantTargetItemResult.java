//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPutEnchantTargetItemResult extends L2GameServerPacket {
  public static final L2GameServerPacket FAIL = new ExPutEnchantTargetItemResult(0);
  public static final L2GameServerPacket SUCCESS = new ExPutEnchantTargetItemResult(1);
  private int _result;

  private ExPutEnchantTargetItemResult(int result) {
    this._result = result;
  }

  protected void writeImpl() {
    this.writeEx(129);
    this.writeD(this._result);
  }
}
