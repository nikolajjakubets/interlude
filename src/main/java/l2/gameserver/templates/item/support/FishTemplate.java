//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import l2.commons.collections.MultiValueSet;

public class FishTemplate {
  private final FishGroup _group;
  private final FishGrade _grade;
  private final double _biteRate;
  private final double _guts;
  private final double _lengthRate;
  private final double _hpRegen;
  private final double _gutsCheckProbability;
  private final double _cheatingProb;
  private final int _itemId;
  private final int _hp;
  private final int _level;
  private final int _maxLength;
  private final int _startCombatTime;
  private final int _combatDuration;
  private final int _gutsCheckTime;

  public FishTemplate(MultiValueSet<String> map) {
    this._group = (FishGroup)map.getEnum("group", FishGroup.class);
    this._grade = (FishGrade)map.getEnum("grade", FishGrade.class);
    this._biteRate = map.getDouble("bite_rate");
    this._guts = map.getDouble("guts");
    this._lengthRate = map.getDouble("length_rate");
    this._hpRegen = map.getDouble("hp_regen");
    this._gutsCheckProbability = map.getDouble("guts_check_probability");
    this._cheatingProb = map.getDouble("cheating_prob");
    this._itemId = map.getInteger("item_id");
    this._level = map.getInteger("level");
    this._hp = map.getInteger("hp");
    this._maxLength = map.getInteger("max_length");
    this._startCombatTime = map.getInteger("start_combat_time");
    this._combatDuration = map.getInteger("combat_duration");
    this._gutsCheckTime = map.getInteger("guts_check_time");
  }

  public FishGroup getGroup() {
    return this._group;
  }

  public FishGrade getGrade() {
    return this._grade;
  }

  public double getBiteRate() {
    return this._biteRate;
  }

  public double getGuts() {
    return this._guts;
  }

  public double getLengthRate() {
    return this._lengthRate;
  }

  public double getHpRegen() {
    return this._hpRegen;
  }

  public double getGutsCheckProbability() {
    return this._gutsCheckProbability;
  }

  public double getCheatingProb() {
    return this._cheatingProb;
  }

  public int getItemId() {
    return this._itemId;
  }

  public int getHp() {
    return this._hp;
  }

  public int getLevel() {
    return this._level;
  }

  public int getMaxLength() {
    return this._maxLength;
  }

  public int getStartCombatTime() {
    return this._startCombatTime;
  }

  public int getCombatDuration() {
    return this._combatDuration;
  }

  public int getGutsCheckTime() {
    return this._gutsCheckTime;
  }
}
