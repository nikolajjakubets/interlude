//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import java.util.regex.Pattern;
import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.network.l2.components.ChatType;

public class MatchWords implements ChatFilterMatcher {
  public final Pattern[] _patterns;

  public MatchWords(String[] words) {
    this._patterns = new Pattern[words.length];

    for(int i = 0; i < words.length; ++i) {
      this._patterns[i] = Pattern.compile(words[i], 66);
    }

  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    Pattern[] var5 = this._patterns;
    int var6 = var5.length;

    for(int var7 = 0; var7 < var6; ++var7) {
      Pattern p = var5[var7];
      if (p.matcher(msg).find()) {
        return true;
      }
    }

    return false;
  }
}
