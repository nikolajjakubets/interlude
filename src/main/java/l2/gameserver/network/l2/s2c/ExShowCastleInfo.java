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
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.tables.ClanTable;

public class ExShowCastleInfo extends L2GameServerPacket {
  private List<ExShowCastleInfo.CastleInfo> _infos = Collections.emptyList();

  public ExShowCastleInfo() {
    List<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);
    this._infos = new ArrayList(castles.size());
    Iterator var6 = castles.iterator();

    while(var6.hasNext()) {
      Castle castle = (Castle)var6.next();
      String ownerName = ClanTable.getInstance().getClanName(castle.getOwnerId());
      int id = castle.getId();
      int tax = castle.getTaxPercent();
      int nextSiege = (int)(castle.getSiegeDate().getTimeInMillis() / 1000L);
      this._infos.add(new ExShowCastleInfo.CastleInfo(ownerName, id, tax, nextSiege));
    }

  }

  protected final void writeImpl() {
    this.writeEx(20);
    this.writeD(this._infos.size());
    Iterator var1 = this._infos.iterator();

    while(var1.hasNext()) {
      ExShowCastleInfo.CastleInfo info = (ExShowCastleInfo.CastleInfo)var1.next();
      this.writeD(info._id);
      this.writeS(info._ownerName);
      this.writeD(info._tax);
      this.writeD(info._nextSiege);
    }

    this._infos.clear();
  }

  private static class CastleInfo {
    public String _ownerName;
    public int _id;
    public int _tax;
    public int _nextSiege;

    public CastleInfo(String ownerName, int id, int tax, int nextSiege) {
      this._ownerName = ownerName;
      this._id = id;
      this._tax = tax;
      this._nextSiege = nextSiege;
    }
  }
}
