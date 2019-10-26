//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import java.util.List;
import java.util.Map;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import org.napile.primitive.maps.IntObjectMap;

public class InstantZone {
  private final int _id;
  private final String _name;
  private final SchedulingPattern _resetReuse;
  private final int _sharedReuseGroup;
  private final int _timelimit;
  private boolean _dispelBuffs;
  private final int _minLevel;
  private final int _maxLevel;
  private final int _minParty;
  private final int _maxParty;
  private final boolean _onPartyDismiss;
  private final int _timer;
  private final List<Location> _teleportCoords;
  private final Location _returnCoords;
  private final int _mapx;
  private final int _mapy;
  private final IntObjectMap<InstantZone.DoorInfo> _doors;
  private final Map<String, InstantZone.ZoneInfo> _zones;
  private final Map<String, InstantZone.SpawnInfo2> _spawns;
  private final List<InstantZone.SpawnInfo> _spawnsInfo;
  private final int _collapseIfEmpty;
  private final int _maxChannels;
  private final int _removedItemId;
  private final int _removedItemCount;
  private final boolean _removedItemNecessity;
  private final int _giveItemId;
  private final int _givedItemCount;
  private final int _requiredQuestId;
  private final boolean _setReuseUponEntry;
  private final StatsSet _addParams;
  private final InstantZoneEntryType _entryType;

  public InstantZone(int id, String name, SchedulingPattern resetReuse, int sharedReuseGroup, int timelimit, boolean dispelBuffs, int minLevel, int maxLevel, int minParty, int maxParty, int timer, boolean onPartyDismiss, List<Location> tele, Location ret, int mapx, int mapy, IntObjectMap<InstantZone.DoorInfo> doors, Map<String, InstantZone.ZoneInfo> zones, Map<String, InstantZone.SpawnInfo2> spawns, List<InstantZone.SpawnInfo> spawnsInfo, int collapseIfEmpty, int maxChannels, int removedItemId, int removedItemCount, boolean removedItemNecessity, int giveItemId, int givedItemCount, int requiredQuestId, boolean setReuseUponEntry, StatsSet params) {
    this._id = id;
    this._name = name;
    this._resetReuse = resetReuse;
    this._sharedReuseGroup = sharedReuseGroup;
    this._timelimit = timelimit;
    this._dispelBuffs = dispelBuffs;
    this._minLevel = minLevel;
    this._maxLevel = maxLevel;
    this._teleportCoords = tele;
    this._returnCoords = ret;
    this._minParty = minParty;
    this._maxParty = maxParty;
    this._onPartyDismiss = onPartyDismiss;
    this._timer = timer;
    this._mapx = mapx;
    this._mapy = mapy;
    this._doors = doors;
    this._zones = zones;
    this._spawnsInfo = spawnsInfo;
    this._spawns = spawns;
    this._collapseIfEmpty = collapseIfEmpty;
    this._maxChannels = maxChannels;
    this._removedItemId = removedItemId;
    this._removedItemCount = removedItemCount;
    this._removedItemNecessity = removedItemNecessity;
    this._giveItemId = giveItemId;
    this._givedItemCount = givedItemCount;
    this._requiredQuestId = requiredQuestId;
    this._setReuseUponEntry = setReuseUponEntry;
    this._addParams = params;
    if (this.getMinParty() == 1) {
      this._entryType = InstantZoneEntryType.SOLO;
    } else if (this.getMinParty() > 1 && this.getMaxParty() <= 9) {
      this._entryType = InstantZoneEntryType.PARTY;
    } else {
      if (this.getMaxParty() <= 9) {
        throw new IllegalArgumentException("Invalid type?: " + this._name);
      }

      this._entryType = InstantZoneEntryType.COMMAND_CHANNEL;
    }

  }

  public int getId() {
    return this._id;
  }

  public String getName() {
    return this._name;
  }

  public SchedulingPattern getResetReuse() {
    return this._resetReuse;
  }

  public boolean isDispelBuffs() {
    return this._dispelBuffs;
  }

  public int getTimelimit() {
    return this._timelimit;
  }

  public int getMinLevel() {
    return this._minLevel;
  }

  public int getMaxLevel() {
    return this._maxLevel;
  }

  public int getMinParty() {
    return this._minParty;
  }

  public int getMaxParty() {
    return this._maxParty;
  }

  public int getTimerOnCollapse() {
    return this._timer;
  }

