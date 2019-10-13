//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.templates.npc.NpcTemplate;

public final class TrainerInstance extends NpcInstance {
  public TrainerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom = "";
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return "trainer/" + pom + ".htm";
  }
}
