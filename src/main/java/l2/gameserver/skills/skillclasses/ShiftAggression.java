//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;

public class ShiftAggression extends Skill {
  public ShiftAggression(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.getPlayer() != null) {
      Iterator var3 = targets.iterator();

      while(true) {
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
        } while(!target.isPlayer());

        Player player = (Player)target;
        Iterator var6 = World.getAroundNpc(activeChar, this.getSkillRadius(), this.getSkillRadius()).iterator();

        while(var6.hasNext()) {
          NpcInstance npc = (NpcInstance)var6.next();
          AggroInfo ai = npc.getAggroList().get(activeChar);
          if (ai != null) {
            npc.getAggroList().addDamageHate(player, 0, ai.hate);
            npc.getAggroList().remove(activeChar, true);
          }
        }
      }
    }
  }
}
