//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public class ConditionZoneName extends Condition {
  private final String _zoneName;

  public ConditionZoneName(String zoneName) {
    this._zoneName = zoneName;
  }

  protected boolean testImpl(Env env) {
    return !env.character.isPlayer() ? false : env.character.isInZone(this._zoneName);
  }
}
