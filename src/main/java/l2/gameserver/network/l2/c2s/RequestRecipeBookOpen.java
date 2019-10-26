//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;

public class RequestRecipeBookOpen extends L2GameClientPacket {
  private boolean isDwarvenCraft;

  public RequestRecipeBookOpen() {
  }

  protected void readImpl() {
    if (this._buf.hasRemaining()) {
      this.isDwarvenCraft = this.readD() == 0;
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      this.sendPacket(new RecipeBookItemList(activeChar, this.isDwarvenCraft));
    }
  }
}
