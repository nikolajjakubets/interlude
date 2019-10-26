//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.triggers;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.AddedSkill;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class TriggerInfo extends AddedSkill {
  private final TriggerType _type;
  private final double _chance;
  private Condition[] _conditions;

  public TriggerInfo(int id, int level, TriggerType type, double chance) {
    super(id, level);
    this._conditions = Condition.EMPTY_ARRAY;
    this._type = type;
    this._chance = chance;
  }

  public final void addCondition(Condition c) {
    this._conditions = (Condition[])ArrayUtils.add(this._conditions, c);
  }

  public boolean checkCondition(Creature actor, Creature target, Creature aimTarget, Skill owner, double damage) {
    if (this.getSkill().checkTarget(actor, aimTarget, aimTarget, false, false) != null) {
      return false;
    } else {
      Env env = new Env();
      env.character = actor;
      env.skill = owner;
      env.target = target;
      env.value = damage;
      Condition[] var8 = this._conditions;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
        Condition c = var8[var10];
        if (!c.test(env)) {
          return false;
        }
      }

      return true;
    }
  }

  public TriggerType getType() {
    return this._type;
  }

  public double getChance() {
    return this._chance;
  }
}
