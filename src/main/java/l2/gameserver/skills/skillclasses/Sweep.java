//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

public class Sweep extends Skill {
  public Sweep(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (this.isNotTargetAoE()) {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    } else if (target == null) {
      return false;
    } else if (target.isMonster() && target.isDead()) {
      if (!((MonsterInstance)target).isSpoiled()) {
        activeChar.sendPacket(Msg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
        return false;
      } else if (!((MonsterInstance)target).isSpoiled((Player)activeChar)) {
        activeChar.sendPacket(Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
        return false;
      } else {
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
      }
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      Player player = (Player)activeChar;
      Iterator var4 = targets.iterator();

      while(true) {
        while(true) {
          Creature targ;
          do {
            do {
              do {
                do {
                  if (!var4.hasNext()) {
                    return;
                  }

                  targ = (Creature)var4.next();
                } while(targ == null);
              } while(!targ.isMonster());
            } while(!targ.isDead());
          } while(!((MonsterInstance)targ).isSpoiled());

          MonsterInstance target = (MonsterInstance)targ;
          if (!target.isSpoiled(player)) {
            activeChar.sendPacket(Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
          } else {
            List<RewardItem> items = target.takeSweep();
            if (items == null) {
              activeChar.getAI().setAttackTarget((Creature)null);
              target.endDecayTask();
            } else {
              Iterator var8 = items.iterator();

              while(true) {
                while(var8.hasNext()) {
                  RewardItem item = (RewardItem)var8.next();
                  ItemInstance sweep = ItemFunctions.createItem(item.itemId);
                  sweep.setCount(item.count);
                  if (player.isInParty() && player.getParty().isDistributeSpoilLoot()) {
                    player.getParty().distributeItem(player, sweep, (NpcInstance)null);
                  } else if (player.getInventory().validateCapacity(sweep) && player.getInventory().validateWeight(sweep)) {
                    player.getInventory().addItem(sweep);
                    SystemMessage smsg;
                    if (item.count == 1L) {
                      smsg = new SystemMessage(30);
                      smsg.addItemName(item.itemId);
                      player.sendPacket(smsg);
                    } else {
                      smsg = new SystemMessage(29);
                      smsg.addItemName(item.itemId);
                      smsg.addNumber(item.count);
                      player.sendPacket(smsg);
                    }

                    if (player.isInParty()) {
                      if (item.count == 1L) {
                        smsg = new SystemMessage(609);
                        smsg.addName(player);
                        smsg.addItemName(item.itemId);
                        player.getParty().broadcastToPartyMembers(player, smsg);
                      } else {
                        smsg = new SystemMessage(608);
                        smsg.addName(player);
                        smsg.addItemName(item.itemId);
                        smsg.addNumber(item.count);
                        player.getParty().broadcastToPartyMembers(player, smsg);
                      }
                    }
                  } else {
                    sweep.dropToTheGround(player, target);
                  }
                }

                activeChar.getAI().setAttackTarget((Creature)null);
                target.endDecayTask();
                break;
              }
            }
          }
        }
      }
    }
  }
}
