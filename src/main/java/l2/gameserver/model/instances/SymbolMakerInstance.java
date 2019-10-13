//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.HennaEquipList;
import l2.gameserver.network.l2.s2c.HennaUnequipList;
import l2.gameserver.templates.npc.NpcTemplate;

public class SymbolMakerInstance extends NpcInstance {
  public SymbolMakerInstance(int objectID, NpcTemplate template) {
    super(objectID, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (command.equals("Draw")) {
        player.sendPacket(new HennaEquipList(player));
      } else if (command.equals("RemoveList")) {
        player.sendPacket(new HennaUnequipList(player));
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "SymbolMaker";
    } else {
      pom = "SymbolMaker-" + val;
    }

    return "symbolmaker/" + pom + ".htm";
  }
}
