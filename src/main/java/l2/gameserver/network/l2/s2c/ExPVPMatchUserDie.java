//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPVPMatchUserDie extends L2GameServerPacket {
  private int _blueKills;
  private int _redKills;

  public ExPVPMatchUserDie(int blue, int red) {
    this._blueKills = blue;
    this._redKills = red;
  }

  protected final void writeImpl() {
    this.writeEx(127);
    this.writeD(this._blueKills);
    this.writeD(this._redKills);
  }
}
