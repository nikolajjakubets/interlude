//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionLogicNot extends Condition {
  private final Condition _condition;

  public ConditionLogicNot(Condition condition) {
    this._condition = condition;
  }

  protected boolean testImpl(Env env) {
    return !this._condition.test(env);
  }
}
