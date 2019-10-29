//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CharacterRestore extends L2GameClientPacket {
  private int _charSlot;

  public CharacterRestore() {
  }

  protected void readImpl() {
    this._charSlot = this.readD();
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();

    try {
      client.markRestoredChar(this._charSlot);
    } catch (Exception e) {
      log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
    }

    CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
    this.sendPacket(cl);
    client.setCharSelection(cl.getCharInfo());
  }
}
