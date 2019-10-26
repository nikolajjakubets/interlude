//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExVariationResult extends L2GameServerPacket {
  public static final ExVariationResult FAIL_PACKET = new ExVariationResult(0, 0, 0);
  private int _stat1;
  private int _stat2;
  private int _result;

  public ExVariationResult(int unk1, int unk2, int unk3) {
    this._stat1 = unk1;
    this._stat2 = unk2;
    this._result = unk3;
  }

  protected void writeImpl() {
    this.writeEx(85);
    this.writeD(this._stat1);
    this.writeD(this._stat2);
    this.writeD(this._result);
  }
}
