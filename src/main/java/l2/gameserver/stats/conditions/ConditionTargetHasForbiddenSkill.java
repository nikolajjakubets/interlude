//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;

public final class ConditionTargetHasForbiddenSkill extends Condition {
  private final int _skillId;

  public ConditionTargetHasForbiddenSkill(int skillId) {
    this._skillId = skillId;
  }

  protected boolean testImpl(Env env) {
    Creature target = env.target;
    if (!target.isPlayable()) {
      return false;
    } else {
      return target.getSkillLevel(this._skillId) <= 0;
    }
  }
}
