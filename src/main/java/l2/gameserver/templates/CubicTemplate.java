//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import gnu.trove.TIntIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import l2.gameserver.model.Skill;

public class CubicTemplate {
  private final int _id;
  private final int _level;
  private final int _delay;
  private List<Entry<Integer, List<CubicTemplate.SkillInfo>>> _skills = new ArrayList(3);

  public CubicTemplate(int id, int level, int delay) {
    this._id = id;
    this._level = level;
    this._delay = delay;
  }

  public void putSkills(int chance, List<CubicTemplate.SkillInfo> skill) {
    this._skills.add(new SimpleImmutableEntry(chance, skill));
  }

  public Iterable<Entry<Integer, List<CubicTemplate.SkillInfo>>> getSkills() {
    return this._skills;
  }

  public int getDelay() {
    return this._delay;
  }

  public int getId() {
    return this._id;
  }

  public int getLevel() {
    return this._level;
  }

  public static enum ActionType {
    ATTACK,
    DEBUFF,
    CANCEL,
    HEAL;

    private ActionType() {
    }
  }

  public static class SkillInfo {
    private final Skill _skill;
    private final int _chance;
    private final CubicTemplate.ActionType _actionType;
    private final boolean _canAttackDoor;
    private final int _minHp;
    private final int _minHpPercent;
    private final TIntIntHashMap _chanceList;

    public SkillInfo(Skill skill, int chance, CubicTemplate.ActionType actionType, boolean canAttackDoor, int minHp, int minHpPercent, TIntIntHashMap set) {
      this._skill = skill;
      this._chance = chance;
      this._actionType = actionType;
      this._canAttackDoor = canAttackDoor;
      this._minHp = minHp;
      this._minHpPercent = minHpPercent;
      this._chanceList = set;
    }

    public int getChance() {
      return this._chance;
    }

    public CubicTemplate.ActionType getActionType() {
      return this._actionType;
    }

    public Skill getSkill() {
      return this._skill;
    }

    public boolean isCanAttackDoor() {
      return this._canAttackDoor;
    }

    public int getMinHp() {
      return this._minHp;
    }

    public int getMinHpPercent() {
      return this._minHpPercent;
    }

    public int getChance(int a) {
      return this._chanceList.get(a);
    }
  }
}
