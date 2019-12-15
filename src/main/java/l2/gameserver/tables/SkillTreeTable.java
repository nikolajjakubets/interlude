//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.EnchantSkillLearn;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SkillTreeTable {
  public static final int NORMAL_ENCHANT_COST_MULTIPLIER = 1;
  public static final int NORMAL_ENCHANT_BOOK = 6622;
  private static SkillTreeTable _instance;
  public static Map<Integer, List<EnchantSkillLearn>> _enchant = new ConcurrentHashMap<>();

  public static SkillTreeTable getInstance() {
    if (_instance == null) {
      _instance = new SkillTreeTable();
    }

    return _instance;
  }

  private SkillTreeTable() {
    log.info("SkillTreeTable: Loaded " + _enchant.size() + " enchanted skills.");
  }

  public static void checkSkill(Player player, Skill skill) {
    SkillLearn learnBase = SkillAcquireHolder.getInstance().getSkillLearn(player, player.getClassId(), skill.getId(), 1, AcquireType.NORMAL);
    if (learnBase != null) {
      if (learnBase.getMinLevel() >= player.getLevel() + Config.ALT_REMOVE_SKILLS_ON_DELEVEL) {
        player.removeSkill(skill, true);
      }

    }
  }

  private static int levelWithoutEnchant(Skill skill) {
    return skill.getDisplayLevel() > 100 ? skill.getBaseLevel() : skill.getLevel();
  }

  public static int isEnchantable(Skill skill) {
    List<EnchantSkillLearn> enchants = _enchant.get(skill.getId());
    if (enchants == null) {
      return 0;
    } else {
      Iterator var2 = enchants.iterator();

      EnchantSkillLearn e;
      do {
        if (!var2.hasNext()) {
          return 0;
        }

        e = (EnchantSkillLearn) var2.next();
      } while (e.getBaseLevel() > skill.getLevel());

      return 1;
    }
  }

  public static void unload() {
    if (_instance != null) {
      _instance = null;
    }

    _enchant.clear();
  }
}
