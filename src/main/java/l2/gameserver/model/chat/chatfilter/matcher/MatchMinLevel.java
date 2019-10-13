//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinLevel implements ChatFilterMatcher {
  private final int _level;

  public MatchMinLevel(int level) {
    this._level = level;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return player.getLevel() < this._level;
  }
}
