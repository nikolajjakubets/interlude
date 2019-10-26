//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;

public class CastleSiegeAttackerList extends L2GameServerPacket {
  private int _id;
  private int _registrationValid;
  private List<SiegeClanObject> _clans = Collections.emptyList();

  public CastleSiegeAttackerList(Residence residence) {
    this._id = residence.getId();
    this._registrationValid = !residence.getSiegeEvent().isRegistrationOver() ? 1 : 0;
    this._clans = residence.getSiegeEvent().getObjects("attackers");
  }

  protected final void writeImpl() {
    this.writeC(202);
    this.writeD(this._id);
    this.writeD(0);
    this.writeD(this._registrationValid);
    this.writeD(0);
    this.writeD(this._clans.size());
    this.writeD(this._clans.size());
    Iterator var1 = this._clans.iterator();

    while(var1.hasNext()) {
      SiegeClanObject siegeClan = (SiegeClanObject)var1.next();
      Clan clan = siegeClan.getClan();
      this.writeD(clan.getClanId());
      this.writeS(clan.getName());
      this.writeS(clan.getLeaderName());
      this.writeD(clan.getCrestId());
      this.writeD((int)(siegeClan.getDate() / 1000L));
      Alliance alliance = clan.getAlliance();
      this.writeD(clan.getAllyId());
      if (alliance != null) {
        this.writeS(alliance.getAllyName());
        this.writeS(alliance.getAllyLeaderName());
        this.writeD(alliance.getAllyCrestId());
      } else {
        this.writeS("");
        this.writeS("");
        this.writeD(0);
      }
    }

  }
}
