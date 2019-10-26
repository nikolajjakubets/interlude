//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public final class ConditionHasSkill extends Condition {
  private final Integer _id;
  private final int _level;

  public ConditionHasSkill(Integer id, int level) {
    this._id = id;
    this._level = level;
  }

  protected boolean testImpl(Env env) {
    if (env.skill == null) {
      return false;
    } else {
      return env.character.getSkillLevel(this._id) >= this._level;
    }
  }
}
