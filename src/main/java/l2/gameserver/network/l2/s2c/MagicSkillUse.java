//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

public class MagicSkillUse extends L2GameServerPacket {
  private int _targetId;
  private int _skillId;
  private int _skillLevel;
  private int _hitTime;
  private int _reuseDelay;
  private int _chaId;
  private int _x;
  private int _y;
  private int _z;
  private int _tx;
  private int _ty;
  private int _tz;

  public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay) {
    this._chaId = cha.getObjectId();
    this._targetId = target.getObjectId();
    this._skillId = skillId;
    this._skillLevel = skillLevel;
    this._hitTime = hitTime;
    this._reuseDelay = (int)reuseDelay;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
    this._tx = target.getX();
    this._ty = target.getY();
    this._tz = target.getZ();
  }

  public MagicSkillUse(Creature cha, Creature target, Skill skill, int hitTime, long reuseDelay) {
    this._chaId = cha.getObjectId();
    this._targetId = target.getObjectId();
    this._skillId = skill.getDisplayId();
    this._skillLevel = skill.getDisplayLevel();
    this._hitTime = hitTime;
    this._reuseDelay = (int)reuseDelay;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
    this._tx = target.getX();
    this._ty = target.getY();
    this._tz = target.getZ();
  }

  public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay) {
    this._chaId = cha.getObjectId();
    this._targetId = cha.getTargetId();
    this._skillId = skillId;
    this._skillLevel = skillLevel;
    this._hitTime = hitTime;
    this._reuseDelay = (int)reuseDelay;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
    this._tx = cha.getX();
    this._ty = cha.getY();
    this._tz = cha.getZ();
  }

  public MagicSkillUse(Creature cha, Skill skill, int hitTime, long reuseDelay) {
    this._chaId = cha.getObjectId();
    this._targetId = cha.getTargetId();
    this._skillId = skill.getDisplayId();
    this._skillLevel = skill.getDisplayLevel();
    this._hitTime = hitTime;
    this._reuseDelay = (int)reuseDelay;
    this._x = cha.getX();
    this._y = cha.getY();
    this._z = cha.getZ();
    this._tx = cha.getX();
    this._ty = cha.getY();
    this._tz = cha.getZ();
  }

  protected final void writeImpl() {
    this.writeC(72);
    this.writeD(this._chaId);
    this.writeD(this._targetId);
    this.writeD(this._skillId);
    this.writeD(this._skillLevel);
    this.writeD(this._hitTime);
    this.writeD(this._reuseDelay);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
    this.writeD(0);
    this.writeD(this._tx);
    this.writeD(this._ty);
    this.writeD(this._tz);
  }

  public L2GameServerPacket packet(Player player) {
    if (player != null && !player.isInObserverMode()) {
      if (player.buffAnimRange() < 0) {
        return null;
      } else if (player.buffAnimRange() == 0) {
        return this._chaId == player.getObjectId() ? super.packet(player) : null;
      } else {
        return player.getDistance(this._x, this._y) < (double)player.buffAnimRange() ? super.packet(player) : null;
      }
    } else {
      return super.packet(player);
    }
  }
}
