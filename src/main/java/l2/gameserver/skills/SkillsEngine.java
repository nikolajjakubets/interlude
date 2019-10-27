//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.util.NaturalOrderComparator;
import l2.gameserver.Config;
import l2.gameserver.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillsEngine {
  private static final Logger _log = LoggerFactory.getLogger(SkillsEngine.class);
  private static final SkillsEngine _instance = new SkillsEngine();

  public static SkillsEngine getInstance() {
    return _instance;
  }

  private SkillsEngine() {
  }

  public List<Skill> loadSkills(File file) {
    if (file == null) {
      _log.warn("SkillsEngine: File not found!");
      return null;
    } else {
      _log.info("Loading skills from " + file.getName() + " ...");
      DocumentSkill doc = new DocumentSkill(file);
      doc.parse();
      return doc.getSkills();
    }
  }

  public Map<Integer, Map<Integer, Skill>> loadAllSkills() {
    File dir = new File(Config.DATAPACK_ROOT, "data/stats/skills");
    if (!dir.exists()) {
      _log.info("Dir " + dir.getAbsolutePath() + " not exists");
      return Collections.emptyMap();
    } else {
      File[] files = dir.listFiles(new FileFilter() {
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".xml");
        }
      });
      Arrays.sort(files, NaturalOrderComparator.FILE_NAME_COMPARATOR);
      Map<Integer, Map<Integer, Skill>> result = new HashMap<>();
      int maxId = 0;
      int maxLvl = 0;
      File[] var6 = files;
      int var7 = files.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        File file = var6[var8];
        List<Skill> skills = this.loadSkills(file);
        if (skills != null) {
          Iterator var11 = skills.iterator();

          while(var11.hasNext()) {
            Skill skill = (Skill)var11.next();
            int skillId = skill.getId();
            int skillLevel = skill.getLevel();
            Map<Integer, Skill> skillLevels = (Map)result.get(skillId);
            if (skillLevels == null) {
              skillLevels = new HashMap<>();
              result.put(skillId, skillLevels);
            }

            ((Map)skillLevels).put(skillLevel, skill);
            if (skill.getId() > maxId) {
              maxId = skill.getId();
            }

            if (skill.getLevel() > maxLvl) {
              maxLvl = skill.getLevel();
            }
          }
        }
      }

      _log.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
      return result;
    }
  }
}
