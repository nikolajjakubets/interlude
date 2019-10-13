//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.CastleSiegeAttackerList;

public class RequestCastleSiegeAttackerList extends L2GameClientPacket {
  private int _unitId;

  public RequestCastleSiegeAttackerList() {
  }

  protected void readImpl() {
    this._unitId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Residence residence = ResidenceHolder.getInstance().getResidence(this._unitId);
      if (residence != null) {
        this.sendPacket(new CastleSiegeAttackerList(residence));
      }

    }
  }
}
