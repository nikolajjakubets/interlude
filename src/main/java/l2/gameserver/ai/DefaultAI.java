//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.commons.collections.LazyArrayList;
import l2.commons.lang.reference.HardReference;
import l2.commons.math.random.RndSelector;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.*;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.stats.Stats;
import l2.gameserver.taskmanager.AiTaskManager;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;

public class DefaultAI extends CharacterAI {
  protected static final Logger _log = LoggerFactory.getLogger(DefaultAI.class);
  public static final int TaskDefaultWeight = 10000;
  protected long AI_TASK_ATTACK_DELAY;
  protected long AI_TASK_ACTIVE_DELAY;
  protected long AI_TASK_DELAY_CURRENT;
  protected int MAX_PURSUE_RANGE;
  protected ScheduledFuture<?> _aiTask;
  protected ScheduledFuture<?> _runningTask;
  protected ScheduledFuture<?> _madnessTask;
  private boolean _thinking;
  protected boolean _def_think;
  protected long _globalAggro;
  protected long _randomAnimationEnd;
  protected int _pathfindFails;
  protected final NavigableSet<DefaultAI.Task> _tasks;
  protected final Skill[] _damSkills;
  protected final Skill[] _dotSkills;
  protected final Skill[] _debuffSkills;
  protected final Skill[] _healSkills;
  protected final Skill[] _buffSkills;
  protected final Skill[] _stunSkills;
  protected long _lastActiveCheck;
  protected long _checkAggroTimestamp;
  protected long _attackTimeout;
  protected long _lastFactionNotifyTime;
  protected long _minFactionNotifyInterval;
  protected final Comparator<Creature> _nearestTargetComparator;

  public void addTaskCast(Creature target, Skill skill) {
    DefaultAI.Task task = new DefaultAI.Task();
    task.type = DefaultAI.TaskType.CAST;
    task.target = target.getRef();
    task.skill = skill;
    this._tasks.add(task);
    this._def_think = true;
  }

  public void addTaskBuff(Creature target, Skill skill) {
    DefaultAI.Task task = new DefaultAI.Task();
    task.type = DefaultAI.TaskType.BUFF;
    task.target = target.getRef();
    task.skill = skill;
    this._tasks.add(task);
    this._def_think = true;
  }

  public void addTaskAttack(Creature target) {
    DefaultAI.Task task = new DefaultAI.Task();
    task.type = DefaultAI.TaskType.ATTACK;
    task.target = target.getRef();
    this._tasks.add(task);
    this._def_think = true;
  }

  public void addTaskAttack(Creature target, Skill skill, int weight) {
    DefaultAI.Task task = new DefaultAI.Task();
    task.type = skill.isOffensive() ? DefaultAI.TaskType.CAST : DefaultAI.TaskType.BUFF;
    task.target = target.getRef();
    task.skill = skill;
    task.weight = weight;
    this._tasks.add(task);
    this._def_think = true;
  }

  public void addTaskMove(Location loc, boolean pathfind) {
    DefaultAI.Task task = new DefaultAI.Task();
    task.type = DefaultAI.TaskType.MOVE;
    task.loc = loc;
    task.pathfind = pathfind;
    this._tasks.add(task);
    this._def_think = true;
  }

  protected void addTaskMove(int locX, int locY, int locZ, boolean pathfind) {
    this.addTaskMove(new Location(locX, locY, locZ), pathfind);
  }

  public DefaultAI(NpcInstance actor) {
    super(actor);
    this.AI_TASK_ATTACK_DELAY = Config.AI_TASK_ATTACK_DELAY;
    this.AI_TASK_ACTIVE_DELAY = Config.AI_TASK_ACTIVE_DELAY;
    this.AI_TASK_DELAY_CURRENT = this.AI_TASK_ACTIVE_DELAY;
    this._thinking = false;
    this._def_think = false;
    this._tasks = new ConcurrentSkipListSet(DefaultAI.TaskComparator.getInstance());
    this._checkAggroTimestamp = 0L;
    this._lastFactionNotifyTime = 0L;
    this._minFactionNotifyInterval = 3000L;
    this.setAttackTimeout(9223372036854775807L);
    NpcInstance npc = this.getActor();
    this._damSkills = npc.getTemplate().getDamageSkills();
    this._dotSkills = npc.getTemplate().getDotSkills();
    this._debuffSkills = npc.getTemplate().getDebuffSkills();
    this._buffSkills = npc.getTemplate().getBuffSkills();
    this._stunSkills = npc.getTemplate().getStunSkills();
    this._healSkills = npc.getTemplate().getHealSkills();
    this._nearestTargetComparator = new DefaultAI.NearestTargetComparator(actor);
    this.MAX_PURSUE_RANGE = actor.getParameter("MaxPursueRange", actor.isRaid() ? Config.MAX_PURSUE_RANGE_RAID : (npc.isUnderground() ? Config.MAX_PURSUE_UNDERGROUND_RANGE : Config.MAX_PURSUE_RANGE));
    this._minFactionNotifyInterval = actor.getParameter("FactionNotifyInterval", 3000);
  }

  public void runImpl() throws Exception {
    if (this._aiTask != null) {
      if (!this.isGlobalAI() && System.currentTimeMillis() - this._lastActiveCheck > 60000L) {
        this._lastActiveCheck = System.currentTimeMillis();
        NpcInstance actor = this.getActor();
        WorldRegion region = actor == null ? null : actor.getCurrentRegion();
        if (region == null || !region.isActive()) {
          this.stopAITask();
          return;
        }
      }

      this.onEvtThink();
    }
  }

