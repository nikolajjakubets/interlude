//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import gnu.trove.TIntIntHashMap;
import java.util.Iterator;
import java.util.Map;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.SkillsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillTable {
  private static final Logger _log = LoggerFactory.getLogger(SkillTable.class);
  private static final SkillTable _instance = new SkillTable();
  private Map<Integer, Map<Integer, Skill>> _skills;
  private TIntIntHashMap _maxLevelsTable;
  private TIntIntHashMap _baseLevelsTable;

  public SkillTable() {
  }

  public static final SkillTable getInstance() {
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
    Map<Integer, Skill> skillLevels = (Map)this._skills.get(skillId);
    return skillLevels == null ? null : (Skill)skillLevels.get(skillLevel);
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
    Iterator var1 = this._skills.values().iterator();

    while(var1.hasNext()) {
      Map<Integer, Skill> ss = (Map)var1.next();
      Iterator var3 = ss.values().iterator();

      while(var3.hasNext()) {
        Skill s = (Skill)var3.next();
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
