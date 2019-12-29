//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;

import java.util.List;

public class Aggression extends Skill {
  private final boolean _unaggring;
  private final boolean _silent;

  public Aggression(StatsSet set) {
    super(set);
    this._unaggring = set.getBool("unaggroing", false);
    this._silent = set.getBool("silent", false);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    int effect = this._effectPoint;
    if (this.isSSPossible() && (activeChar.getChargedSoulShot() || activeChar.getChargedSpiritShot() > 0)) {
      effect *= 2;
    }

    for (Creature target : targets) {
      if (target != null && target.isAutoAttackable(activeChar)) {
        if (target.isNpc()) {
          if (this._unaggring) {
            if (target.isNpc() && activeChar.isPlayable()) {
              ((NpcInstance) target).getAggroList().addDamageHate(activeChar, 0, -effect);
            }
          } else {
            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, effect);
            if (!this._silent) {
              target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar, 0);
            }
          }
        } else if (target.isPlayable() && !target.isDebuffImmune()) {
          target.setTarget(activeChar);
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
