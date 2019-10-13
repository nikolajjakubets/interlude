//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExQuestNpcLogList;

public class RequestAddExpandQuestAlarm extends L2GameClientPacket {
  private int _questId;

  public RequestAddExpandQuestAlarm() {
  }

  protected void readImpl() throws Exception {
    this._questId = this.readD();
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Quest quest = QuestManager.getQuest(this._questId);
      if (quest != null) {
        QuestState state = player.getQuestState(quest.getClass());
        if (state != null) {
          player.sendPacket(new ExQuestNpcLogList(state));
        }
      }
    }
  }
}
