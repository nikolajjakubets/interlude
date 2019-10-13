//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchChatChannels implements ChatFilterMatcher {
  private final ChatType[] _channels;

  public MatchChatChannels(ChatType[] channels) {
    this._channels = channels;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    ChatType[] var5 = this._channels;
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      ChatType ct = var5[var7];
      if (ct == type) {
        return true;
      }
    }

    return false;
  }
}
