//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.actions.StartStopAction;
import l2.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject.SiegeClanComparatorImpl;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;

public class ClanHallAuctionEvent extends SiegeEvent<ClanHall, AuctionSiegeClanObject> {
  private Calendar _endSiegeDate = Calendar.getInstance();

  public ClanHallAuctionEvent(MultiValueSet<String> set) {
    super(set);
  }

  public void reCalcNextTime(boolean onStart) {
    this.clearActions();
    this._onTimeActions.clear();
    Clan owner = ((ClanHall)this.getResidence()).getOwner();
    this._endSiegeDate.setTimeInMillis(0L);
    if (((ClanHall)this.getResidence()).getAuctionLength() == 0 && owner == null) {
      ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(System.currentTimeMillis());
      ((ClanHall)this.getResidence()).getSiegeDate().set(7, 2);
      ((ClanHall)this.getResidence()).getSiegeDate().set(11, 15);
      ((ClanHall)this.getResidence()).getSiegeDate().set(12, 0);
      ((ClanHall)this.getResidence()).getSiegeDate().set(13, 0);
      ((ClanHall)this.getResidence()).getSiegeDate().set(14, 0);
      ((ClanHall)this.getResidence()).setAuctionLength(Config.CLNHALL_REWARD_CYCLE / 24);
      ((ClanHall)this.getResidence()).setAuctionMinBid(((ClanHall)this.getResidence()).getBaseMinBid());
      ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
      ((ClanHall)this.getResidence()).update();
      this._onTimeActions.clear();
      this.addOnTimeAction(0, new StartStopAction("event", true));
      this.addOnTimeAction(((ClanHall)this.getResidence()).getAuctionLength() * 86400, new StartStopAction("event", false));
      this._endSiegeDate.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L);
      this.registerActions();
    } else if (((ClanHall)this.getResidence()).getAuctionLength() != 0 || owner == null) {
      long endDate = ((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L;
      if (endDate <= System.currentTimeMillis()) {
        ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(System.currentTimeMillis() + 60000L);
        this._endSiegeDate.setTimeInMillis(System.currentTimeMillis() + 60000L);
        this._onTimeActions.clear();
        this.addOnTimeAction(0, new StartStopAction("event", true));
        this.addOnTimeAction(1, new StartStopAction("event", false));
        this.registerActions();
      } else {
        this._endSiegeDate.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L);
        this._onTimeActions.clear();
        this.addOnTimeAction(0, new StartStopAction("event", true));
        this.addOnTimeAction((int)this.getEndSiegeForCH(), new StartStopAction("event", false));
        this.registerActions();
      }
    }

  }

  public void stopEvent(boolean step) {
    List<AuctionSiegeClanObject> siegeClanObjects = this.removeObjects("attackers");
    AuctionSiegeClanObject[] clans = (AuctionSiegeClanObject[])siegeClanObjects.toArray(new AuctionSiegeClanObject[siegeClanObjects.size()]);
    Arrays.sort(clans, SiegeClanComparatorImpl.getInstance());
    Clan oldOwner = ((ClanHall)this.getResidence()).getOwner();
    AuctionSiegeClanObject winnerSiegeClan = clans.length > 0 ? clans[0] : null;
    if (winnerSiegeClan != null) {
      SystemMessage2 msg = (SystemMessage2)(new SystemMessage2(SystemMsg.THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN)).addString(winnerSiegeClan.getClan().getName());
      Iterator var7 = siegeClanObjects.iterator();

      while(var7.hasNext()) {
        AuctionSiegeClanObject $siegeClan = (AuctionSiegeClanObject)var7.next();

        try {
          Player player = $siegeClan.getClan().getLeader().getPlayer();
          if (player != null) {
            player.sendPacket(msg);
          } else {
            PlayerMessageStack.getInstance().mailto($siegeClan.getClan().getLeaderId(), msg);
          }
        } catch (Exception var11) {
          var11.printStackTrace();
        }

        if ($siegeClan != winnerSiegeClan) {
          long returnBid = $siegeClan.getParam() - (long)((double)$siegeClan.getParam() * 0.1D);
          $siegeClan.getClan().getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, returnBid);
        }
      }

      SiegeClanDAO.getInstance().delete(this.getResidence());
      if (oldOwner != null) {
        oldOwner.getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, ((ClanHall)this.getResidence()).getDeposit());
      }

      ((ClanHall)this.getResidence()).setAuctionLength(0);
      ((ClanHall)this.getResidence()).setAuctionMinBid(0L);
      ((ClanHall)this.getResidence()).setAuctionDescription("");
      ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(0L);
      ((ClanHall)this.getResidence()).getLastSiegeDate().setTimeInMillis(0L);
      ((ClanHall)this.getResidence()).getOwnDate().setTimeInMillis(System.currentTimeMillis());
      ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
      ((ClanHall)this.getResidence()).changeOwner(winnerSiegeClan.getClan());
      ((ClanHall)this.getResidence()).startCycleTask();
    } else if (oldOwner != null) {
      Player player = oldOwner.getLeader().getPlayer();
      if (player != null) {
        player.sendPacket(SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
      } else {
        PlayerMessageStack.getInstance().mailto(oldOwner.getLeaderId(), SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED.packet((Player)null));
      }
    } else {
      ((ClanHall)this.getResidence()).setAuctionLength(0);
      ((ClanHall)this.getResidence()).setAuctionMinBid(0L);
      ((ClanHall)this.getResidence()).setAuctionDescription("");
      ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(0L);
      ((ClanHall)this.getResidence()).getLastSiegeDate().setTimeInMillis(0L);
      ((ClanHall)this.getResidence()).getOwnDate().setTimeInMillis(0L);
      ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
    }

    super.stopEvent(step);
  }

  public boolean isParticle(Player player) {
    return false;
  }

  public AuctionSiegeClanObject newSiegeClan(String type, int clanId, long param, long date) {
    Clan clan = ClanTable.getInstance().getClan(clanId);
    return clan == null ? null : new AuctionSiegeClanObject(type, clan, param, date);
  }

  public long getEndSiegeForCH() {
    long start_date_msec = ((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis();
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis());
    cal.set(7, 2);
    cal.set(11, 15);
    cal.set(12, 0);
    cal.set(13, 0);
    cal.set(14, 0);
    long end_date = cal.getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L;
    return (end_date - start_date_msec) / 1000L;
  }

  public Calendar getEndSiegeDate() {
    return this._endSiegeDate;
  }
}
