//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionUsingBlowSkill extends Condition {
  private final boolean _flag;

  public ConditionUsingBlowSkill(boolean flag) {
    this._flag = flag;
  }

  protected boolean testImpl(Env env) {
    if (env.skill == null) {
      return !this._flag;
    } else {
      return env.skill.isBlowSkill() == this._flag;
    }
  }
}
