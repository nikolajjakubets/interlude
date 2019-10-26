//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class SetupGauge extends L2GameServerPacket {
  public static final int BLUE = 0;
  public static final int RED = 1;
  public static final int CYAN = 2;
  private int _charId;
  private int _dat1;
  private int _time;

  public SetupGauge(Creature character, int dat1, int time) {
    this._charId = character.getObjectId();
    this._dat1 = dat1;
    this._time = time;
  }

  protected final void writeImpl() {
    this.writeC(109);
    this.writeD(this._dat1);
    this.writeD(this._time);
    this.writeD(this._time);
  }
}
