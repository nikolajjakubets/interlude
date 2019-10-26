//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestRecipeShopMessageSet extends L2GameClientPacket {
  private String _name;

  public RequestRecipeShopMessageSet() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setManufactureName(this._name);
    }
  }
}
