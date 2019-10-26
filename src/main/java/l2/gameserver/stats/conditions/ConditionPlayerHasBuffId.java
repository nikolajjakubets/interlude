//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class ConditionPlayerHasBuffId extends Condition {
  private final int _id;
  private final int _level;

  public ConditionPlayerHasBuffId(int id, int level) {
    this._id = id;
    this._level = level;
  }

  protected boolean testImpl(Env env) {
    Creature character = env.character;
    if (character == null) {
      return false;
    } else if (this._level == -1) {
      return character.getEffectList().getEffectsBySkillId(this._id) != null;
    } else {
      List<Effect> el = character.getEffectList().getEffectsBySkillId(this._id);
      if (el == null) {
        return false;
      } else {
        Iterator var4 = el.iterator();

        Effect effect;
        do {
          if (!var4.hasNext()) {
            return false;
          }

          effect = (Effect)var4.next();
        } while(effect == null || effect.getSkill().getLevel() < this._level);

        return true;
      }
    }
  }
}
