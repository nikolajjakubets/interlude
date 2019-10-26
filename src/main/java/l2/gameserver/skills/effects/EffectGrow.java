//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Env;

public final class EffectGrow extends Effect {
  public EffectGrow(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected.isNpc()) {
      NpcInstance npc = (NpcInstance)this._effected;
      npc.setCollisionHeight(npc.getCollisionHeight() * 1.24D);
      npc.setCollisionRadius(npc.getCollisionRadius() * 1.19D);
    }

  }

  public void onExit() {
    super.onExit();
    if (this._effected.isNpc()) {
      NpcInstance npc = (NpcInstance)this._effected;
      npc.setCollisionHeight(npc.getTemplate().collisionHeight);
      npc.setCollisionRadius(npc.getTemplate().collisionRadius);
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
