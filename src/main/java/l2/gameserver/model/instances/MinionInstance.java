//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class MinionInstance extends MonsterInstance {
  private MonsterInstance _master;

  public MinionInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void setLeader(MonsterInstance leader) {
    this._master = leader;
  }

  public MonsterInstance getLeader() {
    return this._master;
  }

  public boolean isRaidFighter() {
    return this.getLeader() != null && this.getLeader().isRaid();
  }

  protected void onDeath(Creature killer) {
    if (this.getLeader() != null) {
      this.getLeader().notifyMinionDied(this);
    }

    super.onDeath(killer);
  }

  protected void onDecay() {
    this.decayMe();
    this._spawnAnimation = 2;
  }

  public boolean isFearImmune() {
    return this.isRaidFighter();
  }

  public Location getSpawnedLoc() {
    return this.getLeader() != null ? this.getLeader().getLoc() : this.getLoc();
  }

  public boolean canChampion() {
    return false;
  }

  public boolean isMinion() {
    return true;
  }
}
