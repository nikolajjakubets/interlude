//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class Ride extends Skill {
  public Ride(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (!activeChar.isPlayer()) {
      return false;
    } else {
      Player player = (Player)activeChar;
      if (this.getNpcId() != 0) {
        if (player.isOlyParticipant()) {
          player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
          return false;
        }

        if (player.isInDuel() || player.isSitting() || player.isInCombat() || player.isFishing() || player.isCursedWeaponEquipped() || player.getTransformation() != 0 || player.getPet() != null || player.isMounted() || player.isInBoat()) {
          player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
          return false;
        }
      } else if (this.getNpcId() == 0 && !player.isMounted()) {
        return false;
      }

      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }
  }

  public void useSkill(Creature caster, List<Creature> targets) {
    if (caster.isPlayer()) {
      Player activeChar = (Player)caster;
      activeChar.setMount(this.getNpcId(), 0, 0);
    }
  }
}
