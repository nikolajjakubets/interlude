//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SSQStatus;

public class RequestSSQStatus extends L2GameClientPacket {
  private int _page;

  public RequestSSQStatus() {
  }

  protected void readImpl() {
    this._page = this.readC();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (!SevenSigns.getInstance().isSealValidationPeriod() && !SevenSigns.getInstance().isCompResultsPeriod() || this._page != 4) {
        activeChar.sendPacket(new SSQStatus(activeChar, this._page));
      }
    }
  }
}
