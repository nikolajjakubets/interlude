//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.HennaItemInfo;
import l2.gameserver.templates.Henna;

public class RequestHennaItemInfo extends L2GameClientPacket {
  private int _symbolId;

  public RequestHennaItemInfo() {
  }

  protected void readImpl() {
    this._symbolId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Henna henna = HennaHolder.getInstance().getHenna(this._symbolId);
      if (henna != null) {
        player.sendPacket(new HennaItemInfo(henna, player));
      }

    }
  }
}
