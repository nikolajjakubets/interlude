//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ShowCalc extends L2GameServerPacket {
  private int _calculatorId;

  public ShowCalc(int calculatorId) {
    this._calculatorId = calculatorId;
  }

  protected final void writeImpl() {
    this.writeC(220);
    this.writeD(this._calculatorId);
  }
}