  public boolean isCollapseOnPartyDismiss() {
    return this._onPartyDismiss;
  }

  public Location getTeleportCoord() {
    return this._teleportCoords.size() == 1 ? (Location)this._teleportCoords.get(0) : (Location)this._teleportCoords.get(Rnd.get(this._teleportCoords.size()));
  }

  public Location getReturnCoords() {
    return this._returnCoords;
  }

  public int getMapX() {
    return this._mapx;
  }

  public int getMapY() {
    return this._mapy;
  }

  public List<InstantZone.SpawnInfo> getSpawnsInfo() {
    return this._spawnsInfo;
  }

  public int getSharedReuseGroup() {
    return this._sharedReuseGroup;
  }

  public int getCollapseIfEmpty() {
    return this._collapseIfEmpty;
  }

  public int getRemovedItemId() {
    return this._removedItemId;
  }

  public int getRemovedItemCount() {
    return this._removedItemCount;
  }

  public boolean getRemovedItemNecessity() {
    return this._removedItemNecessity;
  }

  public int getGiveItemId() {
    return this._giveItemId;
  }

  public int getGiveItemCount() {
    return this._givedItemCount;
  }

  public int getRequiredQuestId() {
    return this._requiredQuestId;
  }

  public boolean getSetReuseUponEntry() {
    return this._setReuseUponEntry;
  }

  public int getMaxChannels() {
    return this._maxChannels;
  }

  public InstantZoneEntryType getEntryType() {
    return this._entryType;
  }

  public IntObjectMap<InstantZone.DoorInfo> getDoors() {
    return this._doors;
  }

  public Map<String, InstantZone.ZoneInfo> getZones() {
    return this._zones;
  }

  public List<Location> getTeleportCoords() {
    return this._teleportCoords;
  }

  public Map<String, InstantZone.SpawnInfo2> getSpawns() {
    return this._spawns;
  }

  public StatsSet getAddParams() {
    return this._addParams;
  }

  /** @deprecated */
  @Deprecated
  public static class SpawnInfo {
    private final int _spawnType;
    private final int _npcId;
    private final int _count;
    private final int _respawn;
    private final int _respawnRnd;
    private final List<Location> _coords;
    private final Territory _territory;

    public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, Territory territory) {
      this(spawnType, npcId, count, respawn, respawnRnd, (List)null, territory);
    }

    public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, List<Location> coords) {
      this(spawnType, npcId, count, respawn, respawnRnd, coords, (Territory)null);
    }

    public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, List<Location> coords, Territory territory) {
      this._spawnType = spawnType;
      this._npcId = npcId;
      this._count = count;
      this._respawn = respawn;
      this._respawnRnd = respawnRnd;
      this._coords = coords;
      this._territory = territory;
    }

    public int getSpawnType() {
      return this._spawnType;
    }

    public int getNpcId() {
      return this._npcId;
    }

    public int getCount() {
      return this._count;
    }

    public int getRespawnDelay() {
      return this._respawn;
    }

    public int getRespawnRnd() {
      return this._respawnRnd;
    }

    public List<Location> getCoords() {
      return this._coords;
    }

    public Territory getLoc() {
      return this._territory;
    }
  }

  public static class SpawnInfo2 {
    private List<SpawnTemplate> _template;
    private boolean _spawned;

    public SpawnInfo2(List<SpawnTemplate> template, boolean spawned) {
      this._template = template;
      this._spawned = spawned;
    }

    public List<SpawnTemplate> getTemplates() {
      return this._template;
    }

    public boolean isSpawned() {
      return this._spawned;
    }
  }

  public static class ZoneInfo {
    private final ZoneTemplate _template;
    private final boolean _active;

    public ZoneInfo(ZoneTemplate template, boolean opened) {
      this._template = template;
      this._active = opened;
    }

    public ZoneTemplate getTemplate() {
      return this._template;
    }

    public boolean isActive() {
      return this._active;
    }
  }

  public static class DoorInfo {
    private final DoorTemplate _template;
    private final boolean _opened;
    private final boolean _invul;

    public DoorInfo(DoorTemplate template, boolean opened, boolean invul) {
      this._template = template;
      this._opened = opened;
      this._invul = invul;
    }

    public DoorTemplate getTemplate() {
      return this._template;
    }

    public boolean isOpened() {
      return this._opened;
    }

    public boolean isInvul() {
      return this._invul;
    }
  }
}
