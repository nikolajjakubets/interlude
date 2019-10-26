//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class ExFishingHpRegen extends L2GameServerPacket {
  private int _time;
  private int _fishHP;
  private int _HPmode;
  private int _Anim;
  private int _GoodUse;
  private int _Penalty;
  private int _hpBarColor;
  private int char_obj_id;

  public ExFishingHpRegen(Creature character, int time, int fishHP, int HPmode, int GoodUse, int anim, int penalty, int hpBarColor) {
    this.char_obj_id = character.getObjectId();
    this._time = time;
    this._fishHP = fishHP;
    this._HPmode = HPmode;
    this._GoodUse = GoodUse;
    this._Anim = anim;
    this._Penalty = penalty;
    this._hpBarColor = hpBarColor;
  }

  protected final void writeImpl() {
    this.writeEx(22);
    this.writeD(this.char_obj_id);
    this.writeD(this._time);
    this.writeD(this._fishHP);
    this.writeC(this._HPmode);
    this.writeC(this._GoodUse);
    this.writeC(this._Anim);
    this.writeD(this._Penalty);
    this.writeC(this._hpBarColor);
  }
}
