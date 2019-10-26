//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.Announcements;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetSay implements TelnetCommandHolder {
  private Set<TelnetCommand> _commands = new LinkedHashSet();

  public TelnetSay() {
    this._commands.add(new TelnetCommand("announce", new String[]{"ann"}) {
      public String getUsage() {
        return "announce <text>";
      }

      public String handle(String[] args) {
        if (args.length == 0) {
          return null;
        } else {
          Announcements.getInstance().announceToAll(args[0]);
          return "Announcement sent.\n";
        }
      }
    });
    this._commands.add(new TelnetCommand("message", new String[]{"msg"}) {
      public String getUsage() {
        return "message <player> <text>";
      }

      public String handle(String[] args) {
        if (args.length < 2) {
          return null;
        } else {
          Player player = World.getPlayer(args[0]);
          if (player == null) {
            return "Player not found.\n";
          } else {
            Say2 cs = new Say2(0, ChatType.TELL, "[Admin]", args[1]);
            player.sendPacket(cs);
            return "Message sent.\n";
          }
        }
      }
    });
  }

  public Set<TelnetCommand> getCommands() {
    return this._commands;
  }
}
