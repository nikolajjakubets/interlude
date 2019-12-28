//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.templates.npc.NpcTemplate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ManorManagerInstance extends MerchantInstance {
  public ManorManagerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public void onAction(Player player, boolean shift) {
    if (this != player.getTarget()) {
      player.setTarget(this);
      player.sendPacket(new IStaticPacket[]{new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()), new ValidateLocation(this)});
    } else {
      MyTargetSelected my = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
      player.sendPacket(my);
      if (!this.isInActingRange(player)) {
        if (!player.getAI().isIntendingInteract(this)) {
          player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
        }

        player.sendActionFailed();
      } else {
        if (CastleManorManager.getInstance().isDisabled()) {
          NpcHtmlMessage html = new NpcHtmlMessage(player, this);
          html.setFile("npcdefault.htm");
          html.replace("%objectId%", String.valueOf(this.getObjectId()));
          html.replace("%npcname%", this.getName());
          player.sendPacket(html);
        } else if (!player.isGM() && player.isClanLeader() && this.getCastle() != null && this.getCastle().getOwnerId() == player.getClanId()) {
          this.showMessageWindow(player, "manager-lord.htm");
        } else {
          this.showMessageWindow(player, "manager.htm");
        }

        player.sendActionFailed();
      }
    }

  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (command.startsWith("manor_menu_select")) {
        if (CastleManorManager.getInstance().isUnderMaintenance()) {
          player.sendPacket(new IStaticPacket[]{ActionFail.getStatic(), Msg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE});
          return;
        }

        String params = command.substring(command.indexOf("?") + 1);
        StringTokenizer st = new StringTokenizer(params, "&");
        int ask = Integer.parseInt(st.nextToken().split("=")[1]);
        int state = Integer.parseInt(st.nextToken().split("=")[1]);
        int time = Integer.parseInt(st.nextToken().split("=")[1]);
        Castle castle = this.getCastle();
        int castleId;
        if (state == -1) {
          castleId = castle.getId();
        } else {
          castleId = state;
        }

        switch(ask) {
          case 1:
            if (castleId != castle.getId()) {
              player.sendPacket(Msg._HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR);
            } else {
              NpcTradeList tradeList = new NpcTradeList(0);
              List<SeedProduction> seeds = castle.getSeedProduction(0);
              Iterator var12 = seeds.iterator();

              while(var12.hasNext()) {
                SeedProduction s = (SeedProduction)var12.next();
                TradeItem item = new TradeItem();
                item.setItemId(s.getId());
                item.setOwnersPrice(s.getPrice());
                item.setCount(s.getCanProduce());
                if (item.getCount() > 0L && item.getOwnersPrice() > 0L) {
                  tradeList.addItem(item);
                }
              }

              BuyListSeed bl = new BuyListSeed(tradeList, castleId, player.getAdena());
              player.sendPacket(bl);
            }
            break;
          case 2:
            player.sendPacket(new ExShowSellCropList(player, castleId, castle.getCropProcure(0)));
            break;
          case 3:
            if (time == 1 && !((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, castleId)).isNextPeriodApproved()) {
              player.sendPacket(new ExShowSeedInfo(castleId, Collections.emptyList()));
            } else {
              player.sendPacket(new ExShowSeedInfo(castleId, ((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, castleId)).getSeedProduction(time)));
            }
            break;
          case 4:
            if (time == 1 && !((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, castleId)).isNextPeriodApproved()) {
              player.sendPacket(new ExShowCropInfo(castleId, Collections.emptyList()));
            } else {
              player.sendPacket(new ExShowCropInfo(castleId, ((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, castleId)).getCropProcure(time)));
            }
            break;
          case 5:
            player.sendPacket(new ExShowManorDefaultInfo());
            break;
          case 6:
            this.showShopWindow(player, Integer.parseInt("3" + this.getNpcId()), false);
          case 7:
          case 8:
          default:
            break;
          case 9:
            player.sendPacket(new ExShowProcureCropDetail(state));
        }
      } else if (command.startsWith("help")) {
        StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();
        String filename = "manor_client_help00" + st.nextToken() + ".htm";
        this.showMessageWindow(player, filename);
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public String getHtmlPath() {
    return "manormanager/";
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    return "manormanager/manager.htm";
  }

  private void showMessageWindow(Player player, String filename) {
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    html.setFile(this.getHtmlPath() + filename);
    html.replace("%objectId%", String.valueOf(this.getObjectId()));
    html.replace("%npcId%", String.valueOf(this.getNpcId()));
    html.replace("%npcname%", this.getName());
    player.sendPacket(html);
  }
}
