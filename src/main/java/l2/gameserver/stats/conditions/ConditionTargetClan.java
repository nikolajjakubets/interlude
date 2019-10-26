//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;

public class ConditionTargetClan extends Condition {
  private final boolean _test;

  public ConditionTargetClan(String param) {
    this._test = Boolean.valueOf(param);
  }

  protected boolean testImpl(Env env) {
    Creature Char = env.character;
    Creature target = env.target;
    return Char.getPlayer() != null && target.getPlayer() != null && (Char.getPlayer().getClanId() != 0 && Char.getPlayer().getClanId() == target.getPlayer().getClanId() == this._test || Char.getPlayer().getParty() != null && Char.getPlayer().getParty() == target.getPlayer().getParty());
  }
}
