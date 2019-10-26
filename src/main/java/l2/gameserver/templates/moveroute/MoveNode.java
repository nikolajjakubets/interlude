//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.moveroute;

import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.utils.Location;

public class MoveNode extends Location {
  private static final long serialVersionUID = 8291528118019681063L;
  private final String _msgAddr;
  private final ChatType _chatType;
  private final long _delay;
  private final int _socialId;

  public MoveNode(int x, int y, int z, String msgAddr, int socialId, long delay, ChatType chatType) {
    super(x, y, z);
    this._msgAddr = msgAddr;
    this._socialId = socialId;
    this._delay = delay;
    this._chatType = chatType;
  }

  public String getNpcMsgAddress() {
    return this._msgAddr;
  }

  public long getDelay() {
    return this._delay;
  }

  public int getSocialId() {
    return this._socialId;
  }

  public ChatType getChatType() {
    return this._chatType;
  }
}
