//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import gnu.trove.TIntIntHashMap;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.SkillsEngine;

import java.util.Map;

public class SkillTable {
  private static final SkillTable _instance = new SkillTable();
  private Map<Integer, Map<Integer, Skill>> _skills;
  private TIntIntHashMap _maxLevelsTable;
  private TIntIntHashMap _baseLevelsTable;

  public SkillTable() {
  }

  public static SkillTable getInstance() {
    return _instance;
  }

  public void load() {
    this._skills = SkillsEngine.getInstance().loadAllSkills();
    this.makeLevelsTable();
  }

  public void reload() {
    this.load();
  }

  public Skill getInfo(int skillId, int skillLevel) {
    Map<Integer, Skill> skillLevels = this._skills.get(skillId);
    return skillLevels == null ? null : skillLevels.get(skillLevel);
  }

  public int getMaxLevel(int skillId) {
    return this._maxLevelsTable.get(skillId);
  }

  public int getBaseLevel(int skillId) {
    return this._baseLevelsTable.get(skillId);
  }

  private void makeLevelsTable() {
    this._maxLevelsTable = new TIntIntHashMap();
    this._baseLevelsTable = new TIntIntHashMap();

    for (Map<Integer, Skill> integerSkillMap : this._skills.values()) {

      for (Skill s : integerSkillMap.values()) {
        int skillId = s.getId();
        int level = s.getLevel();
        int maxLevel = this._maxLevelsTable.get(skillId);
        if (level > maxLevel) {
          this._maxLevelsTable.put(skillId, level);
        }

        if (this._baseLevelsTable.get(skillId) == 0) {
          this._baseLevelsTable.put(skillId, s.getBaseLevel());
        }
      }
    }

  }
}
