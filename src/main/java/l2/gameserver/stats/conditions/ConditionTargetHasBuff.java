//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;

public final class ConditionTargetHasBuff extends Condition {
  private final EffectType _effectType;
  private final int _level;

  public ConditionTargetHasBuff(EffectType effectType, int level) {
    this._effectType = effectType;
    this._level = level;
  }

  protected boolean testImpl(Env env) {
    Creature target = env.target;
    if (target == null) {
      return false;
    } else {
      Effect effect = target.getEffectList().getEffectByType(this._effectType);
      if (effect == null) {
        return false;
      } else {
        return this._level == -1 || effect.getSkill().getLevel() >= this._level;
      }
    }
  }
}
