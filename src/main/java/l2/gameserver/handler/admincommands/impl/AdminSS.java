//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;

public class AdminSS implements IAdminCommandHandler {
  public AdminSS() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminSS.Commands command = (AdminSS.Commands)comm;
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      int period;
      int cabal;
      switch(command) {
        case admin_ssq_change:
          if (wordList.length > 2) {
            period = Integer.parseInt(wordList[1]);
            cabal = Integer.parseInt(wordList[2]);
            SevenSigns.getInstance().changePeriod(period, cabal * 60);
          } else if (wordList.length > 1) {
            period = Integer.parseInt(wordList[1]);
            SevenSigns.getInstance().changePeriod(period);
          } else {
            SevenSigns.getInstance().changePeriod();
          }
          break;
        case admin_ssq_time:
          if (wordList.length > 1) {
            period = Integer.parseInt(wordList[1]);
            SevenSigns.getInstance().setTimeToNextPeriodChange(period);
          }
          break;
        case admin_ssq_cabal:
          if (wordList.length > 3) {
            period = Integer.parseInt(wordList[1]);
            cabal = Integer.parseInt(wordList[2]);
            int seal = Integer.parseInt(wordList[3]);
            SevenSigns.getInstance().setPlayerInfo(period, cabal, seal);
          }
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminSS.Commands.values();
  }

  private static enum Commands {
    admin_ssq_change,
    admin_ssq_time,
    admin_ssq_cabal;

    private Commands() {
    }
  }
}
