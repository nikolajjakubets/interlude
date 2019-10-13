//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;

public final class ArmorSet {
  private final int _set_id;
  private final List<Integer> _chest = new ArrayList(1);
  private final List<Integer> _legs = new ArrayList(1);
  private final List<Integer> _head = new ArrayList(1);
  private final List<Integer> _gloves = new ArrayList(1);
  private final List<Integer> _feet = new ArrayList(1);
  private final List<Integer> _shield = new ArrayList(1);
  private final List<Skill> _skills = new ArrayList(1);
  private final List<Skill> _shieldSkills = new ArrayList(1);
  private final List<Skill> _enchant6skills = new ArrayList(1);

  public ArmorSet(int set_id, String[] chest, String[] legs, String[] head, String[] gloves, String[] feet, String[] skills, String[] shield, String[] shield_skills, String[] enchant6skills) {
    this._set_id = set_id;
    String[] var11;
    int var12;
    int var13;
    String skill;
    if (chest != null) {
      var11 = chest;
      var12 = chest.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._chest.add(Integer.parseInt(skill));
      }
    }

    if (legs != null) {
      var11 = legs;
      var12 = legs.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._legs.add(Integer.parseInt(skill));
      }
    }

    if (head != null) {
      var11 = head;
      var12 = head.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._head.add(Integer.parseInt(skill));
      }
    }

    if (gloves != null) {
      var11 = gloves;
      var12 = gloves.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._gloves.add(Integer.parseInt(skill));
      }
    }

    if (feet != null) {
      var11 = feet;
      var12 = feet.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._feet.add(Integer.parseInt(skill));
      }
    }

    if (shield != null) {
      var11 = shield;
      var12 = shield.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        this._shield.add(Integer.parseInt(skill));
      }
    }

    StringTokenizer st;
    int skillId;
    int skillLvl;
    if (skills != null) {
      var11 = skills;
      var12 = skills.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        st = new StringTokenizer(skill, "-");
        if (st.hasMoreTokens()) {
          skillId = Integer.parseInt(st.nextToken());
          skillLvl = Integer.parseInt(st.nextToken());
          this._skills.add(SkillTable.getInstance().getInfo(skillId, skillLvl));
        }

        this._skills.add(SkillTable.getInstance().getInfo(3006, 1));
      }
    }

    if (shield_skills != null) {
      var11 = shield_skills;
      var12 = shield_skills.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        st = new StringTokenizer(skill, "-");
        if (st.hasMoreTokens()) {
          skillId = Integer.parseInt(st.nextToken());
          skillLvl = Integer.parseInt(st.nextToken());
          this._shieldSkills.add(SkillTable.getInstance().getInfo(skillId, skillLvl));
        }
      }
    }

    if (enchant6skills != null) {
      var11 = enchant6skills;
      var12 = enchant6skills.length;

      for(var13 = 0; var13 < var12; ++var13) {
        skill = var11[var13];
        st = new StringTokenizer(skill, "-");
        if (st.hasMoreTokens()) {
          skillId = Integer.parseInt(st.nextToken());
          skillLvl = Integer.parseInt(st.nextToken());
          this._enchant6skills.add(SkillTable.getInstance().getInfo(skillId, skillLvl));
        }
      }
    }

  }

  public boolean containAll(Player player) {
    Inventory inv = player.getInventory();
    ItemInstance chestItem = inv.getPaperdollItem(10);
    ItemInstance legsItem = inv.getPaperdollItem(11);
    ItemInstance headItem = inv.getPaperdollItem(6);
    ItemInstance glovesItem = inv.getPaperdollItem(9);
    ItemInstance feetItem = inv.getPaperdollItem(12);
    int chest = 0;
    int legs = 0;
    int head = 0;
    int gloves = 0;
    int feet = 0;
    if (chestItem != null) {
      legs = chestItem.getItemId();
    }

    if (legsItem != null) {
      legs = legsItem.getItemId();
    }

    if (headItem != null) {
      head = headItem.getItemId();
    }

    if (glovesItem != null) {
      gloves = glovesItem.getItemId();
    }

    if (feetItem != null) {
      feet = feetItem.getItemId();
    }

    return this.containAll(chest, legs, head, gloves, feet);
  }

  public boolean containAll(int chest, int legs, int head, int gloves, int feet) {
    if (this._chest.isEmpty() && !this._chest.contains(chest)) {
      return false;
    } else if (!this._legs.isEmpty() && !this._legs.contains(legs)) {
      return false;
    } else if (!this._head.isEmpty() && !this._head.contains(head)) {
      return false;
    } else if (!this._gloves.isEmpty() && !this._gloves.contains(gloves)) {
      return false;
    } else {
      return this._feet.isEmpty() || this._feet.contains(feet);
    }
  }

  public boolean containItem(int slot, int itemId) {
    switch(slot) {
      case 6:
        return this._head.contains(itemId);
      case 7:
      case 8:
      default:
        return false;
      case 9:
        return this._gloves.contains(itemId);
      case 10:
        return this._chest.contains(itemId);
      case 11:
        return this._legs.contains(itemId);
      case 12:
        return this._feet.contains(itemId);
    }
  }

  public int getSetById() {
    return this._set_id;
  }

  public List<Integer> getChestItemIds() {
    return this._chest;
  }

  public List<Skill> getSkills() {
    return this._skills;
  }

  public List<Skill> getShieldSkills() {
    return this._shieldSkills;
  }

  public List<Skill> getEnchant6skills() {
    return this._enchant6skills;
  }

  public boolean containShield(Player player) {
    Inventory inv = player.getInventory();
    ItemInstance shieldItem = inv.getPaperdollItem(8);
    return shieldItem != null && this._shield.contains(shieldItem.getItemId());
  }

  public boolean containShield(int shield_id) {
    return this._shield.isEmpty() ? false : this._shield.contains(shield_id);
  }

  public boolean isEnchanted6(Player player) {
    if (!this.containAll(player)) {
      return false;
    } else {
      Inventory inv = player.getInventory();
      ItemInstance chestItem = inv.getPaperdollItem(10);
      ItemInstance legsItem = inv.getPaperdollItem(11);
      ItemInstance headItem = inv.getPaperdollItem(6);
      ItemInstance glovesItem = inv.getPaperdollItem(9);
      ItemInstance feetItem = inv.getPaperdollItem(12);
      if (!this._chest.isEmpty() && chestItem.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
        return false;
      } else if (!this._legs.isEmpty() && legsItem.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
        return false;
      } else if (!this._gloves.isEmpty() && glovesItem.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
        return false;
      } else if (!this._head.isEmpty() && headItem.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
        return false;
      } else {
        return this._feet.isEmpty() || feetItem.getEnchantLevel() >= Config.ARMOR_ENCHANT_6_SKILL;
      }
    }
  }
}
