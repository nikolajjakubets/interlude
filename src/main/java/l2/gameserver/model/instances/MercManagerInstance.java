//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;

public final class MercManagerInstance extends MerchantInstance {
  private static int COND_ALL_FALSE = 0;
  private static int COND_BUSY_BECAUSE_OF_SIEGE = 1;
  private static int COND_OWNER = 2;

  public MercManagerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      int condition = this.validateCondition(player);
      if (condition > COND_ALL_FALSE && condition != COND_BUSY_BECAUSE_OF_SIEGE) {
        if (condition == COND_OWNER) {
          StringTokenizer st = new StringTokenizer(command, " ");
          String actualCommand = st.nextToken();
          String val = "";
          if (st.countTokens() >= 1) {
            val = st.nextToken();
          }

          if (actualCommand.equalsIgnoreCase("hire")) {
            if (val.equals("")) {
              return;
            }

            this.showShopWindow(player, Integer.parseInt(val), false);
          } else {
            super.onBypassFeedback(player, command);
          }
        }

      }
    }
  }

  public void showChatWindow(Player player, int val, Object... arg) {
    String filename = "castle/mercmanager/mercmanager-no.htm";
    int condition = this.validateCondition(player);
    if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
      filename = "castle/mercmanager/mercmanager-busy.htm";
    } else if (condition == COND_OWNER) {
      if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
        if (SevenSigns.getInstance().getSealOwner(3) == 2) {
          filename = "castle/mercmanager/mercmanager_dawn.htm";
        } else if (SevenSigns.getInstance().getSealOwner(3) == 1) {
          filename = "castle/mercmanager/mercmanager_dusk.htm";
        } else {
          filename = "castle/mercmanager/mercmanager.htm";
        }
      } else {
        filename = "castle/mercmanager/mercmanager_nohire.htm";
      }
    }

    player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
  }

  private int validateCondition(Player player) {
    if (player.isGM()) {
      return COND_OWNER;
    } else {
      if (this.getCastle() != null && this.getCastle().getId() > 0 && player.getClan() != null) {
        if (this.getCastle().getSiegeEvent().isInProgress()) {
          return COND_BUSY_BECAUSE_OF_SIEGE;
        }

        if (this.getCastle().getOwnerId() == player.getClanId() && (player.getClanPrivileges() & 2097152) == 2097152) {
          return COND_OWNER;
        }
      }

      return COND_ALL_FALSE;
    }
  }
}
