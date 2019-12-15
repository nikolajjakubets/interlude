//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import gnu.trove.TIntObjectHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.Summon;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PetSkillsTable {
  private TIntObjectHashMap<List<SkillLearn>> _skillTrees = new TIntObjectHashMap<>();
  private static PetSkillsTable _instance = new PetSkillsTable();

  public static PetSkillsTable getInstance() {
    return _instance;
  }

  public void reload() {
    _instance = new PetSkillsTable();
  }

  private PetSkillsTable() {
    this.load();
  }

  private void load() {
    int npcId = 0;
    int count = 0;
//    int id = false;
//    int lvl = false;
//    int minLvl = false;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM pets_skills ORDER BY templateId");

      for (rset = statement.executeQuery(); rset.next(); ++count) {
        npcId = rset.getInt("templateId");
        int id = rset.getInt("skillId");
        int lvl = rset.getInt("skillLvl");
        int minLvl = rset.getInt("minLvl");
        List<SkillLearn> list = this._skillTrees.get(npcId);
        if (list == null) {
          this._skillTrees.put(npcId, list = new ArrayList<>());
        }

        SkillLearn skillLearn = new SkillLearn(id, lvl, minLvl, 0, 0, 0L, false, false);
        list.add(skillLearn);
      }
    } catch (Exception var14) {
      log.error("Error while creating pet skill tree (Pet ID " + npcId + ")", var14);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    log.info("PetSkillsTable: Loaded " + count + " skills.");
  }

  public int getAvailableLevel(Summon cha, int skillId) {
    List<SkillLearn> skills = this._skillTrees.get(cha.getNpcId());
    if (skills == null) {
      return 0;
    } else {
      int lvl = 0;

      for (SkillLearn temp : skills) {
        if (temp.getId() == skillId) {
          if (temp.getLevel() == 0) {
            if (cha.getLevel() < 70) {
              lvl = cha.getLevel() / 10;
              if (lvl <= 0) {
                lvl = 1;
              }
            } else {
              lvl = 7 + (cha.getLevel() - 70) / 5;
            }

            int maxLvl = SkillTable.getInstance().getMaxLevel(temp.getId());
            if (lvl > maxLvl) {
              lvl = maxLvl;
            }
            break;
          }

          if (temp.getMinLevel() <= cha.getLevel() && temp.getLevel() > lvl) {
            lvl = temp.getLevel();
          }
        }
      }

      return lvl;
    }
  }
}
