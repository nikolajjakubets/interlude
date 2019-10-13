//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands;

import l2.gameserver.model.Player;

public interface IUserCommandHandler {
  boolean useUserCommand(int var1, Player var2);

  int[] getUserCommandList();
}
