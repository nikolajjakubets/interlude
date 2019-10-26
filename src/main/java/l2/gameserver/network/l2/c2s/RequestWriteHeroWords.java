//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.network.l2.GameClient;

public class RequestWriteHeroWords extends L2GameClientPacket {
  private String _heroWords;

  public RequestWriteHeroWords() {
  }

  protected void readImpl() {
    this._heroWords = this.readS();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null && player.isHero()) {
      if (this._heroWords != null && this._heroWords.length() <= 300) {
        HeroController.getInstance().setHeroMessage(player.getObjectId(), this._heroWords);
      }
    }
  }
}
