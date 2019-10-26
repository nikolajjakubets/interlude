//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectSkillSeed;
import l2.gameserver.stats.Env;

public class ConditionPlayerSkillMinSeed extends Condition {
  private final int _skillId;
  private final int _skillMinSeed;

  public ConditionPlayerSkillMinSeed(int skillId, int skillMinSeed) {
    this._skillId = skillId;
    this._skillMinSeed = skillMinSeed;
  }

  protected boolean testImpl(Env env) {
    Creature activeChar = env.character;
    if (activeChar == null) {
      return false;
    } else {
      List<Effect> effects = activeChar.getEffectList().getEffectsBySkillId(this._skillId);
      if (effects != null && !effects.isEmpty()) {
        Iterator var4 = effects.iterator();

        while(var4.hasNext()) {
          Effect effect = (Effect)var4.next();
          if (effect instanceof EffectSkillSeed) {
            EffectSkillSeed effectSeed = (EffectSkillSeed)effect;
            if (effectSeed.getSeeds() >= this._skillMinSeed) {
              return true;
            }
          }
        }

        return false;
      } else {
        return false;
      }
    }
  }
}
