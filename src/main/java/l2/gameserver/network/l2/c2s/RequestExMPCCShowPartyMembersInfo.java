//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExMPCCShowPartyMemberInfo;

public class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket {
  private int _objectId;

  public RequestExMPCCShowPartyMembersInfo() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && activeChar.isInParty() && activeChar.getParty().isInCommandChannel()) {
      Iterator var2 = activeChar.getParty().getCommandChannel().getParties().iterator();

      while(var2.hasNext()) {
        Party party = (Party)var2.next();
        Player leader = party.getPartyLeader();
        if (leader != null && leader.getObjectId() == this._objectId) {
          activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(party));
          break;
        }
      }

    }
  }
}
