//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.*;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class PetSummon extends Skill {
  public PetSummon(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    Player player = activeChar.getPlayer();
    if (player == null) {
      return false;
    } else if (player.getPetControlItem() == null) {
      return false;
    } else {
      int npcId = PetDataTable.getSummonId(player.getPetControlItem());
      if (npcId == 0) {
        return false;
      } else if (player.isInCombat()) {
        player.sendPacket(Msg.YOU_CANNOT_SUMMON_DURING_COMBAT);
        return false;
      } else if (player.isProcessingRequest()) {
        player.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
        return false;
      } else if (!player.isMounted() && player.getPet() == null) {
        if (player.isInBoat()) {
          player.sendPacket(Msg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
          return false;
        } else if (player.isInFlyingTransform()) {
          return false;
        } else if (player.isOlyParticipant()) {
          player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
          return false;
        } else if (player.isCursedWeaponEquipped()) {
          player.sendPacket(Msg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
          return false;
        } else {
          Iterator var8 = World.getAroundObjects(player, 120, 200).iterator();

          GameObject o;
          do {
            if (!var8.hasNext()) {
              return super.checkCondition(activeChar, target, forceUse, dontMove, first);
            }

            o = (GameObject)var8.next();
          } while(!o.isDoor());

          player.sendPacket(Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
          return false;
        }
      } else {
        player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
        return false;
      }
    }
  }

  public void useSkill(Creature caster, List<Creature> targets) {
    Player activeChar = caster.getPlayer();
    activeChar.summonPet();
    if (this.isSSPossible()) {
      caster.unChargeShots(this.isMagic());
    }

  }
}
