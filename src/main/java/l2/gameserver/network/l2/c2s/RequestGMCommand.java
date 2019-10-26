//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.GMHennaInfo;
import l2.gameserver.network.l2.s2c.GMViewCharacterInfo;
import l2.gameserver.network.l2.s2c.GMViewItemList;
import l2.gameserver.network.l2.s2c.GMViewPledgeInfo;
import l2.gameserver.network.l2.s2c.GMViewQuestInfo;
import l2.gameserver.network.l2.s2c.GMViewSkillInfo;
import l2.gameserver.network.l2.s2c.GMViewWarehouseWithdrawList;

public class RequestGMCommand extends L2GameClientPacket {
  private String _targetName;
  private int _command;

  public RequestGMCommand() {
  }

  protected void readImpl() {
    this._targetName = this.readS();
    this._command = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    Player target = World.getPlayer(this._targetName);
    if (player != null && target != null) {
      if (player.getPlayerAccess().CanViewChar) {
        switch(this._command) {
          case 1:
            player.sendPacket(new GMViewCharacterInfo(target));
            player.sendPacket(new GMHennaInfo(target));
            break;
          case 2:
            if (target.getClan() != null) {
              player.sendPacket(new GMViewPledgeInfo(target));
            }
            break;
          case 3:
            player.sendPacket(new GMViewSkillInfo(target));
            break;
          case 4:
            player.sendPacket(new GMViewQuestInfo(target));
            break;
          case 5:
            ItemInstance[] items = target.getInventory().getItems();
            int questSize = 0;
            player.sendPacket(new GMViewItemList(target, items, items.length - questSize));
            break;
          case 6:
            player.sendPacket(new GMViewWarehouseWithdrawList(target));
        }

      }
    }
  }
}
