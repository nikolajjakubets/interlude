//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.AutoBan;

public class TelnetBan implements TelnetCommandHolder {
  private Set<TelnetCommand> _commands = new LinkedHashSet();

  public TelnetBan() {
    this._commands.add(new TelnetCommand("kick") {
      public String getUsage() {
        return "kick <name>";
      }

      public String handle(String[] args) {
        if (args.length != 0 && !args[0].isEmpty()) {
          return AdminFunctions.kick(args[0], "telnet") ? "Player kicked.\n" : "Player not found.\n";
        } else {
          return null;
        }
      }
    });
    this._commands.add(new TelnetCommand("chat_ban") {
      public String getUsage() {
        return "chat_ban <name> <period>";
      }

      public String handle(String[] args) {
        if (args.length != 0 && !args[0].isEmpty()) {
          int period = args.length > 1 && !args[1].isEmpty() ? Integer.parseInt(args[1]) : -1;
          return AdminFunctions.banChat((Player)null, "GMTelnet", args[0], period, "telnet banned") + "\n";
        } else {
          return null;
        }
      }
    });
    this._commands.add(new TelnetCommand("char_ban") {
      public String getUsage() {
        return "char_ban <name> <days>";
      }

      public String handle(String[] args) {
        if (args.length != 0 && !args[0].isEmpty()) {
          String playerName = args[0];
          int period = args.length > 1 && !args[1].isEmpty() ? Integer.parseInt(args[1]) : -1;
          if (period == 0) {
            return !AutoBan.Banned(playerName, 0, 0, "unban", "telnet") ? "Can't unban \"" + playerName + "\".\n" : "\"" + playerName + "\" unbanned.\n";
          } else if (!AutoBan.Banned(playerName, -100, period, "unban", "telnet")) {
            return "Can't ban \"" + playerName + "\".\n";
          } else {
            Player player = World.getPlayer(playerName);
            if (player != null) {
              player.kick();
            }

            return "\"" + playerName + "\" banned.\n";
          }
        } else {
          return null;
        }
      }
    });
  }

  public Set<TelnetCommand> getCommands() {
    return this._commands;
  }
}
