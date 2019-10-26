//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.skills.TimeStamp;

public class ExUseSharedGroupItem extends L2GameServerPacket {
  private int _itemId;
  private int _grpId;
  private int _remainedTime;
  private int _totalTime;

  public ExUseSharedGroupItem(int grpId, TimeStamp timeStamp) {
    this._grpId = grpId;
    this._itemId = timeStamp.getId();
    this._remainedTime = (int)(timeStamp.getReuseCurrent() / 1000L);
    this._totalTime = (int)(timeStamp.getReuseBasic() / 1000L);
  }

  protected final void writeImpl() {
    this.writeEx(73);
    this.writeD(this._itemId);
    this.writeD(this._grpId);
    this.writeD(this._remainedTime);
    this.writeD(this._totalTime);
  }
}
