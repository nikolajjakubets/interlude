//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;

public class AdminNochannel implements IAdminCommandHandler {
  public AdminNochannel() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminNochannel.Commands command = (AdminNochannel.Commands)comm;
    if (!activeChar.getPlayerAccess().CanBanChat) {
      return false;
    } else {
      int banChatCount = 0;
      int penaltyCount = 0;
      int banChatCountPerDay = activeChar.getPlayerAccess().BanChatCountPerDay;
      String msg;
      if (banChatCountPerDay > 0) {
        String count = activeChar.getVar("banChatCount");
        if (count != null) {
          banChatCount = Integer.parseInt(count);
        }

        msg = activeChar.getVar("penaltyChatCount");
        if (msg != null) {
          penaltyCount = Integer.parseInt(msg);
        }

        long LastBanChatDayTime = 0L;
        String time = activeChar.getVar("LastBanChatDayTime");
        if (time != null) {
          LastBanChatDayTime = Long.parseLong(time);
        }

        if (LastBanChatDayTime != 0L) {
          if (System.currentTimeMillis() - LastBanChatDayTime < 86400000L) {
            if (banChatCount >= banChatCountPerDay) {
              activeChar.sendMessage("В сутки, вы можете выдать не более " + banChatCount + " банов чата.");
              return false;
            }
          } else {
            int bonus_mod = banChatCount / 10;
            bonus_mod = Math.max(1, bonus_mod);
            int bonus_mod = 1;
            if (activeChar.getPlayerAccess().BanChatBonusId > 0 && activeChar.getPlayerAccess().BanChatBonusCount > 0) {
              int add_count = activeChar.getPlayerAccess().BanChatBonusCount * bonus_mod;
              ItemTemplate item = ItemHolder.getInstance().getTemplate(activeChar.getPlayerAccess().BanChatBonusId);
              activeChar.sendMessage("Бонус за модерирование: " + add_count + " " + item.getName());
              if (penaltyCount > 0) {
                activeChar.sendMessage("Штраф за нарушения: " + penaltyCount + " " + item.getName());
                activeChar.setVar("penaltyChatCount", "" + Math.max(0, penaltyCount - add_count), -1L);
                add_count -= penaltyCount;
              }

              if (add_count > 0) {
                ItemFunctions.addItem(activeChar, activeChar.getPlayerAccess().BanChatBonusId, (long)add_count, true);
              }
            }

            activeChar.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1L);
            activeChar.setVar("banChatCount", "0", -1L);
            banChatCount = 0;
          }
        } else {
          activeChar.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1L);
        }
      }

      switch(command) {
        case admin_nochannel:
        case admin_nc:
          if (wordList.length < 2) {
            activeChar.sendMessage("USAGE: //nochannel charName [period] [reason]");
            return false;
          } else {
            int timeval = 30;
            if (wordList.length > 2) {
              try {
                timeval = Integer.parseInt(wordList[2]);
              } catch (Exception var17) {
                timeval = 30;
              }
            }

            msg = AdminFunctions.banChat(activeChar, (String)null, wordList[1], timeval, wordList.length > 3 ? Util.joinStrings(" ", wordList, 3) : null);
            activeChar.sendMessage(msg);
            if (banChatCountPerDay > -1 && msg.startsWith("Вы забанили чат")) {
              ++banChatCount;
              activeChar.setVar("banChatCount", "" + banChatCount, -1L);
              activeChar.sendMessage("У вас осталось " + (banChatCountPerDay - banChatCount) + " банов чата.");
            }
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminNochannel.Commands.values();
  }

  private static enum Commands {
    admin_nochannel,
    admin_nc;

    private Commands() {
    }
  }
}
