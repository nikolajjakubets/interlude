//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.bypass;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public interface IBypassHandler {
  String[] getBypasses();

  void onBypassFeedback(NpcInstance var1, Player var2, String var3);
}
