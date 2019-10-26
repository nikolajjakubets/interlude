//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestPartyLootModification extends L2GameClientPacket {
  private byte _mode;

  public RequestPartyLootModification() {
  }

  protected void readImpl() {
    this._mode = (byte)this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._mode >= 0 && this._mode <= 4) {
        Party party = activeChar.getParty();
        if (party != null && this._mode != party.getLootDistribution() && party.getPartyLeader() == activeChar) {
          party.requestLootChange(this._mode);
        }
      }
    }
  }
}
