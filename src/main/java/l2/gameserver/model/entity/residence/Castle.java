//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dbutils.DbUtils;
import l2.commons.math.SafeMath;
import l2.gameserver.dao.CastleDAO;
import l2.gameserver.dao.CastleHiredGuardDAO;
import l2.gameserver.dao.ClanDataDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.templates.manor.CropProcure;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.Log;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Castle extends Residence {
  private static final Logger _log = LoggerFactory.getLogger(Castle.class);
  private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
  private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
  private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
  private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
  private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
  private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";
  private final IntObjectMap<MerchantGuard> _merchantGuards = new HashIntObjectMap();
  private List<CropProcure> _procure;
  private List<SeedProduction> _production;
  private List<CropProcure> _procureNext;
  private List<SeedProduction> _productionNext;
  private boolean _isNextPeriodApproved;
  private int _TaxPercent;
  private double _TaxRate;
  private long _treasury;
  private long _collectedShops;
  private long _collectedSeed;
  private final NpcString _npcStringName;
  private Set<ItemInstance> _spawnMerchantTickets = new CopyOnWriteArraySet();

  public Castle(StatsSet set) {
    super(set);
    this._npcStringName = NpcString.valueOf(1001000 + this._id);
  }

  public ResidenceType getType() {
    return ResidenceType.Castle;
  }

  public void changeOwner(Clan newOwner) {
    Castle oldCastle;
    if (newOwner != null && newOwner.getCastle() != 0) {
      oldCastle = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, newOwner.getCastle());
      if (oldCastle != null) {
        oldCastle.changeOwner((Clan)null);
      }
    }

    oldCastle = null;
    if (this.getOwnerId() > 0 && (newOwner == null || newOwner.getClanId() != this.getOwnerId())) {
      this.removeSkills();
      this.setTaxPercent((Player)null, 0);
      this.cancelCycleTask();
      Clan oldOwner = this.getOwner();
      if (oldOwner != null) {
        long amount = this.getTreasury();
        if (amount > 0L) {
          Warehouse warehouse = oldOwner.getWarehouse();
          if (warehouse != null) {
            warehouse.addItem(57, amount);
            this.addToTreasuryNoTax(-amount, false, false);
            Log.add(this.getName() + "|" + -amount + "|Castle:changeOwner", "treasury");
          }
        }

        Iterator var8 = oldOwner.getOnlineMembers(0).iterator();

        while(var8.hasNext()) {
          Player clanMember = (Player)var8.next();
          if (clanMember != null && clanMember.getInventory() != null) {
            clanMember.getInventory().validateItems();
          }
        }

        oldOwner.setHasCastle(0);
      }
    }

    if (newOwner != null) {
      newOwner.setHasCastle(this.getId());
    }

    this.updateOwnerInDB(newOwner);
    this.rewardSkills();
    this.update();
  }

  protected void loadData() {
    this._TaxPercent = 0;
    this._TaxRate = 0.0D;
    this._treasury = 0L;
    this._procure = new ArrayList<>();
    this._production = new ArrayList<>();
    this._procureNext = new ArrayList<>();
    this._productionNext = new ArrayList<>();
    this._isNextPeriodApproved = false;
    this._owner = ClanDataDAO.getInstance().getOwner(this);
    CastleDAO.getInstance().select(this);
    CastleHiredGuardDAO.getInstance().load(this);
  }

  public void setTaxPercent(int p) {
    this._TaxPercent = Math.min(Math.max(0, p), 100);
    this._TaxRate = (double)this._TaxPercent / 100.0D;
  }

  public void setTreasury(long t) {
    this._treasury = t;
  }

  private void updateOwnerInDB(Clan clan) {
    this._owner = clan;
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=? LIMIT 1");
      statement.setInt(1, this.getId());
      statement.execute();
      DbUtils.close(statement);
      if (clan != null) {
        statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=? LIMIT 1");
        statement.setInt(1, this.getId());
        statement.setInt(2, this.getOwnerId());
        statement.execute();
        clan.broadcastClanStatus(true, false, false);
      }
    } catch (Exception var8) {
      _log.error("", var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public int getTaxPercent() {
    if (this._TaxPercent > 5 && SevenSigns.getInstance().getSealOwner(3) == 1) {
      this._TaxPercent = 5;
    }

    return this._TaxPercent;
  }

  public int getTaxPercent0() {
    return this._TaxPercent;
  }

  public long getCollectedShops() {
    return this._collectedShops;
  }

  public long getCollectedSeed() {
    return this._collectedSeed;
  }

  public void setCollectedShops(long value) {
    this._collectedShops = value;
  }

  public void setCollectedSeed(long value) {
    this._collectedSeed = value;
  }

  public void addToTreasury(long amount, boolean shop, boolean seed) {
    if (this.getOwnerId() > 0) {
      if (amount != 0L) {
        if (amount > 1L && this._id != 5 && this._id != 8) {
          Castle royal = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._id >= 7 ? 8 : 5);
          if (royal != null) {
            long royalTax = (long)((double)amount * royal.getTaxRate());
            if (royal.getOwnerId() > 0) {
              royal.addToTreasury(royalTax, shop, seed);
              if (this._id == 5) {
                Log.add("Aden|" + royalTax + "|Castle:adenTax", "treasury");
              } else if (this._id == 8) {
                Log.add("Rune|" + royalTax + "|Castle:runeTax", "treasury");
              }
            }

            amount -= royalTax;
          }
        }

        this.addToTreasuryNoTax(amount, shop, seed);
      }
    }
  }

  public void addToTreasuryNoTax(long amount, boolean shop, boolean seed) {
    if (this.getOwnerId() > 0) {
      if (amount != 0L) {
        GameStats.addAdena(amount);
        this._treasury = SafeMath.addAndLimit(this._treasury, amount);
        if (shop) {
          this._collectedShops += amount;
        }

        if (seed) {
          this._collectedSeed += amount;
        }

        this.setJdbcState(JdbcEntityState.UPDATED);
        this.update();
      }
    }
  }

  public int getCropRewardType(int crop) {
    int rw = 0;
    Iterator var3 = this._procure.iterator();

    while(var3.hasNext()) {
      CropProcure cp = (CropProcure)var3.next();
      if (cp.getId() == crop) {
        rw = cp.getReward();
      }
    }

    return rw;
  }

  public void setTaxPercent(Player activeChar, int taxPercent) {
    this.setTaxPercent(taxPercent);
    this.setJdbcState(JdbcEntityState.UPDATED);
    this.update();
    if (activeChar != null) {
      activeChar.sendMessage((new CustomMessage("l2p.gameserver.model.entity.Castle.OutOfControl.CastleTaxChangetTo", activeChar, new Object[0])).addString(this.getName()).addNumber((long)taxPercent));
    }

  }

  public double getTaxRate() {
    if (this._TaxRate > 0.05D && SevenSigns.getInstance().getSealOwner(3) == 1) {
      this._TaxRate = 0.05D;
    }

    return this._TaxRate;
  }

  public long getTreasury() {
    return this._treasury;
  }

  public List<SeedProduction> getSeedProduction(int period) {
    return period == 0 ? this._production : this._productionNext;
  }

  public List<CropProcure> getCropProcure(int period) {
    return period == 0 ? this._procure : this._procureNext;
  }

  public void setSeedProduction(List<SeedProduction> seed, int period) {
    if (period == 0) {
      this._production = seed;
    } else {
      this._productionNext = seed;
    }

  }

  public void setCropProcure(List<CropProcure> crop, int period) {
    if (period == 0) {
      this._procure = crop;
    } else {
      this._procureNext = crop;
    }

  }

  public synchronized SeedProduction getSeed(int seedId, int period) {
    Iterator var3 = this.getSeedProduction(period).iterator();

    SeedProduction seed;
    do {
      if (!var3.hasNext()) {
        return null;
      }

      seed = (SeedProduction)var3.next();
    } while(seed.getId() != seedId);

    return seed;
  }

  public synchronized CropProcure getCrop(int cropId, int period) {
    Iterator var3 = this.getCropProcure(period).iterator();

    CropProcure crop;
    do {
      if (!var3.hasNext()) {
        return null;
      }

      crop = (CropProcure)var3.next();
    } while(crop.getId() != cropId);

    return crop;
  }

  public long getManorCost(int period) {
    List procure;
    List production;
    if (period == 0) {
      procure = this._procure;
      production = this._production;
    } else {
      procure = this._procureNext;
      production = this._productionNext;
    }

    long total = 0L;
    Iterator var6;
    SeedProduction seed;
    if (production != null) {
      for(var6 = production.iterator(); var6.hasNext(); total += Manor.getInstance().getSeedBuyPrice(seed.getId()) * seed.getStartProduce()) {
        seed = (SeedProduction)var6.next();
      }
    }

    CropProcure crop;
    if (procure != null) {
      for(var6 = procure.iterator(); var6.hasNext(); total += crop.getPrice() * crop.getStartAmount()) {
        crop = (CropProcure)var6.next();
      }
    }

    return total;
  }

  public void saveSeedData() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM castle_manor_production WHERE castle_id=?;");
      statement.setInt(1, this.getId());
      statement.execute();
      DbUtils.close(statement);
      int count;
      String query;
      String[] values;
      Iterator var6;
      SeedProduction s;
      int i;
      if (this._production != null) {
        count = 0;
        query = "INSERT INTO castle_manor_production VALUES ";
        values = new String[this._production.size()];

        for(var6 = this._production.iterator(); var6.hasNext(); ++count) {
          s = (SeedProduction)var6.next();
          values[count] = "(" + this.getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + 0 + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }

      if (this._productionNext != null) {
        count = 0;
        query = "INSERT INTO castle_manor_production VALUES ";
        values = new String[this._productionNext.size()];

        for(var6 = this._productionNext.iterator(); var6.hasNext(); ++count) {
          s = (SeedProduction)var6.next();
          values[count] = "(" + this.getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + 1 + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }
    } catch (Exception var11) {
      _log.error("Error adding seed production data for castle " + this.getName() + "!", var11);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void saveSeedData(int period) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;");
      statement.setInt(1, this.getId());
      statement.setInt(2, period);
      statement.execute();
      DbUtils.close(statement);
      List<SeedProduction> prod = null;
      prod = this.getSeedProduction(period);
      if (prod != null) {
        int count = 0;
        String query = "INSERT INTO castle_manor_production VALUES ";
        String[] values = new String[prod.size()];

        for(Iterator var8 = prod.iterator(); var8.hasNext(); ++count) {
          SeedProduction s = (SeedProduction)var8.next();
          values[count] = "(" + this.getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + period + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(int i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }
    } catch (Exception var13) {
      _log.error("Error adding seed production data for castle " + this.getName() + "!", var13);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void saveCropData() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM castle_manor_procure WHERE castle_id=?;");
      statement.setInt(1, this.getId());
      statement.execute();
      DbUtils.close(statement);
      int count;
      String query;
      String[] values;
      Iterator var6;
      CropProcure cp;
      int i;
      if (this._procure != null) {
        count = 0;
        query = "INSERT INTO castle_manor_procure VALUES ";
        values = new String[this._procure.size()];

        for(var6 = this._procure.iterator(); var6.hasNext(); ++count) {
          cp = (CropProcure)var6.next();
          values[count] = "(" + this.getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + 0 + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }

      if (this._procureNext != null) {
        count = 0;
        query = "INSERT INTO castle_manor_procure VALUES ";
        values = new String[this._procureNext.size()];

        for(var6 = this._procureNext.iterator(); var6.hasNext(); ++count) {
          cp = (CropProcure)var6.next();
          values[count] = "(" + this.getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + 1 + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }
    } catch (Exception var11) {
      _log.error("Error adding crop data for castle " + this.getName() + "!", var11);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void saveCropData(int period) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;");
      statement.setInt(1, this.getId());
      statement.setInt(2, period);
      statement.execute();
      DbUtils.close(statement);
      List<CropProcure> proc = null;
      proc = this.getCropProcure(period);
      if (proc != null) {
        int count = 0;
        String query = "INSERT INTO castle_manor_procure VALUES ";
        String[] values = new String[proc.size()];

        for(Iterator var8 = proc.iterator(); var8.hasNext(); ++count) {
          CropProcure cp = (CropProcure)var8.next();
          values[count] = "(" + this.getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + period + ")";
        }

        if (values.length > 0) {
          query = query + values[0];

          for(int i = 1; i < values.length; ++i) {
            query = query + "," + values[i];
          }

          statement = con.prepareStatement(query);
          statement.execute();
          DbUtils.close(statement);
        }
      }
    } catch (Exception var13) {
      _log.error("Error adding crop data for castle " + this.getName() + "!", var13);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void updateCrop(int cropId, long amount, int period) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?");
      statement.setLong(1, amount);
      statement.setInt(2, cropId);
      statement.setInt(3, this.getId());
      statement.setInt(4, period);
      statement.execute();
    } catch (Exception var11) {
      _log.error("Error adding crop data for castle " + this.getName() + "!", var11);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void updateSeed(int seedId, long amount, int period) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?");
      statement.setLong(1, amount);
      statement.setInt(2, seedId);
      statement.setInt(3, this.getId());
      statement.setInt(4, period);
      statement.execute();
    } catch (Exception var11) {
      _log.error("Error adding seed production data for castle " + this.getName() + "!", var11);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public boolean isNextPeriodApproved() {
    return this._isNextPeriodApproved;
  }

  public void setNextPeriodApproved(boolean val) {
    this._isNextPeriodApproved = val;
  }

  public void update() {
    CastleDAO.getInstance().update(this);
  }

  public NpcString getNpcStringName() {
    return this._npcStringName;
  }

  public void addMerchantGuard(MerchantGuard merchantGuard) {
    this._merchantGuards.put(merchantGuard.getItemId(), merchantGuard);
  }

  public MerchantGuard getMerchantGuard(int itemId) {
    return (MerchantGuard)this._merchantGuards.get(itemId);
  }

  public IntObjectMap<MerchantGuard> getMerchantGuards() {
    return this._merchantGuards;
  }

  public Set<ItemInstance> getSpawnMerchantTickets() {
    return this._spawnMerchantTickets;
  }

  public void startCycleTask() {
  }
}
