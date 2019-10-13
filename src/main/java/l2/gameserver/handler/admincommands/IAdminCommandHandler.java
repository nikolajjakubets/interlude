//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands;

import l2.gameserver.model.Player;

public interface IAdminCommandHandler {
  boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4);

  Enum[] getAdminCommandEnum();
}
