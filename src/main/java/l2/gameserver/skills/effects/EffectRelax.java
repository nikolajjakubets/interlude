//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;

import java.util.Iterator;

public class EffectRelax extends Effect {
  private boolean _isWereSitting;

  public EffectRelax(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    Player player = this._effected.getPlayer();
    if (player == null) {
      return false;
    } else if (player.isMounted()) {
      player.sendPacket((new SystemMessage(113)).addSkillName(this._skill.getId(), this._skill.getLevel()));
      return false;
    } else {
      return super.checkCondition();
    }
  }

  public void onStart() {
    super.onStart();
    Player player = this._effected.getPlayer();
    if (player.isMoving()) {
      player.stopMove();
    }

    this._isWereSitting = player.isSitting();
    player.sitDown((StaticObjectInstance)null);
  }

  public void onExit() {
    super.onExit();
    Skill skill = this.getSkill();
    if (skill != null) {
      Iterator var2 = this._effected.getEffectList().getEffectsBySkill(skill).iterator();

      while(var2.hasNext()) {
        Effect other = (Effect)var2.next();
        if (other != this) {
          other.exit();
        }
      }
    }

    if (!this._isWereSitting) {
      this._effected.getPlayer().standUp();
    }

  }

  public boolean onActionTime() {
    Player player = this._effected.getPlayer();
    if (!player.isAlikeDead() && player != null) {
      if (!player.isSitting()) {
        return false;
      } else if (player.isCurrentHpFull() && this.getSkill().isToggle()) {
        this.getEffected().sendPacket(Msg.HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED);
        return false;
      } else {
        double manaDam = this.calc();
        if (manaDam > this._effected.getCurrentMp() && this.getSkill().isToggle()) {
          player.sendPacket(new IStaticPacket[]{Msg.NOT_ENOUGH_MP, (new SystemMessage(749)).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel())});
          return false;
        } else {
          this._effected.reduceCurrentMp(manaDam, (Creature)null);
          return true;
        }
      }
    } else {
      return false;
    }
  }
}
