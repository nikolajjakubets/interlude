//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.LockType;
import l2.gameserver.stats.Env;

public class EffectLockInventory extends Effect {
  private LockType _lockType;
  private int[] _lockItems;

  public EffectLockInventory(Env env, EffectTemplate template) {
    super(env, template);
    this._lockType = (LockType)template.getParam().getEnum("lockType", LockType.class);
    this._lockItems = template.getParam().getIntegerArray("lockItems");
  }

  public void onStart() {
    super.onStart();
    Player player = this._effector.getPlayer();
    player.getInventory().lockItems(this._lockType, this._lockItems);
  }

  public void onExit() {
    super.onExit();
    Player player = this._effector.getPlayer();
    player.getInventory().unlock();
  }

  protected boolean onActionTime() {
    return false;
  }
}
