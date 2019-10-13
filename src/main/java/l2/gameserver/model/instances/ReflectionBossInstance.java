//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.templates.npc.NpcTemplate;

public class ReflectionBossInstance extends RaidBossInstance {
  private static final int COLLAPSE_AFTER_DEATH_TIME = 5;

  public ReflectionBossInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  protected void onDeath(Creature killer) {
    this.getMinionList().unspawnMinions();
    super.onDeath(killer);
    this.clearReflection();
  }

  protected void clearReflection() {
    this.getReflection().clearReflection(5, true);
  }
}