  public synchronized void startAITask() {
    if (this._aiTask == null) {
      this.AI_TASK_DELAY_CURRENT = this.AI_TASK_ACTIVE_DELAY;
      this._aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, this.AI_TASK_DELAY_CURRENT);
    }

  }

  protected synchronized void switchAITask(long NEW_DELAY) {
    if (this._aiTask != null) {
      if (this.AI_TASK_DELAY_CURRENT != NEW_DELAY) {
        this._aiTask.cancel(false);
        this.AI_TASK_DELAY_CURRENT = NEW_DELAY;
        this._aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, this.AI_TASK_DELAY_CURRENT);
      }

    }
  }

  public final synchronized void stopAITask() {
    if (this._aiTask != null) {
      this._aiTask.cancel(false);
      this._aiTask = null;
    }

  }

  protected boolean canSeeInSilentMove(Playable target) {
    if (this.getActor().getParameter("canSeeInSilentMove", false)) {
      return true;
    } else {
      return !target.isSilentMoving();
    }
  }

  protected boolean canSeeInHide(Playable target) {
    if (this.getActor().getParameter("canSeeInHide", false)) {
      return true;
    } else {
      return !target.isInvisible();
    }
  }

  protected boolean checkAggression(Creature target) {
    NpcInstance actor = getActor();
    if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro()) {
      return false;
    }
    if (target.isAlikeDead()) {
      return false;
    }

    if (target.isNpc() && target.isInvul()) {
      return false;
    }

    if (target.isPlayable()) {
      if (!canSeeInSilentMove((Playable) target)) {
        return false;
      }
      if (!canSeeInHide((Playable) target)) {
        return false;
      }
      if (actor.getFaction().getName().equalsIgnoreCase("varka_silenos_clan") && target.getPlayer().getVarka() > 0) {
        return false;
      }
      if (actor.getFaction().getName().equalsIgnoreCase("ketra_orc_clan") && target.getPlayer().getKetra() > 0) {
        return false;
      }
      /*
       * if(target.isFollow && !target.isPlayer() && target.getFollowTarget() != null && target.getFollowTarget().isPlayer()) return;
       */
      if (target.isPlayer() && ((Player) target).isGM() && target.isInvisible()) {
        return true;
      }
      if (((Playable) target).getNonAggroTime() > System.currentTimeMillis()) {
        return false;
      }
      if (target.isPlayer() && !target.getPlayer().isActive()) {
        return false;
      }
      if (actor.isMonster() && target.isInZonePeace()) {
        return false;
      }
    }

    AggroInfo ai = actor.getAggroList().get(target);
    if (ai != null && ai.hate > 0) {
      if (!target.isInRangeZ(actor.getSpawnedLoc(), MAX_PURSUE_RANGE)) {
        return false;
      }
    } else if (!actor.isAggressive() || !target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange())) {
      return false;
    }

    if (!canAttackCharacter(target)) {
      return false;
    }
    if (!GeoEngine.canSeeTarget(actor, target, false)) {
      return false;
    }

    actor.getAggroList().addDamageHate(target, 0, 2);

    if ((target.isSummon() || target.isPet())) {
      actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
    }

    startRunningTask(AI_TASK_ATTACK_DELAY);
    setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

    return true;
  }

  protected Location getPursueBaseLoc() {
    NpcInstance actor = this.getActor();
    Location spawnLoc = actor.getSpawnedLoc();
    return spawnLoc != null ? spawnLoc : actor.getLoc();
  }

  protected boolean isInAggroRange(Creature target) {
    NpcInstance actor = this.getActor();
    AggroInfo ai = actor.getAggroList().get(target);
    if (ai != null && ai.hate > 0) {
      return target.isInRangeZ(this.getPursueBaseLoc(), this.MAX_PURSUE_RANGE);
    } else return this.isAggressive() && target.isInRangeZ(this.getPursueBaseLoc(), actor.getAggroRange());

  }

  protected void setIsInRandomAnimation(long time) {
    this._randomAnimationEnd = System.currentTimeMillis() + time;
  }

  protected boolean randomAnimation() {
    NpcInstance actor = this.getActor();
    if (actor.getParameter("noRandomAnimation", false)) {
      return false;
    } else if (actor.hasRandomAnimation() && !actor.isActionsDisabled() && !actor.isMoving() && !actor.isInCombat() && Rnd.chance(Config.RND_ANIMATION_RATE)) {
      this.setIsInRandomAnimation(3000L);
      actor.onRandomAnimation();
      return true;
    } else {
      return false;
    }
  }

  protected Creature getNearestTarget(List<Creature> targets) {
    NpcInstance actor = this.getActor();
    Creature nextTarget = null;
    long minDist = 9223372036854775807L;

    for (Creature target : targets) {
      long dist = actor.getXYZDeltaSq(target.getX(), target.getY(), target.getZ());
      if (dist < minDist) {
        nextTarget = target;
      }
    }

    return nextTarget;
  }

  protected boolean randomWalk() {
    NpcInstance actor = this.getActor();
    if (actor.getParameter("noRandomWalk", false)) {
      return false;
    } else {
      return !actor.isMoving() && this.maybeMoveToHome();
    }
  }

  /**
   * @return true если действие выполнено, false если нет
   */
  protected boolean thinkActive() {
    NpcInstance actor = getActor();
    if (actor.isActionsDisabled()) {
      return true;
    }

    if (_randomAnimationEnd > System.currentTimeMillis()) {
      return true;
    }

    if (_def_think) {
      if (doTask()) {
        clearTasks();
      }
      return true;
    }

    long now = System.currentTimeMillis();
    if (now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL) {
      _checkAggroTimestamp = now;

      boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", actor.isAggressive() ? 100 : 0));
      if (!actor.getAggroList().isEmpty() || aggressive) {
        List<Creature> chars = World.getAroundCharacters(actor);
        chars.sort(_nearestTargetComparator);
        for (Creature cha : chars) {
          if (aggressive || actor.getAggroList().get(cha) != null) {
            if (checkAggression(cha)) {
              return true;
            }
          }
        }
      }
    }

    if (actor.isMinion()) {
      MonsterInstance leader = ((MinionInstance) actor).getLeader();
      if (leader != null) {
        double distance = actor.getDistance(leader.getX(), leader.getY());
        if (distance > 1000) {
          actor.teleToLocation(leader.getMinionPosition());
        } else if (distance > 200) {
          addTaskMove(leader.getMinionPosition(), false);
        }
        return true;
      }
    }

    if (randomAnimation()) {
      return true;
    }

    return randomWalk();
  }

  protected void onIntentionIdle() {
    NpcInstance actor = this.getActor();
    this.clearTasks();
    actor.stopMove();
    actor.getAggroList().clear(true);
    this.setAttackTimeout(9223372036854775807L);
    this.setAttackTarget(null);
    this.changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
  }

  protected void onIntentionActive() {
    NpcInstance actor = this.getActor();
    actor.stopMove();
    this.setAttackTimeout(9223372036854775807L);
    if (this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
      this.switchAITask(this.AI_TASK_ACTIVE_DELAY);
      this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
    }

    this.onEvtThink();
  }

  protected void onIntentionAttack(Creature target) {
    NpcInstance actor = this.getActor();
    this.clearTasks();
    actor.stopMove();
    this.setAttackTarget(target);
    this.setAttackTimeout((long) this.getMaxAttackTimeout() + System.currentTimeMillis());
    this.setGlobalAggro(0L);
    if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
      this.changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
      this.switchAITask(this.AI_TASK_ATTACK_DELAY);
    }

    this.onEvtThink();
  }

  protected boolean canAttackCharacter(Creature target) {
    return target.isPlayable();
  }

  protected boolean isAggressive() {
    return this.getActor().isAggressive();
  }

  protected boolean checkTarget(Creature target, int range) {
    NpcInstance actor = this.getActor();
    if (target != null && !target.isAlikeDead() && actor.isInRangeZ(target, range)) {
      boolean hided = target.isPlayable() && !this.canSeeInHide((Playable) target);
      if (!hided && actor.isConfused()) {
        return true;
      } else if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
        AggroInfo ai = actor.getAggroList().get(target);
        if (ai != null) {
          if (hided) {
            ai.hate = 0;
            return false;
          } else {
            return ai.hate > 0;
          }
        } else {
          return false;
        }
      } else {
        return this.canAttackCharacter(target);
      }
    } else {
      return false;
    }
  }

  public void setAttackTimeout(long time) {
    this._attackTimeout = time;
  }

  protected long getAttackTimeout() {
    return this._attackTimeout;
  }

  protected void thinkAttack() {
    NpcInstance actor = this.getActor();
    if (!actor.isDead()) {
      Location loc = this.getPursueBaseLoc();
      if (!actor.isInRange(loc, this.MAX_PURSUE_RANGE)) {
        this.teleportHome();
      } else {
        if (this.doTask() && !actor.isAttackingNow() && !actor.isCastingNow() && !this.createNewTask() && System.currentTimeMillis() > this.getAttackTimeout()) {
          this.returnHome();
        }

      }
    }
  }

  protected void onEvtSpawn() {
    this.setGlobalAggro(System.currentTimeMillis() + this.getActor().getParameter("globalAggro", 10000L));
    this.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
  }

  protected void onEvtReadyToAct() {
    this.onEvtThink();
  }

  protected void onEvtArrivedTarget() {
    this.onEvtThink();
  }

  protected void onEvtArrived() {
    this.onEvtThink();
  }

  protected boolean tryMoveToTarget(Creature target) {
    return this.tryMoveToTarget(target, getIndentRange(target.getActingRange()), target.getActingRange());
  }

  protected boolean tryMoveToTarget(Creature target, int indent, int range) {
    NpcInstance actor = this.getActor();
    if (range > 16) {
      if (!actor.moveToRelative(target, indent, range)) {
        ++this._pathfindFails;
      }
    } else if (!actor.moveToLocation(target.getLoc(), indent, true)) {
      ++this._pathfindFails;
    }

    if (this._pathfindFails >= this.getMaxPathfindFails() && System.currentTimeMillis() > this.getAttackTimeout() - (long) this.getMaxAttackTimeout() + (long) this.getTeleportTimeout() && actor.isInRange(target, this.MAX_PURSUE_RANGE)) {
      this._pathfindFails = 0;
      if (target.isPlayable()) {
        AggroInfo hate = actor.getAggroList().get(target);
        if (hate == null || hate.hate < 100 || actor.isRaid() && Math.abs(target.getZ() - actor.getZ()) > 900 || actor.isSevenSignsMonster() || actor.getParameter("PursueToTargetGeoCheck", true) && Math.abs(target.getZ() - actor.getZ()) > 150) {
          return false;
        }
      }

      Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getGeoIndex());
      if (!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex())) {
        loc = target.getLoc();
      }

      actor.teleToLocation(loc);
    }

    return true;
  }

  protected boolean maybeNextTask(DefaultAI.Task currentTask) {
    this._tasks.remove(currentTask);
    return this._tasks.size() == 0;
  }

  protected boolean doTask() {
    NpcInstance actor = this.getActor();
    if (!this._def_think) {
      return true;
    } else {
      DefaultAI.Task currentTask = this._tasks.pollFirst();
      if (currentTask == null) {
        this.clearTasks();
        return true;
      } else if (!actor.isDead() && !actor.isAttackingNow() && !actor.isCastingNow()) {
        Creature target;
        boolean isAoE;
        int castRange;
        int collisions;
        boolean incZ;
        int dist;
        switch (currentTask.type) {
          case MOVE:
            if (!actor.isMovementDisabled() && this.getIsMobile()) {
              if (actor.isInRange(currentTask.loc, 100L)) {
                return this.maybeNextTask(currentTask);
              }

              if (actor.isMoving()) {
                return false;
              }

              if (!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind)) {
                this.clientStopMoving();
                this._pathfindFails = 0;
                actor.teleToLocation(currentTask.loc);
                return this.maybeNextTask(currentTask);
              }
              break;
            }

            return true;
          case ATTACK:
            target = currentTask.target.get();
            if (!this.checkTarget(target, this.MAX_PURSUE_RANGE)) {
              return true;
            } else {
              this.setAttackTarget(target);
              if (actor.isMoving()) {
                return Rnd.chance(25);
              } else {
                int pAtkRng = actor.getPhysicalAttackRange();
                castRange = (int) (actor.getCollisionRadius() + target.getColRadius());
                boolean incZEls = actor.isFlying() || actor.isInWater() || target.isFlying() || target.isInWater();
                int distEls = (int) (!incZEls ? actor.getDistance(target) : actor.getDistance3D(target)) - castRange;
                if (distEls <= pAtkRng + 16 && GeoEngine.canSeeTarget(actor, target, incZEls)) {
                  this.clientStopMoving();
                  this._pathfindFails = 0;
                  this.setAttackTimeout((long) this.getMaxAttackTimeout() + System.currentTimeMillis());
                  actor.doAttack(target);
                  return this.maybeNextTask(currentTask);
                } else if (!actor.isMovementDisabled() && this.getIsMobile()) {
                  return !this.tryMoveToTarget(target, castRange + getIndentRange(pAtkRng), castRange + pAtkRng);
                } else {
                  return true;
                }
              }
            }
          case CAST:
            target = currentTask.target.get();
            if (!actor.isMuted(currentTask.skill) && !actor.isSkillDisabled(currentTask.skill) && !actor.isUnActiveSkill(currentTask.skill.getId())) {
              isAoE = currentTask.skill.getTargetType() == SkillTargetType.TARGET_AURA;
              castRange = currentTask.skill.getAOECastRange();
              if (!this.checkTarget(target, this.MAX_PURSUE_RANGE + castRange)) {
                return true;
              }

              this.setAttackTarget(target);
              collisions = (int) (actor.getCollisionRadius() + target.getColRadius());
              incZ = actor.isFlying() || actor.isInWater() || target.isFlying() || target.isInWater();
              dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
              if (dist <= castRange && GeoEngine.canSeeTarget(actor, target, incZ)) {
                this.clientStopMoving();
                this._pathfindFails = 0;
                this.setAttackTimeout((long) this.getMaxAttackTimeout() + System.currentTimeMillis());
                actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
                return this.maybeNextTask(currentTask);
              }

              if (actor.isMoving()) {
                return Rnd.chance(10);
              }

              if (!actor.isMovementDisabled() && this.getIsMobile()) {
                return !this.tryMoveToTarget(target, collisions + getIndentRange(castRange), collisions + castRange);
              }

              return true;
            }

            return true;
          case BUFF:
            target = currentTask.target.get();
            if (!actor.isMuted(currentTask.skill) && !actor.isSkillDisabled(currentTask.skill) && !actor.isUnActiveSkill(currentTask.skill.getId())) {
              if (target != null && !target.isAlikeDead() && actor.isInRange(target, 2000L)) {
                isAoE = currentTask.skill.getTargetType() == SkillTargetType.TARGET_AURA;
                castRange = currentTask.skill.getAOECastRange();
                if (actor.isMoving()) {
                  return Rnd.chance(10);
                }

                collisions = (int) (actor.getCollisionRadius() + target.getColRadius());
                incZ = actor.isFlying() || actor.isInWater() || target.isFlying() || target.isInWater();
                dist = (int) (!incZ ? actor.getDistance(target) : actor.getDistance3D(target)) - collisions;
                if (dist <= castRange && GeoEngine.canSeeTarget(actor, target, incZ)) {
                  this.clientStopMoving();
                  this._pathfindFails = 0;
                  actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
                  return this.maybeNextTask(currentTask);
                }

                if (!actor.isMovementDisabled() && this.getIsMobile()) {
                  return !this.tryMoveToTarget(target, collisions + getIndentRange(castRange), collisions + castRange);
                }

                return true;
              }

              return true;
            }

            return true;
        }

        return false;
      } else {
        return false;
      }
    }
  }

  protected boolean createNewTask() {
    return false;
  }

  protected boolean defaultNewTask() {
    this.clearTasks();
    NpcInstance actor = this.getActor();
    Creature target;
    if (actor != null && (target = this.prepareTarget()) != null) {
      double distance = actor.getDistance(target);
      return this.chooseTaskAndTargets(null, target, distance);
    } else {
      return false;
    }
  }

  protected void onEvtThink() {
    NpcInstance actor = getActor();
    if (_thinking || actor == null || actor.isActionsDisabled() || actor.isAfraid()) {
      return;
    }

    if (_randomAnimationEnd > System.currentTimeMillis()) {
      return;
    }

    if (actor.isRaid() && (actor.isInZonePeace() || actor.isInZoneBattle() || actor.isInZone(ZoneType.SIEGE))) {
      teleportHome();
      return;
    }

    _thinking = true;
    try {
      if (!Config.BLOCK_ACTIVE_TASKS && getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
        thinkActive();
      } else if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
        thinkAttack();
      }
    } finally {
      _thinking = false;
    }
  }

  protected void onEvtDead(Creature killer) {
    NpcInstance actor = this.getActor();
    int transformer = actor.getParameter("transformOnDead", 0);
    int amount = actor.getParameter("transformSpawnAmount", 1);
    int rndRadius = actor.getParameter("transformSpawnRndRadius", 0);
    int chance = actor.getParameter("transformChance", 100);
    if (transformer > 0 && Rnd.chance(chance)) {
      for (int cnt = 0; cnt < amount; ++cnt) {
        Location loc = actor.getLoc();
        if (rndRadius > 0) {
          loc = Location.findPointToStay(loc, rndRadius, killer.getGeoIndex());
        }

        NpcInstance npc = NpcUtils.spawnSingle(transformer, loc, actor.getReflection());
        if (killer != null && killer.isPlayable()) {
          npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
          killer.sendPacket(npc.makeStatusUpdate(9, 10));
        }
      }
    }

    super.onEvtDead(killer);
  }

  protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage) {
    if (this.getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && this.isGlobalAggro()) {
      this.notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
    }
  }

  protected void onEvtAttacked(Creature attacker, int damage) {
    NpcInstance actor = this.getActor();
    if (attacker != null && !actor.isDead()) {
      int transformer = actor.getParameter("transformOnUnderAttack", 0);
      if (transformer > 0) {
        int chance = actor.getParameter("transformChance", 5);
        if (chance == 100 || ((MonsterInstance) actor).getChampion() == 0 && actor.getCurrentHpPercents() > 50.0D && Rnd.chance(chance)) {
          MonsterInstance npc = (MonsterInstance) NpcHolder.getInstance().getTemplate(transformer).getNewInstance();
          npc.setSpawnedLoc(actor.getLoc());
          npc.setReflection(actor.getReflection());
          npc.setChampion(((MonsterInstance) actor).getChampion());
          npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
          npc.spawnMe(npc.getSpawnedLoc());
          npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
          actor.decayOrDelete();
          attacker.setTarget(npc);
          attacker.sendPacket(npc.makeStatusUpdate(9, 10));
          return;
        }
      }

      Player player = attacker.getPlayer();
      if (player != null) {
        if (Config.ALT_TELEPORT_FROM_SEVEN_SING_MONSTER && (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) && actor.isSevenSignsMonster()) {
          int pcabal = SevenSigns.getInstance().getPlayerCabal(player);
          int wcabal = SevenSigns.getInstance().getCabalHighestScore();
          if (pcabal != wcabal && wcabal != 0) {
            player.sendMessage(new CustomMessage("defaultAI.CabalTeleported", player));
            player.teleToClosestTown();
            return;
          }
        }

        List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
        if (quests != null) {
          Iterator var12 = quests.iterator();

          while (var12.hasNext()) {
            QuestState qs = (QuestState) var12.next();
            qs.getQuest().notifyAttack(actor, qs);
          }
        }
      }

      actor.getAggroList().addDamageHate(attacker, 0, damage);
      if (damage > 0 && (attacker.isSummon() || attacker.isPet())) {
        actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
      }

      if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
        if (!actor.isRunning()) {
          this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
        }

        this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
      }

      this.notifyFriends(attacker, damage);
    }
  }

  protected void onEvtAggression(Creature attacker, int aggro) {
    NpcInstance actor = this.getActor();
    if (attacker != null && !actor.isDead()) {
      actor.getAggroList().addDamageHate(attacker, 0, aggro);
      if (aggro > 0 && (attacker.isSummon() || attacker.isPet())) {
        actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? aggro : 1);
      }

      if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
        if (!actor.isRunning()) {
          this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
        }

        this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
      }

      this.notifyMinions(attacker, aggro);
    }
  }

  protected boolean maybeMoveToHome() {
    NpcInstance actor = this.getActor();
    if (actor.isDead()) {
      return false;
    } else {
      boolean randomWalk = actor.hasRandomWalk();
      Location sloc = this.getPursueBaseLoc();
      if (randomWalk && (!Config.RND_WALK || !Rnd.chance(Config.RND_WALK_RATE))) {
        return false;
      } else {
        boolean isInRange = actor.isInRangeZ(sloc, Config.MAX_DRIFT_RANGE);
        if (!randomWalk && isInRange) {
          return false;
        } else {
          Location pos = Location.findPointToStay(actor, sloc, 0, Config.MAX_DRIFT_RANGE);
          actor.setWalking();
          if (!actor.moveToLocation(pos.x, pos.y, pos.z, 0, true) && !isInRange) {
            this.teleportHome();
          }

          return true;
        }
      }
    }
  }

  protected void returnHome() {
    this.returnHome(true, Config.ALWAYS_TELEPORT_HOME);
  }

  protected void teleportHome() {
    this.returnHome(true, true);
  }

  protected void returnHome(boolean clearAggro, boolean teleport) {
    NpcInstance actor = this.getActor();
    Location sloc = this.getPursueBaseLoc();
    this.clearTasks();
    actor.stopMove();
    if (clearAggro) {
      actor.getAggroList().clear(true);
    }

    this.setAttackTimeout(9223372036854775807L);
    this.setAttackTarget(null);
    if (Config.RESTORE_HP_MP_ON_TELEPORT_HOME) {
      actor.setCurrentHpMp(actor.getMaxHp(), actor.getMaxMp(), true);
    }

    this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
    if (teleport) {
      actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 0L));
      actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
    } else {
      if (!clearAggro) {
        actor.setRunning();
      } else {
        actor.setWalking();
      }

      this.addTaskMove(sloc, false);
    }

  }

  protected Creature prepareTarget() {
    NpcInstance actor = this.getActor();
    if (actor.isConfused()) {
      return this.getAttackTarget();
    } else {
      if (Rnd.chance(actor.getParameter("isMadness", 0))) {
        Creature randomHated = actor.getAggroList().getRandomHated();
        if (randomHated != null) {
          this.setAttackTarget(randomHated);
          if (this._madnessTask == null && !actor.isConfused()) {
            actor.startConfused();
            this._madnessTask = ThreadPoolManager.getInstance().schedule(new DefaultAI.MadnessTask(), 10000L);
          }

          return randomHated;
        }
      }

      List<Creature> hateList = actor.getAggroList().getHateList(this.MAX_PURSUE_RANGE);
      Creature hated = null;
      Iterator var4 = hateList.iterator();

      while (var4.hasNext()) {
        Creature cha = (Creature) var4.next();
        if (this.checkTarget(cha, this.MAX_PURSUE_RANGE)) {
          hated = cha;
          break;
        }

        actor.getAggroList().remove(cha, true);
      }

      if (hated != null) {
        this.setAttackTarget(hated);
        return hated;
      } else {
        return null;
      }
    }
  }

  protected boolean canUseSkill(Skill skill, Creature target, double distance) {
    NpcInstance actor = this.getActor();
    if (skill != null && !skill.isNotUsedByAI()) {
      if (skill.getTargetType() == SkillTargetType.TARGET_SELF && target != actor) {
        return false;
      } else {
        int castRange = skill.getAOECastRange();
        if (castRange <= 200 && distance > 200.0D) {
          return false;
        } else if (!actor.isSkillDisabled(skill) && !actor.isMuted(skill) && !actor.isUnActiveSkill(skill.getId())) {
          double mpConsume2 = skill.getMpConsume2();
          if (skill.isMagic()) {
            mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
          } else {
            mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
          }

          if (actor.getCurrentMp() < mpConsume2) {
            return false;
          } else {
            return target.getEffectList().getEffectsCountForSkill(skill.getId()) == 0;
          }
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
  }

  protected boolean canUseSkill(Skill sk, Creature target) {
    return this.canUseSkill(sk, target, 0.0D);
  }

  protected Skill[] selectUsableSkills(Creature target, double distance, Skill[] skills) {
    if (skills != null && skills.length != 0 && target != null) {
      Skill[] ret = null;
      int usable = 0;
      Skill[] var7 = skills;
      int var8 = skills.length;

      for (int var9 = 0; var9 < var8; ++var9) {
        Skill skill = var7[var9];
        if (this.canUseSkill(skill, target, distance)) {
          if (ret == null) {
            ret = new Skill[skills.length];
          }

          ret[usable++] = skill;
        }
      }

      if (ret != null && usable != skills.length) {
        if (usable == 0) {
          return null;
        } else {
          ret = Arrays.copyOf(ret, usable);
          return ret;
        }
      } else {
        return ret;
      }
    } else {
      return null;
    }
  }

  protected static Skill selectTopSkillByDamage(Creature actor, Creature target, double distance, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      if (skills.length == 1) {
        return skills[0];
      } else {
        RndSelector<Skill> rnd = new RndSelector(skills.length);
        Skill[] var8 = skills;
        int var9 = skills.length;

        for (int var10 = 0; var10 < var9; ++var10) {
          Skill skill = var8[var10];
          double weight = skill.getSimpleDamage(actor, target) * (double) skill.getAOECastRange() / distance;
          if (weight < 1.0D) {
            weight = 1.0D;
          }

          rnd.add(skill, (int) weight);
        }

        return rnd.select();
      }
    } else {
      return null;
    }
  }

  protected static Skill selectTopSkillByDebuff(Creature actor, Creature target, double distance, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      if (skills.length == 1) {
        return skills[0];
      } else {
        RndSelector<Skill> rnd = new RndSelector(skills.length);
        Skill[] var8 = skills;
        int var9 = skills.length;

        for (int var10 = 0; var10 < var9; ++var10) {
          Skill skill = var8[var10];
          if (skill.getSameByStackType(target) == null) {
            double weight;
            if ((weight = 100.0D * (double) skill.getAOECastRange() / distance) <= 0.0D) {
              weight = 1.0D;
            }

            rnd.add(skill, (int) weight);
          }
        }

        return rnd.select();
      }
    } else {
      return null;
    }
  }

  protected static Skill selectTopSkillByBuff(Creature target, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      if (skills.length == 1) {
        return skills[0];
      } else {
        RndSelector<Skill> rnd = new RndSelector(skills.length);
        Skill[] var5 = skills;
        int var6 = skills.length;

        for (int var7 = 0; var7 < var6; ++var7) {
          Skill skill = var5[var7];
          if (skill.getSameByStackType(target) == null) {
            double weight;
            if ((weight = skill.getPower()) <= 0.0D) {
              weight = 1.0D;
            }

            rnd.add(skill, (int) weight);
          }
        }

        return rnd.select();
      }
    } else {
      return null;
    }
  }

  protected static Skill selectTopSkillByHeal(Creature target, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      double hpReduced = (double) target.getMaxHp() - target.getCurrentHp();
      if (hpReduced < 1.0D) {
        return null;
      } else if (skills.length == 1) {
        return skills[0];
      } else {
        RndSelector<Skill> rnd = new RndSelector(skills.length);
        Skill[] var7 = skills;
        int var8 = skills.length;

        for (int var9 = 0; var9 < var8; ++var9) {
          Skill skill = var7[var9];
          double weight;
          if ((weight = Math.abs(skill.getPower() - hpReduced)) <= 0.0D) {
            weight = 1.0D;
          }

          rnd.add(skill, (int) weight);
        }

        return rnd.select();
      }
    } else {
      return null;
    }
  }

  protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill[] skills) {
    if (skills != null && skills.length != 0 && target != null) {
      Skill[] var6 = skills;
      int var7 = skills.length;

      for (int var8 = 0; var8 < var7; ++var8) {
        Skill sk = var6[var8];
        this.addDesiredSkill(skillMap, target, distance, sk);
      }

    }
  }

  protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill skill) {
    if (skill != null && target != null && this.canUseSkill(skill, target)) {
      int weight = (int) (-Math.abs((double) skill.getAOECastRange() - distance));
      if ((double) skill.getAOECastRange() >= distance) {
        weight += 1000000;
      } else if (skill.isNotTargetAoE() && skill.getTargets(this.getActor(), target, false).size() == 0) {
        return;
      }

      skillMap.put(skill, weight);
    }
  }

  protected void addDesiredHeal(Map<Skill, Integer> skillMap, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      NpcInstance actor = this.getActor();
      double hpReduced = (double) actor.getMaxHp() - actor.getCurrentHp();
      double hpPercent = actor.getCurrentHpPercents();
      if (hpReduced >= 1.0D) {
        Skill[] var9 = skills;
        int var10 = skills.length;

        for (int var11 = 0; var11 < var10; ++var11) {
          Skill sk = var9[var11];
          if (this.canUseSkill(sk, actor) && sk.getPower() <= hpReduced) {
            int weight = (int) sk.getPower();
            if (hpPercent < 50.0D) {
              weight += 1000000;
            }

            skillMap.put(sk, weight);
          }
        }

      }
    }
  }

  protected void addDesiredBuff(Map<Skill, Integer> skillMap, Skill[] skills) {
    if (skills != null && skills.length != 0) {
      NpcInstance actor = this.getActor();
      Skill[] var4 = skills;
      int var5 = skills.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        Skill sk = var4[var6];
        if (this.canUseSkill(sk, actor)) {
          skillMap.put(sk, 1000000);
        }
      }

    }
  }

  protected Skill selectTopSkill(Map<Skill, Integer> skillMap) {
    if (skillMap != null && !skillMap.isEmpty()) {
      int topWeight = -2147483648;
      Iterator var4 = skillMap.keySet().iterator();

      int nWeight;
      while (var4.hasNext()) {
        Skill next = (Skill) var4.next();
        if ((nWeight = skillMap.get(next)) > topWeight) {
          topWeight = nWeight;
        }
      }

      if (topWeight == -2147483648) {
        return null;
      } else {
        Skill[] skills = new Skill[skillMap.size()];
        nWeight = 0;
        Iterator var8 = skillMap.entrySet().iterator();

        while (var8.hasNext()) {
          Entry<Skill, Integer> e = (Entry) var8.next();
          if (e.getValue() >= topWeight) {
            skills[nWeight++] = e.getKey();
          }
        }

        return skills[Rnd.get(nWeight)];
      }
    } else {
      return null;
    }
  }

  protected boolean chooseTaskAndTargets(Skill skill, Creature target, double distance) {
    NpcInstance actor = this.getActor();
    LazyArrayList<Creature> targets;
    Iterator var7;
    Creature cha;
    if (skill != null) {
      if (actor.isMovementDisabled() && distance > (double) (skill.getAOECastRange() + 60)) {
        target = null;
        if (skill.isOffensive()) {
          targets = LazyArrayList.newInstance();
          var7 = actor.getAggroList().getHateList(this.MAX_PURSUE_RANGE).iterator();

          while (var7.hasNext()) {
            cha = (Creature) var7.next();
            if (this.checkTarget(cha, skill.getAOECastRange() + 60) && this.canUseSkill(skill, cha)) {
              targets.add(cha);
            }
          }

          if (!targets.isEmpty()) {
            target = targets.get(Rnd.get(targets.size()));
          }

          LazyArrayList.recycle(targets);
        }
      }

      if (target == null) {
        return false;
      } else {
        if (skill.isOffensive()) {
          this.addTaskCast(target, skill);
        } else {
          this.addTaskBuff(target, skill);
        }

        return true;
      }
    } else {
      if (actor.isMovementDisabled() && distance > (double) (actor.getPhysicalAttackRange() + 40)) {
        target = null;
        targets = LazyArrayList.newInstance();
        var7 = actor.getAggroList().getHateList(this.MAX_PURSUE_RANGE).iterator();

        while (var7.hasNext()) {
          cha = (Creature) var7.next();
          if (this.checkTarget(cha, actor.getPhysicalAttackRange() + 40)) {
            targets.add(cha);
          }
        }

        if (!targets.isEmpty()) {
          target = targets.get(Rnd.get(targets.size()));
        }

        LazyArrayList.recycle(targets);
      }

      if (target == null) {
        return false;
      } else {
        this.addTaskAttack(target);
        return true;
      }
    }
  }

  public boolean isActive() {
    return this._aiTask != null;
  }

  protected void clearTasks() {
    this._def_think = false;
    this._tasks.clear();
  }

  protected void startRunningTask(long interval) {
    NpcInstance actor = this.getActor();
    if (actor != null && this._runningTask == null && !actor.isRunning()) {
      this._runningTask = ThreadPoolManager.getInstance().schedule(new DefaultAI.RunningTask(), interval);
    }

  }

  protected boolean isGlobalAggro() {
    if (this._globalAggro == 0L) {
      return true;
    } else if (this._globalAggro <= System.currentTimeMillis()) {
      this._globalAggro = 0L;
      return true;
    } else {
      return false;
    }
  }

  public void setGlobalAggro(long value) {
    this._globalAggro = value;
  }

  public NpcInstance getActor() {
    return (NpcInstance) super.getActor();
  }

  protected boolean defaultThinkBuff(int rateSelf) {
    return this.defaultThinkBuff(rateSelf, 0);
  }

  protected void notifyFriends(Creature attacker, int damage) {
    NpcInstance actor = this.getActor();
    if (System.currentTimeMillis() - this._lastFactionNotifyTime > this._minFactionNotifyInterval) {
      this._lastFactionNotifyTime = System.currentTimeMillis();
      if (actor.isMinion()) {
        MonsterInstance master = ((MinionInstance) actor).getLeader();
        if (master != null) {
          if (!master.isDead() && master.isVisible()) {
            master.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
          }

          MinionList minionList = master.getMinionList();
          if (minionList != null) {

            for (MinionInstance minion : minionList.getAliveMinions()) {
              if (minion != actor) {
                minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
              }
            }
          }
        }
      }

      MinionList minionList = actor.getMinionList();
      Iterator var9;
      if (minionList != null && minionList.hasAliveMinions()) {
        var9 = minionList.getAliveMinions().iterator();

        while (var9.hasNext()) {
          MinionInstance minion = (MinionInstance) var9.next();
          minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
        }
      }

      var9 = this.activeFactionTargets().iterator();

      while (var9.hasNext()) {
        NpcInstance npc = (NpcInstance) var9.next();
        npc.getAI().notifyEvent(CtrlEvent.EVT_CLAN_ATTACKED, new Object[]{actor, attacker, damage});
      }
    }

  }

  protected void notifyMinions(Creature attacker, int damage) {
    NpcInstance actor = this.getActor();
    MinionList minionList = actor.getMinionList();
    if (minionList != null && minionList.hasAliveMinions()) {

      for (MinionInstance minion : minionList.getAliveMinions()) {
        minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
      }
    }

  }

  protected List<NpcInstance> activeFactionTargets() {
    NpcInstance actor = this.getActor();
    if (actor.getFaction().isNone()) {
      return Collections.emptyList();
    } else {
      List<NpcInstance> npcFriends = new LazyArrayList<>();

      for (NpcInstance npc : World.getAroundNpc(actor)) {
        if (!npc.isDead() && npc.isInFaction(actor) && npc.isInRangeZ(actor, npc.getFaction().getRange()) && GeoEngine.canSeeTarget(npc, actor, false)) {
          npcFriends.add(npc);
        }
      }

      return npcFriends;
    }
  }

  protected boolean defaultThinkBuff(int rateSelf, int rateFriends) {
    NpcInstance actor = this.getActor();
    if (actor.isDead()) {
      return true;
    } else if (Rnd.chance(rateSelf)) {
      double actorHp = actor.getCurrentHpPercents();
      Skill[] skills = actorHp < 50.0D ? this.selectUsableSkills(actor, 0.0D, this._healSkills) : this.selectUsableSkills(actor, 0.0D, this._buffSkills);
      if (skills != null && skills.length != 0) {
        Skill skill = skills[Rnd.get(skills.length)];
        this.addTaskBuff(actor, skill);
        return true;
      } else {
        return false;
      }
    } else {
      if (Rnd.chance(rateFriends)) {
        Iterator var4 = this.activeFactionTargets().iterator();

        while (var4.hasNext()) {
          NpcInstance npc = (NpcInstance) var4.next();
          double targetHp = npc.getCurrentHpPercents();
          Skill[] skills = targetHp < 50.0D ? this.selectUsableSkills(actor, 0.0D, this._healSkills) : this.selectUsableSkills(actor, 0.0D, this._buffSkills);
          if (skills != null && skills.length != 0) {
            Skill skill = skills[Rnd.get(skills.length)];
            this.addTaskBuff(actor, skill);
            return true;
          }
        }
      }

      return false;
    }
  }

  protected boolean defaultFightTask() {
    this.clearTasks();
    NpcInstance actor = this.getActor();
    if (!actor.isDead() && !actor.isAMuted()) {
      Creature target;
      if ((target = this.prepareTarget()) == null) {
        return false;
      } else {
        double distance = actor.getDistance(target);
        double targetHp = target.getCurrentHpPercents();
        double actorHp = actor.getCurrentHpPercents();
        Skill[] dam = Rnd.chance(this.getRateDAM()) ? this.selectUsableSkills(target, distance, this._damSkills) : null;
        Skill[] dot = Rnd.chance(this.getRateDOT()) ? this.selectUsableSkills(target, distance, this._dotSkills) : null;
        Skill[] debuff = targetHp > 10.0D ? (Rnd.chance(this.getRateDEBUFF()) ? this.selectUsableSkills(target, distance, this._debuffSkills) : null) : null;
        Skill[] stun = Rnd.chance(this.getRateSTUN()) ? this.selectUsableSkills(target, distance, this._stunSkills) : null;
        Skill[] heal = actorHp < 50.0D ? (Rnd.chance(this.getRateHEAL()) ? this.selectUsableSkills(actor, 0.0D, this._healSkills) : null) : null;
        Skill[] buff = Rnd.chance(this.getRateBUFF()) ? this.selectUsableSkills(actor, 0.0D, this._buffSkills) : null;
        RndSelector<Skill[]> rnd = new RndSelector<>();
        if (!actor.isAMuted()) {
          rnd.add(null, this.getRatePHYS());
        }

        rnd.add(dam, this.getRateDAM());
        rnd.add(dot, this.getRateDOT());
        rnd.add(debuff, this.getRateDEBUFF());
        rnd.add(heal, this.getRateHEAL());
        rnd.add(buff, this.getRateBUFF());
        rnd.add(stun, this.getRateSTUN());
        Skill[] selected = rnd.select();
        if (selected != null) {
          if (selected == dam || selected == dot) {
            return this.chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);
          }

          if (selected == debuff || selected == stun) {
            return this.chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);
          }

          if (selected == buff) {
            return this.chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);
          }

          if (selected == heal) {
            return this.chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);
          }
        }

        return this.chooseTaskAndTargets(null, target, distance);
      }
    } else {
      return false;
    }
  }

  public int getRatePHYS() {
    return 100;
  }

  public int getRateDOT() {
    return 0;
  }

  public int getRateDEBUFF() {
    return 0;
  }

  public int getRateDAM() {
    return 0;
  }

  public int getRateSTUN() {
    return 0;
  }

  public int getRateBUFF() {
    return 0;
  }

  public int getRateHEAL() {
    return 0;
  }

  public boolean getIsMobile() {
    return !this.getActor().getParameter("isImmobilized", false);
  }

  public int getMaxPathfindFails() {
    return 3;
  }

  public int getMaxAttackTimeout() {
    return 15000;
  }

  public int getTeleportTimeout() {
    return 10000;
  }

  protected class NearestTargetComparator implements Comparator<Creature> {
    private final Creature actor;

    public NearestTargetComparator(Creature actor) {
      this.actor = actor;
    }

    public int compare(Creature o1, Creature o2) {
      double diff = this.actor.getDistance3D(o1) - this.actor.getDistance3D(o2);
      if (diff < 0.0D) {
        return -1;
      } else {
        return diff > 0.0D ? 1 : 0;
      }
    }
  }

  public class MadnessTask extends RunnableImpl {
    public MadnessTask() {
    }

    public void runImpl() throws Exception {
      NpcInstance actor = DefaultAI.this.getActor();
      if (actor != null) {
        actor.stopConfused();
      }

      DefaultAI.this._madnessTask = null;
    }
  }

  protected class RunningTask extends RunnableImpl {
    protected RunningTask() {
    }

    public void runImpl() throws Exception {
      NpcInstance actor = DefaultAI.this.getActor();
      if (actor != null) {
        actor.setRunning();
      }

      DefaultAI.this._runningTask = null;
    }
  }

  protected class Teleport extends RunnableImpl {
    Location _destination;

    public Teleport(Location destination) {
      this._destination = destination;
    }

    public void runImpl() throws Exception {
      NpcInstance actor = DefaultAI.this.getActor();
      if (actor != null) {
        actor.teleToLocation(this._destination);
      }

    }
  }

  private static class TaskComparator implements Comparator<DefaultAI.Task> {
    private static final Comparator<DefaultAI.Task> instance = new DefaultAI.TaskComparator();

    private TaskComparator() {
    }

    public static final Comparator<DefaultAI.Task> getInstance() {
      return instance;
    }

    public int compare(DefaultAI.Task o1, DefaultAI.Task o2) {
      return o1 != null && o2 != null ? o2.weight - o1.weight : 0;
    }
  }

  public static class Task {
    public DefaultAI.TaskType type;
    public Skill skill;
    public HardReference<? extends Creature> target;
    public Location loc;
    public boolean pathfind;
    public int weight = 10000;

    public Task() {
    }
  }

  public enum TaskType {
    MOVE,
    ATTACK,
    CAST,
    BUFF;

    TaskType() {
    }
  }
}
