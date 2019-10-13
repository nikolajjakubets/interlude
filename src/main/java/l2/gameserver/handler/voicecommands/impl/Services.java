//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class Services extends Functions implements IVoicedCommandHandler {
  private static String[] _voicedCommands = new String[]{"autoloot", "xpfreez", "ru", "en"};

  public Services() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String target) {
    command = command.intern();
    if (command.startsWith("autoloot") && target != null && target.length() > 1) {
      this.autoLoot(activeChar, target.startsWith("on"), target.startsWith("adena"));
      return true;
    } else {
      if (command.startsWith("xpfreez")) {
        if (target.startsWith("on")) {
          activeChar.setVar("NoExp", "1", -1L);
          activeChar.sendMessage(new CustomMessage("usercommandhandlers.ExpFreezed", activeChar, new Object[0]));
          return true;
        }

        if (target.startsWith("off")) {
          activeChar.unsetVar("NoExp");
          activeChar.sendMessage(new CustomMessage("usercommandhandlers.ExpNormal", activeChar, new Object[0]));
          return true;
        }
      } else {
        if (command.startsWith("ru") && Config.ALT_ALLOW_LANG_COMMAND) {
          activeChar.setVar("lang@", "ru", -1L);
          activeChar.sendMessage(new CustomMessage("usercommandhandlers.LangRu", activeChar, new Object[0]));
          return true;
        }

        if (command.startsWith("en") && Config.ALT_ALLOW_LANG_COMMAND) {
          activeChar.setVar("lang@", "en", -1L);
          activeChar.sendMessage(new CustomMessage("usercommandhandlers.LangEn", activeChar, new Object[0]));
          return true;
        }
      }

      return false;
    }
  }

  public void autoLoot(Player player, boolean on, boolean adena) {
    if (on && !adena) {
      player.setAutoLoot(on);
      if (Config.AUTO_LOOT_HERBS) {
        player.setAutoLootHerbs(on);
      }

      player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAll", player, new Object[0]));
    } else if (adena) {
      player.setAutoLoot(false);
      player.setAutoLootAdena(adena);
      player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAdena", player, new Object[0]));
    } else {
      player.setAutoLootAdena(false);
      player.setAutoLoot(false);
      player.setAutoLootHerbs(false);
      player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootOff", player, new Object[0]));
    }

  }

  public String[] getVoicedCommandList() {
    return _voicedCommands;
  }
}
