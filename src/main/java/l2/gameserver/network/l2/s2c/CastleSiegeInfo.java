//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Calendar;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import org.apache.commons.lang3.ArrayUtils;

public class CastleSiegeInfo extends L2GameServerPacket {
  private long _startTime;
  private int _id;
  private int _ownerObjectId;
  private int _allyId;
  private boolean _isLeader;
  private String _ownerName;
  private String _leaderName;
  private String _allyName;
  private int[] _nextTimeMillis;

  public CastleSiegeInfo(Castle castle, Player player) {
    this((Residence)castle, player);
    CastleSiegeEvent siegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
    long siegeTimeMillis = castle.getSiegeDate().getTimeInMillis();
    if (siegeTimeMillis == 0L) {
      this._nextTimeMillis = siegeEvent.getNextSiegeTimes();
    } else {
      this._startTime = (long)((int)(siegeTimeMillis / 1000L));
    }

  }

  public CastleSiegeInfo(ClanHall ch, Player player) {
    this((Residence)ch, player);
    this._startTime = (long)((int)(ch.getSiegeDate().getTimeInMillis() / 1000L));
  }

  protected CastleSiegeInfo(Residence residence, Player player) {
    this._ownerName = "NPC";
    this._leaderName = "";
    this._allyName = "";
    this._nextTimeMillis = ArrayUtils.EMPTY_INT_ARRAY;
    this._id = residence.getId();
    this._ownerObjectId = residence.getOwnerId();
    Clan owner = residence.getOwner();
    if (owner != null) {
      this._isLeader = owner.getLeaderId(0) == player.getObjectId();
      this._ownerName = owner.getName();
      this._leaderName = owner.getLeaderName(0);
      Alliance ally = owner.getAlliance();
      if (ally != null) {
        this._allyId = ally.getAllyId();
        this._allyName = ally.getAllyName();
      }
    }

  }

  protected void writeImpl() {
    this.writeC(201);
    this.writeD(this._id);
    this.writeD(this._isLeader ? 1 : 0);
    this.writeD(this._ownerObjectId);
    this.writeS(this._ownerName);
    this.writeS(this._leaderName);
    this.writeD(this._allyId);
    this.writeS(this._allyName);
    this.writeD((int)(Calendar.getInstance().getTimeInMillis() / 1000L));
    this.writeD((int)this._startTime);
    if (this._startTime == 0L) {
      this.writeDD(this._nextTimeMillis, true);
    }

  }
}
