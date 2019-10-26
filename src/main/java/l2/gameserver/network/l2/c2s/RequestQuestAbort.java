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

public class RequestQuestAbort extends L2GameClientPacket {
  private int _questID;

  public RequestQuestAbort() {
  }

  protected void readImpl() {
    this._questID = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    Quest quest = QuestManager.getQuest(this._questID);
    if (activeChar != null && quest != null) {
      if (quest.canAbortByPacket()) {
        QuestState qs = activeChar.getQuestState(quest.getClass());
        if (qs != null && !qs.isCompleted()) {
          qs.abortQuest();
        }

      }
    }
  }
}
