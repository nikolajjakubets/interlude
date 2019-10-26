//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.Henna;

public class RequestHennaUnequip extends L2GameClientPacket {
  private int _symbolId;

  public RequestHennaUnequip() {
  }

  protected void readImpl() {
    this._symbolId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      for(int i = 1; i <= 3; ++i) {
        Henna henna = player.getHenna(i);
        if (henna != null && henna.getSymbolId() == this._symbolId) {
          long price = henna.getPrice() / 5L;
          if (player.getAdena() < price) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
          } else {
            player.reduceAdena(price);
            player.removeHenna(i);
            player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_DELETED);
          }
          break;
        }
      }

    }
  }
}
