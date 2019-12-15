//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import l2.commons.util.NaturalOrderComparator;
import l2.gameserver.Config;
import l2.gameserver.model.Skill;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class SkillsEngine {
  private static final SkillsEngine _instance = new SkillsEngine();

  public static SkillsEngine getInstance() {
    return _instance;
  }

  private SkillsEngine() {
  }

  public List<Skill> loadSkills(File file) {
    if (file == null) {
      log.warn("SkillsEngine: File not found!");
      return null;
    } else {
      log.info("Loading skills from " + file.getName() + " ...");
      DocumentSkill doc = new DocumentSkill(file);
      doc.parse();
      return doc.getSkills();
    }
  }

  public Map<Integer, Map<Integer, Skill>> loadAllSkills() {
    File dir = new File(Config.DATAPACK_ROOT, "data/stats/skills");
    if (!dir.exists()) {
      log.info("Dir " + dir.getAbsolutePath() + " not exists");
      return Collections.emptyMap();
    } else {
      File[] files = dir.listFiles(pathname -> pathname.getName().endsWith(".xml"));
      Arrays.sort(files, NaturalOrderComparator.FILE_NAME_COMPARATOR);
      Map<Integer, Map<Integer, Skill>> result = new HashMap<>();
      int maxId = 0;
      int maxLvl = 0;

      for (File file : files) {
        List<Skill> skills = this.loadSkills(file);
        if (skills != null) {

          for (Skill skill : skills) {
            int skillId = skill.getId();
            int skillLevel = skill.getLevel();
            Map<Integer, Skill> skillLevels = result.computeIfAbsent(skillId, k -> new HashMap<>());

            ((Map) skillLevels).put(skillLevel, skill);
            if (skill.getId() > maxId) {
              maxId = skill.getId();
            }

            if (skill.getLevel() > maxLvl) {
              maxLvl = skill.getLevel();
            }
          }
        }
      }

      log.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
      return result;
    }
  }
}
