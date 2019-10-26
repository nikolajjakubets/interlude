//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PledgeReceiveMemberInfo;

public class RequestPledgeMemberInfo extends L2GameClientPacket {
  private int _pledgeType;
  private String _target;

  public RequestPledgeMemberInfo() {
  }

  protected void readImpl() {
    this._pledgeType = this.readD();
    this._target = this.readS(16);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        UnitMember cm = clan.getAnyMember(this._target);
        if (cm != null) {
          activeChar.sendPacket(new PledgeReceiveMemberInfo(cm));
        }
      }

    }
  }
}
