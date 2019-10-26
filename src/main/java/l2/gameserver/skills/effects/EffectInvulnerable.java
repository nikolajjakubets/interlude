//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.stats.Env;

public final class EffectInvulnerable extends Effect {
  public EffectInvulnerable(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    if (this._effected.isInvul()) {
      return false;
    } else {
      Skill skill = this._effected.getCastingSkill();
      return skill != null && skill.getSkillType() == SkillType.TAKECASTLE ? false : super.checkCondition();
    }
  }

  public void onStart() {
    super.onStart();
    this._effected.startHealBlocked();
    this._effected.setIsInvul(true);
  }

  public void onExit() {
    super.onExit();
    this._effected.stopHealBlocked();
    this._effected.setIsInvul(false);
  }

  public boolean onActionTime() {
    return false;
  }
}
