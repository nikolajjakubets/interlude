//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.quest;

public class QuestNpcLogInfo {
  private final int[] _npcIds;
  private final String _varName;
  private final int _maxCount;

  public QuestNpcLogInfo(int[] npcIds, String varName, int maxCount) {
    this._npcIds = npcIds;
    this._varName = varName;
    this._maxCount = maxCount;
  }

  public int[] getNpcIds() {
    return this._npcIds;
  }

  public String getVarName() {
    return this._varName;
  }

  public int getMaxCount() {
    return this._maxCount;
  }
}
