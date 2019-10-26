//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class Ex2ndPasswordCheck extends L2GameServerPacket {
  private final int _arg0;
  private final int _arg1;

  public Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult result) {
    this._arg0 = result.getArg0();
    this._arg1 = result.getArg1();
  }

  public Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult result, int time) {
    this._arg0 = result.getArg0();
    this._arg1 = time;
  }

  protected void writeImpl() {
    this.writeEx(229);
    this.writeD(this._arg0);
    this.writeD(this._arg1);
  }

  public static enum Ex2ndPasswordCheckResult {
    CREATE(0, 0),
    CHECK(1, 0),
    BLOCK_TIME(1),
    SUCCESS(2, 0),
    ERROR(3, 0);

    private final int _arg0;
    private final int _arg1;

    private Ex2ndPasswordCheckResult(int arg0, int arg1) {
      this._arg0 = arg0;
      this._arg1 = arg1;
    }

    private Ex2ndPasswordCheckResult(int arg0) {
      this._arg0 = arg0;
      this._arg1 = -1;
    }

    public int getArg0() {
      return this._arg0;
    }

    public int getArg1() {
      return this._arg1;
    }
  }
}
