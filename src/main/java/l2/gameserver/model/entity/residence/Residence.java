//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import l2.commons.dao.JdbcEntity;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Residence implements JdbcEntity {
  private static final Logger _log = LoggerFactory.getLogger(Residence.class);
  public static final long CYCLE_TIME = 3600000L;
  protected final int _id;
  protected final String _name;
  protected Clan _owner;
  protected Zone _zone;
  protected List<ResidenceFunction> _functions = new ArrayList();
  protected List<Skill> _skills = new ArrayList();
  protected SiegeEvent<?, ?> _siegeEvent;
  protected Calendar _siegeDate = Calendar.getInstance();
  protected Calendar _lastSiegeDate = Calendar.getInstance();
  protected Calendar _ownDate = Calendar.getInstance();
  protected ScheduledFuture<?> _cycleTask;
  private int _cycle;
  private int _rewardCount;
  private int _paidCycle;
  protected JdbcEntityState _jdbcEntityState;
  protected List<Location> _banishPoints;
  protected List<Location> _ownerRestartPoints;
  protected List<Location> _otherRestartPoints;
  protected List<Location> _chaosRestartPoints;

  public Residence(StatsSet set) {
    this._jdbcEntityState = JdbcEntityState.CREATED;
    this._banishPoints = new ArrayList();
    this._ownerRestartPoints = new ArrayList();
    this._otherRestartPoints = new ArrayList();
    this._chaosRestartPoints = new ArrayList();
    this._id = set.getInteger("id");
    this._name = set.getString("name");
  }

  public abstract ResidenceType getType();

  public void init() {
    this.initZone();
    this.initEvent();
    this.loadData();
    this.loadFunctions();
    this.rewardSkills();
    this.startCycleTask();
  }

  protected void initZone() {
    this._zone = ReflectionUtils.getZone("residence_" + this._id);
    this._zone.setParam("residence", this);
  }

  protected void initEvent() {
    this._siegeEvent = (SiegeEvent)EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, this._id);
  }

  public <E extends SiegeEvent> E getSiegeEvent() {
    return this._siegeEvent;
  }

  public int getId() {
    return this._id;
  }

  public String getName() {
    return this._name;
  }

  public int getOwnerId() {
    return this._owner == null ? 0 : this._owner.getClanId();
  }

  public Clan getOwner() {
    return this._owner;
  }

  public Zone getZone() {
    return this._zone;
  }

  protected abstract void loadData();

  public abstract void changeOwner(Clan var1);

  public Calendar getOwnDate() {
    return this._ownDate;
  }

  public Calendar getSiegeDate() {
    return this._siegeDate;
  }

  public Calendar getLastSiegeDate() {
    return this._lastSiegeDate;
  }

  public void addSkill(Skill skill) {
    this._skills.add(skill);
  }

  public void addFunction(ResidenceFunction function) {
    this._functions.add(function);
  }

  public boolean checkIfInZone(Location loc, Reflection ref) {
    return this.checkIfInZone(loc.x, loc.y, loc.z, ref);
  }

  public boolean checkIfInZone(int x, int y, int z, Reflection ref) {
    return this.getZone() != null && this.getZone().checkIfInZone(x, y, z, ref);
  }

  public void banishForeigner() {
    Iterator var1 = this._zone.getInsidePlayers().iterator();

    while(var1.hasNext()) {
      Player player = (Player)var1.next();
      if (player.getClanId() != this.getOwnerId()) {
        player.teleToLocation(this.getBanishPoint());
      }
    }

  }

  public void rewardSkills() {
    Clan owner = this.getOwner();
    if (owner != null) {
      Iterator var2 = this._skills.iterator();

      while(var2.hasNext()) {
        Skill skill = (Skill)var2.next();
        owner.addSkill(skill, false);
        owner.broadcastToOnlineMembers(new L2GameServerPacket[]{(new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED)).addSkillName(skill)});
      }
    }

  }

  public void removeSkills() {
    Clan owner = this.getOwner();
    if (owner != null) {
      Iterator var2 = this._skills.iterator();

      while(var2.hasNext()) {
        Skill skill = (Skill)var2.next();
        owner.removeSkill(skill.getId());
      }
    }

  }

  protected void loadFunctions() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM residence_functions WHERE id = ?");
      statement.setInt(1, this.getId());
      rs = statement.executeQuery();

      while(rs.next()) {
        ResidenceFunction function = this.getFunction(rs.getInt("type"));
        function.setLvl(rs.getInt("lvl"));
        function.setEndTimeInMillis((long)rs.getInt("endTime") * 1000L);
        function.setInDebt(rs.getBoolean("inDebt"));
        function.setActive(true);
        this.startAutoTaskForFunction(function);
      }
    } catch (Exception var8) {
      _log.warn("Residence: loadFunctions(): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

  }

  public boolean isFunctionActive(int type) {
    ResidenceFunction function = this.getFunction(type);
    return function != null && function.isActive() && function.getLevel() > 0;
  }

  public ResidenceFunction getFunction(int type) {
    for(int i = 0; i < this._functions.size(); ++i) {
      if (((ResidenceFunction)this._functions.get(i)).getType() == type) {
        return (ResidenceFunction)this._functions.get(i);
      }
    }

    return null;
  }

  public boolean updateFunctions(int type, int level) {
    Clan clan = this.getOwner();
    if (clan == null) {
      return false;
    } else {
      long count = clan.getAdenaCount();
      ResidenceFunction function = this.getFunction(type);
      if (function == null) {
        return false;
      } else if (function.isActive() && function.getLevel() == level) {
        return true;
      } else {
        int lease = level == 0 ? 0 : this.getFunction(type).getLease(level);
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          boolean var17;
          if (!function.isActive()) {
            if (count < (long)lease) {
              var17 = false;
              return var17;
            }

            clan.getWarehouse().destroyItemByItemId(57, (long)lease);
            long time = Calendar.getInstance().getTimeInMillis() + 86400000L;
            statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?, endTime=?");
            statement.setInt(1, this.getId());
            statement.setInt(2, type);
            statement.setInt(3, level);
            statement.setInt(4, (int)(time / 1000L));
            statement.execute();
            function.setLvl(level);
            function.setEndTimeInMillis(time);
            function.setActive(true);
            this.startAutoTaskForFunction(function);
          } else {
            if (count < (long)(lease - this.getFunction(type).getLease())) {
              var17 = false;
              return var17;
            }

            if (lease > this.getFunction(type).getLease()) {
              clan.getWarehouse().destroyItemByItemId(57, (long)(lease - this.getFunction(type).getLease()));
            }

            statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?");
            statement.setInt(1, this.getId());
            statement.setInt(2, type);
            statement.setInt(3, level);
            statement.execute();
            function.setLvl(level);
          }
        } catch (Exception var15) {
          _log.warn("Exception: SiegeUnit.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + var15);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }

        return true;
      }
    }
  }

  public void removeFunction(int type) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=? AND type=?");
      statement.setInt(1, this.getId());
      statement.setInt(2, type);
      statement.execute();
    } catch (Exception var8) {
      _log.warn("Exception: removeFunctions(int type): " + var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private void startAutoTaskForFunction(ResidenceFunction function) {
    if (this.getOwnerId() != 0) {
      Clan clan = this.getOwner();
      if (clan != null) {
        if (function.getEndTimeInMillis() > System.currentTimeMillis()) {
          ThreadPoolManager.getInstance().schedule(new Residence.AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (function.isInDebt() && clan.getAdenaCount() >= (long)function.getLease()) {
          clan.getWarehouse().destroyItemByItemId(57, (long)function.getLease());
          function.updateRentTime(false);
          ThreadPoolManager.getInstance().schedule(new Residence.AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (!function.isInDebt()) {
          function.setInDebt(true);
          function.updateRentTime(true);
          ThreadPoolManager.getInstance().schedule(new Residence.AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else {
          function.setLvl(0);
          function.setActive(false);
          this.removeFunction(function.getType());
        }

      }
    }
  }

  public void setJdbcState(JdbcEntityState state) {
    this._jdbcEntityState = state;
  }

  public JdbcEntityState getJdbcState() {
    return this._jdbcEntityState;
  }

  public void save() {
    throw new UnsupportedOperationException();
  }

  public void delete() {
    throw new UnsupportedOperationException();
  }

  public void cancelCycleTask() {
    this._cycle = 0;
    this._paidCycle = 0;
    this._rewardCount = 0;
    if (this._cycleTask != null) {
      this._cycleTask.cancel(false);
      this._cycleTask = null;
    }

    this.setJdbcState(JdbcEntityState.UPDATED);
  }

  public void startCycleTask() {
    if (this._owner != null) {
      long ownedTime = this.getOwnDate().getTimeInMillis();
      if (ownedTime != 0L) {
        long diff;
        for(diff = System.currentTimeMillis() - ownedTime; diff >= 3600000L; diff -= 3600000L) {
        }

        this._cycleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Residence.ResidenceCycleTask(), diff, 3600000L);
      }
    }
  }

  public void chanceCycle() {
    this.setCycle(this.getCycle() + 1);
    this.setJdbcState(JdbcEntityState.UPDATED);
  }

  public List<Skill> getSkills() {
    return this._skills;
  }

  public void addBanishPoint(Location loc) {
    this._banishPoints.add(loc);
  }

  public void addOwnerRestartPoint(Location loc) {
    this._ownerRestartPoints.add(loc);
  }

  public void addOtherRestartPoint(Location loc) {
    this._otherRestartPoints.add(loc);
  }

  public void addChaosRestartPoint(Location loc) {
    this._chaosRestartPoints.add(loc);
  }

  public Location getBanishPoint() {
    return this._banishPoints.isEmpty() ? null : (Location)this._banishPoints.get(Rnd.get(this._banishPoints.size()));
  }

  public Location getOwnerRestartPoint() {
    return this._ownerRestartPoints.isEmpty() ? null : (Location)this._ownerRestartPoints.get(Rnd.get(this._ownerRestartPoints.size()));
  }

  public Location getOtherRestartPoint() {
    return this._otherRestartPoints.isEmpty() ? null : (Location)this._otherRestartPoints.get(Rnd.get(this._otherRestartPoints.size()));
  }

  public Location getChaosRestartPoint() {
    return this._chaosRestartPoints.isEmpty() ? null : (Location)this._chaosRestartPoints.get(Rnd.get(this._chaosRestartPoints.size()));
  }

  public Location getNotOwnerRestartPoint(Player player) {
    return player.getKarma() > 0 ? this.getChaosRestartPoint() : this.getOtherRestartPoint();
  }

  public int getCycle() {
    return this._cycle;
  }

  public long getCycleDelay() {
    return this._cycleTask == null ? 0L : this._cycleTask.getDelay(TimeUnit.SECONDS);
  }

  public void setCycle(int cycle) {
    this._cycle = cycle;
  }

  public int getPaidCycle() {
    return this._paidCycle;
  }

  public void setPaidCycle(int paidCycle) {
    this._paidCycle = paidCycle;
  }

  public int getRewardCount() {
    return this._rewardCount;
  }

  public void setRewardCount(int rewardCount) {
    this._rewardCount = rewardCount;
  }

  private class AutoTaskForFunctions extends RunnableImpl {
    ResidenceFunction _function;

    public AutoTaskForFunctions(ResidenceFunction function) {
      this._function = function;
    }

    public void runImpl() throws Exception {
      Residence.this.startAutoTaskForFunction(this._function);
    }
  }

  public class ResidenceCycleTask extends RunnableImpl {
    public ResidenceCycleTask() {
    }

    public void runImpl() throws Exception {
      Residence.this.chanceCycle();
      Residence.this.update();
    }
  }
}
