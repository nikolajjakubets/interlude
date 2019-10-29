//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import l2.commons.geometry.Polygon;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.DoorAI;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

@Slf4j
public class DoorTemplate extends CharTemplate {
  private static final Logger _log = LoggerFactory.getLogger(DoorTemplate.class);
  public static final Constructor<?> DEFAULT_AI_CONSTRUCTOR = CharacterAI.class.getConstructors()[0];
  private final int _id;
  private final String _name;
  private final DoorTemplate.DoorType _doorType;
  private final boolean _unlockable;
  private final boolean _isHPVisible;
  private final boolean _opened;
  private final boolean _targetable;
  private final Polygon _polygon;
  private final Location _loc;
  private final int _key;
  private final int _openTime;
  private final int _rndTime;
  private final int _closeTime;
  private final int _masterDoor;
  private StatsSet _aiParams;
  private Class<DoorAI> _classAI = DoorAI.class;
  private Constructor<?> _constructorAI;

  public DoorTemplate(StatsSet set) {
    super(set);
    this._constructorAI = DEFAULT_AI_CONSTRUCTOR;
    this._id = set.getInteger("uid");
    this._name = set.getString("name");
    this._doorType = set.getEnum("door_type", DoorType.class, DoorType.DOOR);
    this._unlockable = set.getBool("unlockable", false);
    this._isHPVisible = set.getBool("show_hp", false);
    this._opened = set.getBool("opened", false);
    this._targetable = set.getBool("targetable", true);
    this._loc = (Location)set.get("pos");
    this._polygon = (Polygon)set.get("shape");
    this._key = set.getInteger("key", 0);
    this._openTime = set.getInteger("open_time", 0);
    this._rndTime = set.getInteger("random_time", 0);
    this._closeTime = set.getInteger("close_time", 0);
    this._masterDoor = set.getInteger("master_door", 0);
    this._aiParams = (StatsSet)set.getObject("ai_params", StatsSet.EMPTY);
    this.setAI(set.getString("ai", "DoorAI"));
  }

  private void setAI(String ai) {
    Class classAI;

    try {
      classAI = Class.forName("l2.gameserver.ai." + ai);
    } catch (ClassNotFoundException e) {
      log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
      classAI = Scripts.getInstance().getClasses().get("ai.door." + ai);
    }

    if (classAI == null) {
      _log.error("Not found ai class for ai: " + ai + ". DoorId: " + this._id);
    } else {
      this._classAI = classAI;
      this._constructorAI = this._classAI.getConstructors()[0];
    }

    if (this._classAI.isAnnotationPresent(Deprecated.class)) {
      _log.error("Ai type: " + ai + ", is deprecated. DoorId: " + this._id);
    }

  }

  public CharacterAI getNewAI(DoorInstance door) {
    try {
      return (CharacterAI) this._constructorAI.newInstance(door);
    } catch (Exception var3) {
      _log.error("Unable to create ai of doorId " + this._id, var3);
      return new DoorAI(door);
    }
  }

  public int getNpcId() {
    return this._id;
  }

  public String getName() {
    return this._name;
  }

  public DoorTemplate.DoorType getDoorType() {
    return this._doorType;
  }

  public boolean isUnlockable() {
    return this._unlockable;
  }

  public boolean isHPVisible() {
    return this._isHPVisible;
  }

  public Polygon getPolygon() {
    return this._polygon;
  }

  public int getKey() {
    return this._key;
  }

  public boolean isOpened() {
    return this._opened;
  }

  public Location getLoc() {
    return this._loc;
  }

  public int getOpenTime() {
    return this._openTime;
  }

  public int getRandomTime() {
    return this._rndTime;
  }

  public int getCloseTime() {
    return this._closeTime;
  }

  public boolean isTargetable() {
    return this._targetable;
  }

  public int getMasterDoor() {
    return this._masterDoor;
  }

  public StatsSet getAIParams() {
    return this._aiParams;
  }

  public enum DoorType {
    DOOR,
    WALL;

    DoorType() {
    }
  }
}
