//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminIP implements IAdminCommandHandler {
  public AdminIP() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminIP.Commands command = (AdminIP.Commands)comm;
    if (!activeChar.getPlayerAccess().CanBan) {
      return false;
    } else {
      switch(command) {
        case admin_charip:
          if (wordList.length != 2) {
            activeChar.sendMessage("Command syntax: //charip <char_name>");
            activeChar.sendMessage(" Gets character's IP.");
          } else {
            Player pl = World.getPlayer(wordList[1]);
            if (pl == null) {
              activeChar.sendMessage("Character " + wordList[1] + " not found.");
            } else {
              String ip_adr = pl.getIP();
              if (ip_adr.equalsIgnoreCase("<not connected>")) {
                activeChar.sendMessage("Character " + wordList[1] + " not found.");
              } else {
                activeChar.sendMessage("Character's IP: " + ip_adr);
              }
            }
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminIP.Commands.values();
  }

  private static enum Commands {
    admin_charip;

    private Commands() {
    }
  }
}
