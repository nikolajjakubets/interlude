//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;

public interface ChatFilterMatcher {
  boolean isMatch(Player var1, ChatType var2, String var3, Player var4);
}
