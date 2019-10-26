//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.pledge.Clan;

public class PledgeReceiveWarList extends L2GameServerPacket {
  private List<PledgeReceiveWarList.WarInfo> infos = new ArrayList();
  private int _updateType;
  private int _page;

  public PledgeReceiveWarList(Clan clan, int type, int page) {
    this._updateType = type;
    this._page = page;
    Iterator var4 = (this._updateType == 1 ? clan.getAttackerClans() : clan.getEnemyClans()).iterator();

    while(var4.hasNext()) {
      Clan c = (Clan)var4.next();
      if (c != null) {
        this.infos.add(new PledgeReceiveWarList.WarInfo(c.getName(), this._updateType, 0));
      }
    }

  }

  protected final void writeImpl() {
    this.writeEx(62);
    this.writeD(this._updateType);
    this.writeD(0);
    this.writeD(this.infos.size());
    Iterator var1 = this.infos.iterator();

    while(var1.hasNext()) {
      PledgeReceiveWarList.WarInfo _info = (PledgeReceiveWarList.WarInfo)var1.next();
      this.writeS(_info.clan_name);
      this.writeD(_info.unk1);
      this.writeD(_info.unk2);
    }

  }

  static class WarInfo {
    public String clan_name;
    public int unk1;
    public int unk2;

    public WarInfo(String _clan_name, int _unk1, int _unk2) {
      this.clan_name = _clan_name;
      this.unk1 = _unk1;
      this.unk2 = _unk2;
    }
  }
}
