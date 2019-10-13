//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinJobLevel implements ChatFilterMatcher {
  private final int _classLevel;

  public MatchMinJobLevel(int classLevel) {
    this._classLevel = classLevel;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return player.getClassId().level() < this._classLevel;
  }
}
