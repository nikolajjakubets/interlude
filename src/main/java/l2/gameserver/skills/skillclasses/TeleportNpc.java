//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class TeleportNpc extends Skill {
  public TeleportNpc(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(true) {
      Creature target;
      do {
        do {
          if (!var3.hasNext()) {
            return;
          }

          target = (Creature)var3.next();
        } while(target == null);
      } while(target.isDead());

      this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      target.abortAttack(true, true);
      target.abortCast(true, true);
      target.stopMove();
      int x = activeChar.getX();
      int y = activeChar.getY();
      int z = activeChar.getZ();
      int h = activeChar.getHeading();
      int range = (int)(activeChar.getColRadius() + target.getColRadius());
      int hyp = (int)Math.sqrt((double)(range * range / 2));
      if (h < 16384) {
        x += hyp;
        y += hyp;
      } else if (h > 16384 && h <= 32768) {
        x -= hyp;
        y += hyp;
      } else if (h < 32768 && h <= 49152) {
        x -= hyp;
        y -= hyp;
      } else if (h > 49152) {
        x += hyp;
        y -= hyp;
      }

      target.setXYZ(x, y, z);
      target.validateLocation(1);
    }
  }
}
