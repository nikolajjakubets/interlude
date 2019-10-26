//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Fishing;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.WeaponTemplate;

public class ReelingPumping extends Skill {
  public ReelingPumping(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (!((Player)activeChar).isFishing()) {
      activeChar.sendPacket(this.getSkillType() == SkillType.PUMPING ? Msg.PUMPING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING : Msg.REELING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING);
      activeChar.sendActionFailed();
      return false;
    } else {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }
  }

  public void useSkill(Creature caster, List<Creature> targets) {
    if (caster != null && caster.isPlayer()) {
      Player player = caster.getPlayer();
      Fishing fishing = player.getFishing();
      if (fishing != null && fishing.isInCombat()) {
        WeaponTemplate weaponItem = player.getActiveWeaponItem();
        int SS = player.getChargedFishShot() ? 2 : 1;
        int pen = 0;
        double gradebonus = 1.0D + (double)weaponItem.getCrystalType().ordinal() * 0.1D;
        int dmg = (int)(this.getPower() * gradebonus * (double)SS);
        if (player.getSkillLevel(1315) < this.getLevel() - 2) {
          player.sendPacket(Msg.SINCE_THE_SKILL_LEVEL_OF_REELING_PUMPING_IS_HIGHER_THAN_THE_LEVEL_OF_YOUR_FISHING_MASTERY_A_PENALTY_OF_S1_WILL_BE_APPLIED);
          pen = 50;
          int penatlydmg = dmg - pen;
          dmg = penatlydmg;
        }

        if (SS == 2) {
          player.unChargeFishShot();
        }

        fishing.useFishingSkill(dmg, pen, this.getSkillType());
      }
    }
  }
}
