//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.stats.Env;

public class ConditionPlayerRace extends Condition {
  private final Race _race;

  public ConditionPlayerRace(String race) {
    this._race = Race.valueOf(race.toLowerCase());
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else {
      return ((Player)env.character).getRace() == this._race;
    }
  }
}
