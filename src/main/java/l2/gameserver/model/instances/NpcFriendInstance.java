//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.WarehouseFunctions;

public final class NpcFriendInstance extends MerchantInstance {
  public NpcFriendInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onAction(Player player, boolean shift) {
    if (this != player.getTarget()) {
      player.setTarget(this);
      player.sendPacket(new IStaticPacket[]{new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()), new ValidateLocation(this)});
      if (this.isAutoAttackable(player)) {
        player.sendPacket(this.makeStatusUpdate(new int[]{9, 10}));
      }

      player.sendActionFailed();
    } else {
      player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()));
      if (!Events.onAction(player, this, shift)) {
        if (this.isAutoAttackable(player)) {
          player.getAI().Attack(this, false, shift);
        } else if (!this.isInActingRange(player)) {
          if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
          }

        } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
          player.sendActionFailed();
        } else if ((Config.ALLOW_TALK_WHILE_SITTING || !player.isSitting()) && !player.isAlikeDead()) {
          if (this.hasRandomAnimation()) {
            this.onRandomAnimation();
          }

          player.sendActionFailed();
          player.setLastNpcInteractionTime();
          String filename = "";
          if (this.getNpcId() >= 31370 && this.getNpcId() <= 31376 && player.getVarka() > 0 || this.getNpcId() >= 31377 && this.getNpcId() < 31384 && player.getKetra() > 0) {
            filename = "npc_friend/" + this.getNpcId() + "-nofriend.htm";
            this.showChatWindow(player, filename, new Object[0]);
          } else {
            switch(this.getNpcId()) {
              case 31370:
              case 31371:
              case 31373:
              case 31377:
              case 31378:
              case 31380:
              case 31553:
              case 31554:
                filename = "npc_friend/" + this.getNpcId() + ".htm";
                break;
              case 31372:
                if (player.getKetra() > 2) {
                  filename = "npc_friend/" + this.getNpcId() + "-bufflist.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31374:
                if (player.getKetra() > 1) {
                  filename = "npc_friend/" + this.getNpcId() + "-warehouse.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31375:
                if (player.getKetra() != 3 && player.getKetra() != 4) {
                  if (player.getKetra() == 5) {
                    filename = "npc_friend/" + this.getNpcId() + "-special2.htm";
                  } else {
                    filename = "npc_friend/" + this.getNpcId() + ".htm";
                  }
                } else {
                  filename = "npc_friend/" + this.getNpcId() + "-special1.htm";
                }
                break;
              case 31376:
                if (player.getKetra() == 4) {
                  filename = "npc_friend/" + this.getNpcId() + "-normal.htm";
                } else if (player.getKetra() == 5) {
                  filename = "npc_friend/" + this.getNpcId() + "-special.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31379:
                if (player.getVarka() > 2) {
                  filename = "npc_friend/" + this.getNpcId() + "-bufflist.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31381:
                if (player.getVarka() > 1) {
                  filename = "npc_friend/" + this.getNpcId() + "-warehouse.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31382:
                if (player.getVarka() != 3 && player.getVarka() != 4) {
                  if (player.getVarka() == 5) {
                    filename = "npc_friend/" + this.getNpcId() + "-special2.htm";
                  } else {
                    filename = "npc_friend/" + this.getNpcId() + ".htm";
                  }
                } else {
                  filename = "npc_friend/" + this.getNpcId() + "-special1.htm";
                }
                break;
              case 31383:
                if (player.getVarka() == 4) {
                  filename = "npc_friend/" + this.getNpcId() + "-normal.htm";
                } else if (player.getVarka() == 5) {
                  filename = "npc_friend/" + this.getNpcId() + "-special.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31555:
                if (player.getRam() == 1) {
                  filename = "npc_friend/" + this.getNpcId() + "-special1.htm";
                } else if (player.getRam() == 2) {
                  filename = "npc_friend/" + this.getNpcId() + "-special2.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
                break;
              case 31556:
                if (player.getRam() == 2) {
                  filename = "npc_friend/" + this.getNpcId() + "-bufflist.htm";
                } else {
                  filename = "npc_friend/" + this.getNpcId() + ".htm";
                }
            }

            this.showChatWindow(player, filename, new Object[0]);
          }
        }
      }
    }
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      StringTokenizer st = new StringTokenizer(command, " ");
      String actualCommand = st.nextToken();
      int val;
      if (actualCommand.equalsIgnoreCase("Buff")) {
        if (st.countTokens() < 1) {
          return;
        }

        val = Integer.parseInt(st.nextToken());
        int item = 0;
        switch(this.getNpcId()) {
          case 31372:
            item = 7186;
            break;
          case 31379:
            item = 7187;
            break;
          case 31556:
            item = 7251;
        }

        int skill = 0;
        int level = 0;
        long count = 0L;
        switch(val) {
          case 1:
            skill = 4359;
            level = 2;
            count = 2L;
            break;
          case 2:
            skill = 4360;
            level = 2;
            count = 2L;
            break;
          case 3:
            skill = 4345;
            level = 3;
            count = 3L;
            break;
          case 4:
            skill = 4355;
            level = 2;
            count = 3L;
            break;
          case 5:
            skill = 4352;
            level = 1;
            count = 3L;
            break;
          case 6:
            skill = 4354;
            level = 3;
            count = 3L;
            break;
          case 7:
            skill = 4356;
            level = 1;
            count = 6L;
            break;
          case 8:
            skill = 4357;
            level = 2;
            count = 6L;
        }

        if (skill != 0 && player.getInventory().destroyItemByItemId(item, count)) {
          player.doCast(SkillTable.getInstance().getInfo(skill, level), player, true);
        } else {
          this.showChatWindow(player, "npc_friend/" + this.getNpcId() + "-havenotitems.htm", new Object[0]);
        }
      } else if (command.startsWith("Chat")) {
        val = Integer.parseInt(command.substring(5));
        String fname = "";
        fname = "npc_friend/" + this.getNpcId() + "-" + val + ".htm";
        if (!fname.equals("")) {
          this.showChatWindow(player, fname, new Object[0]);
        }
      } else if (command.startsWith("Buy")) {
        val = Integer.parseInt(command.substring(4));
        this.showShopWindow(player, val, false);
      } else if (actualCommand.equalsIgnoreCase("Sell")) {
        this.showShopWindow(player);
      } else if (command.startsWith("WithdrawP")) {
        val = Integer.parseInt(command.substring(10));
        if (val == 99) {
          NpcHtmlMessage html = new NpcHtmlMessage(player, this);
          html.setFile("npc-friend/personal.htm");
          html.replace("%npcname%", this.getName());
          player.sendPacket(html);
        } else {
          WarehouseFunctions.showRetrieveWindow(player, val);
        }
      } else if (command.equals("DepositP")) {
        WarehouseFunctions.showDepositWindow(player);
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }
}
