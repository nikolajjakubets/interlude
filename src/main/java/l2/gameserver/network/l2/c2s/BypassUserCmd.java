//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.handler.usercommands.UserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;

public class BypassUserCmd extends L2GameClientPacket {
  private int _command;

  public BypassUserCmd() {
  }

  protected void readImpl() {
    this._command = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(this._command);
      if (handler == null) {
        activeChar.sendMessage((new CustomMessage("common.S1NotImplemented", activeChar, new Object[0])).addString(String.valueOf(this._command)));
      } else {
        handler.useUserCommand(this._command, activeChar);
      }

    }
  }
}
