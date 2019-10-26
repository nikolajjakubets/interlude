//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestHandOverPartyMaster extends L2GameClientPacket {
  private String _name;

  public RequestHandOverPartyMaster() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Party party = activeChar.getParty();
      if (party != null && activeChar.getParty().isLeader(activeChar)) {
        Player member = party.getPlayerByName(this._name);
        if (member == activeChar) {
          activeChar.sendPacket(Msg.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF);
        } else if (member == null) {
          activeChar.sendPacket(Msg.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
        } else {
          activeChar.getParty().changePartyLeader(member);
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
