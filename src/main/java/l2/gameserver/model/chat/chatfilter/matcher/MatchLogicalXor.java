//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchLogicalXor implements ChatFilterMatcher {
  private final ChatFilterMatcher[] _matches;

  public MatchLogicalXor(ChatFilterMatcher[] matches) {
    this._matches = matches;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    boolean match = false;
    ChatFilterMatcher[] var6 = this._matches;
    int var7 = var6.length;

    for(int var8 = 0; var8 < var7; ++var8) {
      ChatFilterMatcher m = var6[var8];
      if (m.isMatch(player, type, msg, recipient)) {
        if (match) {
          return false;
        }

        match = true;
      }
    }

    return match;
  }
}
