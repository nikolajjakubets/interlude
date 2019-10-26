//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.EffectsDAO;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.GameObjectTasks.DeleteTask;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncAdd;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class Summon extends Skill {
  private final Summon.SummonType _summonType;
  private final double _expPenalty;
  private final int _itemConsumeIdInTime;
  private final int _itemConsumeCountInTime;
  private final int _itemConsumeDelay;
  private final int _lifeTime;
  private final int _minRadius;

  public Summon(StatsSet set) {
    super(set);
    this._summonType = (Summon.SummonType)Enum.valueOf(Summon.SummonType.class, set.getString("summonType", "PET").toUpperCase());
    this._expPenalty = set.getDouble("expPenalty", 0.0D);
    this._itemConsumeIdInTime = set.getInteger("itemConsumeIdInTime", 0);
    this._itemConsumeCountInTime = set.getInteger("itemConsumeCountInTime", 0);
    this._itemConsumeDelay = set.getInteger("itemConsumeDelay", 240) * 1000;
    this._lifeTime = set.getInteger("lifeTime", 1200) * 1000;
    this._minRadius = set.getInteger("minRadius", 0);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    Player player = activeChar.getPlayer();
    if (player == null) {
      return false;
    } else {
      switch(this._summonType) {
        case TRAP:
          if (player.isInZonePeace()) {
            activeChar.sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
          }
          break;
        case PET:
        case SIEGE_SUMMON:
          if (player.isProcessingRequest()) {
            player.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
          }

          if (player.getPet() == null && !player.isMounted()) {
            break;
          }

          player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
          return false;
        case MERCHANT:
          if (player.isProcessingRequest()) {
            player.sendPacket(Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
          }
          break;
        case AGATHION:
          if (player.getAgathionId() > 0 && this._npcId != 0) {
            player.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
            return false;
          }
        case NPC:
          if (this._minRadius > 0) {
            Iterator var7 = World.getAroundNpc(player, this._minRadius, 200).iterator();

            while(var7.hasNext()) {
              NpcInstance npc = (NpcInstance)var7.next();
              if (npc != null && npc.getNpcId() == this.getNpcId()) {
                player.sendPacket((new SystemMessage(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN)).addName(npc));
                return false;
              }
            }
          }
      }

      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }
  }

  public void useSkill(Creature caster, List<Creature> targets) {
    Player activeChar = caster.getPlayer();
    switch(this._summonType) {
      case TRAP:
        Skill trapSkill = this.getFirstAddedSkill();
        if (activeChar.getTrapsCount() >= 5) {
          activeChar.destroyFirstTrap();
        }

        TrapInstance trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(this.getNpcId()), activeChar, trapSkill);
        activeChar.addTrap(trap);
        trap.spawnMe();
        break;
      case PET:
      case SIEGE_SUMMON:
        Location loc = null;
        if (this._targetType == SkillTargetType.TARGET_CORPSE) {
          Iterator var7 = targets.iterator();

          while(var7.hasNext()) {
            Creature target = (Creature)var7.next();
            if (target != null && target.isDead()) {
              activeChar.getAI().setAttackTarget((Creature)null);
              loc = target.getLoc();
              if (target.isNpc()) {
                ((NpcInstance)target).endDecayTask();
              } else {
                if (!target.isSummon()) {
                  return;
                }

                ((SummonInstance)target).endDecayTask();
              }
            }
          }
        }

        if (activeChar.getPet() != null || activeChar.isMounted()) {
          return;
        }

        NpcTemplate summonTemplate = NpcHolder.getInstance().getTemplate(this.getNpcId());
        SummonInstance summon = new SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, activeChar, this._lifeTime, this._itemConsumeIdInTime, this._itemConsumeCountInTime, this._itemConsumeDelay, this);
        activeChar.setPet(summon);
        summon.setExpPenalty(this._expPenalty);
        summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.LEVEL.length - 1)]);
        summon.setHeading(activeChar.getHeading());
        summon.setReflection(activeChar.getReflection());
        summon.spawnMe(loc == null ? Location.findAroundPosition(activeChar, 50, 70) : loc);
        summon.setRunning();
        summon.setFollowMode(true);
        if (summon.getSkillLevel(4140) > 0) {
          summon.altUseSkill(SkillTable.getInstance().getInfo(4140, summon.getSkillLevel(4140)), activeChar);
        }

        if (summon.getName().equalsIgnoreCase("Shadow")) {
          summon.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 64, this, 15.0D));
        }

        EffectsDAO.getInstance().restoreEffects(summon);
        if (activeChar.isOlyParticipant()) {
          summon.getEffectList().stopAllEffects();
        }

        summon.setCurrentHpMp((double)summon.getMaxHp(), (double)summon.getMaxMp(), false);
        if (this._summonType == Summon.SummonType.SIEGE_SUMMON) {
          SiegeEvent siegeEvent = (SiegeEvent)activeChar.getEvent(SiegeEvent.class);
          siegeEvent.addSiegeSummon(summon);
        }
        break;
      case MERCHANT:
        if (activeChar.getPet() != null || activeChar.isMounted()) {
          return;
        }

        NpcTemplate merchantTemplate = NpcHolder.getInstance().getTemplate(this.getNpcId());
        MerchantInstance merchant = new MerchantInstance(IdFactory.getInstance().getNextId(), merchantTemplate);
        merchant.setCurrentHp((double)merchant.getMaxHp(), false);
        merchant.setCurrentMp((double)merchant.getMaxMp());
        merchant.setHeading(activeChar.getHeading());
        merchant.setReflection(activeChar.getReflection());
        merchant.spawnMe(activeChar.getLoc());
        ThreadPoolManager.getInstance().schedule(new DeleteTask(merchant), (long)this._lifeTime);
        break;
      case AGATHION:
        activeChar.setAgathion(this.getNpcId());
        break;
      case NPC:
        NpcUtils.spawnSingle(this.getNpcId(), activeChar.getLoc(), activeChar.getReflection(), (long)this._lifeTime, activeChar.getName());
    }

    if (this.isSSPossible()) {
      caster.unChargeShots(this.isMagic());
    }

  }

  public boolean isOffensive() {
    return this._targetType == SkillTargetType.TARGET_CORPSE;
  }

  private static enum SummonType {
    PET,
    SIEGE_SUMMON,
    AGATHION,
    TRAP,
    MERCHANT,
    NPC;

    private SummonType() {
    }
  }
}
