//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.BuyList;
import l2.gameserver.network.l2.s2c.ExGetPremiumItemList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SellRefundList;
import l2.gameserver.network.l2.s2c.ShopPreviewList;
import l2.gameserver.templates.mapregion.DomainArea;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantInstance extends NpcInstance {
  private static final Logger _log = LoggerFactory.getLogger(MerchantInstance.class);
  private static final int NEWBIE_EXCHANGE_MULTISELL = 6001;

  public MerchantInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    if (this.getTemplate().getHtmRoot() != null) {
      return this.getTemplate().getHtmRoot() + pom + ".htm";
    } else {
      String temp = "merchant/" + pom + ".htm";
      if (HtmCache.getInstance().getNullable(temp, player) != null) {
        return temp;
      } else {
        temp = "teleporter/" + pom + ".htm";
        if (HtmCache.getInstance().getNullable(temp, player) != null) {
          return temp;
        } else {
          temp = "petmanager/" + pom + ".htm";
          return HtmCache.getInstance().getNullable(temp, player) != null ? temp : "default/" + pom + ".htm";
        }
      }
    }
  }

  private void showWearWindow(Player player, int val) {
    if (player.getPlayerAccess().UseShop) {
      NpcTradeList list = BuyListHolder.getInstance().getBuyList(val);
      if (list != null) {
        ShopPreviewList bl = new ShopPreviewList(list, player);
        player.sendPacket(bl);
      } else {
        _log.warn("no buylist with id:" + val);
        player.sendActionFailed();
      }

    }
  }

  protected void showShopWindow(Player player, int listId, boolean tax) {
    if (player.getPlayerAccess().UseShop) {
      double taxRate = 0.0D;
      if (tax) {
        Castle castle = this.getCastle(player);
        if (castle != null) {
          taxRate = castle.getTaxRate();
        }
      }

      NpcTradeList list = BuyListHolder.getInstance().getBuyList(listId);
      if (list != null && list.getNpcId() != this.getNpcId()) {
        _log.warn("[L2MerchantInstance] possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
        _log.warn("buylist id:" + listId + " / list_npc = " + list.getNpcId() + " / npc = " + this.getNpcId());
      } else {
        player.sendPacket(new BuyList(list, player, taxRate));
      }

    }
  }

  protected void showShopWindow(Player player) {
    this.showShopWindow(player, 0, false);
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      StringTokenizer st = new StringTokenizer(command, " ");
      String actualCommand = st.nextToken();
      int val;
      if (actualCommand.equalsIgnoreCase("Buy")) {
        val = 0;
        if (st.countTokens() > 0) {
          val = Integer.parseInt(st.nextToken());
        }

        this.showShopWindow(player, val, true);
      } else if (actualCommand.equalsIgnoreCase("Sell")) {
        player.sendPacket(new SellRefundList(player, false));
      } else if (actualCommand.equalsIgnoreCase("Wear")) {
        if (st.countTokens() < 1) {
          return;
        }

        val = Integer.parseInt(st.nextToken());
        this.showWearWindow(player, val);
      } else if (actualCommand.equalsIgnoreCase("Multisell")) {
        if (st.countTokens() < 1) {
          return;
        }

        val = Integer.parseInt(st.nextToken());
        Castle castle = this.getCastle(player);
        MultiSellHolder.getInstance().SeparateAndSend(val, player, castle != null ? castle.getTaxRate() : 0.0D);
      } else if (actualCommand.equalsIgnoreCase("Exchange")) {
        if (player.getLevel() < 25) {
          MultiSellHolder.getInstance().SeparateAndSend(6001, player, 0.0D);
        } else {
          player.sendPacket(new NpcHtmlMessage(player, this, "merchant/merchant_for_newbie001.htm", 0));
        }
      } else if (actualCommand.equalsIgnoreCase("ReceivePremium")) {
        if (player.getPremiumItemList().isEmpty()) {
          player.sendPacket(Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
          return;
        }

        player.sendPacket(new ExGetPremiumItemList(player));
      } else {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public Castle getCastle(Player player) {
    if (!Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && (this.getReflection() != ReflectionManager.GIRAN_HARBOR || !Config.SERVICES_GIRAN_HARBOR_NOTAX)) {
      if (this.getReflection() == ReflectionManager.GIRAN_HARBOR) {
        String var = player.getVar("backCoords");
        if (var != null && !var.isEmpty()) {
          Location loc = Location.parseLoc(var);
          DomainArea domain = (DomainArea)MapRegionManager.getInstance().getRegionData(DomainArea.class, loc);
          if (domain != null) {
            return (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, domain.getId());
          }
        }

        return super.getCastle();
      } else {
        return super.getCastle(player);
      }
    } else {
      return null;
    }
  }

  public boolean isMerchantNpc() {
    return true;
  }
}
