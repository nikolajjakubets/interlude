//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.residence;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.dao.ClanDataDAO;
import l2.gameserver.dao.ClanHallDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.StatsSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
public class ClanHall extends Residence {
  private int _auctionLength;
  private long _auctionMinBid;
  private String _auctionDescription = "";
  private final int _grade;
  private final long _rentalFee;
  private final long _minBid;
  private final long _deposit;

  public ClanHall(StatsSet set) {
    super(set);
    this._grade = set.getInteger("grade", 0);
    this._rentalFee = set.getInteger("rental_fee", 0);
    this._minBid = set.getInteger("min_bid", 0);
    this._deposit = set.getInteger("deposit", 0);
  }

  public void init() {
    this.initZone();
    this.initEvent();
    this.loadData();
    this.loadFunctions();
    this.rewardSkills();
    if (this.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && this._owner != null && this.getAuctionLength() == 0) {
      this.startCycleTask();
    }

  }

  public void changeOwner(Clan clan) {
    Clan oldOwner = this.getOwner();
    if (oldOwner != null && (clan == null || clan.getClanId() != oldOwner.getClanId())) {
      this.removeSkills();
      oldOwner.setHasHideout(0);
      this.cancelCycleTask();
    }

    this.updateOwnerInDB(clan);
    this.rewardSkills();
    this.update();
    if (clan == null && this.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
      this.getSiegeEvent().reCalcNextTime(false);
    }

  }

  public ResidenceType getType() {
    return ResidenceType.ClanHall;
  }

  protected void loadData() {
    this._owner = ClanDataDAO.getInstance().getOwner(this);
    ClanHallDAO.getInstance().select(this);
  }

  private void updateOwnerInDB(Clan clan) {
    this._owner = clan;
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET hasHideout=0 WHERE hasHideout=?");
      statement.setInt(1, this.getId());
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE clan_data SET hasHideout=? WHERE clan_id=?");
      statement.setInt(1, this.getId());
      statement.setInt(2, this.getOwnerId());
      statement.execute();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=?");
      statement.setInt(1, this.getId());
      statement.execute();
      DbUtils.close(statement);
      if (clan != null) {
        clan.setHasHideout(this.getId());
        clan.broadcastClanStatus(false, true, false);
      }
    } catch (Exception var8) {
      log.warn("Exception: updateOwnerInDB(L2Clan clan): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public int getGrade() {
    return this._grade;
  }

  public void update() {
    ClanHallDAO.getInstance().update(this);
  }

  public int getAuctionLength() {
    return this._auctionLength;
  }

  public void setAuctionLength(int auctionLength) {
    this._auctionLength = auctionLength;
  }

  public String getAuctionDescription() {
    return this._auctionDescription;
  }

  public void setAuctionDescription(String auctionDescription) {
    this._auctionDescription = auctionDescription == null ? "" : auctionDescription;
  }

  public long getAuctionMinBid() {
    return this._auctionMinBid;
  }

  public void setAuctionMinBid(long auctionMinBid) {
    this._auctionMinBid = auctionMinBid;
  }

  public long getRentalFee() {
    return this._rentalFee;
  }

  public long getBaseMinBid() {
    return this._minBid;
  }

  public long getDeposit() {
    return this._deposit;
  }

  public void chanceCycle() {
    super.chanceCycle();
    this.setPaidCycle(this.getPaidCycle() + 1);
    if (this.getPaidCycle() >= Config.CLNHALL_REWARD_CYCLE) {
      if (this._owner.getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID) > this._rentalFee) {
        this._owner.getWarehouse().destroyItemByItemId(Config.CH_BID_CURRENCY_ITEM_ID, this._rentalFee);
        this.setPaidCycle(0);
      } else {
        UnitMember member = this._owner.getLeader();
        if (member.isOnline()) {
          member.getPlayer().sendPacket(SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
        } else {
          PlayerMessageStack.getInstance().mailto(member.getObjectId(), SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED.packet(null));
        }

        this.changeOwner(null);
      }
    }

  }
}
