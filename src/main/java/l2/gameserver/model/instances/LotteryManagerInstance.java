//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.text.DateFormat;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.games.LotteryManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class LotteryManagerInstance extends NpcInstance {
  public LotteryManagerInstance(int objectID, NpcTemplate template) {
    super(objectID, template);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (command.startsWith("Loto")) {
        try {
          int val = Integer.parseInt(command.substring(5));
          this.showLotoWindow(player, val);
        } catch (NumberFormatException var4) {
          Log.debug("L2LotteryManagerInstance: bypass: " + command + "; player: " + player, var4);
        }
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "LotteryManager";
    } else {
      pom = "LotteryManager-" + val;
    }

    return "lottery/" + pom + ".htm";
  }

  public void showLotoWindow(Player player, int val) {
    int npcId = this.getTemplate().npcId;
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    String filename;
    int count;
    int type2;
    int i;
    int i;
    switch(val) {
      case 0:
        filename = this.getHtmlPath(npcId, 1, player);
        html.setFile(filename);
        break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
        if (!LotteryManager.getInstance().isStarted()) {
          player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
          return;
        }

        if (!LotteryManager.getInstance().isSellableTickets()) {
          player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
          return;
        }

        filename = this.getHtmlPath(npcId, 5, player);
        html.setFile(filename);
        count = 0;
        int found = false;

        for(i = 0; i < 5; ++i) {
          if (player.getLoto(i) == val) {
            player.setLoto(i, 0);
            found = true;
          } else if (player.getLoto(i) > 0) {
            ++count;
          }
        }

        if (count < 5 && !found && val <= 20) {
          for(i = 0; i < 5; ++i) {
            if (player.getLoto(i) == 0) {
              player.setLoto(i, val);
              break;
            }
          }
        }

        count = 0;

        String replace;
        for(i = 0; i < 5; ++i) {
          if (player.getLoto(i) > 0) {
            ++count;
            replace = String.valueOf(player.getLoto(i));
            if (player.getLoto(i) < 10) {
              replace = "0" + replace;
            }

            String search = "fore=\"L2UI.lottoNum" + replace + "\" back=\"L2UI.lottoNum" + replace + "a_check\"";
            String replace = "fore=\"L2UI.lottoNum" + replace + "a_check\" back=\"L2UI.lottoNum" + replace + "\"";
            html.replace(search, replace);
          }
        }

        if (count == 5) {
          String search = "";
          replace = "";
          if (!player.isLangRus()) {
            search = "0\">Return";
            replace = "22\">The winner selected the numbers above.";
          } else {
            search = "0\">Назад";
            replace = "22\">Выигрышные номера выбранные выше.";
          }

          html.replace(search, replace);
        }
        break;
      case 22:
        if (!LotteryManager.getInstance().isStarted()) {
          player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
          return;
        }

        if (!LotteryManager.getInstance().isSellableTickets()) {
          player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
          return;
        }

        count = Config.SERVICES_ALT_LOTTERY_PRICE;
        int lotonumber = LotteryManager.getInstance().getId();
        i = 0;
        type2 = 0;

        for(i = 0; i < 5; ++i) {
          if (player.getLoto(i) == 0) {
            return;
          }

          if (player.getLoto(i) < 17) {
            i = (int)((double)i + Math.pow(2.0D, (double)(player.getLoto(i) - 1)));
          } else {
            type2 = (int)((double)type2 + Math.pow(2.0D, (double)(player.getLoto(i) - 17)));
          }
        }

        if (player.getAdena() < (long)count) {
          player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
          return;
        }

        player.reduceAdena((long)count, true);
        SystemMessage sm = new SystemMessage(371);
        sm.addNumber(lotonumber);
        sm.addItemName(4442);
        player.sendPacket(sm);
        ItemInstance item = ItemFunctions.createItem(4442);
        item.setBlessed(lotonumber);
        item.setEnchantLevel(i);
        item.setDamaged(type2);
        player.getInventory().addItem(item);
        filename = this.getHtmlPath(npcId, 3, player);
        html.setFile(filename);
        break;
      case 23:
        filename = this.getHtmlPath(npcId, 3, player);
        html.setFile(filename);
        break;
      case 24:
        filename = this.getHtmlPath(npcId, 4, player);
        html.setFile(filename);
        count = LotteryManager.getInstance().getId();
        String message = "";
        ItemInstance[] var9 = player.getInventory().getItems();
        type2 = var9.length;

        for(i = 0; i < type2; ++i) {
          ItemInstance item = var9[i];
          if (item != null && item.getItemId() == 4442 && item.getBlessed() < count) {
            message = message + "<a action=\"bypass -h npc_%objectId%_Loto " + item.getObjectId() + "\">" + item.getBlessed();
            message = message + " " + (new CustomMessage("LotteryManagerInstance.NpcString.EVENT_NUMBER", player, new Object[0])).toString() + " ";
            int[] numbers = LotteryManager.getInstance().decodeNumbers(item.getEnchantLevel(), item.getDamaged());

            for(int i = 0; i < 5; ++i) {
              message = message + numbers[i] + " ";
            }

            int[] check = LotteryManager.getInstance().checkTicket(item);
            if (check[0] > 0) {
              message = message + "- ";
              switch(check[0]) {
                case 1:
                  message = message + (new CustomMessage("LotteryManagerInstance.NpcString.FIRST_PRIZE", player, new Object[0])).toString();
                  break;
                case 2:
                  message = message + (new CustomMessage("LotteryManagerInstance.NpcString.SECOND_PRIZE", player, new Object[0])).toString();
                  break;
                case 3:
                  message = message + (new CustomMessage("LotteryManagerInstance.NpcString.THIRD_PRIZE", player, new Object[0])).toString();
                  break;
                case 4:
                  message = message + (new CustomMessage("LotteryManagerInstance.NpcString.FOURTH_PRIZE", player, new Object[0])).toString();
              }

              message = message + " " + check[1] + "a.";
            }

            message = message + "</a>";
          }
        }

        if (message.length() == 0) {
          message = message + (new CustomMessage("LotteryManagerInstance.NpcString.THERE_HAS_BEEN_NO_WINNING_LOTTERY_TICKET", player, new Object[0])).toString();
        }

        html.replace("%result%", message);
        break;
      case 25:
        filename = this.getHtmlPath(npcId, 2, player);
        html.setFile(filename);
        break;
      default:
        if (val > 25) {
          count = LotteryManager.getInstance().getId();
          ItemInstance item = player.getInventory().getItemByObjectId(val);
          if (item != null && item.getItemId() == 4442 && item.getBlessed() < count) {
            int[] check = LotteryManager.getInstance().checkTicket(item);
            if (player.getInventory().destroyItem(item, 1L)) {
              player.sendPacket(SystemMessage2.removeItems(4442, 1L));
              type2 = check[1];
              if (type2 > 0) {
                player.addAdena((long)type2);
              }
            }

            return;
          }

          return;
        }
    }

    html.replace("%objectId%", String.valueOf(this.getObjectId()));
    html.replace("%race%", "" + LotteryManager.getInstance().getId());
    html.replace("%adena%", "" + LotteryManager.getInstance().getPrize());
    html.replace("%ticket_price%", "" + Config.SERVICES_LOTTERY_TICKET_PRICE);
    html.replace("%prize5%", "" + Config.SERVICES_LOTTERY_5_NUMBER_RATE * 100.0D);
    html.replace("%prize4%", "" + Config.SERVICES_LOTTERY_4_NUMBER_RATE * 100.0D);
    html.replace("%prize3%", "" + Config.SERVICES_LOTTERY_3_NUMBER_RATE * 100.0D);
    html.replace("%prize2%", "" + Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE);
    html.replace("%enddate%", "" + DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));
    player.sendPacket(html);
    player.sendActionFailed();
  }
}
