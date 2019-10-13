//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.scripts.Functions;

public class Help extends Functions implements IVoicedCommandHandler {
  private String[] _commandList = new String[]{"exp", "whereis", "help"};

  public Help() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String args) {
    command = command.intern();
    if (command.equalsIgnoreCase("help")) {
      return this.help(command, activeChar, args);
    } else if (command.equalsIgnoreCase("whereis")) {
      return this.whereis(command, activeChar, args);
    } else {
      return command.equalsIgnoreCase("exp") ? this.exp(command, activeChar, args) : false;
    }
  }

  private boolean exp(String command, Player activeChar, String args) {
    if (activeChar.getLevel() >= (activeChar.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel())) {
      activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.MaxLevel", activeChar, new Object[0]));
    } else {
      long exp = Experience.LEVEL[activeChar.getLevel() + 1] - activeChar.getExp();
      activeChar.sendMessage((new CustomMessage("voicedcommandhandlers.Help.ExpLeft", activeChar, new Object[0])).addNumber(exp));
    }

    return true;
  }

  private boolean whereis(String command, Player activeChar, String args) {
    Player friend = World.getPlayer(args);
    if (friend == null) {
      return false;
    } else if (friend.getParty() != activeChar.getParty() && friend.getClan() != activeChar.getClan()) {
      return false;
    } else {
      RadarControl rc = new RadarControl(0, 1, friend.getLoc());
      activeChar.sendPacket(rc);
      return true;
    }
  }

  private boolean help(String command, Player activeChar, String args) {
    String dialog = HtmCache.getInstance().getNotNull("command/help.htm", activeChar);
    Functions.show(dialog, activeChar, (NpcInstance)null, new Object[0]);
    return true;
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }
}
