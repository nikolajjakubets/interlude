//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class Ex2ndPasswordAck extends L2GameServerPacket {
  private final Ex2ndPasswordAck.Ex2ndPasswordAckResult _result;
  private final int _arg3;

  public Ex2ndPasswordAck(Ex2ndPasswordAck.Ex2ndPasswordAckResult result) {
    this._result = result;
    this._arg3 = 0;
  }

  public Ex2ndPasswordAck(Ex2ndPasswordAck.Ex2ndPasswordAckResult result, int arg) {
    this._result = result;
    this._arg3 = arg;
  }

  protected void writeImpl() {
    this.writeEx(231);
    this.writeC(this._result.getArg0());
    this.writeD(this._result.getArg1());
    this.writeD(this._arg3);
  }

  public static enum Ex2ndPasswordAckResult {
    SUCCESS_CREATE(0, 0),
    SUCCESS_VERIFY(2, 0),
    FAIL_CREATE(0, 1),
    FAIL_VERIFY(2, 1),
    BLOCK_HOMEPAGE(0, 2),
    ERROR(3, 0);

    private int _arg0;
    private int _arg1;

    private Ex2ndPasswordAckResult(int arg0, int arg1) {
      this._arg0 = arg0;
      this._arg1 = arg1;
    }

    public int getArg0() {
      return this._arg0;
    }

    public int getArg1() {
      return this._arg1;
    }
  }
}
