//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class PcBangPointsAdd extends Skill {
  public PcBangPointsAdd(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    int points = (int)this._power;

    Creature target;
    for(Iterator var4 = targets.iterator(); var4.hasNext(); this.getEffects(activeChar, target, this.getActivateRate() > 0, false)) {
      target = (Creature)var4.next();
      if (target.isPlayer()) {
        Player player = target.getPlayer();
        player.addPcBangPoints(points, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
