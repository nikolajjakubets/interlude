//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchLogicalAnd implements ChatFilterMatcher {
  private final ChatFilterMatcher[] _matches;

  public MatchLogicalAnd(ChatFilterMatcher[] matches) {
    this._matches = matches;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    ChatFilterMatcher[] var5 = this._matches;
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      ChatFilterMatcher m = var5[var7];
      if (!m.isMatch(player, type, msg, recipient)) {
        return false;
      }
    }

    return true;
  }
}
