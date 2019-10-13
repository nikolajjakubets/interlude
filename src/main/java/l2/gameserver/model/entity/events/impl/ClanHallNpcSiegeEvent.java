//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class ClanHallNpcSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
  public ClanHallNpcSiegeEvent(MultiValueSet<String> set) {
    super(set);
  }

  public void startEvent() {
    this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
    this.broadcastInZone(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN)).addResidenceName(this.getResidence())});
    super.startEvent();
  }

  public void stopEvent(boolean step) {
    Clan newOwner = ((ClanHall)this.getResidence()).getOwner();
    if (newOwner != null) {
      if (this._oldOwner != newOwner) {
        newOwner.broadcastToOnlineMembers(new L2GameServerPacket[]{PlaySound.SIEGE_VICTORY});
        newOwner.incReputation(1700, false, this.toString());
        if (this._oldOwner != null) {
          this._oldOwner.incReputation(-1700, false, this.toString());
        }
      }

      this.broadcastInZone(new L2GameServerPacket[]{((SystemMessage2)(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2)).addString(newOwner.getName())).addResidenceName(this.getResidence())});
      this.broadcastInZone(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED)).addResidenceName(this.getResidence())});
    } else {
      this.broadcastInZone(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW)).addResidenceName(this.getResidence())});
    }

    super.stopEvent(step);
    this._oldOwner = null;
  }

  public void processStep(Clan clan) {
    if (clan != null) {
      ((ClanHall)this.getResidence()).changeOwner(clan);
    }

    this.stopEvent(true);
  }

  public void loadSiegeClans() {
  }
}
