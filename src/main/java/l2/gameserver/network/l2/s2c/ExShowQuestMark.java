//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExShowQuestMark extends L2GameServerPacket {
  private int _questId;

  public ExShowQuestMark(int questId) {
    this._questId = questId;
  }

  protected void writeImpl() {
    this.writeEx(26);
    this.writeD(this._questId);
  }
}
