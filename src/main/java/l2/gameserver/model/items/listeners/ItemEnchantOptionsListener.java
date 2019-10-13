//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.templates.OptionDataTemplate;

public final class ItemEnchantOptionsListener implements OnEquipListener {
  private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();

  public ItemEnchantOptionsListener() {
  }

  public static ItemEnchantOptionsListener getInstance() {
    return _instance;
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = actor.getPlayer();
      boolean needSendInfo = false;
      int[] var6 = item.getEnchantOptions();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        int i = var6[var8];
        OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
        if (template != null) {
          player.addStatFuncs(template.getStatFuncs(template));

          Iterator var11;
          for(var11 = template.getSkills().iterator(); var11.hasNext(); needSendInfo = true) {
            Skill skill = (Skill)var11.next();
            player.addSkill(skill, false);
          }

          var11 = template.getTriggerList().iterator();

          while(var11.hasNext()) {
            TriggerInfo triggerInfo = (TriggerInfo)var11.next();
            player.addTrigger(triggerInfo);
          }
        }
      }

      if (needSendInfo) {
        player.sendPacket(new SkillList(player));
      }

      player.sendChanges();
    }
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      Player player = actor.getPlayer();
      boolean needSendInfo = false;
      int[] var6 = item.getEnchantOptions();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        int i = var6[var8];
        OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
        if (template != null) {
          player.removeStatsOwner(template);

          Iterator var11;
          for(var11 = template.getSkills().iterator(); var11.hasNext(); needSendInfo = true) {
            Skill skill = (Skill)var11.next();
            player.removeSkill(skill, false);
          }

          var11 = template.getTriggerList().iterator();

          while(var11.hasNext()) {
            TriggerInfo triggerInfo = (TriggerInfo)var11.next();
            player.removeTrigger(triggerInfo);
          }
        }
      }

      if (needSendInfo) {
        player.sendPacket(new SkillList(player));
      }

      player.sendChanges();
    }
  }
}
