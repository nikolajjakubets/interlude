//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;

public class Transformation extends Skill {
  public final boolean useSummon;
  public final boolean isDisguise;
  public final String transformationName;

  public Transformation(StatsSet set) {
    super(set);
    this.useSummon = set.getBool("useSummon", false);
    this.isDisguise = set.getBool("isDisguise", false);
    this.transformationName = set.getString("transformationName", (String)null);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    Player player = target.getPlayer();
    if (player != null && player.getActiveWeaponFlagAttachment() == null) {
      if (player.getTransformation() != 0 && this.getId() != 619) {
        activeChar.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
        return false;
      } else if ((this.getId() == 840 || this.getId() == 841 || this.getId() == 842) && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || player.getPet() != null || player.getReflection() != ReflectionManager.DEFAULT)) {
        activeChar.sendPacket((new SystemMessage(113)).addSkillName(this._id, this._level));
        return false;
      } else if (player.isInFlyingTransform() && this.getId() == 619 && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333) {
        activeChar.sendPacket((new SystemMessage(113)).addSkillName(this._id, this._level));
        return false;
      } else if (player.isInWater()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
        return false;
      } else if (!player.isRiding() && player.getMountType() != 2) {
        if (player.getEffectList().getEffectsBySkillId(1411) != null) {
          activeChar.sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
          return false;
        } else if (player.isInBoat()) {
          activeChar.sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
          return false;
        } else {
          if (this.useSummon) {
            if (player.getPet() == null || !player.getPet().isSummon() || player.getPet().isDead()) {
              activeChar.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
              return false;
            }
          } else if (player.getPet() != null && player.getPet().isPet() && this.getId() != 619 && !this.isBaseTransformation()) {
            activeChar.sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITOR_PET);
            return false;
          }

          return super.checkCondition(activeChar, target, forceUse, dontMove, first);
        }
      } else {
        activeChar.sendPacket(Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
        return false;
      }
    } else {
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (this.useSummon) {
      if (activeChar.getPet() == null || !activeChar.getPet().isSummon() || activeChar.getPet().isDead()) {
        activeChar.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
        return;
      }

      activeChar.getPet().unSummon();
    }

    if (this.isSummonerTransformation() && activeChar.getPet() != null && activeChar.getPet().isSummon()) {
      activeChar.getPet().unSummon();
    }

    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && target.isPlayer()) {
        this.getEffects(activeChar, target, false, false);
      }
    }

    if (this.isSSPossible() && (!Config.SAVING_SPS || this._skillType != SkillType.BUFF)) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
