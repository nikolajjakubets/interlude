//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.templates.npc.NpcTemplate;

public class BossInstance extends RaidBossInstance {
  private boolean _teleportedToNest;

  public BossInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean isBoss() {
    return true;
  }

  public final boolean isMovementDisabled() {
    return this.getNpcId() == 29006 || super.isMovementDisabled();
  }

  public void setTeleported(boolean flag) {
    this._teleportedToNest = flag;
  }

  public boolean isTeleported() {
    return this._teleportedToNest;
  }

  public boolean hasRandomAnimation() {
    return false;
  }
}
