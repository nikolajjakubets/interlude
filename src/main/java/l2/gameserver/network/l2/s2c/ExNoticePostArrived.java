//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExNoticePostArrived extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC_TRUE = new ExNoticePostArrived(1);
  public static final L2GameServerPacket STATIC_FALSE = new ExNoticePostArrived(0);
  private int _anim;

  public ExNoticePostArrived(int useAnim) {
    this._anim = useAnim;
  }

  protected void writeImpl() {
    this.writeEx(169);
    this.writeD(this._anim);
  }
}
