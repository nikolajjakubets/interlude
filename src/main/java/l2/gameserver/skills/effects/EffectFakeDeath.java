//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ChangeWaitType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;

public final class EffectFakeDeath extends Effect {
  public static final int FAKE_DEATH_OFF = 0;
  public static final int FAKE_DEATH_ON = 1;
  public static final int FAKE_DEATH_FAILED = 2;
  private final int _failChance;

  public EffectFakeDeath(Env env, EffectTemplate template) {
    super(env, template);
    this._failChance = template.getParam().getInteger("failChance", 0);
  }

  public void onStart() {
    super.onStart();
    Player player = (Player)this.getEffected();
    player.abortAttack(true, false);
    if (player.isMoving()) {
      player.stopMove();
    }

    if (this._failChance > 0 && Rnd.chance(this._failChance)) {
      player.setFakeDeath(2);
    } else {
      player.setFakeDeath(1);
      player.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH, (Object)null, (Object)null);
    }

    player.broadcastPacket(new L2GameServerPacket[]{new ChangeWaitType(player, 2)});
    player.broadcastCharInfo();
  }

  public void onExit() {
    super.onExit();
    Player player = (Player)this.getEffected();
    player.setNonAggroTime(System.currentTimeMillis() + 5000L);
    player.setFakeDeath(0);
    player.broadcastPacket(new L2GameServerPacket[]{new ChangeWaitType(player, 3)});
    player.broadcastPacket(new L2GameServerPacket[]{new Revive(player)});
    player.broadcastCharInfo();
  }

  public boolean onActionTime() {
    if (this.getEffected().isDead()) {
      return false;
    } else {
      double manaDam = this.calc();
      if (manaDam > this.getEffected().getCurrentMp() && this.getSkill().isToggle()) {
        this.getEffected().sendPacket(Msg.NOT_ENOUGH_MP);
        this.getEffected().sendPacket((new SystemMessage(749)).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
        return false;
      } else {
        this.getEffected().reduceCurrentMp(manaDam, (Creature)null);
        return true;
      }
    }
  }
}
