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
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.templates.OptionDataTemplate;

public final class ItemAugmentationListener implements OnEquipListener {
  private static final ItemAugmentationListener _instance = new ItemAugmentationListener();

  public ItemAugmentationListener() {
  }

  public static ItemAugmentationListener getInstance() {
    return _instance;
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      if (item.isAugmented()) {
        Player player = actor.getPlayer();
        int[] stats = new int[]{item.getVariationStat1(), item.getVariationStat2()};
        boolean sendList = false;
        int[] var7 = stats;
        int var8 = stats.length;

        for(int var9 = 0; var9 < var8; ++var9) {
          int i = var7[var9];
          OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
          if (template != null) {
            player.removeStatsOwner(template);
            Iterator var12 = template.getSkills().iterator();

            while(var12.hasNext()) {
              Skill skill = (Skill)var12.next();
              sendList = true;
              player.removeSkill(skill);
            }

            player.removeTriggers(template);
          }
        }

        if (sendList) {
          player.sendPacket(new SkillList(player));
        }

        player.updateStats();
      }
    }
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable()) {
      if (item.isAugmented()) {
        Player player = actor.getPlayer();
        if (player.getExpertisePenalty(item) <= 0) {
          int[] stats = new int[]{item.getVariationStat1(), item.getVariationStat2()};
          boolean sendList = false;
          boolean sendReuseList = false;
          int[] var8 = stats;
          int var9 = stats.length;

          for(int var10 = 0; var10 < var9; ++var10) {
            int i = var8[var10];
            OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
            if (template != null) {
              player.addStatFuncs(template.getStatFuncs(template));
              Iterator var13 = template.getSkills().iterator();

              while(var13.hasNext()) {
                Skill skill = (Skill)var13.next();
                sendList = true;
                player.addSkill(skill);
                if (player.isSkillDisabled(skill)) {
                  sendReuseList = true;
                }
              }

              player.addTriggers(template);
            }
          }

          if (sendList) {
            player.sendPacket(new SkillList(player));
          }

          if (sendReuseList) {
            player.sendPacket(new SkillCoolTime(player));
          }

          player.updateStats();
        }
      }
    }
  }
}
