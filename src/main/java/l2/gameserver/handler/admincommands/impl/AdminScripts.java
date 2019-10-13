//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Scripts;

public class AdminScripts implements IAdminCommandHandler {
  public AdminScripts() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminScripts.Commands command = (AdminScripts.Commands)comm;
    if (!activeChar.isGM()) {
      return false;
    } else {
      switch(command) {
        case admin_scripts_reload:
        case admin_sreload:
          if (wordList.length < 2) {
            return false;
          }

          String param = wordList[1];
          if (param.equalsIgnoreCase("all")) {
            activeChar.sendMessage("Scripts reload starting...");
            if (!Scripts.getInstance().reload()) {
              activeChar.sendMessage("Scripts reloaded with errors. Loaded " + Scripts.getInstance().getClasses().size() + " classes.");
            } else {
              activeChar.sendMessage("Scripts successfully reloaded. Loaded " + Scripts.getInstance().getClasses().size() + " classes.");
            }
          } else if (!Scripts.getInstance().reload(param)) {
            activeChar.sendMessage("Script(s) reloaded with errors.");
          } else {
            activeChar.sendMessage("Script(s) successfully reloaded.");
          }
          break;
        case admin_sqreload:
          if (wordList.length < 2) {
            return false;
          }

          String quest = wordList[1];
          if (!Scripts.getInstance().reload("quests/" + quest)) {
            activeChar.sendMessage("Quest \"" + quest + "\" reloaded with errors.");
          } else {
            activeChar.sendMessage("Quest \"" + quest + "\" successfully reloaded.");
          }

          this.reloadQuestStates(activeChar);
      }

      return true;
    }
  }

  private void reloadQuestStates(Player p) {
    QuestState[] var2 = p.getAllQuestsStates();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      QuestState qs = var2[var4];
      p.removeQuestState(qs.getQuest().getName());
    }

    Quest.restoreQuestStates(p);
  }

  public Enum[] getAdminCommandEnum() {
    return AdminScripts.Commands.values();
  }

  private static enum Commands {
    admin_scripts_reload,
    admin_sreload,
    admin_sqreload;

    private Commands() {
    }
  }
}
