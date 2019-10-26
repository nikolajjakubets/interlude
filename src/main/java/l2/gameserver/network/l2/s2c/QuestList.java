//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;

public class QuestList extends L2GameServerPacket {
  private List<int[]> questlist;

  public QuestList(Player player) {
    QuestState[] allQuestStates = player.getAllQuestsStates();
    this.questlist = new ArrayList(allQuestStates.length);
    QuestState[] var3 = allQuestStates;
    int var4 = allQuestStates.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      QuestState quest = var3[var5];
      if (quest.getQuest().isVisible() && quest.isStarted()) {
        this.questlist.add(new int[]{quest.getQuest().getQuestIntId(), quest.getInt("cond")});
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(128);
    this.writeH(this.questlist.size());
    Iterator var1 = this.questlist.iterator();

    while(var1.hasNext()) {
      int[] q = (int[])var1.next();
      this.writeD(q[0]);
      this.writeD(q[1]);
    }

  }
}
