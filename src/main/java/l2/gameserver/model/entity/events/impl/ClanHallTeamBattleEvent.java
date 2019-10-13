//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.dao.SiegePlayerDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.Location;

public class ClanHallTeamBattleEvent extends SiegeEvent<ClanHall, CTBSiegeClanObject> {
  public static final String TRYOUT_PART = "tryout_part";
  public static final String CHALLENGER_RESTART_POINTS = "challenger_restart_points";
  public static final String FIRST_DOORS = "first_doors";
  public static final String SECOND_DOORS = "second_doors";
  public static final String NEXT_STEP = "next_step";

  public ClanHallTeamBattleEvent(MultiValueSet<String> set) {
    super(set);
  }

  public void startEvent() {
    this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
    List<CTBSiegeClanObject> attackers = this.getObjects("attackers");
    if (attackers.isEmpty()) {
      if (this._oldOwner == null) {
        this.broadcastInZone2(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST)).addResidenceName(this.getResidence())});
      } else {
        this.broadcastInZone2(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED)).addResidenceName(this.getResidence())});
      }

      this.reCalcNextTime(false);
    } else {
      if (this._oldOwner != null) {
        this.addObject("defenders", new SiegeClanObject("defenders", this._oldOwner, 0L));
      }

      SiegeClanDAO.getInstance().delete(this.getResidence());
      SiegePlayerDAO.getInstance().delete(this.getResidence());
      List<CTBTeamObject> teams = this.getObjects("tryout_part");

      for(int i = 0; i < 5; ++i) {
        CTBTeamObject team = (CTBTeamObject)teams.get(i);
        team.setSiegeClan((CTBSiegeClanObject)CollectionUtils.safeGet(attackers, i));
      }

      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN)).addResidenceName(this.getResidence()), new String[]{"attackers", "defenders"});
      this.broadcastTo(SystemMsg.THE_TRYOUTS_ARE_ABOUT_TO_BEGIN, new String[]{"attackers"});
      super.startEvent();
    }
  }

  public void nextStep() {
    this.broadcastTo(SystemMsg.THE_TRYOUTS_HAVE_BEGUN, new String[]{"attackers", "defenders"});
    this.updateParticles(true, new String[]{"attackers", "defenders"});
  }

  public void processStep(CTBTeamObject team) {
    if (team.getSiegeClan() != null) {
      CTBSiegeClanObject object = team.getSiegeClan();
      object.setEvent(false, this);
      this.teleportPlayers("spectators");
    }

    team.despawnObject(this);
    List<CTBTeamObject> teams = this.getObjects("tryout_part");
    boolean hasWinner = false;
    CTBTeamObject winnerTeam = null;
    Iterator var5 = teams.iterator();

    while(var5.hasNext()) {
      CTBTeamObject t = (CTBTeamObject)var5.next();
      if (t.isParticle()) {
        hasWinner = winnerTeam == null;
        winnerTeam = t;
      }
    }

    if (hasWinner) {
      SiegeClanObject clan = winnerTeam.getSiegeClan();
      if (clan != null) {
        ((ClanHall)this.getResidence()).changeOwner(clan.getClan());
      }

      this.stopEvent(true);
    }
  }

  public void announce(int val) {
    int minute = val / 60;
    if (minute > 0) {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES)).addInteger((double)minute), new String[]{"attackers", "defenders"});
    } else {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS)).addInteger((double)val), new String[]{"attackers", "defenders"});
    }

  }

  public void stopEvent(boolean step) {
    Clan newOwner = ((ClanHall)this.getResidence()).getOwner();
    if (newOwner != null) {
      if (this._oldOwner != newOwner) {
        newOwner.broadcastToOnlineMembers(new L2GameServerPacket[]{PlaySound.SIEGE_VICTORY});
        newOwner.incReputation(1700, false, this.toString());
      }

      this.broadcastTo(((SystemMessage2)(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2)).addString(newOwner.getName())).addResidenceName(this.getResidence()), new String[]{"attackers", "defenders"});
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED)).addResidenceName(this.getResidence()), new String[]{"attackers", "defenders"});
    } else {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence()), new String[]{"attackers"});
    }

    this.updateParticles(false, new String[]{"attackers", "defenders"});
    this.removeObjects("defenders");
    this.removeObjects("attackers");
    super.stopEvent(step);
    this._oldOwner = null;
  }

  public void loadSiegeClans() {
    List<SiegeClanObject> siegeClanObjectList = SiegeClanDAO.getInstance().load(this.getResidence(), "attackers");
    this.addObjects("attackers", siegeClanObjectList);
    List<CTBSiegeClanObject> objects = this.getObjects("attackers");
    Iterator var3 = objects.iterator();

    while(var3.hasNext()) {
      CTBSiegeClanObject clan = (CTBSiegeClanObject)var3.next();
      clan.select(this.getResidence());
    }

  }

  public CTBSiegeClanObject newSiegeClan(String type, int clanId, long i, long date) {
    Clan clan = ClanTable.getInstance().getClan(clanId);
    return clan == null ? null : new CTBSiegeClanObject(type, clan, i, date);
  }

  public boolean isParticle(Player player) {
    if (this.isInProgress() && player.getClan() != null) {
      CTBSiegeClanObject object = (CTBSiegeClanObject)this.getSiegeClan("attackers", player.getClan());
      return object != null && object.getPlayers().contains(player.getObjectId());
    } else {
      return false;
    }
  }

  public Location getRestartLoc(Player player, RestartType type) {
    if (!this.checkIfInZone(player)) {
      return null;
    } else {
      SiegeClanObject attackerClan = this.getSiegeClan("attackers", player.getClan());
      Location loc = null;
      switch(type) {
        case TO_VILLAGE:
          if (attackerClan != null && this.checkIfInZone(player)) {
            List<SiegeClanObject> objectList = this.getObjects("attackers");
            List<Location> teleportList = this.getObjects("challenger_restart_points");
            int index = objectList.indexOf(attackerClan);
            loc = (Location)teleportList.get(index);
          }
        default:
          return loc;
      }
    }
  }

  public void action(String name, boolean start) {
    if (name.equalsIgnoreCase("next_step")) {
      this.nextStep();
    } else {
      super.action(name, start);
    }

  }

  public int getUserRelation(Player thisPlayer, int result) {
    return result;
  }

  public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
    return result;
  }
}
