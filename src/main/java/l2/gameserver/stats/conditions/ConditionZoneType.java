//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.stats.Env;

public class ConditionZoneType extends Condition {
  private final ZoneType _zoneType;

  public ConditionZoneType(String zoneType) {
    this._zoneType = ZoneType.valueOf(zoneType);
  }

  protected boolean testImpl(Env env) {
    return !env.character.isPlayer() ? false : env.character.isInZone(this._zoneType);
  }
}
