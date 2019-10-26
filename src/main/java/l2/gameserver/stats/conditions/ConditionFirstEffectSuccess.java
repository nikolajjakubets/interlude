//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionFirstEffectSuccess extends Condition {
  boolean _param;

  public ConditionFirstEffectSuccess(boolean param) {
    this._param = param;
  }

  protected boolean testImpl(Env env) {
    return this._param == (env.value == 2.147483647E9D);
  }
}
