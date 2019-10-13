//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class Online extends Functions implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"online"};

  public Online() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player player, String args) {
    if (!Config.SERVICES_ONLINE_COMMAND_ENABLE && !player.isGM()) {
      return true;
    } else {
      player.sendMessage((new CustomMessage("scripts.commands.user.online.service", player, new Object[0])).addNumber(Math.round((double)GameObjectsStorage.getAllPlayersCount() * Config.SERVICE_COMMAND_MULTIPLIER)));
      return false;
    }
  }
}
