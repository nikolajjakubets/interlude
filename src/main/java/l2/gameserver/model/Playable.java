//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.util.Rnd;
import l2.commons.util.concurrent.atomic.AtomicState;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExServerPrimitive;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.utils.Location;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Playable extends Creature {
  private AtomicState _isSilentMoving = new AtomicState();
  private boolean _isPendingRevive;
  protected final ReadWriteLock questLock = new ReentrantReadWriteLock();
  protected final Lock questRead;
  protected final Lock questWrite;
  private long _nonAggroTime;

  public Playable(int objectId, CharTemplate template) {
    super(objectId, template);
    this.questRead = this.questLock.readLock();
    this.questWrite = this.questLock.writeLock();
    this._nonAggroTime = 0L;
  }

//  public HardReference<? extends Playable> getRef() {
//    return super.getRef();
//  }

  public abstract Inventory getInventory();

  public abstract long getWearedMask();

  public boolean checkPvP(Creature target, Skill skill) {
    Player player = this.getPlayer();
    if (!this.isDead() && target != null && player != null && target != this && target != player && target != player.getPet() && player.getKarma() <= 0) {
      if (skill != null) {
        if (skill.altUse()) {
          return false;
        }

        if (skill.getTargetType() == SkillTargetType.TARGET_FEEDABLE_BEAST) {
          return false;
        }

        if (skill.getTargetType() == SkillTargetType.TARGET_UNLOCKABLE) {
          return false;
        }

        if (skill.getTargetType() == SkillTargetType.TARGET_CHEST) {
          return false;
        }
      }

      DuelEvent duelEvent = this.getEvent(DuelEvent.class);
      if (duelEvent != null && duelEvent == target.getEvent(DuelEvent.class)) {
        return false;
      } else if (this.isInZonePeace() && target.isInZonePeace()) {
        return false;
      } else if (this.isInZoneBattle() && target.isInZoneBattle()) {
        return false;
      } else if (this.isInZone(ZoneType.SIEGE) && target.isInZone(ZoneType.SIEGE)) {
        return false;
      } else if (this.isInZone(ZoneType.fun) && target.isInZone(ZoneType.fun)) {
        return false;
      } else {
        if (skill != null && !skill.isOffensive()) {
          return target.getPvpFlag() > 0 || target.getKarma() > 0 || target.isMonster();
        } else {
          if (target.getKarma() > 0) {
            return false;
          }

          return target.isPlayable();
        }

      }
    } else {
      return false;
    }
  }

  public boolean checkTarget(Creature target) {
    Player player = this.getPlayer();
    if (player == null) {
      return false;
    } else if (target != null && !target.isDead()) {
      if (!this.isInRange(target, 2000L)) {
        player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
        return false;
      } else if (target.isDoor() && !target.isAttackable(this)) {
        player.sendPacket(Msg.INVALID_TARGET);
        return false;
      } else if (target.paralizeOnAttack(this)) {
        if (Config.PARALIZE_ON_RAID_DIFF) {
          this.paralizeMe(target);
        }

        return false;
      } else if (!target.isInvisible() && this.getReflection() == target.getReflection() && GeoEngine.canSeeTarget(this, target, false)) {
        if (player.isInZone(ZoneType.epic) != target.isInZone(ZoneType.epic) && target.isMonster()) {
          player.sendPacket(Msg.INVALID_TARGET);
          return false;
        } else {
          if (target.isPlayable()) {
            if (this.isInZoneBattle() != target.isInZoneBattle()) {
              player.sendPacket(Msg.INVALID_TARGET);
              return false;
            }

            if (this.isInZonePeace() || target.isInZonePeace()) {
              player.sendPacket(Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
              return false;
            }

            if (player.isOlyParticipant() && !player.isOlyCompetitionStarted()) {
              return false;
            }

            if (target.isPlayer()) {
              Player pcAttacker = target.getPlayer();
              if (player.isOlyParticipant()) {
                if (pcAttacker.isOlyParticipant() && player.getOlyParticipant().getCompetition() != pcAttacker.getOlyParticipant().getCompetition()) {
                  return false;
                }

                if (player.isOlyCompetitionStarted() && player.getOlyParticipant() == pcAttacker.getOlyParticipant()) {
                  return false;
                }

                return !player.isLooseOlyCompetition();
              }
            }
          }

          return true;
        }
      } else {
        player.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
        return false;
      }
    } else {
      player.sendPacket(Msg.INVALID_TARGET);
      return false;
    }
  }

  public void setXYZ(int x, int y, int z, boolean MoveTask) {
    super.setXYZ(x, y, z, MoveTask);
    if (MoveTask && this.isPlayable()) {
      Player player = this.getPlayer();
      int dbgMove = player.getVarInt("debugMove", 0);
      if (dbgMove > 0) {
        Location loc = this.getLoc();
        ExServerPrimitive tracePkt = new ExServerPrimitive(loc.toXYZString(), loc.getX(), loc.getY(), (int)((double)loc.getZ() + this.getColHeight() + 16.0D));
        if (this.moveAction != null) {
          Color[] ccs = new Color[]{Color.CYAN, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.RED, Color.YELLOW, Color.RED};
          Color c = ccs[System.identityHashCode(this.moveAction) % ccs.length];
          tracePkt.addPoint(String.format("%s|%08x", loc.toXYZString(), this.moveAction.hashCode()), c, true, loc.getX(), loc.getY(), loc.getZ());
        } else {
          tracePkt.addPoint(loc.toXYZString(), 16777215, true, loc.getX(), loc.getY(), loc.getZ());
        }

        player.sendPacket(tracePkt);
        if (dbgMove > 1) {
          player.broadcastPacketToOthers(tracePkt);
        }
      }
    }

  }

  public void doAttack(Creature target) {
    Player player = this.getPlayer();
    if (player != null) {
      if (!this.isAMuted() && !this.isAttackingNow()) {
        if (player.isInObserverMode()) {
          player.sendMessage(new CustomMessage("l2p.gameserver.model.L2Playable.OutOfControl.ObserverNoAttack", player));
        } else if (!this.checkTarget(target)) {
          this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
          player.sendActionFailed();
        } else {
          DuelEvent duelEvent = this.getEvent(DuelEvent.class);
          if (duelEvent != null && target.getEvent(DuelEvent.class) != duelEvent) {
            duelEvent.abortDuel(this.getPlayer());
          }

          WeaponTemplate weaponItem = this.getActiveWeaponItem();
          if (weaponItem != null && weaponItem.getItemType() == WeaponType.BOW) {
            double bowMpConsume = weaponItem.getMpConsume();
            if (bowMpConsume > 0.0D) {
              double chance = this.calcStat(Stats.MP_USE_BOW_CHANCE, 0.0D, target, null);
              if (chance > 0.0D && Rnd.chance(chance)) {
                bowMpConsume = this.calcStat(Stats.MP_USE_BOW, bowMpConsume, target, null);
              }

              if (this._currentMp < bowMpConsume) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
                player.sendPacket(Msg.NOT_ENOUGH_MP);
                player.sendActionFailed();
                return;
              }

              this.reduceCurrentMp(bowMpConsume, null);
            }

            if (!player.checkAndEquipArrows()) {
              this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
              player.sendPacket(Msg.YOU_HAVE_RUN_OUT_OF_ARROWS);
              player.sendActionFailed();
              return;
            }
          }

          super.doAttack(target);
        }
      } else {
        player.sendActionFailed();
      }
    }
  }

  public void doPurePk(Player killer) {
    int pkCountMulti = Math.max(killer.getPkKills() / 2, 1);
    killer.increaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti);
  }

  public void doCast(Skill skill, Creature target, boolean forceUse) {
    if (skill != null) {
      DuelEvent duelEvent = this.getEvent(DuelEvent.class);
      if (duelEvent != null && target.getEvent(DuelEvent.class) != duelEvent) {
        duelEvent.abortDuel(this.getPlayer());
      }

      if (!this.isInPeaceZone() || skill.getTargetType() != SkillTargetType.TARGET_AREA && skill.getTargetType() != SkillTargetType.TARGET_AURA && skill.getTargetType() != SkillTargetType.TARGET_MULTIFACE && skill.getTargetType() != SkillTargetType.TARGET_MULTIFACE_AURA) {
        if (skill.getSkillType() == SkillType.DEBUFF && skill.isMagic() && target.isNpc() && target.isInvul() && !target.isMonster()) {
          this.getPlayer().sendPacket(Msg.INVALID_TARGET);
        } else {
          super.doCast(skill, target, forceUse);
        }
      } else {
        this.getPlayer().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
      }
    }
  }

  public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
    if (attacker != null && !this.isDead() && (!attacker.isDead() || isDot)) {
      if (!this.isDamageBlocked() || !transferDamage) {
        if (this.isDamageBlocked() && attacker != this) {
          if (sendMessage) {
            attacker.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
          }

        } else {
          if (attacker != this && attacker.isPlayable()) {
            Player player = this.getPlayer();
            Player pcAttacker = attacker.getPlayer();
            if (pcAttacker != player && player.isOlyParticipant() && !player.isOlyCompetitionStarted()) {
              if (sendMessage) {
                pcAttacker.sendPacket(Msg.INVALID_TARGET);
              }

              return;
            }

            if (this.isInZoneBattle() != attacker.isInZoneBattle()) {
              if (sendMessage) {
                attacker.getPlayer().sendPacket(Msg.INVALID_TARGET);
              }

              return;
            }

            DuelEvent duelEvent = this.getEvent(DuelEvent.class);
            if (duelEvent != null && attacker.getEvent(DuelEvent.class) != duelEvent) {
              duelEvent.abortDuel(player);
            }
          }

          super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
        }
      }
    }
  }

  public int getPAtkSpd() {
    return Math.max((int) this.calcStat(Stats.POWER_ATTACK_SPEED, this.calcStat(Stats.ATK_BASE, this._template.basePAtkSpd, null, null), null, null), 1);
  }

  public int getPAtk(Creature target) {
    double init = this.getActiveWeaponInstance() == null ? (double)this._template.basePAtk : 0.0D;
    return (int) this.calcStat(Stats.POWER_ATTACK, init, target, null);
  }

  public int getMAtk(Creature target, Skill skill) {
    if (skill != null && skill.getMatak() > 0) {
      return skill.getMatak();
    } else {
      double init = this.getActiveWeaponInstance() == null ? (double)this._template.baseMAtk : 0.0D;
      return (int)this.calcStat(Stats.MAGIC_ATTACK, init, target, skill);
    }
  }

  public boolean isAttackable(Creature attacker) {
    return this.isCtrlAttackable(attacker, true, false);
  }

  public boolean isAutoAttackable(Creature attacker) {
    return this.isCtrlAttackable(attacker, false, false);
  }

  public boolean isCtrlAttackable(Creature attacker, boolean force, boolean witchCtrl) {
    Player player = this.getPlayer();
    if (attacker != null && player != null && attacker != this && (attacker != player || force) && !this.isAlikeDead() && !attacker.isAlikeDead()) {
      if (!this.isInvisible() && this.getReflection() == attacker.getReflection()) {
        if (this.isInBoat()) {
          return false;
        } else if (attacker == this.getPet()) {
          return false;
        } else {
          Iterator var5 = this.getEvents().iterator();

          GlobalEvent e;
          while(var5.hasNext()) {
            e = (GlobalEvent)var5.next();
            if (e.checkForAttack(attacker, this, null, force) != null) {
              return false;
            }
          }

          var5 = player.getEvents().iterator();

          while(var5.hasNext()) {
            e = (GlobalEvent)var5.next();
            if (e.canAttack(this, attacker, null, force)) {
              return true;
            }
          }

          Player pcAttacker = attacker.getPlayer();
          if (pcAttacker != null && pcAttacker != player) {
            if (pcAttacker.isInBoat()) {
              return false;
            } else if ((!pcAttacker.isCursedWeaponEquipped() || player.getLevel() >= 21) && (!player.isCursedWeaponEquipped() || pcAttacker.getLevel() >= 21)) {
              if (player.isInZone(ZoneType.epic) != pcAttacker.isInZone(ZoneType.epic)) {
                return false;
              } else {
                if (player.isOlyParticipant()) {
                  if (pcAttacker.isOlyParticipant() && player.getOlyParticipant().getCompetition() != pcAttacker.getOlyParticipant().getCompetition()) {
                    return false;
                  }

                  if (player.isOlyCompetitionStarted() && player.getOlyParticipant() == pcAttacker.getOlyParticipant()) {
                    return false;
                  }

                  if (player.isLooseOlyCompetition()) {
                    return false;
                  }

                  if (player.getClan() != null && player.getClan() == pcAttacker.getClan()) {
                    return true;
                  }
                }

                if (player.getTeam() != TeamType.NONE && player.getTeam() == pcAttacker.getTeam()) {
                  return false;
                } else if (this.isInZonePeace()) {
                  return false;
                } else if (!force && player.getParty() != null && player.getParty() == pcAttacker.getParty()) {
                  return false;
                } else if (this.isInZoneBattle()) {
                  return true;
                } else {
                  if (!force) {
                    if (player.getClan() != null && player.getClan() == pcAttacker.getClan()) {
                      return false;
                    }

                    if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player.getAlliance() != null && player.getAlliance() == pcAttacker.getAlliance()) {
                      return false;
                    }
                  }

                  if (this.isInZone(ZoneType.SIEGE)) {
                    return true;
                  } else if (this.isInZone(ZoneType.fun)) {
                    return true;
                  } else if (pcAttacker.atMutualWarWith(player)) {
                    return true;
                  } else if (player.getKarma() <= 0 && player.getPvpFlag() == 0) {
                    if (witchCtrl && player.getPvpFlag() > 0) {
                      return true;
                    } else {
                      return pcAttacker.isCursedWeaponEquipped() || force;
                    }
                  } else {
                    return true;
                  }
                }
              }
            } else {
              return false;
            }
          } else {
            return true;
          }
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int getKarma() {
    Player player = this.getPlayer();
    return player == null ? 0 : player.getKarma();
  }

  public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills) {
    Player player = this.getPlayer();
    if (player != null) {
      if (useActionSkills && !skill.altUse() && !skill.getSkillType().equals(SkillType.BEAST_FEED)) {

        for (Creature target : targets) {
          int aggro;
          if (target.isNpc()) {
            if (skill.isOffensive()) {
              if (target.paralizeOnAttack(player)) {
                if (Config.PARALIZE_ON_RAID_DIFF) {
                  this.paralizeMe(target);
                }

                return;
              }

              if (!skill.isAI()) {
                aggro = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : 1;
                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, this, aggro);
              }
            }

            target.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
          } else if (target.isPlayable() && target != this.getPet() && (!this.isSummon() && !this.isPet() || target != player)) {
            aggro = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : Math.max(1, (int) skill.getPower());
            List<NpcInstance> npcs = World.getAroundNpc(target);

            for (NpcInstance npc : npcs) {
              if (!npc.isDead() && npc.isInRangeZ(this, 2000L)) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
                AggroInfo ai = npc.getAggroList().get(target);
                if (ai != null) {
                  if (!skill.isHandler() && npc.paralizeOnAttack(player)) {
                    if (Config.PARALIZE_ON_RAID_DIFF) {
                      this.paralizeMe(npc);
                    }

                    return;
                  }

                  if (ai.hate >= 100 && GeoEngine.canSeeTarget(npc, target, false)) {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, ai.damage == 0 ? aggro / 2 : aggro);
                  }
                }
              }
            }
          }

          if (this.checkPvP(target, skill)) {
            this.startPvPFlag(target);
          }
        }
      }

      super.callSkill(skill, targets, useActionSkills);
    }
  }

  public void broadcastPickUpMsg(ItemInstance item) {
    Player player = this.getPlayer();
    if (item != null && player != null && !player.isInvisible()) {
      if (item.isEquipable() && !(item.getTemplate() instanceof EtcItemTemplate)) {
        SystemMessage msg;
        String player_name = player.getName();
        int msg_id;
        if (item.getEnchantLevel() > 0) {
          msg_id = this.isPlayer() ? 1534 : 1536;
          msg = (new SystemMessage(msg_id)).addString(player_name).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
        } else {
          msg_id = this.isPlayer() ? 1533 : 1536;
          msg = (new SystemMessage(msg_id)).addString(player_name).addItemName(item.getItemId());
        }

        player.broadcastPacket(msg);
      }

    }
  }

  public void paralizeMe(Creature effector) {
    Skill revengeSkill = SkillTable.getInstance().getInfo(4515, 1);
    revengeSkill.getEffects(effector, this, false, false);
  }

  public final void setPendingRevive(boolean value) {
    this._isPendingRevive = value;
  }

  public boolean isPendingRevive() {
    return this._isPendingRevive;
  }

  public void doRevive() {
    if (!this.isTeleporting()) {
      this.setPendingRevive(false);
      this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
      if (!this.isSalvation()) {
        if (Config.RESPAWN_RESTORE_HP >= 0.0D) {
          this.setCurrentHp((double)this.getMaxHp() * Config.RESPAWN_RESTORE_HP, true);
        }

        if (Config.RESPAWN_RESTORE_MP >= 0.0D) {
          this.setCurrentMp((double)this.getMaxMp() * Config.RESPAWN_RESTORE_MP);
        }

        if (this.isPlayer() && Config.RESPAWN_RESTORE_CP >= 0.0D) {
          this.setCurrentCp((double)this.getMaxCp() * Config.RESPAWN_RESTORE_CP, true);
        }
      } else {

        for (Effect e : this.getEffectList().getAllEffects()) {
          if (e.getEffectType() == EffectType.Salvation) {
            e.exit();
            break;
          }
        }

        this.setCurrentHp(this.getMaxHp(), true);
        this.setCurrentMp(this.getMaxMp());
        this.setCurrentCp(this.getMaxCp());
      }

      this.broadcastPacket(new Revive(this));
    } else {
      this.setPendingRevive(true);
    }

  }

  public abstract void doPickupItem(GameObject var1);

  public void sitDown(StaticObjectInstance throne) {
  }

  public void standUp() {
  }

  public long getNonAggroTime() {
    return this._nonAggroTime;
  }

  public void setNonAggroTime(long time) {
    this._nonAggroTime = time;
  }

  public boolean startSilentMoving() {
    return this._isSilentMoving.getAndSet(true);
  }

  public boolean stopSilentMoving() {
    return this._isSilentMoving.setAndGet(false);
  }

  public boolean isSilentMoving() {
    return this._isSilentMoving.get();
  }

  public boolean isInCombatZone() {
    return this.isInZoneBattle();
  }

  public boolean isInPeaceZone() {
    return this.isInZonePeace();
  }

  public boolean isInZoneBattle() {
    return super.isInZoneBattle();
  }

  public boolean isOnSiegeField() {
    return this.isInZone(ZoneType.SIEGE);
  }

  public boolean isInSSQZone() {
    return this.isInZone(ZoneType.ssq_zone);
  }

  public boolean isInDangerArea() {
    return this.isInZone(ZoneType.damage) || this.isInZone(ZoneType.swamp) || this.isInZone(ZoneType.poison) || this.isInZone(ZoneType.instant_skill);
  }

  public int getMaxLoad() {
    return 0;
  }

  public int getInventoryLimit() {
    return 0;
  }

  public boolean isPlayable() {
    return true;
  }
}
