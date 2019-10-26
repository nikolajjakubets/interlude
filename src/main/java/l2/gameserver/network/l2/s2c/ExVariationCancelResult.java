//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExVariationCancelResult extends L2GameServerPacket {
  public static final ExVariationCancelResult FAIL_PACKET = new ExVariationCancelResult(0);
  private int _closeWindow = 1;
  private int _unk1;

  public ExVariationCancelResult(int result) {
    this._unk1 = result;
  }

  protected void writeImpl() {
    this.writeEx(87);
    this.writeD(this._closeWindow);
    this.writeD(this._unk1);
  }
}
