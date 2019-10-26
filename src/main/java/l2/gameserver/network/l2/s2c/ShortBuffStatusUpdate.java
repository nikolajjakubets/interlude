//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Effect;

public class ShortBuffStatusUpdate extends L2GameServerPacket {
  int _skillId;
  int _skillLevel;
  int _skillDuration;

  public ShortBuffStatusUpdate(Effect effect) {
    this._skillId = effect.getSkill().getDisplayId();
    this._skillLevel = effect.getSkill().getDisplayLevel();
    this._skillDuration = effect.getTimeLeft();
  }

  public ShortBuffStatusUpdate() {
    this._skillId = 0;
    this._skillLevel = 0;
    this._skillDuration = 0;
  }

  protected final void writeImpl() {
    this.writeC(244);
    this.writeD(this._skillId);
    this.writeD(this._skillLevel);
    this.writeD(this._skillDuration);
  }
}
