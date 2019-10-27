//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class EffectDispelEffects extends Effect {
  private final String _dispelType;
  private final int _cancelRate;
  private final String[] _stackTypes;
  private final int _negateCount;
  private final int _reApplyDelay;

  public EffectDispelEffects(Env env, EffectTemplate template) {
    super(env, template);
    this._dispelType = template.getParam().getString("dispelType", "");
    this._cancelRate = template.getParam().getInteger("cancelRate", 0);
    this._negateCount = template.getParam().getInteger("negateCount", 5);
    this._stackTypes = template.getParam().getString("negateStackTypes", "").split(";");
    this._reApplyDelay = template.getParam().getInteger("reApplyDelay", 0);
  }

  public void onStart() {
    List<Effect> musicList = new ArrayList<>();
    List<Effect> buffList = new ArrayList<>();
    Iterator var3 = this._effected.getEffectList().getAllEffects().iterator();

    while(true) {
      while(var3.hasNext()) {
        Effect e = (Effect)var3.next();
        if (this._dispelType.equals("cancellation")) {
          if (!e.isOffensive() && !e.getSkill().isToggle() && e.isCancelable()) {
            if (e.getSkill().isMusic()) {
              musicList.add(e);
            } else {
              buffList.add(e);
            }
          }
        } else if (this._dispelType.equals("bane")) {
          if (e.isCancelable() && (ArrayUtils.contains(this._stackTypes, e.getStackType()) || ArrayUtils.contains(this._stackTypes, e.getStackType2()))) {
            buffList.add(e);
          }
        } else if (this._dispelType.equals("cleanse") && e.isOffensive() && e.isCancelable()) {
          buffList.add(e);
        }
      }

      List<Effect> _effectList = new ArrayList<>();
      _effectList.addAll(musicList);
      _effectList.addAll(buffList);
      if (_effectList.isEmpty()) {
        return;
      }

      double cancel_res_multiplier = this._effected.calcStat(Stats.CANCEL_RESIST, 0.0D, null, null);
      Collections.shuffle(_effectList);
      List<Effect> subList = _effectList.subList(0, Math.min(this._negateCount, _effectList.size()));
      Set<Skill> _stopSkills = new HashSet<>();
      Iterator var14 = subList.iterator();

      while(var14.hasNext()) {
        Effect e = (Effect)var14.next();
        double eml = e.getSkill().getMagicLevel();
        double dml = (double)this.getSkill().getMagicLevel() - (eml == 0.0D ? (double)this._effected.getLevel() : eml);
        int buffTime = e.getTimeLeft();
        cancel_res_multiplier = 1.0D - cancel_res_multiplier * 0.01D;
        double prelimChance = (2.0D * dml + (double)this._cancelRate + (double)(buffTime / 120)) * cancel_res_multiplier;
        if (Rnd.chance(this.calcSkillChanceLimits(prelimChance, this._effector.isPlayable()))) {
          _stopSkills.add(e.getSkill());
        }
      }

      var14 = _stopSkills.iterator();

      while(var14.hasNext()) {
        Skill stopSkill = (Skill)var14.next();
        this._effected.getEffectList().stopEffect(stopSkill);
        this._effected.sendPacket((new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED)).addSkillName(stopSkill));
      }

      if (this._effected.isPlayer() && this._reApplyDelay > 0) {
        final HardReference<Player> reApplyRef = this._effected.getPlayer().getRef();
        final List<Skill> reApplySkills = new LinkedList<>();

        for (Skill stopSkill : _stopSkills) {
          if (!stopSkill.isOffensive() && !stopSkill.isToggle() && !stopSkill.isTrigger()) {
            reApplySkills.add(stopSkill);
          }
        }

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            Player player = reApplyRef.get();
            if (player != null && !player.isLogoutStarted() && !player.isOutOfControl() && !player.isDead() && !player.isInDuel() && !player.isAlikeDead() && !player.isOlyParticipant() && !player.isFlying() && !player.isSitting() && player.getTeam() == TeamType.NONE && !player.isInStoreMode()) {

              for (Skill reApplySkill : reApplySkills) {
                reApplySkill.getEffects(player, player, false, false);
              }

            }
          }
        }, (long)this._reApplyDelay * 1000L);
      }

      return;
    }
  }

  private double calcSkillChanceLimits(double prelimChance, boolean isPlayable) {
    if (this._dispelType.equals("bane")) {
      if (prelimChance < 40.0D) {
        return 40.0D;
      }

      if (prelimChance > 90.0D) {
        return 90.0D;
      }
    } else {
      if (this._dispelType.equals("cancellation")) {
        return Math.max(Config.SKILLS_DISPEL_MOD_MIN, Math.min(Config.SKILLS_DISPEL_MOD_MAX, prelimChance));
      }

      if (this._dispelType.equals("cleanse")) {
        return this._cancelRate;
      }
    }

    return prelimChance;
  }

  protected boolean onActionTime() {
    return false;
  }
}
