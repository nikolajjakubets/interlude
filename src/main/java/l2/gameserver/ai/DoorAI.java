//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import java.util.Iterator;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;

public class DoorAI extends CharacterAI {
  public DoorAI(DoorInstance actor) {
    super(actor);
  }

  public void onEvtTwiceClick(Player player) {
  }

  public void onEvtOpen(Player player) {
  }

  public void onEvtClose(Player player) {
  }

  public DoorInstance getActor() {
    return (DoorInstance)super.getActor();
  }

  protected void onEvtAttacked(Creature attacker, int damage) {
    DoorInstance actor;
    if (attacker != null && (actor = this.getActor()) != null) {
      Player player = attacker.getPlayer();
      if (player != null) {
        SiegeEvent<?, ?> siegeEvent1 = (SiegeEvent)player.getEvent(SiegeEvent.class);
        SiegeEvent<?, ?> siegeEvent2 = (SiegeEvent)actor.getEvent(SiegeEvent.class);
        if (siegeEvent1 == null || siegeEvent1 == siegeEvent2 && siegeEvent1.getSiegeClan("attackers", player.getClan()) != null) {
          Iterator var7 = actor.getAroundNpc(900, 200).iterator();

          while(var7.hasNext()) {
            NpcInstance npc = (NpcInstance)var7.next();
            if (npc.isSiegeGuard()) {
              if (Rnd.chance(20)) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10000);
              } else {
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2000);
              }
            }
          }
        }

      }
    }
  }
}
