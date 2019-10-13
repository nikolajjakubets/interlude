//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.templates.npc.NpcTemplate;

/** @deprecated */
@Deprecated
public class NoActionNpcInstance extends NpcInstance {
  public NoActionNpcInstance(int objectID, NpcTemplate template) {
    super(objectID, template);
  }

  public void onAction(Player player, boolean dontMove) {
    player.sendActionFailed();
  }
}
