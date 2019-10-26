//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.Env;

public final class EffectDisarm extends Effect {
  public EffectDisarm(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    if (!this._effected.isPlayer()) {
      return false;
    } else {
      Player player = this._effected.getPlayer();
      return !player.isCursedWeaponEquipped() && player.getActiveWeaponFlagAttachment() == null ? super.checkCondition() : false;
    }
  }

  public void onStart() {
    super.onStart();
    Player player = (Player)this._effected;
    ItemInstance wpn = player.getActiveWeaponInstance();
    if (wpn != null) {
      player.getInventory().unEquipItem(wpn);
      player.sendDisarmMessage(wpn);
    }

    player.startWeaponEquipBlocked();
  }

  public void onExit() {
    super.onExit();
    this._effected.stopWeaponEquipBlocked();
  }

  public boolean onActionTime() {
    return false;
  }
}
