//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class StartRotating extends L2GameServerPacket {
  private int _charId;
  private int _degree;
  private int _side;
  private int _speed;

  public StartRotating(Creature cha, int degree, int side, int speed) {
    this._charId = cha.getObjectId();
    this._degree = degree;
    this._side = side;
    this._speed = speed;
  }

  protected final void writeImpl() {
    this.writeC(98);
    this.writeD(this._charId);
    this.writeD(this._degree);
    this.writeD(this._side);
    this.writeD(this._speed);
  }
}
