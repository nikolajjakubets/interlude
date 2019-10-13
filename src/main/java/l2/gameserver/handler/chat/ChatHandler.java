//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.chat;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.network.l2.components.ChatType;

public class ChatHandler extends AbstractHolder {
  private static final ChatHandler _instance = new ChatHandler();
  private IChatHandler[] _handlers;

  public static ChatHandler getInstance() {
    return _instance;
  }

  private ChatHandler() {
    this._handlers = new IChatHandler[ChatType.VALUES.length];
  }

  public void register(IChatHandler chatHandler) {
    this._handlers[chatHandler.getType().ordinal()] = chatHandler;
  }

  public IChatHandler getHandler(ChatType type) {
    return this._handlers[type.ordinal()];
  }

  public int size() {
    return this._handlers.length;
  }

  public void clear() {
  }
}
