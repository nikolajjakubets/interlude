//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.network.l2.GameClient;

public class RequestOlympiadMatchList extends L2GameClientPacket {
  public RequestOlympiadMatchList() {
  }

  protected void readImpl() throws Exception {
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      CompetitionController.getInstance().showCompetitionList(player);
    }
  }
}
