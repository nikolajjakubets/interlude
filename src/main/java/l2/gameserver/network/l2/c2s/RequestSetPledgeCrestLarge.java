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

public class RequestSetPledgeCrestLarge extends L2GameClientPacket {
  private int _length;
  private byte[] _data;

  public RequestSetPledgeCrestLarge() {
  }

  protected void readImpl() {
    this._length = this.readD();
    if (this._length == 2176 && this._length == this._buf.remaining()) {
      this._data = new byte[this._length];
      this.readB(this._data);
    }

  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        if ((activeChar.getClanPrivileges() & 128) == 128) {
          if (clan.isPlacedForDisband()) {
            activeChar.sendPacket(Msg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
            return;
          }

          if (clan.getCastle() == 0 && clan.getHasHideout() == 0) {
            activeChar.sendPacket(Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
            return;
          }

          int crestId = 0;
          if (this._data != null && CrestCache.isValidCrestData(this._data)) {
            crestId = CrestCache.getInstance().savePledgeCrestLarge(clan.getClanId(), this._data);
            activeChar.sendPacket(Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
          } else if (clan.hasCrestLarge()) {
            CrestCache.getInstance().removePledgeCrestLarge(clan.getClanId());
          }

          clan.setCrestLargeId(crestId);
          clan.broadcastClanStatus(false, true, false);
        }

      }
    }
  }
}
