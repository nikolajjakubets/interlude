//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class TutorialShowQuestionMark extends L2GameServerPacket {
  private int _number;

  public TutorialShowQuestionMark(int number) {
    this._number = number;
  }

  protected final void writeImpl() {
    this.writeC(161);
    this.writeD(this._number);
  }
}
