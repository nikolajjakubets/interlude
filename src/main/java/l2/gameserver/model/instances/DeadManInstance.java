//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance {
  public DeadManInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this.setAI(new CharacterAI(this));
  }

  protected void onSpawn() {
    super.onSpawn();
    this.setCurrentHp(0.0D, false);
    this.broadcastPacket(new L2GameServerPacket[]{new Die(this)});
    this.setWalking();
  }

  public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
  }

  public boolean isInvul() {
    return true;
  }

  public boolean isBlocked() {
    return true;
  }
}
