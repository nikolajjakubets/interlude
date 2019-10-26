//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.Henna;

public class RequestHennaEquip extends L2GameClientPacket {
  private int _symbolId;

  public RequestHennaEquip() {
  }

  protected void readImpl() {
    this._symbolId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Henna temp = HennaHolder.getInstance().getHenna(this._symbolId);
      if (temp != null && temp.isForThisClass(player)) {
        long adena = player.getAdena();
        long countDye = player.getInventory().getCountOf(temp.getDyeId());
        if (countDye >= temp.getDrawCount() && adena >= temp.getPrice()) {
          if (player.consumeItem(temp.getDyeId(), temp.getDrawCount()) && player.reduceAdena(temp.getPrice())) {
            player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
            player.addHenna(temp);
          }
        } else {
          player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
        }

      } else {
        player.sendPacket(Msg.THE_SYMBOL_CANNOT_BE_DRAWN);
      }
    }
  }
}
