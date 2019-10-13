//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.templates.npc.NpcTemplate;

public final class ArtefactInstance extends NpcInstance {
  public ArtefactInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this.setHasChatWindow(false);
  }

  public boolean isArtefact() {
    return true;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return false;
  }

  public boolean isAttackable(Creature attacker) {
    return false;
  }

  public boolean isInvul() {
    return true;
  }

  public boolean isFearImmune() {
    return true;
  }

  public boolean isParalyzeImmune() {
    return true;
  }

  public boolean isLethalImmune() {
    return true;
  }
}
