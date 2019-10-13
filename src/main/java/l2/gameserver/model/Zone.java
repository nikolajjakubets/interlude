//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.s2c.EventTrigger;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncAdd;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zone {
  private static final Logger _log = LoggerFactory.getLogger(Zone.class);
  public static final Zone[] EMPTY_L2ZONE_ARRAY = new Zone[0];
  public static final String BLOCKED_ACTION_PRIVATE_STORE = "open_private_store";
  public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "open_private_workshop";
  public static final String BLOCKED_ACTION_DROP_MERCHANT_GUARD = "drop_merchant_guard";
  public static final String BLOCKED_ACTION_SAVE_BOOKMARK = "save_bookmark";
  public static final String BLOCKED_ACTION_USE_BOOKMARK = "use_bookmark";
  public static final String BLOCKED_ACTION_MINIMAP = "open_minimap";
  public static final String BLOCKED_ACTION_DROP_ITEM = "drop_item";
  private Zone.ZoneType _type;
  private boolean _active;
  private final MultiValueSet<String> _params;
  private final ZoneTemplate _template;
  private Reflection _reflection;
  private final Zone.ZoneListenerList listeners;
  private final ReadWriteLock lock;
  private final Lock readLock;
  private final Lock writeLock;
  private final List<Creature> _objects;
  private final Map<Creature, Zone.ZoneTimer> _zoneTimers;
  public static final int ZONE_STATS_ORDER = 64;

  public Zone(ZoneTemplate template) {
    this(template.getType(), template);
  }

  public Zone(Zone.ZoneType type, ZoneTemplate template) {
    this.listeners = new Zone.ZoneListenerList();
    this.lock = new ReentrantReadWriteLock();
    this.readLock = this.lock.readLock();
    this.writeLock = this.lock.writeLock();
    this._objects = new LazyArrayList(32);
    this._zoneTimers = new ConcurrentHashMap();
    this._type = type;
    this._template = template;
    this._params = template.getParams();
  }

  public ZoneTemplate getTemplate() {
    return this._template;
  }

  public final String getName() {
    return this.getTemplate().getName();
  }

  public Zone.ZoneType getType() {
    return this._type;
  }

  public void setType(Zone.ZoneType type) {
    this._type = type;
  }

  public Territory getTerritory() {
    return this.getTemplate().getTerritory();
  }

  public final int getEnteringMessageId() {
    return this.getTemplate().getEnteringMessageId();
  }

  public final int getLeavingMessageId() {
    return this.getTemplate().getLeavingMessageId();
  }

  public Skill getZoneSkill() {
    return this.getTemplate().getZoneSkill();
  }

  public Zone.ZoneTarget getZoneTarget() {
    return this.getTemplate().getZoneTarget();
  }

  public Race getAffectRace() {
    return this.getTemplate().getAffectRace();
  }

  public int getDamageMessageId() {
    return this.getTemplate().getDamageMessageId();
  }

  public int getDamageOnHP() {
    return this.getTemplate().getDamageOnHP();
  }

  public int getDamageOnMP() {
    return this.getTemplate().getDamageOnMP();
  }

  public double getMoveBonus() {
    return this.getTemplate().getMoveBonus();
  }

  public double getRegenBonusHP() {
    return this.getTemplate().getRegenBonusHP();
  }

  public double getRegenBonusMP() {
    return this.getTemplate().getRegenBonusMP();
  }

  public long getRestartTime() {
    return this.getTemplate().getRestartTime();
  }

  public List<Location> getRestartPoints() {
    return this.getTemplate().getRestartPoints();
  }

  public List<Location> getPKRestartPoints() {
    return this.getTemplate().getPKRestartPoints();
  }

  public Location getSpawn() {
    if (this.getRestartPoints() == null) {
      return null;
    } else {
      Location loc = (Location)this.getRestartPoints().get(Rnd.get(this.getRestartPoints().size()));
      return loc.clone();
    }
  }

  public Location getPKSpawn() {
    if (this.getPKRestartPoints() == null) {
      return this.getSpawn();
    } else {
      Location loc = (Location)this.getPKRestartPoints().get(Rnd.get(this.getPKRestartPoints().size()));
      return loc.clone();
    }
  }

  public boolean checkIfInZone(int x, int y) {
    return this.getTerritory().isInside(x, y);
  }

  public boolean checkIfInZone(int x, int y, int z) {
    return this.checkIfInZone(x, y, z, this.getReflection());
  }

  public boolean checkIfInZone(int x, int y, int z, Reflection reflection) {
    return this.isActive() && this._reflection == reflection && this.getTerritory().isInside(x, y, z);
  }

  public boolean checkIfInZone(Creature cha) {
    this.readLock.lock();

    boolean var2;
    try {
      var2 = this._objects.contains(cha);
    } finally {
      this.readLock.unlock();
    }

    return var2;
  }

  public final double findDistanceToZone(GameObject obj, boolean includeZAxis) {
    return this.findDistanceToZone(obj.getX(), obj.getY(), obj.getZ(), includeZAxis);
  }

  public final double findDistanceToZone(int x, int y, int z, boolean includeZAxis) {
    return PositionUtils.calculateDistance(x, y, z, (this.getTerritory().getXmax() + this.getTerritory().getXmin()) / 2, (this.getTerritory().getYmax() + this.getTerritory().getYmin()) / 2, (this.getTerritory().getZmax() + this.getTerritory().getZmin()) / 2, includeZAxis);
  }

  public void doEnter(Creature cha) {
    boolean added = false;
    this.writeLock.lock();

    try {
      if (!this._objects.contains(cha)) {
        added = this._objects.add(cha);
      }
    } finally {
      this.writeLock.unlock();
    }

    if (added) {
      this.onZoneEnter(cha);
    }

  }

  protected void onZoneEnter(Creature actor) {
    this.checkEffects(actor, true);
    this.addZoneStats(actor);
    if (actor.isPlayer()) {
      if (this.getEnteringMessageId() != 0) {
        actor.sendPacket(new SystemMessage(this.getEnteringMessageId()));
      }

      if (this.getTemplate().getEventId() != 0) {
        actor.sendPacket(new EventTrigger(this.getTemplate().getEventId(), true));
      }

      if (this.getTemplate().getBlockedActions() != null) {
        ((Player)actor).blockActions(this.getTemplate().getBlockedActions());
      }
    }

    this.listeners.onEnter(actor);
  }

  public void doLeave(Creature cha) {
    boolean removed = false;
    this.writeLock.lock();

    try {
      removed = this._objects.remove(cha);
    } finally {
      this.writeLock.unlock();
    }

    if (removed) {
      this.onZoneLeave(cha);
    }

  }

  protected void onZoneLeave(Creature actor) {
    this.checkEffects(actor, false);
    this.removeZoneStats(actor);
    if (actor.isPlayer()) {
      if (this.getLeavingMessageId() != 0 && actor.isPlayer()) {
        actor.sendPacket(new SystemMessage(this.getLeavingMessageId()));
      }

      if (this.getTemplate().getEventId() != 0 && actor.isPlayer()) {
        actor.sendPacket(new EventTrigger(this.getTemplate().getEventId(), false));
      }

      if (this.getTemplate().getBlockedActions() != null) {
        ((Player)actor).unblockActions(this.getTemplate().getBlockedActions());
      }
    }

    this.listeners.onLeave(actor);
  }

  private void addZoneStats(Creature cha) {
    if (this.checkTarget(cha)) {
      if (this.getMoveBonus() != 0.0D && cha.isPlayable()) {
        cha.addStatFunc(new FuncAdd(Stats.RUN_SPEED, 64, this, this.getMoveBonus()));
        cha.sendChanges();
      }

      if (this.getRegenBonusHP() != 0.0D) {
        cha.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, 64, this, this.getRegenBonusHP()));
      }

      if (this.getRegenBonusMP() != 0.0D) {
        cha.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, 64, this, this.getRegenBonusMP()));
      }

    }
  }

  private void removeZoneStats(Creature cha) {
    if (this.getRegenBonusHP() != 0.0D || this.getRegenBonusMP() != 0.0D || this.getMoveBonus() != 0.0D) {
      cha.removeStatsOwner(this);
      cha.sendChanges();
    }
  }

  private void checkEffects(Creature cha, boolean enter) {
    if (this.checkTarget(cha)) {
      if (enter) {
        if (this.getZoneSkill() != null) {
          Zone.ZoneTimer timer = new Zone.SkillTimer(cha);
          this._zoneTimers.put(cha, timer);
          timer.start();
        } else if (this.getDamageOnHP() > 0 || this.getDamageOnMP() > 0) {
          Zone.ZoneTimer timer = new Zone.DamageTimer(cha);
          this._zoneTimers.put(cha, timer);
          timer.start();
        }
      } else {
        Zone.ZoneTimer timer = (Zone.ZoneTimer)this._zoneTimers.remove(cha);
        if (timer != null) {
          timer.stop();
        }

        if (this.getZoneSkill() != null) {
          cha.getEffectList().stopEffect(this.getZoneSkill());
        }
      }
    }

  }

  private boolean checkTarget(Creature cha) {
    switch(this.getZoneTarget()) {
      case pc:
        if (!cha.isPlayable()) {
          return false;
        }
        break;
      case only_pc:
        if (!cha.isPlayer()) {
          return false;
        }
        break;
      case npc:
        if (!cha.isNpc()) {
          return false;
        }
    }

    if (this.getAffectRace() != null) {
      Player player = cha.getPlayer();
      if (player == null) {
        return false;
      }

      if (player.getRace() != this.getAffectRace()) {
        return false;
      }
    }

    return true;
  }

  public Creature[] getObjects() {
    this.readLock.lock();

    Creature[] var1;
    try {
      var1 = (Creature[])this._objects.toArray(new Creature[this._objects.size()]);
    } finally {
      this.readLock.unlock();
    }

    return var1;
  }

  public List<Player> getInsidePlayers() {
    List<Player> result = new LazyArrayList();
    this.readLock.lock();

    try {
      for(int i = 0; i < this._objects.size(); ++i) {
        Creature cha;
        if ((cha = (Creature)this._objects.get(i)) != null && cha.isPlayer()) {
          result.add((Player)cha);
        }
      }
    } finally {
      this.readLock.unlock();
    }

    return result;
  }

  public List<Playable> getInsidePlayables() {
    List<Playable> result = new LazyArrayList();
    this.readLock.lock();

    try {
      for(int i = 0; i < this._objects.size(); ++i) {
        Creature cha;
        if ((cha = (Creature)this._objects.get(i)) != null && cha.isPlayable()) {
          result.add((Playable)cha);
        }
      }
    } finally {
      this.readLock.unlock();
    }

    return result;
  }

  public void setActive(boolean value) {
    this.writeLock.lock();

    label49: {
      try {
        if (this._active != value) {
          this._active = value;
          break label49;
        }
      } finally {
        this.writeLock.unlock();
      }

      return;
    }

    if (this.isActive()) {
      World.addZone(this);
    } else {
      World.removeZone(this);
    }

  }

  public boolean isActive() {
    return this._active;
  }

  public void setReflection(Reflection reflection) {
    this._reflection = reflection;
  }

  public Reflection getReflection() {
    return this._reflection;
  }

  public void setParam(String name, String value) {
    this._params.put(name, value);
  }

  public void setParam(String name, Object value) {
    this._params.put(name, value);
  }

  public MultiValueSet<String> getParams() {
    return this._params;
  }

  public <T extends Listener<Zone>> boolean addListener(T listener) {
    return this.listeners.add(listener);
  }

  public <T extends Listener<Zone>> boolean removeListener(T listener) {
    return this.listeners.remove(listener);
  }

  public final String toString() {
    return "[Zone " + this.getType() + " name: " + this.getName() + "]";
  }

  public void broadcastPacket(L2GameServerPacket packet, boolean toAliveOnly) {
    List<Player> insideZoners = this.getInsidePlayers();
    if (insideZoners != null && !insideZoners.isEmpty()) {
      Iterator var4 = insideZoners.iterator();

      while(var4.hasNext()) {
        Player player = (Player)var4.next();
        if (toAliveOnly) {
          if (!player.isDead()) {
            player.broadcastPacket(new L2GameServerPacket[]{packet});
          }
        } else {
          player.broadcastPacket(new L2GameServerPacket[]{packet});
        }
      }
    }

  }

  public class ZoneListenerList extends ListenerList<Zone> {
    public ZoneListenerList() {
    }

    public void onEnter(Creature actor) {
      if (!this.getListeners().isEmpty()) {
        Iterator var2 = this.getListeners().iterator();

        while(var2.hasNext()) {
          Listener<Zone> listener = (Listener)var2.next();
          ((OnZoneEnterLeaveListener)listener).onZoneEnter(Zone.this, actor);
        }
      }

    }

    public void onLeave(Creature actor) {
      if (!this.getListeners().isEmpty()) {
        Iterator var2 = this.getListeners().iterator();

        while(var2.hasNext()) {
          Listener<Zone> listener = (Listener)var2.next();
          ((OnZoneEnterLeaveListener)listener).onZoneLeave(Zone.this, actor);
        }
      }

    }
  }

  private class DamageTimer extends Zone.ZoneTimer {
    public DamageTimer(Creature cha) {
      super(cha);
    }

    public void runImpl() throws Exception {
      if (Zone.this.isActive()) {
        if (Zone.this.checkTarget(this.cha)) {
          int hp = Zone.this.getDamageOnHP();
          int mp = Zone.this.getDamageOnMP();
          int message = Zone.this.getDamageMessageId();
          if (hp != 0 || mp != 0) {
            if (hp > 0) {
              this.cha.reduceCurrentHp((double)hp, this.cha, (Skill)null, false, false, true, false, false, false, true);
              if (message > 0) {
                this.cha.sendPacket((new SystemMessage(message)).addNumber(hp));
              }
            }

            if (mp > 0) {
              this.cha.reduceCurrentMp((double)mp, (Creature)null);
              if (message > 0) {
                this.cha.sendPacket((new SystemMessage(message)).addNumber(mp));
              }
            }

            this.next();
          }
        }
      }
    }
  }

  private class SkillTimer extends Zone.ZoneTimer {
    public SkillTimer(Creature cha) {
      super(cha);
    }

    public void runImpl() throws Exception {
      if (Zone.this.isActive()) {
        if (Zone.this.checkTarget(this.cha)) {
          Skill skill = Zone.this.getZoneSkill();
          if (skill != null) {
            if (Rnd.chance(Zone.this.getTemplate().getSkillProb()) && !this.cha.isDead()) {
              skill.getEffects(this.cha, this.cha, false, false);
            }

            this.next();
          }
        }
      }
    }
  }

  private abstract class ZoneTimer extends RunnableImpl {
    protected Creature cha;
    protected Future<?> future;
    protected boolean active;

    public ZoneTimer(Creature cha) {
      this.cha = cha;
    }

    public void start() {
      this.active = true;
      this.future = EffectTaskManager.getInstance().schedule(this, (long)Zone.this.getTemplate().getInitialDelay() * 1000L);
    }

    public void stop() {
      this.active = false;
      if (this.future != null) {
        this.future.cancel(false);
        this.future = null;
      }

    }

    public void next() {
      if (this.active) {
        if (Zone.this.getTemplate().getUnitTick() != 0 || Zone.this.getTemplate().getRandomTick() != 0) {
          this.future = EffectTaskManager.getInstance().schedule(this, (long)(Zone.this.getTemplate().getUnitTick() + Rnd.get(0, Zone.this.getTemplate().getRandomTick())) * 1000L);
        }
      }
    }

    public abstract void runImpl() throws Exception;
  }

  public static enum ZoneTarget {
    pc,
    npc,
    only_pc;

    private ZoneTarget() {
    }
  }

  public static enum ZoneType {
    SIEGE,
    RESIDENCE,
    HEADQUARTER,
    FISHING,
    water,
    battle_zone,
    damage,
    instant_skill,
    mother_tree,
    peace_zone,
    poison,
    ssq_zone,
    swamp,
    no_escape,
    no_landing,
    no_restart,
    no_summon,
    dummy,
    offshore,
    epic,
    fun;

    private ZoneType() {
    }
  }
}
