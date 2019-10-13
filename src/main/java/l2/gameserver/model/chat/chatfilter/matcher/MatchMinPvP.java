//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchMinPvP implements ChatFilterMatcher {
  private final int _pvp;

  public MatchMinPvP(int pvp) {
    this._pvp = pvp;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return player.getPvpKills() < this._pvp;
  }
}
