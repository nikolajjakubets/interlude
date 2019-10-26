//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;

public class RequestOustPartyMember extends L2GameClientPacket {
  private String _name;

  public RequestOustPartyMember() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Party party = activeChar.getParty();
      if (party != null && activeChar.getParty().isLeader(activeChar)) {
        if (activeChar.isOlyParticipant()) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustNow", activeChar, new Object[0]));
        } else {
          Player member = party.getPlayerByName(this._name);
          if (member == activeChar) {
            activeChar.sendActionFailed();
          } else if (member == null) {
            activeChar.sendActionFailed();
          } else {
            Reflection r = party.getReflection();
            if (r != null && r instanceof DimensionalRift && member.getReflection().equals(r)) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", activeChar, new Object[0]));
            } else if (r != null && !(r instanceof DimensionalRift)) {
              activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", activeChar, new Object[0]));
            } else {
              party.removePartyMember(member, true);
            }

          }
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
