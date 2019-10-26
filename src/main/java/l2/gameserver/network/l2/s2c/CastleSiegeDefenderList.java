//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;

public class CastleSiegeDefenderList extends L2GameServerPacket {
  public static int OWNER = 1;
  public static int WAITING = 2;
  public static int ACCEPTED = 3;
  public static int REFUSE = 4;
  private int _id;
  private int _registrationValid;
  private List<CastleSiegeDefenderList.DefenderClan> _defenderClans = Collections.emptyList();

  public CastleSiegeDefenderList(Castle castle) {
    this._id = castle.getId();
    this._registrationValid = !castle.getSiegeEvent().isRegistrationOver() && castle.getOwner() != null ? 1 : 0;
    List<SiegeClanObject> defenders = castle.getSiegeEvent().getObjects("defenders");
    List<SiegeClanObject> defendersWaiting = castle.getSiegeEvent().getObjects("defenders_waiting");
    List<SiegeClanObject> defendersRefused = castle.getSiegeEvent().getObjects("defenders_refused");
    this._defenderClans = new ArrayList(defenders.size() + defendersWaiting.size() + defendersRefused.size());
    if (castle.getOwner() != null) {
      this._defenderClans.add(new CastleSiegeDefenderList.DefenderClan(castle.getOwner(), OWNER, 0));
    }

    Iterator var5 = defenders.iterator();

    SiegeClanObject siegeClan;
    while(var5.hasNext()) {
      siegeClan = (SiegeClanObject)var5.next();
      this._defenderClans.add(new CastleSiegeDefenderList.DefenderClan(siegeClan.getClan(), ACCEPTED, (int)(siegeClan.getDate() / 1000L)));
    }

    var5 = defendersWaiting.iterator();

    while(var5.hasNext()) {
      siegeClan = (SiegeClanObject)var5.next();
      this._defenderClans.add(new CastleSiegeDefenderList.DefenderClan(siegeClan.getClan(), WAITING, (int)(siegeClan.getDate() / 1000L)));
    }

    var5 = defendersRefused.iterator();

    while(var5.hasNext()) {
      siegeClan = (SiegeClanObject)var5.next();
      this._defenderClans.add(new CastleSiegeDefenderList.DefenderClan(siegeClan.getClan(), REFUSE, (int)(siegeClan.getDate() / 1000L)));
    }

  }

  protected final void writeImpl() {
    this.writeC(203);
    this.writeD(this._id);
    this.writeD(0);
    this.writeD(this._registrationValid);
    this.writeD(0);
    this.writeD(this._defenderClans.size());
    this.writeD(this._defenderClans.size());
    Iterator var1 = this._defenderClans.iterator();

    while(var1.hasNext()) {
      CastleSiegeDefenderList.DefenderClan defenderClan = (CastleSiegeDefenderList.DefenderClan)var1.next();
      Clan clan = defenderClan._clan;
      this.writeD(clan.getClanId());
      this.writeS(clan.getName());
      this.writeS(clan.getLeaderName());
      this.writeD(clan.getCrestId());
      this.writeD(defenderClan._time);
      this.writeD(defenderClan._type);
      this.writeD(clan.getAllyId());
      Alliance alliance = clan.getAlliance();
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

  private static class DefenderClan {
    private Clan _clan;
    private int _type;
    private int _time;

    public DefenderClan(Clan clan, int type, int time) {
      this._clan = clan;
      this._type = type;
      this._time = time;
    }
  }
}
