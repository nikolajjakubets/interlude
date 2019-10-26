//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Playable;
import l2.gameserver.stats.Env;

public final class ConditionUsingItemType extends Condition {
  private final long _mask;

  public ConditionUsingItemType(long mask) {
    this._mask = mask;
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayable()) {
      return false;
    } else {
      return (this._mask & ((Playable)env.character).getWearedMask()) != 0L;
    }
  }
}
