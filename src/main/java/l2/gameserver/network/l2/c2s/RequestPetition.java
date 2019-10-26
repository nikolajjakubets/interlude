//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public final class RequestPetition extends L2GameClientPacket {
  private String _content;
  private int _type;

  public RequestPetition() {
  }

  protected void readImpl() {
    this._content = this.readS();
    this._type = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      PetitionManager.getInstance().handle(player, this._type, this._content);
    }
  }
}
