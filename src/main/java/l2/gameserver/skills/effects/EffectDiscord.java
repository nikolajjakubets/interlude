//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Env;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EffectDiscord extends Effect {
  public EffectDiscord(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    int skilldiff = this._effected.getLevel() - this._skill.getMagicLevel();
    int lvldiff = this._effected.getLevel() - this._effector.getLevel();
    if (skilldiff <= 10 && (skilldiff <= 5 || !Rnd.chance(30)) && !Rnd.chance(Math.abs(lvldiff) * 2)) {
      boolean multitargets = this._skill.isAoE();
      if (!this._effected.isMonster()) {
        if (!multitargets) {
          this.getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
        }

        return false;
      } else if (!this._effected.isFearImmune() && !this._effected.isRaid()) {
        Player player = this._effected.getPlayer();
        if (player != null) {
          SiegeEvent<?, ?> siegeEvent = (SiegeEvent)player.getEvent(SiegeEvent.class);
          if (this._effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance)this._effected)) {
            if (!multitargets) {
              this.getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            }

            return false;
          }
        }

        if (this._effected.isInZonePeace()) {
          if (!multitargets) {
            this.getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
          }

          return false;
        } else {
          return super.checkCondition();
        }
      } else {
        if (!multitargets) {
          this.getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
        }

        return false;
      }
    } else {
      return false;
    }
  }

  public void onStart() {
    super.onStart();
    this._effected.startConfused();
    this.onActionTime();
  }

  public void onExit() {
    super.onExit();
    if (!this._effected.stopConfused()) {
      this._effected.abortAttack(true, true);
      this._effected.abortCast(true, true);
      this._effected.stopMove();
      this._effected.getAI().setAttackTarget((Creature)null);
      this._effected.setWalking();
      this._effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

  }

  public boolean onActionTime() {
    List<Creature> targetList = new ArrayList<>();
    Iterator var2 = this._effected.getAroundCharacters(900, 200).iterator();

    while(var2.hasNext()) {
      Creature character = (Creature)var2.next();
      if (character.isNpc() && character != this.getEffected()) {
        targetList.add(character);
      }
    }

    if (targetList.isEmpty()) {
      return true;
    } else {
      Creature target = (Creature)targetList.get(Rnd.get(targetList.size()));
      this._effected.setRunning();
      this._effected.getAI().Attack(target, true, false);
      return false;
    }
  }
}
