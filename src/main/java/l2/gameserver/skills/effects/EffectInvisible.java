//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.stats.Env;

public final class EffectInvisible extends Effect {
  private InvisibleType _invisibleType;

  public EffectInvisible(Env env, EffectTemplate template) {
    super(env, template);
    this._invisibleType = InvisibleType.NONE;
  }

  public boolean checkCondition() {
    if (!this._effected.isPlayer()) {
      return false;
    } else {
      Player player = (Player)this._effected;
      if (player.isInvisible()) {
        return false;
      } else {
        return player.getActiveWeaponFlagAttachment() != null ? false : super.checkCondition();
      }
    }
  }

  public void onStart() {
    super.onStart();
    Player player = (Player)this._effected;
    this._invisibleType = player.getInvisibleType();
    player.setInvisibleType(InvisibleType.EFFECT);
    World.removeObjectFromPlayers(player);
  }

  public void onExit() {
    super.onExit();
    Player player = (Player)this._effected;
    if (player.isInvisible()) {
      player.setInvisibleType(this._invisibleType);
      player.broadcastUserInfo(true);
      if (player.getPet() != null) {
        player.getPet().broadcastCharInfo();
      }

    }
  }

  public boolean onActionTime() {
    return false;
  }
}
