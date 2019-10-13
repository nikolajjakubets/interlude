//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.boat.Boat;

public class BoatAI extends CharacterAI {
  public BoatAI(Creature actor) {
    super(actor);
  }

  protected void onEvtArrived() {
    Boat actor = (Boat)this.getActor();
    if (actor != null) {
      actor.onEvtArrived();
    }
  }

  public boolean isGlobalAI() {
    return true;
  }
}
