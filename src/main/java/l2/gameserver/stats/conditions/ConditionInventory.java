//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;

public abstract class ConditionInventory extends Condition {
  protected final int _slot;

  public ConditionInventory(int slot) {
    this._slot = slot;
  }

  protected abstract boolean testImpl(Env var1);
}
