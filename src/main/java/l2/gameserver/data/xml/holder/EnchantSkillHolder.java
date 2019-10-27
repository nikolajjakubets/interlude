//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.SkillEnchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantSkillHolder extends AbstractHolder {
  private static final Logger LOG = LoggerFactory.getLogger(EnchantSkillHolder.class);
  private static final EnchantSkillHolder INSTANCE = new EnchantSkillHolder();
  private final Map<Integer, Map<Integer, SkillEnchant>> _skillsEnchantLevels = new TreeMap<>();
  private final Map<Integer, Map<Integer, Map<Integer, SkillEnchant>>> _skillsEnchantRoutes = new TreeMap<>();

  private EnchantSkillHolder() {
  }

  public static EnchantSkillHolder getInstance() {
    return INSTANCE;
  }

  public void addEnchantSkill(SkillEnchant skillEnchant) {
    int skillId = skillEnchant.getSkillId();
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    if (skillEnchantLevels == null) {
      skillEnchantLevels = new TreeMap<>();
      this._skillsEnchantLevels.put(skillId, skillEnchantLevels);
    }

    ((Map)skillEnchantLevels).put(skillEnchant.getSkillLevel(), skillEnchant);
    Map<Integer, Map<Integer, SkillEnchant>> skillEnchantRoutes = (Map)this._skillsEnchantRoutes.get(skillId);
    if (skillEnchantRoutes == null) {
      skillEnchantRoutes = new TreeMap<>();
      this._skillsEnchantRoutes.put(skillId, skillEnchantRoutes);
    }

    int skillRouteId = skillEnchant.getRouteId();
    Map<Integer, SkillEnchant> skillRouteLevels = (Map)((Map)skillEnchantRoutes).get(skillRouteId);
    if (skillRouteLevels == null) {
      skillRouteLevels = new TreeMap<>();
      ((Map)skillEnchantRoutes).put(skillRouteId, skillRouteLevels);
    }

    ((Map)skillRouteLevels).put(skillEnchant.getSkillLevel(), skillEnchant);
  }

  public SkillEnchant getSkillEnchant(int skillId, int skillLvl) {
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    return skillEnchantLevels == null ? null : (SkillEnchant)skillEnchantLevels.get(skillLvl);
  }

  public SkillEnchant getSkillEnchant(int skillId, int routeId, int enchantLevel) {
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    if (skillEnchantLevels == null) {
      return null;
    } else {
      Iterator var5 = skillEnchantLevels.values().iterator();

      SkillEnchant skillEnchant;
      do {
        if (!var5.hasNext()) {
          return null;
        }

        skillEnchant = (SkillEnchant)var5.next();
      } while(skillEnchant.getRouteId() != routeId || skillEnchant.getEnchantLevel() != enchantLevel);

      return skillEnchant;
    }
  }

  public Map<Integer, Map<Integer, SkillEnchant>> getRoutesOf(int skillId) {
    Map<Integer, Map<Integer, SkillEnchant>> skillEnchantRoutes = (Map)this._skillsEnchantRoutes.get(skillId);
    return skillEnchantRoutes == null ? Collections.emptyMap() : Collections.unmodifiableMap(skillEnchantRoutes);
  }

  public int getFirstSkillLevelOf(int skillId, int routeId) {
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    if (skillEnchantLevels == null) {
      return 0;
    } else {
      Iterator var4 = skillEnchantLevels.values().iterator();

      SkillEnchant se;
      do {
        if (!var4.hasNext()) {
          return 0;
        }

        se = (SkillEnchant)var4.next();
      } while(se.getRouteId() != routeId || se.getEnchantLevel() != 1);

      return se.getSkillLevel();
    }
  }

  public int getMaxEnchantLevelOf(int skillId) {
    int maxEnchLevel = 0;
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    if (skillEnchantLevels == null) {
      return 0;
    } else {
      Iterator var4 = skillEnchantLevels.values().iterator();

      while(var4.hasNext()) {
        SkillEnchant se = (SkillEnchant)var4.next();
        if (se.getEnchantLevel() > maxEnchLevel) {
          maxEnchLevel = se.getEnchantLevel();
        }
      }

      return maxEnchLevel;
    }
  }

  public Map<Integer, SkillEnchant> getLevelsOf(int skillId) {
    Map<Integer, SkillEnchant> skillEnchantLevels = (Map)this._skillsEnchantLevels.get(skillId);
    return skillEnchantLevels == null ? Collections.emptyMap() : Collections.unmodifiableMap(skillEnchantLevels);
  }

  public void addEnchantSkill(int skillId, int skillLevel, int enchantLevel, int routeId, long exp, int sp, int[] chances, int itemId, long itemCount) {
    this.addEnchantSkill(new SkillEnchant(skillId, skillLevel, enchantLevel, routeId, exp, sp, chances, itemId, itemCount));
  }

  public int size() {
    return this._skillsEnchantLevels.size();
  }

  public void clear() {
    this._skillsEnchantLevels.clear();
  }
}
