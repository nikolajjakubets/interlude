//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands;

import l2.gameserver.model.Player;

public interface IVoicedCommandHandler {
  boolean useVoicedCommand(String var1, Player var2, String var3);

  String[] getVoicedCommandList();
}
