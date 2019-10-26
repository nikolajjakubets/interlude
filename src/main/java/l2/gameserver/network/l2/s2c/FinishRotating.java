//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class FinishRotating extends L2GameServerPacket {
  private int _charId;
  private int _degree;
  private int _speed;

  public FinishRotating(Creature player, int degree, int speed) {
    this._charId = player.getObjectId();
    this._degree = degree;
    this._speed = speed;
  }

  protected final void writeImpl() {
    this.writeC(99);
    this.writeD(this._charId);
    this.writeD(this._degree);
    this.writeD(this._speed);
    this.writeD(0);
  }
}
