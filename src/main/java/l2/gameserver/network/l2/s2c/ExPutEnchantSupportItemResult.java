//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPutEnchantSupportItemResult extends L2GameServerPacket {
  public static final L2GameServerPacket FAIL = new ExPutEnchantSupportItemResult(0);
  public static final L2GameServerPacket SUCCESS = new ExPutEnchantSupportItemResult(1);
  private int _result;

  private ExPutEnchantSupportItemResult(int result) {
    this._result = result;
  }

  protected void writeImpl() {
    this.writeEx(130);
    this.writeD(this._result);
  }
}
