//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class ClanHallSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
  public static final String BOSS = "boss";

  public ClanHallSiegeEvent(MultiValueSet<String> set) {
    super(set);
  }

  public void startEvent() {
    this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
    if (this._oldOwner != null) {
      ((ClanHall)this.getResidence()).changeOwner((Clan)null);
      this.addObject("attackers", new SiegeClanObject("attackers", this._oldOwner, 0L));
    }

    if (this.getObjects("attackers").size() == 0) {
      this.broadcastInZone2(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST)).addResidenceName(this.getResidence())});
      this.reCalcNextTime(false);
    } else {
      SiegeClanDAO.getInstance().delete(this.getResidence());
      this.updateParticles(true, new String[]{"attackers"});
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN)).addResidenceName(this.getResidence()), new String[]{"attackers"});
      super.startEvent();
    }
  }

  public void stopEvent(boolean step) {
    Clan newOwner = ((ClanHall)this.getResidence()).getOwner();
    if (newOwner != null) {
      newOwner.broadcastToOnlineMembers(new L2GameServerPacket[]{PlaySound.SIEGE_VICTORY});
      newOwner.incReputation(1700, false, this.toString());
      this.broadcastTo(((SystemMessage2)(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2)).addString(newOwner.getName())).addResidenceName(this.getResidence()), new String[]{"attackers"});
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED)).addResidenceName(this.getResidence()), new String[]{"attackers"});
    } else {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence()), new String[]{"attackers"});
    }

    this.updateParticles(false, new String[]{"attackers"});
    this.removeObjects("attackers");
    super.stopEvent(step);
    this._oldOwner = null;
  }

  public void setRegistrationOver(boolean b) {
    if (b) {
      this.broadcastTo((new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED)).addResidenceName(this.getResidence()), new String[]{"attackers"});
    }

    super.setRegistrationOver(b);
  }

  public void processStep(Clan clan) {
    if (clan != null) {
      ((ClanHall)this.getResidence()).changeOwner(clan);
    }

    this.stopEvent(true);
  }

  public void loadSiegeClans() {
    this.addObjects("attackers", SiegeClanDAO.getInstance().load(this.getResidence(), "attackers"));
  }

  public int getUserRelation(Player thisPlayer, int result) {
    return result;
  }

  public int getRelation(Player thisPlayer, Player targetPlayer, int result) {
    return result;
  }

  public boolean canResurrect(Player resurrectPlayer, Creature target, boolean force) {
    boolean playerInZone = resurrectPlayer.isInZone(ZoneType.SIEGE);
    boolean targetInZone = target.isInZone(ZoneType.SIEGE);
    if (!playerInZone && !targetInZone) {
      return true;
    } else if (!targetInZone) {
      return false;
    } else {
      Player targetPlayer = target.getPlayer();
      ClanHallSiegeEvent siegeEvent = (ClanHallSiegeEvent)target.getEvent(ClanHallSiegeEvent.class);
      if (siegeEvent != this) {
        if (force) {
          targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
        }

        resurrectPlayer.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
        return false;
      } else {
        SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan("attackers", targetPlayer.getClan());
        if (targetSiegeClan.getFlag() == null) {
          if (force) {
            targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
          }

          resurrectPlayer.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
          return false;
        } else if (force) {
          return true;
        } else {
          resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
          return false;
        }
      }
    }
  }
}
