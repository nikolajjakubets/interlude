//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class Toggle extends Skill {
  public Toggle(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.getEffectList().getEffectsBySkillId(this._id) != null) {
      activeChar.getEffectList().stopEffect(this._id);
      activeChar.sendActionFailed();
    } else {
      this.getEffects(activeChar, activeChar, this.getActivateRate() > 0, false);
    }
  }
}
