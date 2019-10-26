//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.TamedBeastInstance;
import l2.gameserver.templates.StatsSet;

public class TameControl extends Skill {
  private final int _type;

  public TameControl(StatsSet set) {
    super(set);
    this._type = set.getInteger("type", 0);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

    if (activeChar.isPlayer()) {
      Player player = activeChar.getPlayer();
      if (player.getTrainedBeast() != null) {
        if (this._type == 0) {
          Iterator var4 = targets.iterator();

          while(var4.hasNext()) {
            Creature target = (Creature)var4.next();
            if (target != null && target instanceof TamedBeastInstance && player.getTrainedBeast() == target) {
              player.getTrainedBeast().despawnWithDelay(1000);
            }
          }
        } else if (this._type > 0) {
          TamedBeastInstance tamedBeast = player.getTrainedBeast();
          if (tamedBeast != null) {
            switch(this._type) {
              case 1:
                tamedBeast.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
              case 2:
              default:
                break;
              case 3:
                tamedBeast.buffOwner();
                break;
              case 4:
                tamedBeast.doDespawn();
            }
          }
        }

      }
    }
  }
}
