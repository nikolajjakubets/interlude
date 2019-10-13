//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;

public interface OnTeleportListener extends PlayerListener {
  void onTeleport(Player var1, int var2, int var3, int var4, Reflection var5);
}
