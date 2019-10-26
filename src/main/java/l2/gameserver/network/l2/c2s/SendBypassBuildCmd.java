//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class SendBypassBuildCmd extends L2GameClientPacket {
  private String _command;

  public SendBypassBuildCmd() {
  }

  protected void readImpl() {
    this._command = this.readS();
    if (this._command != null) {
      this._command = this._command.trim();
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      String cmd = this._command;
      if (!cmd.contains("admin_")) {
        cmd = "admin_" + cmd;
      }

      AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, cmd);
    }
  }
}
