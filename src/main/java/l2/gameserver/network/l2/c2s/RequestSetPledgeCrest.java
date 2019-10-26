//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.CrestCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;

public class RequestSetPledgeCrest extends L2GameClientPacket {
  private int _length;
  private byte[] _data;

  public RequestSetPledgeCrest() {
  }

  protected void readImpl() {
    this._length = this.readD();
    if (this._length == 256 && this._length == this._buf.remaining()) {
      this._data = new byte[this._length];
      this.readB(this._data);
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if ((activeChar.getClanPrivileges() & 128) == 128) {
        if (clan.isPlacedForDisband()) {
          activeChar.sendPacket(Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
          return;
        }

        if (clan.getLevel() < 3) {
          activeChar.sendPacket(Msg.CLAN_CREST_REGISTRATION_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
          return;
        }

        int crestId = 0;
        if (this._data != null && CrestCache.isValidCrestData(this._data)) {
          crestId = CrestCache.getInstance().savePledgeCrest(clan.getClanId(), this._data);
        } else if (clan.hasCrest()) {
          CrestCache.getInstance().removePledgeCrest(clan.getClanId());
        }

        clan.setCrestId(crestId);
        clan.broadcastClanStatus(false, true, false);
      }

    }
  }
}
