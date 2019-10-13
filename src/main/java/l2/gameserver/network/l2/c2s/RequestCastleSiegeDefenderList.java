//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.CastleSiegeDefenderList;

public class RequestCastleSiegeDefenderList extends L2GameClientPacket {
  private int _unitId;

  public RequestCastleSiegeDefenderList() {
  }

  protected void readImpl() {
    this._unitId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Castle castle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._unitId);
      if (castle != null && castle.getOwner() != null) {
        player.sendPacket(new CastleSiegeDefenderList(castle));
      }
    }
  }
}
