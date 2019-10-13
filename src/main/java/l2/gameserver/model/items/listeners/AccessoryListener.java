//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.items.ItemInstance;

public final class AccessoryListener implements OnEquipListener {
  private static final AccessoryListener _instance = new AccessoryListener();

  public AccessoryListener() {
  }

  public static AccessoryListener getInstance() {
    return _instance;
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = (Player)actor;
      if (item.getBodyPart() == 2097152 && item.getTemplate().getAttachedSkills().length > 0) {
        int agathionId = player.getAgathionId();
        int transformNpcId = player.getTransformationTemplate();
        Skill[] var7 = item.getTemplate().getAttachedSkills();
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
          Skill skill = var7[var9];
          if (agathionId > 0 && skill.getNpcId() == agathionId) {
            player.setAgathion(0);
          }

          if (skill.getNpcId() == transformNpcId && skill.getSkillType() == SkillType.TRANSFORMATION) {
            player.setTransformation(0);
          }
        }
      }

      if (!item.isAccessory() && !item.getTemplate().isTalisman() && !item.getTemplate().isBracelet()) {
        player.broadcastCharInfo();
      } else {
        player.sendUserInfo(true);
      }

    }
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = (Player)actor;
      if (!item.isAccessory() && !item.getTemplate().isTalisman() && !item.getTemplate().isBracelet()) {
        player.broadcastCharInfo();
      } else {
        player.sendUserInfo(true);
        player.sendItemList(false);
      }

    }
  }
}
