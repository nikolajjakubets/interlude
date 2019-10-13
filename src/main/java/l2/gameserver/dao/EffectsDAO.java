//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectsDAO {
  private static final int SUMMON_SKILL_OFFSET = 100000;
  private static final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);
  private static final EffectsDAO _instance = new EffectsDAO();

  EffectsDAO() {
  }

  public static EffectsDAO getInstance() {
    return _instance;
  }

  public void restoreEffects(Playable playable) {
    int objectId;
    int id;
    if (playable.isPlayer()) {
      objectId = playable.getObjectId();
      id = ((Player)playable).getActiveClassId();
    } else {
      if (!playable.isSummon()) {
        return;
      }

      objectId = playable.getPlayer().getObjectId();
      id = ((SummonInstance)playable).getEffectIdentifier() + 100000;
    }

    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `character_effects_save` WHERE `object_id`=? AND `id`=? ORDER BY `order` ASC");
      statement.setInt(1, objectId);
      statement.setInt(2, id);
      rset = statement.executeQuery();

      while(true) {
        int effectCount;
        long effectCurTime;
        long duration;
        Skill skill;
        do {
          if (!rset.next()) {
            DbUtils.closeQuietly(statement, rset);
            statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?");
            statement.setInt(1, objectId);
            statement.setInt(2, id);
            statement.execute();
            DbUtils.close(statement);
            return;
          }

          int skillId = rset.getInt("skill_id");
          int skillLvl = rset.getInt("skill_level");
          effectCount = rset.getInt("effect_count");
          effectCurTime = rset.getLong("effect_cur_time");
          duration = rset.getLong("duration");
          skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
        } while(skill == null);

        EffectTemplate[] var15 = skill.getEffectTemplates();
        int var16 = var15.length;

        for(int var17 = 0; var17 < var16; ++var17) {
          EffectTemplate et = var15[var17];
          if (et != null) {
            Env env = new Env(playable, playable, skill);
            Effect effect = et.getEffect(env);
            if (effect != null && !effect.isOneTime()) {
              effect.setCount(effectCount);
              effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);
              playable.getEffectList().addEffect(effect);
            }
          }
        }
      }
    } catch (Exception var24) {
      _log.error("Could not restore active effects data!", var24);
    } finally {
      DbUtils.closeQuietly(con);
    }

  }

  public void deleteEffects(int objectId, int skillId) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?");
      statement.setInt(1, objectId);
      statement.setInt(2, 100000 + skillId);
      statement.execute();
    } catch (Exception var9) {
      _log.error("Could not delete effects active effects data!" + var9, var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void insert(Playable playable) {
    int objectId;
    int id;
    if (playable.isPlayer()) {
      objectId = playable.getObjectId();
      id = ((Player)playable).getActiveClassId();
    } else {
      if (!playable.isSummon()) {
        return;
      }

      objectId = playable.getPlayer().getObjectId();
      id = ((SummonInstance)playable).getEffectIdentifier() + 100000;
    }

    List<Effect> effects = playable.getEffectList().getAllEffects();
    if (!effects.isEmpty()) {
      Connection con = null;
      Statement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.createStatement();
        int order = 0;
        SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`,`id`) VALUES");
        Iterator var10 = effects.iterator();

        while(true) {
          Effect effect;
          do {
            do {
              do {
                do {
                  do {
                    if (!var10.hasNext()) {
                      if (!b.isEmpty()) {
                        statement.executeUpdate(b.close());
                      }

                      return;
                    }

                    effect = (Effect)var10.next();
                  } while(effect == null);
                } while(!effect.isInUse());
              } while(effect.getSkill().isToggle());
            } while(effect.getEffectType() == EffectType.HealOverTime);
          } while(effect.getEffectType() == EffectType.CombatPointHealOverTime);

          StringBuilder sb;
          if (effect.isSaveable()) {
            sb = new StringBuilder("(");
            sb.append(objectId).append(",");
            sb.append(effect.getSkill().getId()).append(",");
            sb.append(effect.getSkill().getLevel()).append(",");
            sb.append(effect.getCount()).append(",");
            sb.append(effect.getTime()).append(",");
            sb.append(effect.getPeriod()).append(",");
            sb.append(order).append(",");
            sb.append(id).append(")");
            b.write(sb.toString());
          }

          while((effect = effect.getNext()) != null && effect.isSaveable()) {
            sb = new StringBuilder("(");
            sb.append(objectId).append(",");
            sb.append(effect.getSkill().getId()).append(",");
            sb.append(effect.getSkill().getLevel()).append(",");
            sb.append(effect.getCount()).append(",");
            sb.append(effect.getTime()).append(",");
            sb.append(effect.getPeriod()).append(",");
            sb.append(order).append(",");
            sb.append(id).append(")");
            b.write(sb.toString());
          }

          ++order;
        }
      } catch (Exception var15) {
        _log.error("Could not store active effects data!", var15);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }
}
