//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;

public class ConditionPlayerHasBuff extends Condition {
  private final EffectType _effectType;
  private final int _level;

  public ConditionPlayerHasBuff(EffectType effectType, int level) {
    this._effectType = effectType;
    this._level = level;
  }

  protected boolean testImpl(Env env) {
    Creature character = env.character;
    if (character == null) {
      return false;
    } else {
      Effect effect = character.getEffectList().getEffectByType(this._effectType);
      if (effect == null) {
        return false;
      } else {
        return this._level == -1 || effect.getSkill().getLevel() >= this._level;
      }
    }
  }
}
