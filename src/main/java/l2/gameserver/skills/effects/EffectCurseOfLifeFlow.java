//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;

public final class EffectCurseOfLifeFlow extends Effect {
  private EffectCurseOfLifeFlow.CurseOfLifeFlowListener _listener;
  private TObjectIntHashMap<HardReference<? extends Creature>> _damageList = new TObjectIntHashMap();

  public EffectCurseOfLifeFlow(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._listener = new EffectCurseOfLifeFlow.CurseOfLifeFlowListener();
    this._effected.addListener(this._listener);
  }

  public void onExit() {
    super.onExit();
    this._effected.removeListener(this._listener);
    this._listener = null;
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else {
      TObjectIntIterator iterator = this._damageList.iterator();

      while(iterator.hasNext()) {
        iterator.advance();
        Creature damager = (Creature)((HardReference)iterator.key()).get();
        if (damager != null && !damager.isDead() && !damager.isCurrentHpFull()) {
          int damage = iterator.value();
          if (damage > 0) {
            double max_heal = this.calc();
            double heal = Math.min((double)damage, max_heal);
            double newHp = Math.min(damager.getCurrentHp() + heal, (double)damager.getMaxHp());
            damager.sendPacket((new SystemMessage(1066)).addNumber((long)(newHp - damager.getCurrentHp())));
            damager.setCurrentHp(newHp, false);
          }
        }
      }

      this._damageList.clear();
      return true;
    }
  }

  private class CurseOfLifeFlowListener implements OnCurrentHpDamageListener {
    private CurseOfLifeFlowListener() {
    }

    public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill) {
      if (attacker != actor && attacker != EffectCurseOfLifeFlow.this._effected) {
        int old_damage = EffectCurseOfLifeFlow.this._damageList.get(attacker.getRef());
        EffectCurseOfLifeFlow.this._damageList.put(attacker.getRef(), old_damage == 0 ? (int)damage : old_damage + (int)damage);
      }
    }
  }
}
