//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Map;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExEnchantSkillInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.SkillEnchant;

public class RequestExEnchantSkillInfo extends L2GameClientPacket {
  private int _skillId;
  private int _skillLvl;

  public RequestExEnchantSkillInfo() {
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
              } else if (newSkillEnch.getEnchantLevel() != 1) {
                player.sendPacket(new SystemMessage(1438));
                return;
              }

              int[] chances = newSkillEnch.getChances();
              int minPlayerLevel = Experience.LEVEL.length - chances.length - 1;
              if (player.getLevel() < minPlayerLevel) {
                this.sendPacket((new SystemMessage(607)).addNumber(minPlayerLevel));
              } else {
                int chanceIdx = Math.max(0, Math.min(player.getLevel() - minPlayerLevel, chances.length - 1));
                int chance = chances[chanceIdx];
                ExEnchantSkillInfo esi = new ExEnchantSkillInfo(newSkillEnch.getSkillId(), newSkillEnch.getSkillLevel(), newSkillEnch.getSp(), newSkillEnch.getExp(), chance);
                if (newSkillEnch.getItemId() > 0 && newSkillEnch.getItemCount() > 0L) {
                  esi.addNeededItem(newSkillEnch.getItemId(), newSkillEnch.getItemCount());
                }

                player.sendPacket(esi);
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
}
