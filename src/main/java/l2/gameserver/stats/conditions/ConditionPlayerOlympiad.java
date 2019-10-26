//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionPlayerOlympiad extends Condition {
  private final boolean _value;

  public ConditionPlayerOlympiad(boolean v) {
    this._value = v;
  }

  protected boolean testImpl(Env env) {
    return env.character.isOlyParticipant() == this._value;
  }
}
