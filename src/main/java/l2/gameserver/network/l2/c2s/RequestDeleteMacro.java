//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestDeleteMacro extends L2GameClientPacket {
  private int _id;

  public RequestDeleteMacro() {
  }

  protected void readImpl() {
    this._id = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.deleteMacro(this._id);
    }
  }
}
