//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectCharmOfCourage extends Effect {
  public EffectCharmOfCourage(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected.isPlayer()) {
      this._effected.getPlayer().setCharmOfCourage(true);
    }

  }

  public void onExit() {
    super.onExit();
    this._effected.getPlayer().setCharmOfCourage(false);
  }

  public boolean onActionTime() {
    return false;
  }
}
