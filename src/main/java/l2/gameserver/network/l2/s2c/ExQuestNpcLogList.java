//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.quest.QuestNpcLogInfo;
import l2.gameserver.model.quest.QuestState;

public class ExQuestNpcLogList extends L2GameServerPacket {
  private int _questId;
  private List<int[]> _logList = Collections.emptyList();

  public ExQuestNpcLogList(QuestState state) {
    this._questId = state.getQuest().getQuestIntId();
    int cond = state.getCond();
    List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
    if (vars != null) {
      this._logList = new ArrayList(vars.size());
      Iterator var4 = vars.iterator();

      while(var4.hasNext()) {
        QuestNpcLogInfo entry = (QuestNpcLogInfo)var4.next();
        int[] i = new int[]{entry.getNpcIds()[0] + 1000000, state.getInt(entry.getVarName())};
        this._logList.add(i);
      }

    }
  }

  protected void writeImpl() {
    this.writeEx(197);
    this.writeD(this._questId);
    this.writeC(this._logList.size());

    for(int i = 0; i < this._logList.size(); ++i) {
      int[] values = (int[])this._logList.get(i);
      this.writeD(values[0]);
      this.writeC(0);
      this.writeD(values[1]);
    }

  }
}
