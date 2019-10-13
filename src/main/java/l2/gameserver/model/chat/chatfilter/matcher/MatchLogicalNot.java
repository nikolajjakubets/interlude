//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchLogicalNot implements ChatFilterMatcher {
  private final ChatFilterMatcher _match;

  public MatchLogicalNot(ChatFilterMatcher match) {
    this._match = match;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return !this._match.isMatch(player, type, msg, recipient);
  }
}
