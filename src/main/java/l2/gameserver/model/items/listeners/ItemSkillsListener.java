//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.stats.Formulas;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;

public final class ItemSkillsListener implements OnEquipListener {
  private static final ItemSkillsListener _instance = new ItemSkillsListener();

  public ItemSkillsListener() {
  }

  public static ItemSkillsListener getInstance() {
    return _instance;
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    Player player = (Player)actor;
    Skill[] itemSkills = null;
    Skill enchant4Skill = null;
    ItemTemplate it = item.getTemplate();
    itemSkills = it.getAttachedSkills();
    enchant4Skill = it.getEnchant4Skill();
    player.removeTriggers(it);
    if (itemSkills != null && itemSkills.length > 0) {
      Skill[] var8 = itemSkills;
      int var9 = itemSkills.length;

      for(int var10 = 0; var10 < var9; ++var10) {
        Skill itemSkill = var8[var10];
        if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
          int level = player.getSkillLevel(itemSkill.getId());
          int newlevel = level - 1;
          if (newlevel > 0) {
            player.addSkill(SkillTable.getInstance().getInfo(itemSkill.getId(), newlevel), false);
          } else {
            player.removeSkillById(itemSkill.getId());
          }
        } else {
          player.removeSkill(itemSkill, false);
        }
      }
    }

    if (enchant4Skill != null) {
      player.removeSkill(enchant4Skill, false);
    }

    if (itemSkills.length > 0 || enchant4Skill != null) {
      player.sendPacket(new SkillList(player));
      player.updateStats();
    }

  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    Player player = (Player)actor;
    Skill[] itemSkills = null;
    Skill enchant4Skill = null;
    ItemTemplate it = item.getTemplate();
    itemSkills = it.getAttachedSkills();
    if (item.getEnchantLevel() >= 4) {
      enchant4Skill = it.getEnchant4Skill();
    }

    if (it.getType2() != 0 || player.getGradePenalty() <= 0) {
      player.addTriggers(it);
      boolean needSendInfo = false;
      if (itemSkills.length > 0) {
        Skill[] var9 = itemSkills;
        int var10 = itemSkills.length;

        for(int var11 = 0; var11 < var10; ++var11) {
          Skill itemSkill = var9[var11];
          if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
            int level = player.getSkillLevel(itemSkill.getId());
            int newlevel = level;
            if (level > 0) {
              if (SkillTable.getInstance().getInfo(itemSkill.getId(), level + 1) != null) {
                newlevel = level + 1;
              }
            } else {
              newlevel = 1;
            }

            if (newlevel != level) {
              player.addSkill(SkillTable.getInstance().getInfo(itemSkill.getId(), newlevel), false);
            }
          } else if (player.getSkillLevel(itemSkill.getId()) < itemSkill.getLevel()) {
            player.addSkill(itemSkill, false);
            if (itemSkill.isActive()) {
              long reuseDelay = Formulas.calcSkillReuseDelay(player, itemSkill);
              reuseDelay = Math.min(reuseDelay, 30000L);
              if (reuseDelay > 0L && !player.isSkillDisabled(itemSkill)) {
                player.disableSkill(itemSkill, reuseDelay);
                needSendInfo = true;
              }
            }
          }
        }
      }

      if (enchant4Skill != null) {
        player.addSkill(enchant4Skill, false);
      }

      if (itemSkills.length > 0 || enchant4Skill != null) {
        player.sendPacket(new SkillList(player));
        player.updateStats();
        if (needSendInfo) {
          player.sendPacket(new SkillCoolTime(player));
        }
      }

    }
  }
}
