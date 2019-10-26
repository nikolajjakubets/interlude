//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerRiding extends Condition {
  private final ConditionPlayerRiding.CheckPlayerRiding _riding;

  public ConditionPlayerRiding(ConditionPlayerRiding.CheckPlayerRiding riding) {
    this._riding = riding;
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else if (this._riding == ConditionPlayerRiding.CheckPlayerRiding.STRIDER && ((Player)env.character).isRiding()) {
      return true;
    } else if (this._riding == ConditionPlayerRiding.CheckPlayerRiding.WYVERN && ((Player)env.character).isFlying()) {
      return true;
    } else {
      return this._riding == ConditionPlayerRiding.CheckPlayerRiding.NONE && !((Player)env.character).isRiding() && !((Player)env.character).isFlying();
    }
  }

  public static enum CheckPlayerRiding {
    NONE,
    STRIDER,
    WYVERN;

    private CheckPlayerRiding() {
    }
  }
}
