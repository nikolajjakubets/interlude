//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExEnchantSkillList;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.SkillEnchant;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestExEnchantSkill extends L2GameClientPacket {
  private static final Logger LOG = LoggerFactory.getLogger(RequestExEnchantSkill.class);
  private int _skillId;
  private int _skillLvl;

  public RequestExEnchantSkill() {
  }

  protected void readImpl() {
    this._skillId = this.readD();
    this._skillLvl = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      if (player.getClassId().getLevel() >= 4 && player.getLevel() >= 76) {
        Skill currSkill = player.getKnownSkill(this._skillId);
        if (currSkill == null) {
          player.sendPacket(new SystemMessage(1438));
        } else {
          int currSkillLevel = currSkill.getLevel();
          int currSkillBaseLevel = currSkill.getBaseLevel();
          Map<Integer, SkillEnchant> skillEnchLevels = EnchantSkillHolder.getInstance().getLevelsOf(this._skillId);
          if (skillEnchLevels != null && !skillEnchLevels.isEmpty()) {
            SkillEnchant currSkillEnch = (SkillEnchant)skillEnchLevels.get(currSkillLevel);
            SkillEnchant newSkillEnch = (SkillEnchant)skillEnchLevels.get(this._skillLvl);
            if (newSkillEnch == null) {
              player.sendPacket(new SystemMessage(1438));
            } else {
              if (currSkillEnch != null) {
                if (currSkillEnch.getRouteId() != newSkillEnch.getRouteId() || newSkillEnch.getEnchantLevel() != currSkillEnch.getEnchantLevel() + 1) {
                  player.sendPacket(new SystemMessage(1438));
                  return;
                }
              } else if (newSkillEnch.getEnchantLevel() != 1 || currSkillLevel != currSkillBaseLevel) {
                player.sendPacket(new SystemMessage(1438));
                LOG.warn("Player \"" + player.toString() + "\" trying to use enchant  exploit" + currSkill.toString() + " to " + this._skillLvl + "(enchant level " + newSkillEnch.getEnchantLevel() + ")");
                return;
              }

              int[] chances = newSkillEnch.getChances();
              int minPlayerLevel = Experience.LEVEL.length - chances.length - 1;
              if (player.getLevel() < minPlayerLevel) {
                this.sendPacket((new SystemMessage(607)).addNumber(minPlayerLevel));
              } else if (player.getSp() < (long)newSkillEnch.getSp()) {
                this.sendPacket(new SystemMessage(1443));
              } else if (player.getExp() < newSkillEnch.getExp()) {
                this.sendPacket(new SystemMessage(1444));
              } else if (newSkillEnch.getItemId() > 0 && newSkillEnch.getItemCount() > 0L && Functions.removeItem(player, newSkillEnch.getItemId(), newSkillEnch.getItemCount()) < newSkillEnch.getItemCount()) {
                this.sendPacket(Msg.ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT);
              } else {
                int chanceIdx = Math.max(0, Math.min(player.getLevel() - minPlayerLevel, chances.length - 1));
                int chance = chances[chanceIdx];
                player.addExpAndSp(-1L * newSkillEnch.getExp(), (long)(-1 * newSkillEnch.getSp()));
                player.sendPacket((new SystemMessage(538)).addNumber(newSkillEnch.getSp()));
                player.sendPacket((new SystemMessage(539)).addNumber(newSkillEnch.getExp()));
                TimeStamp currSkillReuseTimeStamp = player.getSkillReuse(currSkill);
                Skill newSkill = null;
                if (Rnd.chance(chance)) {
                  newSkill = SkillTable.getInstance().getInfo(newSkillEnch.getSkillId(), newSkillEnch.getSkillLevel());
                  player.sendPacket((new SystemMessage(1440)).addSkillName(this._skillId, this._skillLvl));
                  Log.add(player.getName() + "|Successfully enchanted|" + this._skillId + "|to+" + this._skillLvl + "|" + chance, "enchant_skills");
                } else {
                  newSkill = SkillTable.getInstance().getInfo(currSkill.getId(), currSkill.getBaseLevel());
                  player.sendPacket((new SystemMessage(1441)).addSkillName(this._skillId, this._skillLvl));
                  Log.add(player.getName() + "|Failed to enchant|" + this._skillId + "|to+" + this._skillLvl + "|" + chance, "enchant_skills");
                }

                if (currSkillReuseTimeStamp != null && currSkillReuseTimeStamp.hasNotPassed()) {
                  player.disableSkill(newSkill, currSkillReuseTimeStamp.getReuseCurrent());
                }

                player.addSkill(newSkill, true);
                player.sendPacket(new SkillList(player));
                updateSkillShortcuts(player, this._skillId, this._skillLvl);
                player.sendPacket(ExEnchantSkillList.packetFor(player));
              }
            }
          } else {
            player.sendPacket(new SystemMessage(1438));
          }
        }
      } else {
        player.sendPacket(new SystemMessage(1438));
      }
    }
  }

  protected static void updateSkillShortcuts(Player player, int skillId, int skillLevel) {
    Iterator var3 = player.getAllShortCuts().iterator();

    while(var3.hasNext()) {
      ShortCut sc = (ShortCut)var3.next();
      if (sc.getId() == skillId && sc.getType() == 2) {
        ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
        player.sendPacket(new ShortCutRegister(player, newsc));
        player.registerShortCut(newsc);
      }
    }

  }
}
