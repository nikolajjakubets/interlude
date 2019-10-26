//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExChangeNpcState extends L2GameServerPacket {
  private int _objId;
  private int _state;

  public ExChangeNpcState(int objId, int state) {
    this._objId = objId;
    this._state = state;
  }

  protected void writeImpl() {
    this.writeEx(190);
    this.writeD(this._objId);
    this.writeD(this._state);
  }
}
