//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Env;

public class EffectConsumeSoulsOverTime extends Effect {
  public EffectConsumeSoulsOverTime(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else if (this._effected.getConsumedSouls() < 0) {
      return false;
    } else {
      int damage = (int)this.calc();
      if (this._effected.getConsumedSouls() < damage) {
        this._effected.setConsumedSouls(0, (NpcInstance)null);
      } else {
        this._effected.setConsumedSouls(this._effected.getConsumedSouls() - damage, (NpcInstance)null);
      }

      return true;
    }
  }
}
