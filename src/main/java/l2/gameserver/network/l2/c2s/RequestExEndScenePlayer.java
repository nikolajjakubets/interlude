//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestExEndScenePlayer extends L2GameClientPacket {
  private int _movieId;

  public RequestExEndScenePlayer() {
  }

  protected void readImpl() {
    this._movieId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isInMovie() && activeChar.getMovieId() == this._movieId) {
        activeChar.setIsInMovie(false);
        activeChar.setMovieId(0);
        activeChar.decayMe();
        activeChar.spawnMe();
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
