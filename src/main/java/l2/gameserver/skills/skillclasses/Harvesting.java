//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

import java.util.Iterator;
import java.util.List;

public class Harvesting extends Skill {
  public Harvesting(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      Player player = (Player)activeChar;
      Iterator var4 = targets.iterator();

      while(true) {
        while(true) {
          Creature target;
          do {
            do {
              if (!var4.hasNext()) {
                return;
              }

              target = (Creature)var4.next();
            } while(target == null);
          } while(!target.isMonster());

          MonsterInstance monster = (MonsterInstance)target;
          if (!monster.isSeeded()) {
            activeChar.sendPacket(Msg.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
          } else if (!monster.isSeeded(player)) {
            activeChar.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
          } else {
            double SuccessRate = Config.MANOR_HARVESTING_BASIC_SUCCESS;
            int diffPlayerTarget = Math.abs(activeChar.getLevel() - monster.getLevel());
            if (diffPlayerTarget > Config.MANOR_DIFF_PLAYER_TARGET) {
              SuccessRate -= (double)(diffPlayerTarget - Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;
            }

            if (SuccessRate < 1.0D) {
              SuccessRate = 1.0D;
            }

            if (player.isGM()) {
              player.sendMessage((new CustomMessage("l2p.gameserver.skills.skillclasses.Harvesting.Chance", player, new Object[0])).addNumber((long)SuccessRate));
            }

            if (!Rnd.chance(SuccessRate)) {
              activeChar.sendPacket(Msg.THE_HARVEST_HAS_FAILED);
              monster.clearHarvest();
            } else {
              RewardItem item = monster.takeHarvest();
              if (item != null) {
                if (player.getInventory().validateCapacity(item.itemId, item.count) && player.getInventory().validateWeight(item.itemId, item.count)) {
                  player.getInventory().addItem(item.itemId, (long)((double)item.count * Config.MANOR_HARVESTING_REWARD_RATE));
                  player.sendPacket((new SystemMessage(1137)).addName(player).addNumber((long)((double)item.count * Config.MANOR_HARVESTING_REWARD_RATE)).addItemName(item.itemId));
                  if (player.isInParty()) {
                    SystemMessage smsg = (new SystemMessage(1137)).addString(player.getName()).addNumber((long)((double)item.count * Config.MANOR_HARVESTING_REWARD_RATE)).addItemName(item.itemId);
                    player.getParty().broadcastToPartyMembers(player, smsg);
                  }
                } else {
                  ItemInstance harvest = ItemFunctions.createItem(item.itemId);
                  harvest.setCount(item.count);
                  harvest.dropToTheGround(player, monster);
                }
              }
            }
          }
        }
      }
    }
  }
}
