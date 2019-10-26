//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class ExFishingStartCombat extends L2GameServerPacket {
  int _time;
  int _hp;
  int _lureType;
  int _deceptiveMode;
  int _mode;
  private int char_obj_id;

  public ExFishingStartCombat(Creature character, int time, int hp, int mode, int lureType, int deceptiveMode) {
    this.char_obj_id = character.getObjectId();
    this._time = time;
    this._hp = hp;
    this._mode = mode;
    this._lureType = lureType;
    this._deceptiveMode = deceptiveMode;
  }

  protected final void writeImpl() {
    this.writeEx(21);
    this.writeD(this.char_obj_id);
    this.writeD(this._time);
    this.writeD(this._hp);
    this.writeC(this._mode);
    this.writeC(this._lureType);
    this.writeC(this._deceptiveMode);
  }
}
