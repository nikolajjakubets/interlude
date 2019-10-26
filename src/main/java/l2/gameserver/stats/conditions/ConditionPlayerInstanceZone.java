//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.entity.Reflection;
import l2.gameserver.stats.Env;

public class ConditionPlayerInstanceZone extends Condition {
  private final int _id;

  public ConditionPlayerInstanceZone(int id) {
    this._id = id;
  }

  protected boolean testImpl(Env env) {
    Reflection ref = env.character.getReflection();
    return ref.getInstancedZoneId() == this._id;
  }
}
