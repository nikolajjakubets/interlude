//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;

public class ServerInfo extends Functions implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"date", "time"};
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

  public ServerInfo() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player activeChar, String target) {
    if (!command.equals("date") && !command.equals("time")) {
      return false;
    } else {
      activeChar.sendMessage(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
      return true;
    }
  }
}
