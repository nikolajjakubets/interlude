//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class Ex2ndPasswordVerify extends L2GameServerPacket {
  private final Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult _result;
  private final int _arg;

  public Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult result) {
    this._result = result;
    this._arg = 0;
  }

  public Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult result, int count) {
    this._result = result;
    this._arg = count;
  }

  protected void writeImpl() {
    this.writeEx(230);
    this.writeD(this._result.getVal());
    this.writeD(this._arg);
  }

  public static enum Ex2ndPasswordVerifyResult {
    SUCCESS(0),
    FAILED(1),
    BLOCK_HOMEPAGE(2),
    ERROR(3);

    private int _val;

    private Ex2ndPasswordVerifyResult(int arg) {
      this._val = arg;
    }

    public int getVal() {
      return this._val;
    }
  }
}
