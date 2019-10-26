//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectDamOverTime extends Effect {
  private static int[] bleed = new int[]{12, 17, 25, 34, 44, 54, 62, 67, 72, 77, 82, 87};
  private static int[] poison = new int[]{11, 16, 24, 32, 41, 50, 58, 63, 68, 72, 77, 82};
  private boolean _percent = this.getTemplate().getParam().getBool("percent", false);

  public EffectDamOverTime(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else {
      double damage = this.calc();
      if (this._percent) {
        damage = (double)this._effected.getMaxHp() * this._template._value * 0.01D;
      }

      if (damage < 2.0D && this.getStackOrder() != -1) {
        switch(this.getEffectType()) {
          case Poison:
            damage = (double)((long)poison[this.getStackOrder() - 1] * this.getPeriod() / 1000L);
            break;
          case Bleed:
            damage = (double)((long)bleed[this.getStackOrder() - 1] * this.getPeriod() / 1000L);
        }
      }

      damage = this._effector.calcStat(this.getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, this._effected, this.getSkill());
      if (damage > this._effected.getCurrentHp() - 1.0D && !this._effected.isNpc()) {
        if (!this.getSkill().isOffensive()) {
          this._effected.sendPacket(Msg.NOT_ENOUGH_HP);
        }

        return false;
      } else {
        if (this.getSkill().getAbsorbPart() > 0.0D) {
          this._effector.setCurrentHp(this.getSkill().getAbsorbPart() * Math.min(this._effected.getCurrentHp(), damage) + this._effector.getCurrentHp(), false);
        }

        this._effected.reduceCurrentHp(damage, this._effector, this.getSkill(), !this._effected.isNpc() && this._effected != this._effector, this._effected != this._effector, this._effector.isNpc() || this._effected == this._effector, false, false, true, false);
        return true;
      }
    }
  }
}
