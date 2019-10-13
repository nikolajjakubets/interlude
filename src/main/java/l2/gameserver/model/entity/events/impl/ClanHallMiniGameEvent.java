//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject.SiegeClanComparatorImpl;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;

public class ClanHallMiniGameEvent extends SiegeEvent<ClanHall, CMGSiegeClanObject> {
  public static final String NEXT_STEP = "next_step";
  public static final String REFUND = "refund";
  private boolean _arenaClosed = true;

  public ClanHallMiniGameEvent(MultiValueSet<String> set) {
    super(set);
  }

  public void startEvent() {
    this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
    List<CMGSiegeClanObject> siegeClans = this.getObjects("attackers");
    if (siegeClans.size() < 2) {
      CMGSiegeClanObject siegeClan = (CMGSiegeClanObject)CollectionUtils.safeGet(siegeClans, 0);
      if (siegeClan != null) {
        CMGSiegeClanObject oldSiegeClan = (CMGSiegeClanObject)this.getSiegeClan("refund", siegeClan.getObjectId());
        if (oldSiegeClan != null) {
          SiegeClanDAO.getInstance().delete(this.getResidence(), siegeClan);
          oldSiegeClan.setParam(oldSiegeClan.getParam() + siegeClan.getParam());
          SiegeClanDAO.getInstance().update(this.getResidence(), oldSiegeClan);
        } else {
          siegeClan.setType("refund");
          siegeClans.remove(siegeClan);
          this.addObject("refund", siegeClan);
          SiegeClanDAO.getInstance().update(this.getResidence(), siegeClan);
        }
      }

      siegeClans.clear();
      this.broadcastTo(SystemMsg.THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED, new String[]{"attackers"});
      this.broadcastInZone2(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence())});
      this.reCalcNextTime(false);
    } else {
      CMGSiegeClanObject[] clans = (CMGSiegeClanObject[])siegeClans.toArray(new CMGSiegeClanObject[siegeClans.size()]);
      Arrays.sort(clans, SiegeClanComparatorImpl.getInstance());
      List<CMGSiegeClanObject> temp = new ArrayList(4);

      for(int i = 0; i < clans.length; ++i) {
        CMGSiegeClanObject siegeClan = clans[i];
        SiegeClanDAO.getInstance().delete(this.getResidence(), siegeClan);
        if (temp.size() == 4) {
          siegeClans.remove(siegeClan);
          siegeClan.broadcast(new IStaticPacket[]{SystemMsg.YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR});
        } else {
          temp.add(siegeClan);
          siegeClan.broadcast(new IStaticPacket[]{SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR});
        }
      }

      this._arenaClosed = false;
      super.startEvent();
    }
  }

  public void stopEvent(boolean step) {
    this.removeBanishItems();
    Clan newOwner = ((ClanHall)this.getResidence()).getOwner();
    if (newOwner != null) {
      if (this._oldOwner != newOwner) {
        newOwner.broadcastToOnlineMembers(new L2GameServerPacket[]{PlaySound.SIEGE_VICTORY});
        newOwner.incReputation(1700, false, this.toString());
      }

      this.broadcastTo(((SystemMessage2)(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2)).addString(newOwner.getName())).addResidenceName(this.getResidence()), new String[]{"attackers", "defenders"});
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED)).addResidenceName(this.getResidence()), new String[]{"attackers", "defenders"});
    } else {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence()), new String[]{"attackers"});
    }

    this.updateParticles(false, new String[]{"attackers"});
    this.removeObjects("attackers");
    super.stopEvent(step);
    this._oldOwner = null;
  }

  public void nextStep() {
    List<CMGSiegeClanObject> siegeClans = this.getObjects("attackers");

    for(int i = 0; i < siegeClans.size(); ++i) {
      this.spawnAction("arena_" + i, true);
    }

    this._arenaClosed = true;
    this.updateParticles(true, new String[]{"attackers"});
    this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN)).addResidenceName(this.getResidence()), new String[]{"attackers"});
  }

  public void setRegistrationOver(boolean b) {
    if (b) {
      this.broadcastTo(SystemMsg.THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED, new String[]{"attackers"});
    }

    super.setRegistrationOver(b);
  }

  public CMGSiegeClanObject newSiegeClan(String type, int clanId, long param, long date) {
    Clan clan = ClanTable.getInstance().getClan(clanId);
    return clan == null ? null : new CMGSiegeClanObject(type, clan, param, date);
  }

  public void announce(int val) {
    int seconds = val % 60;
    int min = val / 60;
    if (min > 0) {
      SystemMsg msg = min > 10 ? SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA : SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW;
      this.broadcastTo((new SystemMessage2(msg)).addInteger((double)min), new String[]{"attackers"});
    } else {
      this.broadcastTo((new SystemMessage2(SystemMsg.IN_S1_SECONDS_THE_GAME_WILL_BEGIN)).addInteger((double)seconds), new String[]{"attackers"});
    }

  }

  public void processStep(Clan clan) {
    if (clan != null) {
      ((ClanHall)this.getResidence()).changeOwner(clan);
    }

    this.stopEvent(true);
  }

  public void loadSiegeClans() {
    this.addObjects("attackers", SiegeClanDAO.getInstance().load(this.getResidence(), "attackers"));
    this.addObjects("refund", SiegeClanDAO.getInstance().load(this.getResidence(), "refund"));
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

  public boolean isArenaClosed() {
    return this._arenaClosed;
  }

  public void onAddEvent(GameObject object) {
    if (object.isItem()) {
      this.addBanishItem((ItemInstance)object);
    }

  }
}
