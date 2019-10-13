//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;

public class GotoLobby extends L2GameClientPacket {
  public GotoLobby() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    CharacterSelectionInfo cl = new CharacterSelectionInfo(((GameClient)this.getClient()).getLogin(), ((GameClient)this.getClient()).getSessionKey().playOkID1);
    this.sendPacket(cl);
  }
}
