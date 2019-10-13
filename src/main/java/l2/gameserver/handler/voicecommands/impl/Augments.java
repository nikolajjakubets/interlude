//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.templates.OptionDataTemplate;

public class Augments implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"aug", "augments"};

  public Augments() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player player, String args) {
    for(int slot = 0; slot < 17; ++slot) {
      ItemInstance item = player.getInventory().getPaperdollItem(slot);
      if (item != null && item.isAugmented()) {
        StringBuilder info = new StringBuilder(30);
        info.append("<<Detail augments info>>");
        info.append("\n");
        info.append(item.getName() + " with enchant level " + item.getEnchantLevel() + " have augment:");
        info.append("\n");
        info.append("Option id 1 : " + item.getVariationStat1());
        info.append("\n");
        info.append("Option id 2 : " + item.getVariationStat2());
        this.getInfo(info, item.getVariationStat1());
        this.getInfo(info, item.getVariationStat2());
        player.sendMessage(info.toString());
      }
    }

    return true;
  }

  private void getInfo(StringBuilder info, int id) {
    OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(id);
    if (template != null) {
      Iterator var4;
      if (!template.getSkills().isEmpty()) {
        var4 = template.getSkills().iterator();

        while(var4.hasNext()) {
          Skill s = (Skill)var4.next();
          info.append(" ");
          info.append("\n");
          info.append("Skill name: " + s.getName() + " (id: " + s.getId() + ") - level " + s.getLevel());
          info.append("\n");
        }
      }

      if (!template.getTriggerList().isEmpty()) {
        var4 = template.getTriggerList().iterator();

        while(var4.hasNext()) {
          TriggerInfo t = (TriggerInfo)var4.next();
          info.append("\n");
          info.append("Chance skill id: " + t.id);
          info.append(" - level " + t.level);
          info.append("\n");
          info.append("Activation type " + t.getType());
          info.append("\n");
          info.append("Activation chance : " + t.getChance() + "%");
        }
      }
    }

  }
}
