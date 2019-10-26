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

public class RequestTutorialLinkHtml extends L2GameClientPacket {
  String _bypass;

  public RequestTutorialLinkHtml() {
  }

  protected void readImpl() {
    this._bypass = this.readS();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Quest q = QuestManager.getQuest(255);
      if (q != null) {
        player.processQuestEvent(q.getName(), this._bypass, (NpcInstance)null);
      }

    }
  }
}
