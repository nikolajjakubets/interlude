//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExReplyWritePost extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC_TRUE = new ExReplyWritePost(1);
  public static final L2GameServerPacket STATIC_FALSE = new ExReplyWritePost(0);
  private int _reply;

  public ExReplyWritePost(int i) {
    this._reply = i;
  }

  protected void writeImpl() {
    this.writeEx(180);
    this.writeD(this._reply);
  }
}
