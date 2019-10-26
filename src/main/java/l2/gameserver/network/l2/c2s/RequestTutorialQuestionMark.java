//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.network.l2.GameClient;

public class RequestTutorialQuestionMark extends L2GameClientPacket {
  int _number = 0;

  public RequestTutorialQuestionMark() {
  }

  protected void readImpl() {
    this._number = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Quest q = QuestManager.getQuest(255);
      if (q != null) {
        player.processQuestEvent(q.getName(), "QM" + this._number, (NpcInstance)null);
      }

    }
  }
}
