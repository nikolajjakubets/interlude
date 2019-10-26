//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.stats.Env;

public final class EffectTransformation extends Effect {
  private final boolean isFlyingTransform;

  public EffectTransformation(Env env, EffectTemplate template) {
    super(env, template);
    int id = (int)template._value;
    this.isFlyingTransform = template.getParam().getBool("isFlyingTransform", id == 8 || id == 9 || id == 260);
  }

  public boolean checkCondition() {
    if (!this._effected.isPlayer()) {
      return false;
    } else {
      return this.isFlyingTransform && this._effected.getX() > -166168 ? false : super.checkCondition();
    }
  }

  public void onStart() {
    super.onStart();
    Player player = (Player)this._effected;
    player.setTransformationTemplate(this.getSkill().getNpcId());
    if (this.getSkill() instanceof Transformation) {
      player.setTransformationName(((Transformation)this.getSkill()).transformationName);
    }

    int id = (int)this.calc();
    if (this.isFlyingTransform) {
      boolean isVisible = player.isVisible();
      if (player.getPet() != null) {
        player.getPet().unSummon();
      }

      player.decayMe();
      player.setFlying(true);
      player.setLoc(player.getLoc().changeZ(300));
      player.setTransformation(id);
      if (isVisible) {
        player.spawnMe();
      }
    } else {
      player.setTransformation(id);
    }

  }

  public void onExit() {
    super.onExit();
    if (this._effected.isPlayer()) {
      Player player = (Player)this._effected;
      if (this.getSkill() instanceof Transformation) {
        player.setTransformationName((String)null);
      }

      if (this.isFlyingTransform) {
        boolean isVisible = player.isVisible();
        player.decayMe();
        player.setFlying(false);
        player.setLoc(player.getLoc().correctGeoZ());
        player.setTransformation(0);
        if (isVisible) {
          player.spawnMe();
        }
      } else {
        player.setTransformation(0);
      }
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
