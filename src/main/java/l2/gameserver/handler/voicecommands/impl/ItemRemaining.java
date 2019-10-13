//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.ArrayUtils;

public class ItemRemaining extends Functions implements IVoicedCommandHandler {
  private String[] _commandList = new String[]{"itemremaining", "itemsremaining", "rune", "rune_remaining", "runeremaining"};

  public ItemRemaining() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String target) {
    if (ArrayUtils.contains(this._commandList, command.toLowerCase())) {
      this.sendItemRemaining(activeChar);
      return true;
    } else {
      return false;
    }
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  private void sendItemRemaining(Player player) {
    ItemInstance[] items = player.getInventory().getItems();
    ItemInstance[] var3 = items;
    int var4 = items.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      ItemInstance item = var3[var5];
      if (item.isTemporalItem()) {
        int remainingSec = item.getPeriod();
        String remainingText = Util.formatTime(remainingSec);
        player.sendMessage(new CustomMessage("voicedcommandhandlers.ItemRemaining", player, new Object[]{item, remainingText}));
      }
    }

  }
}
