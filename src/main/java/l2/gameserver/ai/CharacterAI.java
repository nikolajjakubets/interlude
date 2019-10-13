//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class CharacterAI extends AbstractAI {
  public CharacterAI(Creature actor) {
    super(actor);
  }

  protected void onIntentionIdle() {
    this.clientStopMoving();
    this.changeIntention(CtrlIntention.AI_INTENTION_IDLE, (Object)null, (Object)null);
  }

  protected void onIntentionActive() {
    this.clientStopMoving();
    this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, (Object)null, (Object)null);
    this.onEvtThink();
  }

  protected void onIntentionAttack(Creature target) {
    this.setAttackTarget(target);
    this.clientStopMoving();
    this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, (Object)null);
    this.onEvtThink();
  }

  protected void onIntentionCast(Skill skill, Creature target) {
    this.setAttackTarget(target);
    this.changeIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
    this.onEvtThink();
  }

  protected void onIntentionFollow(Creature target) {
    this.changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, target, (Object)null);
    this.onEvtThink();
  }

  protected static int getIndentRange(int range) {
    return range < 300 ? range / 3 * 2 : range - 100;
  }

  protected void onIntentionInteract(GameObject object) {
  }

  protected void onIntentionPickUp(GameObject item) {
  }

  protected void onIntentionRest() {
  }

  protected void onEvtArrivedBlocked(Location blocked_at_pos) {
    Creature actor = this.getActor();
    if (actor.isPlayer()) {
      Location loc = ((Player)actor).getLastServerPosition();
      if (loc != null) {
        actor.setLoc(loc, true);
      }

      if (actor.isMoving()) {
        actor.stopMove();
      }
    }

    this.onEvtThink();
  }

  protected void onEvtForgetObject(GameObject object) {
    if (object != null) {
      Creature actor = this.getActor();
      if (actor.isAttackingNow() && this.getAttackTarget() == object) {
        actor.abortAttack(true, true);
      }

      if (actor.isCastingNow() && this.getAttackTarget() == object) {
        actor.abortCast(true, true);
      }

      if (this.getAttackTarget() == object) {
        this.setAttackTarget((Creature)null);
      }

      if (actor.getTargetId() == object.getObjectId()) {
        actor.setTarget((GameObject)null);
      }

      if (actor.getFollowTarget() == object) {
        actor.stopMove();
      }

      if (actor.getPet() != null) {
        actor.getPet().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
      }

    }
  }

  protected void onEvtDead(Creature killer) {
    Creature actor = this.getActor();
    actor.abortAttack(true, true);
    actor.abortCast(true, true);
    actor.stopMove();
    actor.broadcastPacket(new L2GameServerPacket[]{new Die(actor)});
    this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
  }

  protected void onEvtFakeDeath() {
    this.clientStopMoving();
    this.setIntention(CtrlIntention.AI_INTENTION_IDLE);
  }

  protected void onEvtAttacked(Creature attacker, int damage) {
  }

  protected void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
  }

  public void Attack(GameObject target, boolean forceUse, boolean dontMove) {
    this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
  }

  public void Cast(Skill skill, Creature target) {
    this.Cast(skill, target, false, false);
  }

  public void Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
    this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
  }

  protected void onEvtThink() {
  }

  protected void onEvtAggression(Creature target, int aggro) {
  }

  protected void onEvtFinishCasting(Skill skill, Creature target) {
  }

  protected void onEvtReadyToAct() {
  }

  protected void onEvtArrived() {
  }

  protected void onEvtArrivedTarget() {
  }

  protected void onEvtSeeSpell(Skill skill, Creature caster) {
  }

  protected void onEvtSpawn() {
  }

  public void onEvtDeSpawn() {
  }

  public void stopAITask() {
  }

  public void startAITask() {
  }

  public void setNextAction(NextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3) {
  }

  public void clearNextAction() {
  }

  public boolean isActive() {
    return true;
  }

  protected void onEvtTimer(int timerId, Object arg1, Object arg2) {
  }

  public void addTimer(int timerId, long delay) {
    this.addTimer(timerId, (Object)null, (Object)null, delay);
  }

  public void addTimer(int timerId, Object arg1, long delay) {
    this.addTimer(timerId, arg1, (Object)null, delay);
  }

  public void addTimer(int timerId, Object arg1, Object arg2, long delay) {
    ThreadPoolManager.getInstance().schedule(new CharacterAI.Timer(timerId, arg1, arg2), delay);
  }

  protected class Timer extends RunnableImpl {
    private int _timerId;
    private Object _arg1;
    private Object _arg2;

    public Timer(int timerId, Object arg1, Object arg2) {
      this._timerId = timerId;
      this._arg1 = arg1;
      this._arg2 = arg2;
    }

    public void runImpl() {
      CharacterAI.this.notifyEvent(CtrlEvent.EVT_TIMER, this._timerId, this._arg1, this._arg2);
    }
  }
}
