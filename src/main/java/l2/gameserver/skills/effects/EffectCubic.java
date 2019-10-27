//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.CubicHolder;
import l2.gameserver.model.*;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.stats.Env;
import l2.gameserver.templates.CubicTemplate;
import l2.gameserver.templates.CubicTemplate.SkillInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

public class EffectCubic extends Effect {
  private final CubicTemplate _template = CubicHolder.getInstance().getTemplate(this.getTemplate().getParam().getInteger("cubicId"), this.getTemplate().getParam().getInteger("cubicLevel"));
  private Future<?> _task = null;
  private long _reuse = 0L;

  public EffectCubic(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    Player player = this._effected.getPlayer();
    if (player != null) {
      player.addCubic(this);
      this._task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new EffectCubic.ActionTask(), 1000L, 1000L);
    }
  }

  public void onExit() {
    super.onExit();
    Player player = this._effected.getPlayer();
    if (player != null) {
      player.removeCubic(this.getId());
      this._task.cancel(true);
      this._task = null;
    }
  }

  public void doAction(Player player) {
    if (this._reuse <= System.currentTimeMillis()) {
      boolean result = false;
      int chance = Rnd.get(1000);
      Iterator var4 = this._template.getSkills().iterator();

      label39:
      while(var4.hasNext()) {
        Entry<Integer, List<SkillInfo>> entry = (Entry)var4.next();
        if ((chance -= entry.getKey()) < 0) {
          Iterator var6 = ((List)entry.getValue()).iterator();

          while(true) {
            if (!var6.hasNext()) {
              break label39;
            }

            SkillInfo skillInfo = (SkillInfo)var6.next();
            switch(skillInfo.getActionType()) {
              case ATTACK:
                result = doAttack(player, skillInfo);
                break;
              case DEBUFF:
                result = doDebuff(player, skillInfo);
                break;
              case HEAL:
                result = doHeal(player, skillInfo);
                break;
              case CANCEL:
                result = doCancel(player, skillInfo);
            }
          }
        }
      }

      if (result) {
        this._reuse = System.currentTimeMillis() + (long)this._template.getDelay() * 1000L;
      }

    }
  }

  protected boolean onActionTime() {
    return false;
  }

  public boolean isHidden() {
    return true;
  }

  public boolean isCancelable() {
    return false;
  }

  public int getId() {
    return this._template.getId();
  }

  private static boolean doHeal(final Player player, SkillInfo info) {
    final Skill skill = info.getSkill();
     Creature creature = null;
    if (player.getParty() == null) {
      if (!player.isCurrentHpFull() && !player.isDead()) {
        creature = player;
      }
    } else {
      double currentHp = 2.147483647E9D;

      for (Player member : player.getParty().getPartyMembers()) {
        if (player.isInRange(member, info.getSkill().getCastRange()) && !member.isCurrentHpFull() && !member.isDead() && member.getCurrentHp() < currentHp) {
          currentHp = member.getCurrentHp();
          creature = member;
        }
      }
    }

    if (creature == null) {
      return false;
    } else {
      int chance = info.getChance((int)creature.getCurrentHpPercents());
      if (!Rnd.chance(chance)) {
        return false;
      } else {
        player.broadcastPacket(new MagicSkillUse(player, creature, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        Creature finalCreature = creature;
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            List<Creature> targets = new ArrayList<>(1);
            targets.add(finalCreature);
            player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
          }
        }, skill.getHitTime());
        return true;
      }
    }
  }

  private static boolean doAttack(final Player player, SkillInfo info) {
    if (!Rnd.chance(info.getChance())) {
      return false;
    } else {
      final Creature target = getTarget(player, info);
      if (target == null) {
        return false;
      } else {
        final Skill skill = info.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            List<Creature> targets = new ArrayList<>(1);
            targets.add(target);
            player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
            if (target.isNpc()) {
              if (target.paralizeOnAttack(player)) {
                if (Config.PARALIZE_ON_RAID_DIFF) {
                  player.paralizeMe(target);
                }
              } else {
                int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int)skill.getPower();
                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
              }
            }

          }
        }, skill.getHitTime());
        return true;
      }
    }
  }

  private static boolean doDebuff(final Player player, SkillInfo info) {
    if (!Rnd.chance(info.getChance())) {
      return false;
    } else {
      final Creature target = getTarget(player, info);
      if (target == null) {
        return false;
      } else {
        final Skill skill = info.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            List<Creature> targets = new ArrayList<>(1);
            targets.add(target);
            player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
          }
        }, skill.getHitTime());
        return true;
      }
    }
  }

  private static boolean doCancel(final Player player, SkillInfo info) {
    if (!Rnd.chance(info.getChance())) {
      return false;
    } else {
      boolean hasDebuff = false;

      for (Effect e : player.getEffectList().getAllEffects()) {
        if (e.isOffensive() && e.isCancelable() && !e.getTemplate()._applyOnCaster) {
          hasDebuff = true;
          break;
        }
      }

      if (!hasDebuff) {
        return false;
      } else {
        final Skill skill = info.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            List<Creature> targets = new ArrayList(1);
            targets.add(player);
            player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
          }
        }, skill.getHitTime());
        return true;
      }
    }
  }

  private static Creature getTarget(Player owner, SkillInfo info) {
    if (!owner.isInCombat()) {
      return null;
    } else {
      GameObject object = owner.getTarget();
      if (object != null && object.isCreature()) {
        Creature target = (Creature)object;
        if (target.isDead()) {
          return null;
        } else if (target.getCurrentHp() < (double)info.getMinHp() && target.getCurrentHpPercents() < (double)info.getMinHpPercent()) {
          return null;
        } else if (target.isDoor() && !info.isCanAttackDoor()) {
          return null;
        } else if (!owner.isInRangeZ(target, info.getSkill().getCastRange())) {
          return null;
        } else {
          Player targetPlayer = target.getPlayer();
          if (targetPlayer != null && !targetPlayer.isInCombat()) {
            return null;
          } else {
            return !target.isAutoAttackable(owner) ? null : target;
          }
        }
      } else {
        return null;
      }
    }
  }

  private class ActionTask extends RunnableImpl {
    private ActionTask() {
    }

    public void runImpl() throws Exception {
      if (EffectCubic.this.isActive()) {
        Player player = EffectCubic.this._effected != null && EffectCubic.this._effected.isPlayer() ? (Player)EffectCubic.this._effected : null;
        if (player != null) {
          EffectCubic.this.doAction(player);
        }
      }
    }
  }
}
