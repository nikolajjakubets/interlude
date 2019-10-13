//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat.chatfilter.matcher;

import java.util.Deque;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.network.l2.components.ChatType;

public class MatchChatLimit implements ChatFilterMatcher {
  private final int _limitCount;
  private final int _limitTime;
  private final int _limitBurst;

  public MatchChatLimit(int limitCount, int limitTime, int limitBurst) {
    this._limitCount = limitCount;
    this._limitTime = limitTime;
    this._limitBurst = limitBurst;
  }

  public boolean isMatch(Player player, ChatType type, String msg, Player recipient) {
    int currentTime = (int)(System.currentTimeMillis() / 1000L);
    int firstMsgTime = currentTime;
    int count = 0;
    Deque<ChatMsg> msgBucket = player.getMessageBucket();
    Iterator itr = msgBucket.descendingIterator();

    while(itr.hasNext()) {
      ChatMsg cm = (ChatMsg)itr.next();
      if (cm.chatType == type) {
        firstMsgTime = cm.time;
        ++count;
        if (this._limitBurst == count) {
          break;
        }
      }
    }

    count -= (currentTime - firstMsgTime) / this._limitTime * this._limitCount;
    return this._limitBurst <= count;
  }
}
