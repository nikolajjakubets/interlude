//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;

public class Banking extends Functions implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"deposit", "withdraw", "adena", "goldbar", "gb"};

  public Banking() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String args) {
    if (!Config.SERVICES_BANKING_ENABLED) {
      return false;
    } else {
      command = command.intern();
      if (!command.equalsIgnoreCase("deposit") && !command.equalsIgnoreCase("goldbar") && !command.equalsIgnoreCase("gb")) {
        return !command.equalsIgnoreCase("withdraw") && !command.equalsIgnoreCase("adena") ? false : this.withdraw(command, activeChar, args);
      } else {
        return this.deposit(command, activeChar, args);
      }
    }
  }

  public boolean deposit(String command, Player activeChar, String args) {
    if (getItemCount(activeChar, Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED) < (long)Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED) {
      activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
      return false;
    } else {
      removeItem(activeChar, Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED, (long)Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED);
      activeChar.sendMessage(new CustomMessage("services.Banking.DepositSuccessfully", activeChar, new Object[0]));
      addItem(activeChar, Config.SERVICES_DEPOSIT_ITEM_ID_GIVED, (long)Config.SERVICES_DEPOSIT_ITEM_COUNT_GIVED);
      return true;
    }
  }

  public boolean withdraw(String command, Player activeChar, String args) {
    if (getItemCount(activeChar, Config.SERVICES_WITHDRAW_ITEM_ID_NEEDED) < (long)Config.SERVICES_WITHDRAW_ITEM_COUNT_NEEDED) {
      activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
      return false;
    } else {
      removeItem(activeChar, Config.SERVICES_WITHDRAW_ITEM_ID_NEEDED, (long)Config.SERVICES_WITHDRAW_ITEM_COUNT_NEEDED);
      activeChar.sendMessage(new CustomMessage("services.Banking.WithdrawSuccessfully", activeChar, new Object[0]));
      addItem(activeChar, Config.SERVICES_WITHDRAW_ITEM_ID_GIVED, (long)Config.SERVICES_WITHDRAW_ITEM_COUNT_GIVED);
      return true;
    }
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }
}
