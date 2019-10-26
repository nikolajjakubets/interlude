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

public class RequestWithDrawalParty extends L2GameClientPacket {
  public RequestWithDrawalParty() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Party party = activeChar.getParty();
      if (party == null) {
        activeChar.sendActionFailed();
      } else if (activeChar.isOlyParticipant()) {
        activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.CantOustNow", activeChar, new Object[0]));
      } else {
        Reflection r = activeChar.getParty().getReflection();
        if (r != null && r instanceof DimensionalRift && activeChar.getReflection().equals(r)) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.Rift", activeChar, new Object[0]));
        } else if (r != null && activeChar.isInCombat()) {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.CantOustNow", activeChar, new Object[0]));
        } else {
          activeChar.leaveParty();
        }

      }
    }
  }
}
