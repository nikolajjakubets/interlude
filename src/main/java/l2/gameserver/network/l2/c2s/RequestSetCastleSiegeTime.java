//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.CastleSiegeInfo;

public class RequestSetCastleSiegeTime extends L2GameClientPacket {
  private int _id;
  private int _time;

  public RequestSetCastleSiegeTime() {
  }

  protected void readImpl() {
    this._id = this.readD();
    this._time = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Castle castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._id);
      if (castle != null) {
        if (player.getClan().getCastle() == castle.getId()) {
          if ((player.getClanPrivileges() & 131072) != 131072) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
          } else {
            CastleSiegeEvent siegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
            siegeEvent.setNextSiegeTime(this._time);
            player.sendPacket(new CastleSiegeInfo(castle, player));
          }
        }
      }
    }
  }
}
