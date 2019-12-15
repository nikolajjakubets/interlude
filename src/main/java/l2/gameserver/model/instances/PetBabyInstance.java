//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.*;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.Future;

@Slf4j
public final class PetBabyInstance extends PetInstance {
  private Future<?> _actionTask;
  private boolean _buffEnabled = true;
  private static final int HealTrick = 4717;
  private static final int GreaterHealTrick = 4718;
  private static final int GreaterHeal = 5195;
  private static final int BattleHeal = 5590;
  private static final int Recharge = 5200;
  private static final int Pet_Haste = 5186;
  private static final int Pet_Vampiric_Rage = 5187;
  private static final int Pet_Regeneration = 5188;
  private static final int Pet_Blessed_Body = 5189;
  private static final int Pet_Blessed_Soul = 5190;
  private static final int Pet_Guidance = 5191;
  private static final int Pet_Wind_Walk = 5192;
  private static final int Pet_Acumen = 5193;
  private static final int Pet_Empower = 5194;
  private static final int Pet_Concentration = 5201;
  private static final int Pet_Might = 5586;
  private static final int Pet_Shield = 5587;
  private static final int Pet_Focus = 5588;
  private static final int Pet_Death_Wisper = 5589;
  private static final int Pet_Weapon_Maintenance = 5987;
  private static final int Pet_Armor_Maintenance = 5988;
  private static final int WindShackle = 5196;
  private static final int Hex = 5197;
  private static final int Slow = 5198;
  private static final int CurseGloom = 5199;
  private static final Skill[][] COUGAR_BUFFS = new Skill[][]{{SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5186, 2)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5588, 3)}};
  private static final Skill[][] BUFFALO_BUFFS = new Skill[][]{{SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5186, 2)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5588, 3), SkillTable.getInstance().getInfo(5589, 3)}};
  private static final Skill[][] KOOKABURRA_BUFFS = new Skill[][]{{SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5201, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5201, 6)}};

  public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int _currentLevel, long exp) {
    super(objectId, template, owner, control, _currentLevel, exp);
  }

  public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control) {
    super(objectId, template, owner, control);
  }

  public Skill[] getBuffs() {
    switch (this.getNpcId()) {
      case 16034:
        return BUFFALO_BUFFS[this.getBuffLevel()];
      case 16035:
        return KOOKABURRA_BUFFS[this.getBuffLevel()];
      case 16036:
        return COUGAR_BUFFS[this.getBuffLevel()];
      default:
        return Skill.EMPTY_ARRAY;
    }
  }

  public Skill onActionTask() {
    try {
      Player owner = this.getPlayer();
      if (!owner.isDead() && !owner.isInvul() && !this.isCastingNow()) {
        if (this.getEffectList().getEffectsCountForSkill(5753) > 0) {
          return null;
        }

        if (this.getEffectList().getEffectsCountForSkill(5771) > 0) {
          return null;
        }

        boolean improved = PetDataTable.isImprovedBabyPet(this.getNpcId());
        Skill skill = null;
        if (!Config.ALT_PET_HEAL_BATTLE_ONLY || owner.isInCombat()) {
          double curHp = owner.getCurrentHpPercents();
          if (curHp < 90.0D && Rnd.chance((100.0D - curHp) / 3.0D)) {
            if (curHp < 33.0D) {
              skill = SkillTable.getInstance().getInfo(improved ? 5590 : 4718, this.getHealLevel());
            } else if (this.getNpcId() != 16035) {
              skill = SkillTable.getInstance().getInfo(improved ? 5195 : 4717, this.getHealLevel());
            }
          }

          if (skill == null && this.getNpcId() == 16035) {
            double curMp = owner.getCurrentMpPercents();
            if (curMp < 66.0D && Rnd.chance((100.0D - curMp) / 2.0D)) {
              skill = SkillTable.getInstance().getInfo(5200, this.getRechargeLevel());
            }
          }

          if (skill != null && skill.checkCondition(this, owner, false, !this.isFollowMode(), true)) {
            this.setTarget(owner);
            this.getAI().Cast(skill, owner, false, !this.isFollowMode());
            return skill;
          }
        }

        if (!improved || owner.isInOfflineMode() || owner.getEffectList().getEffectsCountForSkill(5771) > 0) {
          return null;
        }

        for (Skill buff : this.getBuffs()) {
          if (this.getCurrentMp() >= buff.getMpConsume2()) {
            Iterator var8 = owner.getEffectList().getAllEffects().iterator();

            Effect ef;
            do {
              if (!var8.hasNext()) {
                if (buff.checkCondition(this, owner, false, !this.isFollowMode(), true)) {
                  this.setTarget(owner);
                  this.getAI().Cast(buff, owner, false, !this.isFollowMode());
                  return buff;
                }

                return null;
              }

              ef = (Effect) var8.next();
            } while (!this.checkEffect(ef, buff));
          }
        }
      }
    } catch (Throwable var10) {
      log.warn("Pet [#" + this.getNpcId() + "] a buff task error has occurred: " + var10);
      log.error("", var10);
    }

    return null;
  }

  private boolean checkEffect(Effect ef, Skill skill) {
    if (ef != null && ef.isInUse() && EffectList.checkStackType(ef.getTemplate(), skill.getEffectTemplates()[0])) {
      if (ef.getStackOrder() < skill.getEffectTemplates()[0]._stackOrder) {
        return false;
      } else if (ef.getTimeLeft() > 10) {
        return true;
      } else {
        return ef.getNext() != null && this.checkEffect(ef.getNext(), skill);
      }
    } else {
      return false;
    }
  }

  public synchronized void stopBuffTask() {
    if (this._actionTask != null) {
      this._actionTask.cancel(false);
      this._actionTask = null;
    }

  }

  public synchronized void startBuffTask() {
    if (this._actionTask != null) {
      this.stopBuffTask();
    }

    if (this._actionTask == null && !this.isDead()) {
      this._actionTask = ThreadPoolManager.getInstance().schedule(new PetBabyInstance.ActionTask(), 5000L);
    }

  }

  public boolean isBuffEnabled() {
    return this._buffEnabled;
  }

  public void triggerBuff() {
    this._buffEnabled = !this._buffEnabled;
  }

  protected void onDeath(Creature killer) {
    this.stopBuffTask();
    super.onDeath(killer);
  }

  public void doRevive() {
    super.doRevive();
    this.startBuffTask();
  }

  public void unSummon() {
    this.stopBuffTask();
    super.unSummon();
  }

  public int getHealLevel() {
    return Math.min(Math.max((this.getLevel() - this.getMinLevel()) / ((80 - this.getMinLevel()) / 12), 1), 12);
  }

  public int getRechargeLevel() {
    return Math.min(Math.max((this.getLevel() - this.getMinLevel()) / ((80 - this.getMinLevel()) / 8), 1), 8);
  }

  public int getBuffLevel() {
    return Math.min(Math.max((this.getLevel() - 55) / 5, 0), 3);
  }

  public int getSoulshotConsumeCount() {
    return 1;
  }

  public int getSpiritshotConsumeCount() {
    return 1;
  }

  class ActionTask extends RunnableImpl {
    ActionTask() {
    }

    public void runImpl() throws Exception {
      Skill skill = PetBabyInstance.this.onActionTask();
      PetBabyInstance.this._actionTask = ThreadPoolManager.getInstance().schedule(PetBabyInstance.this.new ActionTask(), skill == null ? 1000L : (long) (skill.getHitTime() * 333 / Math.max(PetBabyInstance.this.getMAtkSpd(), 1) - 100));
    }
  }
}
