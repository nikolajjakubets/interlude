//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionUsingSkill extends Condition {
  private int _id;

  public ConditionUsingSkill(int id) {
    this._id = id;
  }

  protected boolean testImpl(Env env) {
    if (env.skill == null) {
      return false;
    } else {
      return env.skill.getId() == this._id;
    }
  }
}
