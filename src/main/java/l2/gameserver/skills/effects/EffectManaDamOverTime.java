//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;

public class EffectManaDamOverTime extends Effect {
  public EffectManaDamOverTime(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else {
      double manaDam = this.calc();
      if (manaDam > this._effected.getCurrentMp() && this.getSkill().isToggle()) {
        this._effected.sendPacket(Msg.NOT_ENOUGH_MP);
        this._effected.sendPacket((new SystemMessage(749)).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
        return false;
      } else {
        this._effected.reduceCurrentMp(manaDam, (Creature)null);
        return true;
      }
    }
  }
}
