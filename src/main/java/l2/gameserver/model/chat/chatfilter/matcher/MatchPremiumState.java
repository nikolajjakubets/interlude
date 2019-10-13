//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchPremiumState implements ChatFilterMatcher {
  private final boolean _excludePremium;

  public MatchPremiumState(boolean premiumState) {
    this._excludePremium = premiumState;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    return this._excludePremium || !player.hasBonus();
  }
}
