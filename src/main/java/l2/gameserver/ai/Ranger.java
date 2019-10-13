//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Ranger extends DefaultAI {
  public Ranger(NpcInstance actor) {
    super(actor);
  }

  protected boolean thinkActive() {
    return super.thinkActive() || this.defaultThinkBuff(10);
  }

  protected void onEvtAttacked(Creature attacker, int damage) {
    super.onEvtAttacked(attacker, damage);
    NpcInstance actor = this.getActor();
    if (!actor.isDead() && attacker != null && actor.getDistance(attacker) <= 200.0D) {
      if (!actor.isMoving()) {
        int posX = actor.getX();
        int posY = actor.getY();
        int posZ = actor.getZ();
        int old_posX = posX;
        int old_posY = posY;
        int old_posZ = posZ;
        int signx = posX < attacker.getX() ? -1 : 1;
        int signy = posY < attacker.getY() ? -1 : 1;
        int range = (int)(0.71D * (double)actor.calculateAttackDelay() / 1000.0D * (double)actor.getMoveSpeed());
        posX += signx * range;
        posY += signy * range;
        posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getGeoIndex());
        if (GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, actor.getGeoIndex())) {
          this.addTaskMove(posX, posY, posZ, false);
          this.addTaskAttack(attacker);
        }

      }
    }
  }

  protected boolean createNewTask() {
    return this.defaultFightTask();
  }

  public int getRatePHYS() {
    return 10;
  }

  public int getRateDOT() {
    return 15;
  }

  public int getRateDEBUFF() {
    return 8;
  }

  public int getRateDAM() {
    return 20;
  }

  public int getRateSTUN() {
    return 15;
  }

  public int getRateBUFF() {
    return 3;
  }

  public int getRateHEAL() {
    return 20;
  }
}
