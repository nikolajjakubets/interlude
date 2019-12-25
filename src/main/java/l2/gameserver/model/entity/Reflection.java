//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.database.mysql;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.door.impl.MasterOnOpenCloseListenerImpl;
import l2.gameserver.listener.reflection.OnReflectionCollapseListener;
import l2.gameserver.listener.zone.impl.NoLandingZoneListener;
import l2.gameserver.listener.zone.impl.ResidenceEnterLeaveListenerImpl;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.HardSpawner;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.ZoneTemplate;
import l2.gameserver.templates.InstantZone.DoorInfo;
import l2.gameserver.templates.InstantZone.SpawnInfo;
import l2.gameserver.templates.InstantZone.SpawnInfo2;
import l2.gameserver.templates.InstantZone.ZoneInfo;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reflection {
  private static final AtomicInteger _nextId = new AtomicInteger();
  private final int _id;
  private String _name;
  private InstantZone _instance;
  private int _geoIndex;
  private Location _resetLoc;
  private Location _returnLoc;
  private Location _teleportLoc;
  protected List<Spawner> _spawns;
  protected List<GameObject> _objects;
  protected IntObjectMap<DoorInstance> _doors;
  protected Map<String, Zone> _zones;
  protected Map<String, List<Spawner>> _spawners;
  protected TIntHashSet _visitors;
  protected final Lock lock;
  protected int _playerCount;
  protected Party _party;
  protected CommandChannel _commandChannel;
  private int _collapseIfEmptyTime;
  private boolean _isCollapseStarted;
  private Future<?> _collapseTask;
  private Future<?> _collapse1minTask;
  private Future<?> _hiddencollapseTask;
  private final Reflection.ReflectionListenerList listeners;

  public Reflection() {
    this(_nextId.incrementAndGet());
  }

  private Reflection(int id) {
    this._name = "";
    this._spawns = new ArrayList<>();
    this._objects = new ArrayList<>();
    this._doors = Containers.emptyIntObjectMap();
    this._zones = Collections.emptyMap();
    this._spawners = Collections.emptyMap();
    this._visitors = new TIntHashSet();
    this.lock = new ReentrantLock();
    this.listeners = new Reflection.ReflectionListenerList();
    this._id = id;
  }

  public int getId() {
    return this._id;
  }

  public int getInstancedZoneId() {
    return this._instance == null ? 0 : this._instance.getId();
  }

  public void setParty(Party party) {
    this._party = party;
  }

  public Party getParty() {
    return this._party;
  }

  public void setCommandChannel(CommandChannel commandChannel) {
    this._commandChannel = commandChannel;
  }

  public void setCollapseIfEmptyTime(int value) {
    this._collapseIfEmptyTime = value;
  }

  public String getName() {
    return this._name;
  }

  protected void setName(String name) {
    this._name = name;
  }

  public InstantZone getInstancedZone() {
    return this._instance;
  }

  protected void setInstancedZone(InstantZone iz) {
    this._instance = iz;
  }

  public int getGeoIndex() {
    return this._geoIndex;
  }

  protected void setGeoIndex(int geoIndex) {
    this._geoIndex = geoIndex;
  }

  public void setCoreLoc(Location l) {
    this._resetLoc = l;
  }

  public Location getCoreLoc() {
    return this._resetLoc;
  }

  public void setReturnLoc(Location l) {
    this._returnLoc = l;
  }

  public Location getReturnLoc() {
    return this._returnLoc;
  }

  public void setTeleportLoc(Location l) {
    this._teleportLoc = l;
  }

  public Location getTeleportLoc() {
    return this._teleportLoc;
  }

  public List<Spawner> getSpawns() {
    return this._spawns;
  }

  public Collection<DoorInstance> getDoors() {
    return this._doors.values();
  }

  public DoorInstance getDoor(int id) {
    return this._doors.get(id);
  }

  public Zone getZone(String name) {
    return this._zones.get(name);
  }

  public void startCollapseTimer(long timeInMillis) {
    if (!this.isDefault() && !this.isStatic()) {
      this.lock.lock();

      try {
        if (this._collapseTask != null) {
          this._collapseTask.cancel(false);
          this._collapseTask = null;
        }

        if (this._collapse1minTask != null) {
          this._collapse1minTask.cancel(false);
          this._collapse1minTask = null;
        }

        this._collapseTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            Reflection.this.collapse();
          }
        }, timeInMillis);
        if (timeInMillis >= 60000L) {
          this._collapse1minTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() throws Exception {
              Reflection.this.minuteBeforeCollapse();
            }
          }, timeInMillis - 60000L);
        }
      } finally {
        this.lock.unlock();
      }

    } else {
      (new Exception("Basic reflection " + this._id + " could not be collapsed!")).printStackTrace();
    }
  }

  public void stopCollapseTimer() {
    this.lock.lock();

    try {
      if (this._collapseTask != null) {
        this._collapseTask.cancel(false);
        this._collapseTask = null;
      }

      if (this._collapse1minTask != null) {
        this._collapse1minTask.cancel(false);
        this._collapse1minTask = null;
      }
    } finally {
      this.lock.unlock();
    }

  }

  public void minuteBeforeCollapse() {
    if (!this._isCollapseStarted) {
      this.lock.lock();

      try {

        for (GameObject o : this._objects) {
          if (o.isPlayer()) {
            Player player = o.getPlayer();
            player.sendMessage((new CustomMessage("THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES", player)).addNumber(1L));
          }
        }
      } finally {
        this.lock.unlock();
      }

    }
  }

  public void collapse() {
    if (this._id <= 0) {
      (new Exception("Basic reflection " + this._id + " could not be collapsed!")).printStackTrace();
    } else {
      this.lock.lock();

      try {
        if (this._isCollapseStarted) {
          return;
        }

        this._isCollapseStarted = true;
      } finally {
        this.lock.unlock();
      }

      this.listeners.onCollapse();

      try {
        this.stopCollapseTimer();
        if (this._hiddencollapseTask != null) {
          this._hiddencollapseTask.cancel(false);
          this._hiddencollapseTask = null;
        }

        Iterator var1 = this._spawns.iterator();

        while(var1.hasNext()) {
          Spawner s = (Spawner)var1.next();
          s.deleteAll();
        }

        var1 = this._spawners.keySet().iterator();

        while(var1.hasNext()) {
          String group = (String)var1.next();
          this.despawnByGroup(group);
        }

        var1 = this._doors.values().iterator();

        while(var1.hasNext()) {
          DoorInstance d = (DoorInstance)var1.next();
          d.deleteMe();
        }

        this._doors.clear();
        var1 = this._zones.values().iterator();

        while(var1.hasNext()) {
          Zone zone = (Zone)var1.next();
          zone.setActive(false);
        }

        this._zones.clear();
        List<Player> teleport = new ArrayList<>();
        List<GameObject> delete = new ArrayList<>();
        this.lock.lock();

        Iterator var3;
        GameObject o;
        try {
          var3 = this._objects.iterator();

          while(var3.hasNext()) {
            o = (GameObject)var3.next();
            if (o.isPlayer()) {
              teleport.add((Player)o);
            } else if (!o.isPlayable()) {
              delete.add(o);
            }
          }
        } finally {
          this.lock.unlock();
        }

        var3 = teleport.iterator();

        while(var3.hasNext()) {
          Player player = (Player)var3.next();
          if (player.getParty() != null) {
            if (this.equals(player.getParty().getReflection())) {
              player.getParty().setReflection(null);
            }

            if (player.getParty().getCommandChannel() != null && this.equals(player.getParty().getCommandChannel().getReflection())) {
              player.getParty().getCommandChannel().setReflection(null);
            }
          }

          if (this.equals(player.getReflection())) {
            if (this.getReturnLoc() != null) {
              player.teleToLocation(this.getReturnLoc(), ReflectionManager.DEFAULT);
            } else {
              player.setReflection(ReflectionManager.DEFAULT);
            }
          }
        }

        if (this._commandChannel != null) {
          this._commandChannel.setReflection(null);
          this._commandChannel = null;
        }

        if (this._party != null) {
          this._party.setReflection(null);
          this._party = null;
        }

        var3 = delete.iterator();

        while(var3.hasNext()) {
          o = (GameObject)var3.next();
          o.deleteMe();
        }

        this._spawns.clear();
        this._objects.clear();
        this._visitors.clear();
        this._doors.clear();
        this._playerCount = 0;
        this.onCollapse();
      } finally {
        ReflectionManager.getInstance().remove(this);
        GeoEngine.FreeGeoIndex(this.getGeoIndex());
      }
    }
  }

  protected void onCollapse() {
  }

  public void addObject(GameObject o) {
    if (!this._isCollapseStarted) {
      this.lock.lock();

      try {
        this._objects.add(o);
        if (o.isPlayer()) {
          ++this._playerCount;
          this._visitors.add(o.getObjectId());
          this.onPlayerEnter(o.getPlayer());
        }
      } finally {
        this.lock.unlock();
      }

      if (this._collapseIfEmptyTime > 0 && this._hiddencollapseTask != null) {
        this._hiddencollapseTask.cancel(false);
        this._hiddencollapseTask = null;
      }

    }
  }

  public void removeObject(GameObject o) {
    if (!this._isCollapseStarted) {
      this.lock.lock();

      try {
        if (!this._objects.remove(o)) {
          return;
        }

        if (o.isPlayer()) {
          --this._playerCount;
          this.onPlayerExit(o.getPlayer());
        }
      } finally {
        this.lock.unlock();
      }

      if (this._playerCount <= 0 && !this.isDefault() && !this.isStatic() && this._hiddencollapseTask == null) {
        if (this._collapseIfEmptyTime <= 0) {
          this.collapse();
        } else {
          this._hiddencollapseTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() throws Exception {
              Reflection.this.collapse();
            }
          }, (long)(this._collapseIfEmptyTime * 60) * 1000L);
        }
      }

    }
  }

  public void onPlayerEnter(Player player) {
    player.getInventory().validateItems();
  }

  public void onPlayerExit(Player player) {
    player.getInventory().validateItems();
  }

  public List<Player> getPlayers() {
    List<Player> result = new ArrayList<>();
    this.lock.lock();

    try {

      for (GameObject o : this._objects) {
        if (o.isPlayer()) {
          result.add((Player) o);
        }
      }
    } finally {
      this.lock.unlock();
    }

    return result;
  }

  public List<NpcInstance> getNpcs() {
    List<NpcInstance> result = new ArrayList<>();
    this.lock.lock();

    try {

      for (GameObject o : this._objects) {
        if (o.isNpc()) {
          result.add((NpcInstance) o);
        }
      }
    } finally {
      this.lock.unlock();
    }

    return result;
  }

  public List<NpcInstance> getAllByNpcId(int npcId, boolean onlyAlive) {
    List<NpcInstance> result = new ArrayList<>();
    this.lock.lock();

    try {
      Iterator var4 = this._objects.iterator();

      while(true) {
        NpcInstance npc;
        do {
          do {
            GameObject o;
            do {
              if (!var4.hasNext()) {
                return result;
              }

              o = (GameObject)var4.next();
            } while(!o.isNpc());

            npc = (NpcInstance)o;
          } while(npcId != npc.getNpcId());
        } while(onlyAlive && npc.isDead());

        result.add(npc);
      }
    } finally {
      this.lock.unlock();
    }
  }

  public boolean canChampions() {
    return this._id <= 0;
  }

  public boolean isAutolootForced() {
    return false;
  }

  public boolean isCollapseStarted() {
    return this._isCollapseStarted;
  }

  public void addSpawn(SimpleSpawner spawn) {
    if (spawn != null) {
      this._spawns.add(spawn);
    }

  }

  public void fillSpawns(List<SpawnInfo> si) {
    if (si != null) {
      Iterator var2 = si.iterator();

      while(true) {
        label49:
        while(var2.hasNext()) {
          SpawnInfo s = (SpawnInfo)var2.next();
          SimpleSpawner c;
          switch(s.getSpawnType()) {
            case 0:
              Iterator var7 = s.getCoords().iterator();

              while(true) {
                if (!var7.hasNext()) {
                  continue label49;
                }

                Location loc = (Location)var7.next();
                c = new SimpleSpawner(s.getNpcId());
                c.setReflection(this);
                c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                c.setAmount(s.getCount());
                c.setLoc(loc);
                c.doSpawn(true);
                if (s.getRespawnDelay() == 0) {
                  c.stopRespawn();
                } else {
                  c.startRespawn();
                }

                this.addSpawn(c);
              }
            case 1:
              c = new SimpleSpawner(s.getNpcId());
              c.setReflection(this);
              c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
              c.setAmount(1);
              c.setLoc(s.getCoords().get(Rnd.get(s.getCoords().size())));
              c.doSpawn(true);
              if (s.getRespawnDelay() == 0) {
                c.stopRespawn();
              } else {
                c.startRespawn();
              }

              this.addSpawn(c);
              break;
            case 2:
              c = new SimpleSpawner(s.getNpcId());
              c.setReflection(this);
              c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
              c.setAmount(s.getCount());
              c.setTerritory(s.getLoc());

              for(int j = 0; j < s.getCount(); ++j) {
                c.doSpawn(true);
              }

              if (s.getRespawnDelay() == 0) {
                c.stopRespawn();
              } else {
                c.startRespawn();
              }

              this.addSpawn(c);
          }
        }

        return;
      }
    }
  }

  public void init(IntObjectMap<DoorTemplate> doors, Map<String, ZoneTemplate> zones) {
    if (!doors.isEmpty()) {
      this._doors = new HashIntObjectMap<>(doors.size());
    }

    Iterator var3;
    DoorTemplate template;
    DoorInstance door;
    for(var3 = doors.values().iterator(); var3.hasNext(); this._doors.put(template.getNpcId(), door)) {
      template = (DoorTemplate)var3.next();
      door = new DoorInstance(IdFactory.getInstance().getNextId(), template);
      door.setReflection(this);
      door.setIsInvul(true);
      door.spawnMe(template.getLoc());
      if (template.isOpened()) {
        door.openMe();
      }
    }

    this.initDoors();
    if (!zones.isEmpty()) {
      this._zones = new HashMap<>(zones.size());
    }

    var3 = zones.values().iterator();

    while(true) {
      ZoneTemplate zoneTemplate;
      do {
        if (!var3.hasNext()) {
          return;
        }

        zoneTemplate = (ZoneTemplate)var3.next();
      } while(this.isDefault() && !zoneTemplate.isDefault());

      Zone zone = new Zone(zoneTemplate);
      zone.setReflection(this);
      switch(zone.getType()) {
        case no_landing:
        case SIEGE:
          zone.addListener(NoLandingZoneListener.STATIC);
          break;
        case RESIDENCE:
          zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
      }

      if (zoneTemplate.isEnabled()) {
        zone.setActive(true);
      }

      this._zones.put(zoneTemplate.getName(), zone);
    }
  }

  private void init0(IntObjectMap<DoorInfo> doors, Map<String, ZoneInfo> zones) {
    if (!doors.isEmpty()) {
      this._doors = new HashIntObjectMap<>(doors.size());
    }

    Iterator var3;
    DoorInfo info;
    DoorInstance door;
    for(var3 = doors.values().iterator(); var3.hasNext(); this._doors.put(info.getTemplate().getNpcId(), door)) {
      info = (DoorInfo)var3.next();
      door = new DoorInstance(IdFactory.getInstance().getNextId(), info.getTemplate());
      door.setReflection(this);
      door.setIsInvul(info.isInvul());
      door.spawnMe(info.getTemplate().getLoc());
      if (info.isOpened()) {
        door.openMe();
      }
    }

    this.initDoors();
    if (!zones.isEmpty()) {
      this._zones = new HashMap<>(zones.size());
    }

    ZoneInfo t;
    Zone zone;
    for(var3 = zones.values().iterator(); var3.hasNext(); this._zones.put(t.getTemplate().getName(), zone)) {
      t = (ZoneInfo)var3.next();
      zone = new Zone(t.getTemplate());
      zone.setReflection(this);
      switch(zone.getType()) {
        case no_landing:
        case SIEGE:
          zone.addListener(NoLandingZoneListener.STATIC);
          break;
        case RESIDENCE:
          zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
      }

      if (t.isActive()) {
        zone.setActive(true);
      }
    }

  }

  private void initDoors() {

    for (DoorInstance door : this._doors.values()) {
      if (door.getTemplate().getMasterDoor() > 0) {
        DoorInstance masterDoor = this.getDoor(door.getTemplate().getMasterDoor());
        masterDoor.addListener(new MasterOnOpenCloseListenerImpl(door));
      }
    }

  }

  public void openDoor(int doorId) {
    DoorInstance door = this._doors.get(doorId);
    if (door != null) {
      door.openMe();
    }

  }

  public void closeDoor(int doorId) {
    DoorInstance door = this._doors.get(doorId);
    if (door != null) {
      door.closeMe();
    }

  }

  public void clearReflection(int timeInMinutes, boolean message) {
    if (!this.isDefault() && !this.isStatic()) {

      for (NpcInstance n : this.getNpcs()) {
        n.deleteMe();
      }

      this.startCollapseTimer((long)(timeInMinutes * 60) * 1000L);
      if (message) {
        Iterator var3 = this.getPlayers().iterator();

        while(true) {
          Player pl;
          do {
            if (!var3.hasNext()) {
              return;
            }

            pl = (Player)var3.next();
          } while(pl == null);

          for (Player partyPlayer : pl) {
            partyPlayer.sendMessage((new CustomMessage("THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES", partyPlayer)).addNumber(timeInMinutes));
          }
        }
      }
    }
  }

  public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset) {
    Location newLoc;
    if (randomOffset > 0) {
      newLoc = Location.findPointToStay(loc, 0, randomOffset, this.getGeoIndex()).setH(loc.h);
    } else {
      newLoc = loc;
    }

    return NpcUtils.spawnSingle(npcId, newLoc, this);
  }

  public NpcInstance addSpawnWithRespawn(int npcId, Location loc, int randomOffset, int respawnDelay) {
    SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(npcId));
    sp.setLoc(randomOffset > 0 ? Location.findPointToStay(loc, 0, randomOffset, this.getGeoIndex()) : loc);
    sp.setReflection(this);
    sp.setAmount(1);
    sp.setRespawnDelay(respawnDelay);
    sp.doSpawn(true);
    sp.startRespawn();
    return sp.getLastSpawn();
  }

  public boolean isDefault() {
    return this.getId() <= 0;
  }

  public boolean isStatic() {
    return false;
  }

  public int[] getVisitors() {
    return this._visitors.toArray();
  }

  public void setReenterTime(long time) {
    this.lock.lock();

    int[] players;
    try {
      players = this._visitors.toArray();
    } finally {
      this.lock.unlock();
    }

    if (players != null) {

      for (int objectId : players) {
        try {
          Player player = World.getPlayer(objectId);
          if (player != null) {
            player.setInstanceReuse(this.getInstancedZoneId(), time);
          } else {
            mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId, this.getInstancedZoneId(), time);
          }
        } catch (Exception var12) {
          var12.printStackTrace();
        }
      }
    }

  }

  protected void onCreate() {
    ReflectionManager.getInstance().add(this);
  }

  public static Reflection createReflection(int id) {
    if (id > 0) {
      throw new IllegalArgumentException("id should be <= 0");
    } else {
      return new Reflection(id);
    }
  }

  public void init(InstantZone instantZone) {
    this.setName(instantZone.getName());
    this.setInstancedZone(instantZone);
    if (instantZone.getMapX() >= 0) {
      int geoIndex = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), this.getId());
      this.setGeoIndex(geoIndex);
    }

    this.setTeleportLoc(instantZone.getTeleportCoord());
    if (instantZone.getReturnCoords() != null) {
      this.setReturnLoc(instantZone.getReturnCoords());
    }

    this.fillSpawns(instantZone.getSpawnsInfo());
    if (instantZone.getSpawns().size() > 0) {
      this._spawners = new HashMap(instantZone.getSpawns().size());
      Iterator var8 = instantZone.getSpawns().entrySet().iterator();

      while(var8.hasNext()) {
        Entry<String, SpawnInfo2> entry = (Entry)var8.next();
        List<Spawner> spawnList = new ArrayList(entry.getValue().getTemplates().size());
        this._spawners.put(entry.getKey(), spawnList);
        Iterator var5 = entry.getValue().getTemplates().iterator();

        while(var5.hasNext()) {
          SpawnTemplate template = (SpawnTemplate)var5.next();
          HardSpawner spawner = new HardSpawner(template);
          spawnList.add(spawner);
          spawner.setAmount(template.getCount());
          spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
          spawner.setReflection(this);
          spawner.setRespawnTime(0);
        }

        if (entry.getValue().isSpawned()) {
          this.spawnByGroup(entry.getKey());
        }
      }
    }

    this.init0(instantZone.getDoors(), instantZone.getZones());
    if (!this.isStatic()) {
      this.setCollapseIfEmptyTime(instantZone.getCollapseIfEmpty());
      this.startCollapseTimer((long)(instantZone.getTimelimit() * 60) * 1000L);
    }

    this.onCreate();
  }

  public void spawnByGroup(String name) {
    List<Spawner> list = this._spawners.get(name);
    if (list == null) {
      throw new IllegalArgumentException();
    } else {

      for (Spawner s : list) {
        s.init();
      }

    }
  }

  public void despawnByGroup(String name) {
    List<Spawner> list = this._spawners.get(name);
    if (list == null) {
      throw new IllegalArgumentException();
    } else {
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
        Spawner s = (Spawner)var3.next();
        s.deleteAll();
      }

    }
  }

  public Collection<Zone> getZones() {
    return this._zones.values();
  }

  public <T extends Listener<Reflection>> boolean addListener(T listener) {
    return this.listeners.add(listener);
  }

  public <T extends Listener<Reflection>> boolean removeListener(T listener) {
    return this.listeners.remove(listener);
  }

  public class ReflectionListenerList extends ListenerList<Reflection> {
    public ReflectionListenerList() {
    }

    public void onCollapse() {
      if (!this.getListeners().isEmpty()) {
        Iterator var1 = this.getListeners().iterator();

        while(var1.hasNext()) {
          Listener<Reflection> listener = (Listener)var1.next();
          ((OnReflectionCollapseListener)listener).onReflectionCollapse(Reflection.this);
        }
      }

    }
  }
}
