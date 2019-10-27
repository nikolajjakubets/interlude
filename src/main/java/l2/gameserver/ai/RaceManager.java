//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaceManagerInstance;
import l2.gameserver.network.l2.s2c.MonRaceInfo;

public class RaceManager extends DefaultAI {
  private boolean thinking = false;
  private List<Player> _knownPlayers = new ArrayList<>();

  public RaceManager(NpcInstance actor) {
    super(actor);
    this.AI_TASK_ATTACK_DELAY = 5000L;
  }

  public void runImpl() throws Exception {
    this.onEvtThink();
  }

  protected void onEvtThink() {
    RaceManagerInstance actor = this.getActor();
    if (actor != null) {
      MonRaceInfo packet = actor.getPacket();
      if (packet != null) {
        synchronized(this) {
          if (this.thinking) {
            return;
          }

          this.thinking = true;
        }

        try {
          List<Player> newPlayers = new ArrayList<>();
          Iterator var4 = World.getAroundPlayers(actor, 1200, 200).iterator();

          Player player;
          while(var4.hasNext()) {
            player = (Player)var4.next();
            if (player != null) {
              newPlayers.add(player);
              if (!this._knownPlayers.contains(player)) {
                player.sendPacket(packet);
              }

              this._knownPlayers.remove(player);
            }
          }

          var4 = this._knownPlayers.iterator();

          while(var4.hasNext()) {
            player = (Player)var4.next();
            actor.removeKnownPlayer(player);
          }

          this._knownPlayers = newPlayers;
        } finally {
          this.thinking = false;
        }
      }
    }
  }

  public RaceManagerInstance getActor() {
    return (RaceManagerInstance)super.getActor();
  }
}
