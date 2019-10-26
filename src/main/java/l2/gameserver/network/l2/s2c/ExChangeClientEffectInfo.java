//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExChangeClientEffectInfo extends L2GameServerPacket {
  private int _state;

  public ExChangeClientEffectInfo(int state) {
    this._state = state;
  }

  protected void writeImpl() {
    this.writeEx(193);
    this.writeD(0);
    this.writeD(this._state);
  }
}
