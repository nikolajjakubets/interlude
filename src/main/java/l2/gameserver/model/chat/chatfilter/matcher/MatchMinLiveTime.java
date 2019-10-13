//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinLiveTime implements ChatFilterMatcher {
  private final long _createTime;

  public MatchMinLiveTime(int createTime) {
    this._createTime = (long)createTime * 1000L;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return System.currentTimeMillis() - player.getCreateTime() < this._createTime;
  }
}
