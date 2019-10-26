//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.Config;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetConfig implements TelnetCommandHolder {
  private Set<TelnetCommand> _commands = new LinkedHashSet();

  public TelnetConfig() {
    this._commands.add(new TelnetCommand("config", new String[]{"cfg"}) {
      public String getUsage() {
        return "config parameter[=value]";
      }

      public String handle(String[] args) {
        if (args.length != 0 && !args[0].isEmpty()) {
          String[] val = args[0].split("=");
          if (val.length == 1) {
            String value = Config.getField(args[0]);
            return value == null ? "Not found.\n" : value + "\n";
          } else {
            return Config.setField(val[0], val[1]) ? "Done.\n" : "Error!\n";
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
