//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;

public interface OnQuestStateChangeListener extends PlayerListener {
  void onQuestStateChange(Player var1, QuestState var2);
}
