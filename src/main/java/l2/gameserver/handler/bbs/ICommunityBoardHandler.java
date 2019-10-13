//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.bbs;

import l2.gameserver.model.Player;

public interface ICommunityBoardHandler {
  String[] getBypassCommands();

  void onBypassCommand(Player var1, String var2);

  void onWriteCommand(Player var1, String var2, String var3, String var4, String var5, String var6, String var7);
}
