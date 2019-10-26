//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.stats.Env;

public class ConditionTargetCastleDoor extends Condition {
  private final boolean _isCastleDoor;

  public ConditionTargetCastleDoor(boolean isCastleDoor) {
    this._isCastleDoor = isCastleDoor;
  }

  protected boolean testImpl(Env env) {
    return env.target instanceof DoorInstance == this._isCastleDoor;
  }
}
