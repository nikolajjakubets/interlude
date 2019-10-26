//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class BuffCharger extends Skill {
  private int _target;

  public BuffCharger(StatsSet set) {
    super(set);
    this._target = set.getInteger("targetBuff", 0);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      int level = 0;
      List<Effect> el = target.getEffectList().getEffectsBySkillId(this._target);
      if (el != null) {
        level = ((Effect)el.get(0)).getSkill().getLevel();
      }

      Skill next = SkillTable.getInstance().getInfo(this._target, level + 1);
      if (next != null) {
        next.getEffects(activeChar, target, false, false);
      }
    }

  }
}
