//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerState extends Condition {
  private final ConditionPlayerState.CheckPlayerState _check;
  private final boolean _required;

  public ConditionPlayerState(ConditionPlayerState.CheckPlayerState check, boolean required) {
    this._check = check;
    this._required = required;
  }

  protected boolean testImpl(Env env) {
    switch(this._check) {
      case RESTING:
        if (env.character.isPlayer()) {
          return ((Player)env.character).isSitting() == this._required;
        }

        return !this._required;
      case MOVING:
        return env.character.isMoving() == this._required;
      case RUNNING:
        return (env.character.isMoving() && env.character.isRunning()) == this._required;
      case STANDING:
        if (!env.character.isPlayer()) {
          return env.character.isMoving() != this._required;
        }

        return ((Player)env.character).isSitting() != this._required && env.character.isMoving() != this._required;
      case FLYING:
        if (env.character.isPlayer()) {
          return env.character.isFlying() == this._required;
        }

        return !this._required;
      case FLYING_TRANSFORM:
        if (env.character.isPlayer()) {
          return ((Player)env.character).isInFlyingTransform() == this._required;
        }

        return !this._required;
      default:
        return !this._required;
    }
  }

  public static enum CheckPlayerState {
    RESTING,
    MOVING,
    RUNNING,
    STANDING,
    FLYING,
    FLYING_TRANSFORM;

    private CheckPlayerState() {
    }
  }
}
