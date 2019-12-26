//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

import java.util.Collection;
import java.util.Collections;

public class MagicSkillLaunched extends L2GameServerPacket {
  private final int _casterId;
  private final int _skillId;
  private final int _skillLevel;
  private final int _casterX;
  private final int _casterY;
  private final Collection<Creature> _targets;

  public MagicSkillLaunched(Creature caster, int skillId, int skillLevel, Creature target) {
    this._casterId = caster.getObjectId();
    this._casterX = caster.getX();
    this._casterY = caster.getY();
    this._skillId = skillId;
    this._skillLevel = skillLevel;
    this._targets = Collections.singletonList(target);
  }

  public MagicSkillLaunched(Creature caster, Skill skill, Creature target) {
    this._casterId = caster.getObjectId();
    this._casterX = caster.getX();
    this._casterY = caster.getY();
    this._skillId = skill.getDisplayId();
    this._skillLevel = skill.getDisplayLevel();
    this._targets = Collections.singletonList(target);
  }

  public MagicSkillLaunched(Creature caster, int skillId, int skillLevel, Collection<Creature> targets) {
    this._casterId = caster.getObjectId();
    this._casterX = caster.getX();
    this._casterY = caster.getY();
    this._skillId = skillId;
    this._skillLevel = skillLevel;
    this._targets = targets;
  }

  public MagicSkillLaunched(Creature caster, Skill skill, Collection<Creature> targets) {
    this._casterId = caster.getObjectId();
    this._casterX = caster.getX();
    this._casterY = caster.getY();
    this._skillId = skill.getDisplayId();
    this._skillLevel = skill.getDisplayLevel();
    this._targets = targets;
  }

  protected final void writeImpl() {
    this.writeC(118);
    this.writeD(this._casterId);
    this.writeD(this._skillId);
    this.writeD(this._skillLevel);
    this.writeD(this._targets.size());

    for (Creature target : this._targets) {
      if (target != null) {
        this.writeD(target.getObjectId());
      }
    }

  }

  public L2GameServerPacket packet(Player player) {
    if (player != null && !player.isInObserverMode()) {
      if (player.buffAnimRange() < 0) {
        return null;
      } else if (player.buffAnimRange() == 0) {
        return this._casterId == player.getObjectId() ? super.packet(player) : null;
      } else {
        return player.getDistance(this._casterX, this._casterY) < (double)player.buffAnimRange() ? super.packet(player) : null;
      }
    } else {
      return super.packet(player);
    }
  }
}
