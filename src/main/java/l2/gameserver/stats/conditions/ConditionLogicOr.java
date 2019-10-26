//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionLogicOr extends Condition {
  private static final Condition[] emptyConditions = new Condition[0];
  public Condition[] _conditions;

  public ConditionLogicOr() {
    this._conditions = emptyConditions;
  }

  public void add(Condition condition) {
    if (condition != null) {
      int len = this._conditions.length;
      Condition[] tmp = new Condition[len + 1];
      System.arraycopy(this._conditions, 0, tmp, 0, len);
      tmp[len] = condition;
      this._conditions = tmp;
    }
  }

  protected boolean testImpl(Env env) {
    Condition[] var2 = this._conditions;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Condition c = var2[var4];
      if (c.test(env)) {
        return true;
      }
    }

    return false;
  }
}
