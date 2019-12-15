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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class DoorTemplate extends CharTemplate {
  public static final Constructor<?> DEFAULT_AI_CONSTRUCTOR = CharacterAI.class.getConstructors()[0];
  private final int id;
  private final String name;
  private final DoorTemplate.DoorType doorType;
  private final boolean unlockable;
  private final boolean isHPVisible;
  private final boolean opened;
  private final boolean targetable;
  private final Polygon polygon;
  private final Location loc;
  private final int key;
  private final int openTime;
  private final int randomTime;
  private final int closeTime;
  private final int masterDoor;
  private StatsSet aiParams;
  private Class<DoorAI> classAI = DoorAI.class;
  private Constructor<?> constructorAI;

  public DoorTemplate(StatsSet set) {
    super(set);
    this.constructorAI = DEFAULT_AI_CONSTRUCTOR;
    this.id = set.getInteger("uid");
    this.name = set.getString("name");
    this.doorType = set.getEnum("door_type", DoorType.class, DoorType.DOOR);
    this.unlockable = set.getBool("unlockable", false);
    this.isHPVisible = set.getBool("show_hp", false);
    this.opened = set.getBool("opened", false);
    this.targetable = set.getBool("targetable", true);
    this.loc = (Location) set.get("pos");
    this.polygon = (Polygon) set.get("shape");
    this.key = set.getInteger("key", 0);
    this.openTime = set.getInteger("open_time", 0);
    this.randomTime = set.getInteger("random_time", 0);
    this.closeTime = set.getInteger("close_time", 0);
    this.masterDoor = set.getInteger("master_door", 0);
    this.aiParams = (StatsSet) set.getObject("ai_params", StatsSet.EMPTY);
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
      log.error("Not found ai class for ai: " + ai + ". DoorId: " + this.id);
    } else {
      this.classAI = classAI;
      this.constructorAI = this.classAI.getConstructors()[0];
    }

    if (this.classAI.isAnnotationPresent(Deprecated.class)) {
      log.error("Ai type: " + ai + ", is deprecated. DoorId: " + this.id);
    }

  }

  public CharacterAI getNewAI(DoorInstance door) {
    try {
      return (CharacterAI) this.constructorAI.newInstance(door);
    } catch (Exception var3) {
      log.error("Unable to create ai of doorId " + this.id, var3);
      return new DoorAI(door);
    }
  }

  public enum DoorType {
    DOOR,
    WALL;

    DoorType() {
    }
  }
}
