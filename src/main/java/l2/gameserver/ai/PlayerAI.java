//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;

public class PlayerAI extends PlayableAI {
  public PlayerAI(Player actor) {
    super(actor);
  }

  protected void onIntentionRest() {
    this.changeIntention(CtrlIntention.AI_INTENTION_REST, (Object)null, (Object)null);
    this.setAttackTarget((Creature)null);
    this.clientStopMoving();
  }

  protected void onIntentionActive() {
    this.clearNextAction();
    this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, (Object)null, (Object)null);
  }

  public void onIntentionInteract(GameObject object) {
    Player actor = this.getActor();
    if (actor.getSittingTask()) {
      this.setNextAction(NextAction.INTERACT, object, (Object)null, false, false);
    } else if (actor.isSitting()) {
      actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
      this.clientActionFailed();
    } else {
      super.onIntentionInteract(object);
    }
  }

  public void onIntentionPickUp(GameObject object) {
    Player actor = this.getActor();
    if (actor.getSittingTask()) {
      this.setNextAction(NextAction.PICKUP, object, (Object)null, false, false);
    } else if (actor.isSitting()) {
      actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
      this.clientActionFailed();
    } else {
      super.onIntentionPickUp(object);
    }
  }

  protected void thinkAttack(boolean checkRange) {
    Player actor = this.getActor();
    if (actor.isInFlyingTransform()) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    } else {
      FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
      if (attachment != null && !attachment.canAttack(actor)) {
        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        actor.sendActionFailed();
      } else if (actor.isFrozen()) {
        this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        actor.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC});
      } else {
        super.thinkAttack(checkRange);
      }
    }
  }

  protected void thinkCast(boolean checkRange) {
    Player actor = this.getActor();
    FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
    if (attachment != null && !attachment.canCast(actor, this._skill)) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      actor.sendActionFailed();
    } else if (actor.isFrozen()) {
      this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      actor.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC});
    } else {
      super.thinkCast(checkRange);
    }
  }

  public void Attack(GameObject target, boolean forceUse, boolean dontMove) {
    Player actor = this.getActor();
    if (actor.isInFlyingTransform()) {
      actor.sendActionFailed();
    } else if (System.currentTimeMillis() - actor.getLastAttackPacket() < (long)Config.ATTACK_PACKET_DELAY) {
      actor.sendActionFailed();
    } else {
      actor.setLastAttackPacket();
      if (actor.getSittingTask()) {
        this.setNextAction(NextAction.ATTACK, target, (Object)null, forceUse, false);
      } else if (actor.isSitting()) {
        actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
        this.clientActionFailed();
      } else {
        super.Attack(target, forceUse, dontMove);
      }
    }
  }

  public void Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
    Player actor = this.getActor();
    if (!skill.altUse() && !skill.isToggle() && (skill.getSkillType() != SkillType.CRAFT || !Config.ALLOW_TALK_WHILE_SITTING)) {
      if (actor.getSittingTask()) {
        this.setNextAction(NextAction.CAST, skill, target, forceUse, dontMove);
        this.clientActionFailed();
        return;
      }

      if (skill.getSkillType() == SkillType.SUMMON && actor.getPrivateStoreType() != 0) {
        actor.sendPacket(Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS);
        this.clientActionFailed();
        return;
      }

      if (actor.isSitting()) {
        if (skill.getSkillType() == SkillType.TRANSFORMATION) {
          actor.sendPacket(Msg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
        } else {
          actor.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
        }

        this.clientActionFailed();
        return;
      }
    }

    super.Cast(skill, target, forceUse, dontMove);
  }

  public Player getActor() {
    return (Player)super.getActor();
  }
}
