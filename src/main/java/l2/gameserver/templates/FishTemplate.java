//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

public class FishTemplate {
  private final int _id;
  private final int _level;
  private final String _name;
  private final int _HP;
  private final int _HpRegen;
  private final int _type;
  private final int _group;
  private final int _fish_guts;
  private final int _guts_check_time;
  private final int _wait_time;
  private final int _combat_time;

  public FishTemplate(int id, int lvl, String name, int HP, int HpRegen, int type, int group, int fish_guts, int guts_check_time, int wait_time, int combat_time) {
    this._id = id;
    this._level = lvl;
    this._name = name.intern();
    this._HP = HP;
    this._HpRegen = HpRegen;
    this._type = type;
    this._group = group;
    this._fish_guts = fish_guts;
    this._guts_check_time = guts_check_time;
    this._wait_time = wait_time;
    this._combat_time = combat_time;
  }

  public int getId() {
    return this._id;
  }

  public int getLevel() {
    return this._level;
  }

  public String getName() {
    return this._name;
  }

  public int getHP() {
    return this._HP;
  }

  public int getHpRegen() {
    return this._HpRegen;
  }

  public int getType() {
    return this._type;
  }

  public int getGroup() {
    return this._group;
  }

  public int getFishGuts() {
    return this._fish_guts;
  }

  public int getGutsCheckTime() {
    return this._guts_check_time;
  }

  public int getWaitTime() {
    return this._wait_time;
  }

  public int getCombatTime() {
    return this._combat_time;
  }
}
