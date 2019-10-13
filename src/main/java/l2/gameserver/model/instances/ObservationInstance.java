//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public final class ObservationInstance extends NpcInstance {
  public ObservationInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (!player.isOlyParticipant()) {
        if (command.startsWith("observeSiege")) {
          String val = command.substring(13);
          StringTokenizer st = new StringTokenizer(val);
          st.nextToken();
          List<Zone> zones = new ArrayList();
          World.getZones(zones, new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())), ReflectionManager.DEFAULT);
          Iterator var6 = zones.iterator();

          while(var6.hasNext()) {
            Zone z = (Zone)var6.next();
            if (z.getType() == ZoneType.SIEGE && z.isActive()) {
              this.doObserve(player, val);
              return;
            }
          }

          player.sendPacket(SystemMsg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
        } else if (command.startsWith("observe")) {
          this.doObserve(player, command.substring(8));
        } else {
          super.onBypassFeedback(player, command);
        }

      }
    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom = "";
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return "observation/" + pom + ".htm";
  }

  private void doObserve(Player player, String val) {
    StringTokenizer st = new StringTokenizer(val);
    int cost = Integer.parseInt(st.nextToken());
    int x = Integer.parseInt(st.nextToken());
    int y = Integer.parseInt(st.nextToken());
    int z = Integer.parseInt(st.nextToken());
    if (!player.reduceAdena((long)cost, true)) {
      player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
      player.sendActionFailed();
    } else {
      player.enterObserverMode(new Location(x, y, z));
    }
  }
}
