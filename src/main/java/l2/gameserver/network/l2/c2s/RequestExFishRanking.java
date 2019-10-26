//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestExFishRanking extends L2GameClientPacket {
  public RequestExFishRanking() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
        FishingChampionShipManager.getInstance().showMidResult(((GameClient)this.getClient()).getActiveChar());
      }

    }
  }
}
