//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ManagePledgePower;

public class RequestPledgePower extends L2GameClientPacket {
  private int _rank;
  private int _action;
  private int _privs;

  public RequestPledgePower() {
  }

  protected void readImpl() {
    this._rank = this.readD();
    this._action = this.readD();
    if (this._action == 2) {
      this._privs = this.readD();
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._action == 2) {
        if (this._rank < 1 || this._rank > 9) {
          return;
        }

        if (activeChar.getClan() != null && (activeChar.getClanPrivileges() & 16) == 16) {
          if (this._rank == 9) {
            this._privs = (this._privs & 8) + (this._privs & 1024) + (this._privs & '耀') + (this._privs & 2048) + (this._privs & 262144);
          }

          activeChar.getClan().setRankPrivs(this._rank, this._privs);
          activeChar.getClan().updatePrivsForRank(this._rank);
        }
      } else if (activeChar.getClan() != null) {
        activeChar.sendPacket(new ManagePledgePower(activeChar, this._action, this._rank));
      } else {
        activeChar.sendActionFailed();
      }

    }
  }
}
