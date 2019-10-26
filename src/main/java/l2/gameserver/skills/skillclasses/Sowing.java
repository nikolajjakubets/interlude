//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;

public class Sowing extends Skill {
  public Sowing(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      Player player = (Player)activeChar;
      int seedId = player.getUseSeed();
      boolean altSeed = ItemHolder.getInstance().getTemplate(seedId).isAltSeed();
      if (!player.getInventory().destroyItemByItemId(seedId, 1L)) {
        activeChar.sendActionFailed();
      } else {
        player.sendPacket(SystemMessage2.removeItems(seedId, 1L));
        Iterator var6 = targets.iterator();

        while(true) {
          while(true) {
            Creature target;
            MonsterInstance monster;
            do {
              do {
                if (!var6.hasNext()) {
                  return;
                }

                target = (Creature)var6.next();
              } while(target == null);

              monster = (MonsterInstance)target;
            } while(monster.isSeeded());

            double SuccessRate = Config.MANOR_SOWING_BASIC_SUCCESS;
            double diffPlayerTarget = (double)Math.abs(activeChar.getLevel() - target.getLevel());
            double diffSeedTarget = (double)Math.abs(Manor.getInstance().getSeedLevel(seedId) - target.getLevel());
            if (diffPlayerTarget > (double)Config.MANOR_DIFF_PLAYER_TARGET) {
              SuccessRate -= (diffPlayerTarget - (double)Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;
            }

            if (diffSeedTarget > (double)Config.MANOR_DIFF_SEED_TARGET) {
              SuccessRate -= (diffSeedTarget - (double)Config.MANOR_DIFF_SEED_TARGET) * Config.MANOR_DIFF_SEED_TARGET_PENALTY;
            }

            if (altSeed) {
              SuccessRate *= Config.MANOR_SOWING_ALT_BASIC_SUCCESS / Config.MANOR_SOWING_BASIC_SUCCESS;
            }

            if (SuccessRate < 1.0D) {
              SuccessRate = 1.0D;
            }

            if (player.isGM()) {
              activeChar.sendMessage((new CustomMessage("l2p.gameserver.skills.skillclasses.Sowing.Chance", player, new Object[0])).addNumber((long)SuccessRate));
            }

            if (Rnd.chance(SuccessRate) && monster.setSeeded(player, seedId, altSeed)) {
              activeChar.sendPacket(Msg.THE_SEED_WAS_SUCCESSFULLY_SOWN);
            } else {
              activeChar.sendPacket(Msg.THE_SEED_WAS_NOT_SOWN);
            }
          }
        }
      }
    }
  }
}
