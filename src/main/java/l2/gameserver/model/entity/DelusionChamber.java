//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import java.util.Iterator;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class DelusionChamber extends DimensionalRift {
  private Future<?> killRiftTask;

  public DelusionChamber(Party party, int type, int room) {
    super(party, type, room);
  }

  public synchronized void createNewKillRiftTimer() {
    if (this.killRiftTask != null) {
      this.killRiftTask.cancel(false);
      this.killRiftTask = null;
    }

    this.killRiftTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (DelusionChamber.this.getParty() != null && !DelusionChamber.this.getParty().getPartyMembers().isEmpty()) {
          Iterator var1 = DelusionChamber.this.getParty().getPartyMembers().iterator();

          while(var1.hasNext()) {
            Player p = (Player)var1.next();
            if (p.getReflection() == DelusionChamber.this) {
              String var = p.getVar("backCoords");
              if (var != null && !var.equals("")) {
                p.teleToLocation(Location.parseLoc(var), ReflectionManager.DEFAULT);
                p.unsetVar("backCoords");
              }
            }
          }
        }

        DelusionChamber.this.collapse();
      }
    }, 100L);
  }

  public void partyMemberExited(Player player) {
    if (this.getPlayersInside(false) < 2 || this.getPlayersInside(true) == 0) {
      this.createNewKillRiftTimer();
    }
  }

  public void manualExitRift(Player player, NpcInstance npc) {
    if (player.isInParty() && player.getParty().getReflection() == this) {
      if (!player.getParty().isLeader(player)) {
        DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
      } else {
        this.createNewKillRiftTimer();
      }
    }
  }

  public String getName() {
    InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(this._roomType + 120);
    return iz.getName();
  }

  protected int getManagerId() {
    return 32664;
  }
}
