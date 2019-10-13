//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.ArmorSetsHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.ArmorSet;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SkillList;

public final class ArmorSetListener implements OnEquipListener {
  private static final ArmorSetListener _instance = new ArmorSetListener();

  public ArmorSetListener() {
  }

  public static ArmorSetListener getInstance() {
    return _instance;
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = (Player)actor;
      ItemInstance chestItem = player.getInventory().getPaperdollItem(10);
      if (chestItem != null) {
        ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
        if (armorSet != null) {
          boolean update = false;
          List skills;
          Iterator var9;
          Skill skill;
          if (armorSet.containItem(slot, item.getItemId())) {
            if (armorSet.containAll(player)) {
              skills = armorSet.getSkills();

              for(var9 = skills.iterator(); var9.hasNext(); update = true) {
                skill = (Skill)var9.next();
                player.addSkill(skill, false);
              }

              if (armorSet.containShield(player)) {
                skills = armorSet.getShieldSkills();

                for(var9 = skills.iterator(); var9.hasNext(); update = true) {
                  skill = (Skill)var9.next();
                  player.addSkill(skill, false);
                }
              }

              if (armorSet.isEnchanted6(player)) {
                skills = armorSet.getEnchant6skills();

                for(var9 = skills.iterator(); var9.hasNext(); update = true) {
                  skill = (Skill)var9.next();
                  player.addSkill(skill, false);
                }
              }
            }
          } else if (armorSet.containShield(item.getItemId()) && armorSet.containAll(player)) {
            skills = armorSet.getShieldSkills();

            for(var9 = skills.iterator(); var9.hasNext(); update = true) {
              skill = (Skill)var9.next();
              player.addSkill(skill, false);
            }
          }

          if (update) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
          }

        }
      }
    }
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = (Player)actor;
      boolean remove = false;
      List<Skill> removeSkillId1 = new ArrayList(1);
      List<Skill> removeSkillId2 = new ArrayList(1);
      List<Skill> removeSkillId3 = new ArrayList(1);
      if (slot == 10) {
        ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(item.getItemId());
        if (armorSet == null) {
          return;
        }

        remove = true;
        removeSkillId1 = armorSet.getSkills();
        removeSkillId2 = armorSet.getShieldSkills();
        removeSkillId3 = armorSet.getEnchant6skills();
      } else {
        ItemInstance chestItem = player.getInventory().getPaperdollItem(10);
        if (chestItem == null) {
          return;
        }

        ArmorSet armorSet = ArmorSetsHolder.getInstance().getArmorSet(chestItem.getItemId());
        if (armorSet == null) {
          return;
        }

        if (armorSet.containItem(slot, item.getItemId())) {
          remove = true;
          removeSkillId1 = armorSet.getSkills();
          removeSkillId2 = armorSet.getShieldSkills();
          removeSkillId3 = armorSet.getEnchant6skills();
        } else if (armorSet.containShield(item.getItemId())) {
          remove = true;
          removeSkillId2 = armorSet.getShieldSkills();
        }
      }

      boolean update = false;
      if (remove) {
        Skill skill;
        Iterator var14;
        for(var14 = ((List)removeSkillId1).iterator(); var14.hasNext(); update = true) {
          skill = (Skill)var14.next();
          player.removeSkill(skill, false);
        }

        for(var14 = ((List)removeSkillId2).iterator(); var14.hasNext(); update = true) {
          skill = (Skill)var14.next();
          player.removeSkill(skill);
        }

        for(var14 = ((List)removeSkillId3).iterator(); var14.hasNext(); update = true) {
          skill = (Skill)var14.next();
          player.removeSkill(skill);
        }
      }

      if (update) {
        player.sendPacket(new SkillList(player));
        player.updateStats();
      }

    }
  }
}
