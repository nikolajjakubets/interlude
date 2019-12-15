//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowQuestInfo;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdventurerInstance extends NpcInstance {

  public AdventurerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      int bossLevel;
      if (command.startsWith("npcfind_byid")) {
        try {
          bossLevel = Integer.parseInt(command.substring(12).trim());
          switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossLevel)) {
            case ALIVE:
            case DEAD:
              Spawner spawn = RaidBossSpawnManager.getInstance().getSpawnTable().get(bossLevel);
              Location loc = spawn.getCurrentSpawnRange().getRandomLoc(spawn.getReflection().getGeoIndex());
              player.sendPacket(new RadarControl(2, 2, loc), new RadarControl(0, 1, loc));
              break;
            case UNDEFINED:
              player.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2AdventurerInstance.BossNotInGame", player)).addNumber(bossLevel));
          }
        } catch (NumberFormatException var6) {
          log.warn("AdventurerInstance: Invalid Bypass to Server command parameter.");
        }
      } else if (command.startsWith("raidInfo")) {
        bossLevel = Integer.parseInt(command.substring(9).trim());
        String filename = "adventurer_guildsman/raid_info/info.htm";
        if (bossLevel != 0) {
          filename = "adventurer_guildsman/raid_info/level" + bossLevel + ".htm";
        }

        this.showChatWindow(player, filename);
      } else if (command.equalsIgnoreCase("questlist")) {
        player.sendPacket(ExShowQuestInfo.STATIC);
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return "adventurer_guildsman/" + pom + ".htm";
  }
}
