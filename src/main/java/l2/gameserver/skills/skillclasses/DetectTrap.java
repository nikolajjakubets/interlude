//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DetectTrap extends Skill {
  public DetectTrap(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(true) {
      TrapInstance trap;
      do {
        Creature target;
        do {
          do {
            if (!var3.hasNext()) {
              if (this.isSSPossible()) {
                activeChar.unChargeShots(this.isMagic());
              }

              return;
            }

            target = (Creature)var3.next();
          } while(target == null);
        } while(!target.isTrap());

        trap = (TrapInstance)target;
      } while((double)trap.getLevel() > this.getPower());

      trap.setDetected(true);
      Iterator var6 = World.getAroundPlayers(trap).iterator();

      while(var6.hasNext()) {
        Player player = (Player)var6.next();
        player.sendPacket(new NpcInfo(trap, player));
      }
    }
  }
}
