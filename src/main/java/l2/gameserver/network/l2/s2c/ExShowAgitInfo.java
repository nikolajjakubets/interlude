//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;

public class ExShowAgitInfo extends L2GameServerPacket {
  private List<ExShowAgitInfo.AgitInfo> _clanHalls = Collections.emptyList();

  public ExShowAgitInfo() {
    List<ClanHall> chs = ResidenceHolder.getInstance().getResidenceList(ClanHall.class);
    this._clanHalls = new ArrayList(chs.size());
    Iterator var2 = chs.iterator();

    while(var2.hasNext()) {
      ClanHall clanHall = (ClanHall)var2.next();
      int ch_id = clanHall.getId();
      byte getType;
      if (clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
        getType = 0;
      } else if (clanHall.getSiegeEvent().getClass() == ClanHallMiniGameEvent.class) {
        getType = 2;
      } else {
        getType = 1;
      }

      Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
      String clan_name = clanHall.getOwnerId() != 0 && clan != null ? clan.getName() : "";
      String leader_name = clanHall.getOwnerId() != 0 && clan != null ? clan.getLeaderName() : "";
      this._clanHalls.add(new ExShowAgitInfo.AgitInfo(clan_name, leader_name, ch_id, getType));
    }

  }

  protected final void writeImpl() {
    this.writeEx(22);
    this.writeD(this._clanHalls.size());
    Iterator var1 = this._clanHalls.iterator();

    while(var1.hasNext()) {
      ExShowAgitInfo.AgitInfo info = (ExShowAgitInfo.AgitInfo)var1.next();
      this.writeD(info.ch_id);
      this.writeS(info.clan_name);
      this.writeS(info.leader_name);
      this.writeD(info.getType);
    }

  }

  static class AgitInfo {
    public String clan_name;
    public String leader_name;
    public int ch_id;
    public int getType;

    public AgitInfo(String clan_name, String leader_name, int ch_id, int lease) {
      this.clan_name = clan_name;
      this.leader_name = leader_name;
      this.ch_id = ch_id;
      this.getType = lease;
    }
  }
}
