//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Skill;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAI extends RunnableImpl {
  protected final Creature _actor;
  private HardReference<? extends Creature> _attackTarget = HardReferences.emptyRef();
  private CtrlIntention _intention;

  protected AbstractAI(Creature actor) {
    this._intention = CtrlIntention.AI_INTENTION_IDLE;
    this._actor = actor;
  }

  public void runImpl() throws Exception {
  }

  public void changeIntention(CtrlIntention intention, Object arg0, Object arg1) {
    this._intention = intention;
    if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK) {
      this.setAttackTarget(null);
    }

  }

  public final void setIntention(CtrlIntention intention) {
    this.setIntention(intention, null, null);
  }

  public final void setIntention(CtrlIntention intention, Object arg0) {
    this.setIntention(intention, arg0, null);
  }

  public void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
    if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK) {
      this.setAttackTarget(null);
    }

    Creature actor = this.getActor();
    if (!actor.isVisible()) {
      if (this._intention == CtrlIntention.AI_INTENTION_IDLE) {
        return;
      }

      intention = CtrlIntention.AI_INTENTION_IDLE;
    }

    actor.getListeners().onAiIntention(intention, arg0, arg1);
    switch (intention) {
      case AI_INTENTION_IDLE:
        this.onIntentionIdle();
        break;
      case AI_INTENTION_ACTIVE:
        this.onIntentionActive();
        break;
      case AI_INTENTION_REST:
        this.onIntentionRest();
        break;
      case AI_INTENTION_ATTACK:
        this.onIntentionAttack((Creature) arg0);
        break;
      case AI_INTENTION_CAST:
        this.onIntentionCast((Skill) arg0, (Creature) arg1);
        break;
      case AI_INTENTION_PICK_UP:
        this.onIntentionPickUp((GameObject) arg0);
        break;
      case AI_INTENTION_INTERACT:
        this.onIntentionInteract((GameObject) arg0);
        break;
      case AI_INTENTION_FOLLOW:
        this.onIntentionFollow((Creature) arg0);
    }

  }

  public final void notifyEvent(CtrlEvent evt) {
    this.notifyEvent(evt, new Object[0]);
  }

  public final void notifyEvent(CtrlEvent evt, Object arg0) {
    this.notifyEvent(evt, new Object[]{arg0});
  }

  public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1) {
    this.notifyEvent(evt, new Object[]{arg0, arg1});
  }

  public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1, Object arg2) {
    this.notifyEvent(evt, new Object[]{arg0, arg1, arg2});
  }

  public void notifyEvent(CtrlEvent evt, Object[] args) {
    Creature actor = this.getActor();
    if (actor != null && actor.isVisible()) {
      actor.getListeners().onAiEvent(evt, args);
      switch (evt) {
        case EVT_THINK:
          this.onEvtThink();
          break;
        case EVT_ATTACKED:
          this.onEvtAttacked((Creature) args[0], ((Number) args[1]).intValue());
          break;
        case EVT_CLAN_ATTACKED:
          this.onEvtClanAttacked((Creature) args[0], (Creature) args[1], ((Number) args[2]).intValue());
          break;
        case EVT_AGGRESSION:
          this.onEvtAggression((Creature) args[0], ((Number) args[1]).intValue());
          break;
        case EVT_READY_TO_ACT:
          this.onEvtReadyToAct();
          break;
        case EVT_ARRIVED:
          this.onEvtArrived();
          break;
        case EVT_ARRIVED_TARGET:
          this.onEvtArrivedTarget();
          break;
        case EVT_ARRIVED_BLOCKED:
          this.onEvtArrivedBlocked((Location) args[0]);
          break;
        case EVT_FORGET_OBJECT:
          this.onEvtForgetObject((GameObject) args[0]);
          break;
        case EVT_DEAD:
          this.onEvtDead((Creature) args[0]);
          break;
        case EVT_FAKE_DEATH:
          this.onEvtFakeDeath();
          break;
        case EVT_FINISH_CASTING:
          this.onEvtFinishCasting((Skill) args[0], (Creature) args[1]);
          break;
        case EVT_SEE_SPELL:
          this.onEvtSeeSpell((Skill) args[0], (Creature) args[1]);
          break;
        case EVT_SPAWN:
          this.onEvtSpawn();
          break;
        case EVT_DESPAWN:
          this.onEvtDeSpawn();
          break;
        case EVT_TIMER:
          this.onEvtTimer(((Number) args[0]).intValue(), args[1], args[2]);
      }

    }
  }

  protected void clientActionFailed() {
    Creature actor = this.getActor();
    if (actor != null && actor.isPlayer()) {
      actor.sendActionFailed();
    }

  }

  public void clientStopMoving(boolean validate) {
    Creature actor = this.getActor();
    actor.stopMove(validate);
  }

  public void clientStopMoving() {
    Creature actor = this.getActor();
    actor.stopMove();
  }

  public Creature getActor() {
    return this._actor;
  }

  public CtrlIntention getIntention() {
    return this._intention;
  }

  public void setAttackTarget(Creature target) {
    this._attackTarget = target == null ? HardReferences.emptyRef() : target.getRef();
  }

  public Creature getAttackTarget() {
    return this._attackTarget.get();
  }

  public boolean isGlobalAI() {
    return false;
  }

  public String toString() {
    return this.getClass().getSimpleName() + " for " + this.getActor();
  }

  protected abstract void onIntentionIdle();

  protected abstract void onIntentionActive();

  protected abstract void onIntentionRest();

  protected abstract void onIntentionAttack(Creature var1);

  protected abstract void onIntentionCast(Skill var1, Creature var2);

  protected abstract void onIntentionPickUp(GameObject var1);

  protected abstract void onIntentionInteract(GameObject var1);

  protected abstract void onEvtThink();

  protected abstract void onEvtAttacked(Creature var1, int var2);

  protected abstract void onEvtClanAttacked(Creature var1, Creature var2, int var3);

  protected abstract void onEvtAggression(Creature var1, int var2);

  protected abstract void onEvtReadyToAct();

  protected abstract void onEvtArrived();

  protected abstract void onEvtArrivedTarget();

  protected abstract void onEvtArrivedBlocked(Location var1);

  protected abstract void onEvtForgetObject(GameObject var1);

  protected abstract void onEvtDead(Creature var1);

  protected abstract void onEvtFakeDeath();

  protected abstract void onEvtFinishCasting(Skill var1, Creature var2);

  protected abstract void onEvtSeeSpell(Skill var1, Creature var2);

  protected abstract void onEvtSpawn();

  public abstract void onEvtDeSpawn();

  protected abstract void onIntentionFollow(Creature var1);

  protected abstract void onEvtTimer(int var1, Object var2, Object var3);
}
