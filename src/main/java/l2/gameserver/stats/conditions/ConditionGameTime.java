//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.GameTimeController;
import l2.gameserver.stats.Env;

public class ConditionGameTime extends Condition {
  private final ConditionGameTime.CheckGameTime _check;
  private final boolean _required;

  public ConditionGameTime(ConditionGameTime.CheckGameTime check, boolean required) {
    this._check = check;
    this._required = required;
  }

  protected boolean testImpl(Env env) {
    switch(this._check) {
      case NIGHT:
        return GameTimeController.getInstance().isNowNight() == this._required;
      default:
        return !this._required;
    }
  }

  public static enum CheckGameTime {
    NIGHT;

    private CheckGameTime() {
    }
  }
}
