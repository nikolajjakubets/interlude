//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.chat;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import org.apache.commons.lang3.ArrayUtils;

public class ChatFilters extends AbstractHolder {
  private static final ChatFilters _instance = new ChatFilters();
  private ChatFilter[] filters = new ChatFilter[0];

  public static final ChatFilters getinstance() {
    return _instance;
  }

  private ChatFilters() {
  }

  public ChatFilter[] getFilters() {
    return this.filters;
  }

  public void add(ChatFilter f) {
    this.filters = (ChatFilter[])ArrayUtils.add(this.filters, f);
  }

  public void log() {
    this.info(String.format("loaded %d filter(s).", this.size()));
  }

  public int size() {
    return this.filters.length;
  }

  public void clear() {
    this.filters = new ChatFilter[0];
  }
}
