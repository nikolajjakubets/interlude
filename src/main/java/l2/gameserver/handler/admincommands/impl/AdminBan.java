//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import l2.gameserver.network.l2.CGMHelper;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.AutoBan;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
public class AdminBan implements IAdminCommandHandler {
  public AdminBan() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminBan.Commands command = (AdminBan.Commands)comm;
    StringTokenizer st = new StringTokenizer(fullString);
    if (activeChar.getPlayerAccess().CanTradeBanUnban) {
      switch(command) {
        case admin_trade_ban:
          return this.tradeBan(st, activeChar);
        case admin_trade_unban:
          return this.tradeUnban(st, activeChar);
      }
    }

    if (activeChar.getPlayerAccess().CanBan) {
      String period;
      String player;
      Player target;
      String hwid2ban;
      String ip;
      switch(command) {
        case admin_trade_ban:
          return this.tradeBan(st, activeChar);
        case admin_trade_unban:
          return this.tradeUnban(st, activeChar);
        case admin_ban:
          this.ban(st, activeChar);
          break;
        case admin_accban:
          st.nextToken();
          int level = 0;
          int banExpire = 0;
          hwid2ban = st.nextToken();
          if (st.hasMoreTokens()) {
            banExpire = (int)(System.currentTimeMillis() / 1000L) + Integer.parseInt(st.nextToken()) * 60;
          } else {
            level = -100;
          }

          AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(hwid2ban, level, banExpire));
          GameClient client = AuthServerCommunication.getInstance().getAuthedClient(hwid2ban);
          if (client != null) {
            Player activeChar1 = client.getActiveChar();
            if (activeChar1 != null) {
              activeChar1.kick();
              activeChar.sendMessage("Player " + activeChar1.getName() + " kicked.");
            }
          }
          break;
        case admin_accunban:
          st.nextToken();
          player = st.nextToken();
          AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(player, 0, 0));
          break;
        case admin_hwidban:
          try {
            st.nextToken();
            player = st.nextToken();
            target = World.getPlayer(player);
            hwid2ban = null;
            ip = null;
            String account = null;
            String comment = st.nextToken();
            if (target != null) {
              if (target.getNetConnection() != null && target.getNetConnection().isConnected()) {
                hwid2ban = target.getNetConnection().getHwid();
                ip = target.getNetConnection().getIpAddr();
                account = target.getAccountName();
                AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(account, -100, 0));
                target.kick();
                activeChar.sendMessage("Player " + target.getName() + " kicked.");
              }
            } else {
              hwid2ban = player;
            }

            if (hwid2ban != null && !hwid2ban.isEmpty()) {
              CGMHelper.getInstance().addHWIDBan(hwid2ban, ip, account, comment);
              activeChar.sendMessage("You ban hwid " + hwid2ban + ".");
            } else {
              activeChar.sendMessage("Such HWID or player not found.");
            }
          } catch (Exception e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            activeChar.sendMessage("Command syntax: //hwidban [char_name|hwid] comment");
          }
          break;
        case admin_chatban:
          try {
            st.nextToken();
            player = st.nextToken();
            period = st.nextToken();
            hwid2ban = "admin_chatban " + player + " " + period + " ";
            ip = fullString.substring(hwid2ban.length());
            if (AutoBan.ChatBan(player, Integer.parseInt(period), ip, activeChar.getName())) {
              activeChar.sendMessage("You ban chat for " + player + ".");
            } else {
              activeChar.sendMessage("Can't find char " + player + ".");
            }
          } catch (Exception e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            activeChar.sendMessage("Command syntax: //chatban char_name period reason");
          }
          break;
        case admin_chatunban:
          try {
            st.nextToken();
            player = st.nextToken();
            if (AutoBan.ChatUnBan(player, activeChar.getName())) {
              activeChar.sendMessage("You unban chat for " + player + ".");
            } else {
              activeChar.sendMessage("Can't find char " + player + ".");
            }
          } catch (Exception e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            activeChar.sendMessage("Command syntax: //chatunban char_name");
          }
          break;
        case admin_jail:
          try {
            st.nextToken();
            player = st.nextToken();
            period = st.nextToken();
            hwid2ban = st.nextToken();
            Player player1 = World.getPlayer(player);
            if (player1 != null) {
              player1.setVar("jailedFrom", player1.getX() + ";" + player1.getY() + ";" + player1.getZ() + ";" + player1.getReflectionId(), -1L);
              player1.setVar("jailed", period, -1L);
              player1.startUnjailTask(player1, Integer.parseInt(period));
              player1.teleToLocation(Location.findPointToStay(player1, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);
              if (activeChar.isInStoreMode()) {
                activeChar.setPrivateStoreType(0);
              }

              player1.sitDown(null);
              player1.block();
              player1.sendMessage("You moved to jail, time to escape - " + period + " minutes, reason - " + hwid2ban + " .");
              activeChar.sendMessage("You jailed " + player + ".");
            } else {
              activeChar.sendMessage("Can't find char " + player + ".");
            }
          } catch (Exception e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            activeChar.sendMessage("Command syntax: //jail char_name period reason");
          }
          break;
        case admin_unjail:
          try {
            st.nextToken();
            player = st.nextToken();
            target = World.getPlayer(player);
            if (target != null && target.getVar("jailed") != null) {
              String[] re = target.getVar("jailedFrom").split(";");
              target.teleToLocation(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
              target.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);
              target.stopUnjailTask();
              target.unsetVar("jailedFrom");
              target.unsetVar("jailed");
              target.unblock();
              target.standUp();
              activeChar.sendMessage("You unjailed " + player + ".");
            } else {
              activeChar.sendMessage("Can't find char " + player + ".");
            }
          } catch (Exception e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
            activeChar.sendMessage("Command syntax: //unjail char_name");
          }
          break;
        case admin_cban:
          activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/cban.htm"));
          break;
        case admin_permaban:
          if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
            Functions.sendDebugMessage(activeChar, "Target should be set and be a player instance");
            return false;
          }

          Player banned = activeChar.getTarget().getPlayer();
          period = banned.getAccountName();
          AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(period, -100, 0));
          if (banned.isInOfflineMode()) {
            banned.setOfflineMode(false);
          }

          banned.kick();
          Functions.sendDebugMessage(activeChar, "Player account " + period + " is banned, player " + banned.getName() + " kicked.");
      }
    }

    return true;
  }

  private boolean tradeBan(StringTokenizer st, Player activeChar) {
    if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer()) {
      st.nextToken();
      Player targ = (Player)activeChar.getTarget();
      long days = -1L;
      long time = -1L;
      if (st.hasMoreTokens()) {
        days = Long.parseLong(st.nextToken());
        time = days * 24L * 60L * 60L * 1000L + System.currentTimeMillis();
      }

      targ.setVar("tradeBan", String.valueOf(time), -1L);
      String msg = activeChar.getName() + " заблокировал торговлю персонажу " + targ.getName() + (days == -1L ? " на бессрочный период." : " на " + days + " дней.");
      Log.add(targ.getName() + ":" + days + tradeToString(targ, targ.getPrivateStoreType()), "tradeBan", activeChar);
      if (targ.isInOfflineMode()) {
        targ.setOfflineMode(false);
        targ.kick();
      } else if (targ.isInStoreMode()) {
        targ.setPrivateStoreType(0);
        targ.standUp();
        targ.broadcastCharInfo();
        targ.getBuyList().clear();
      }

      if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
        Announcements.getInstance().announceToAll(msg);
      } else {
        Announcements.shout(activeChar, msg, ChatType.CRITICAL_ANNOUNCE);
      }

      return true;
    } else {
      return false;
    }
  }

  private static String tradeToString(Player targ, int trade) {
    String ret;
    List list;
    Iterator var4;
    TradeItem i;
    switch(trade) {
      case 1:
      case 8:
        list = targ.getSellList();
        if (list != null && !list.isEmpty()) {
          ret = ":sell:";

          for(var4 = list.iterator(); var4.hasNext(); ret = ret + i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":") {
            i = (TradeItem)var4.next();
          }

          return ret;
        } else {
          return "";
        }
      case 2:
      case 4:
      case 6:
      case 7:
      default:
        return "";
      case 3:
        list = targ.getBuyList();
        if (list != null && !list.isEmpty()) {
          ret = ":buy:";

          for(var4 = list.iterator(); var4.hasNext(); ret = ret + i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":") {
            i = (TradeItem)var4.next();
          }

          return ret;
        }

        return "";
      case 5:
        list = targ.getCreateList();
        if (list != null && !list.isEmpty()) {
          ret = ":mf:";

          ManufactureItem manufactureItem;
          for (var4 = list.iterator(); var4.hasNext(); ret = ret + manufactureItem.getRecipeId() + ";" + manufactureItem.getCost() + ":") {
            manufactureItem = (ManufactureItem) var4.next();
          }

          return ret;
        } else {
          return "";
        }
    }
  }

  private boolean tradeUnban(StringTokenizer st, Player activeChar) {
    if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer()) {
      Player targ = (Player)activeChar.getTarget();
      targ.unsetVar("tradeBan");
      if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
        Announcements.getInstance().announceToAll(activeChar + " разблокировал торговлю персонажу " + targ + ".");
      } else {
        Announcements.shout(activeChar, activeChar + " разблокировал торговлю персонажу " + targ + ".", ChatType.CRITICAL_ANNOUNCE);
      }

      Log.add(activeChar + " разблокировал торговлю персонажу " + targ + ".", "tradeBan", activeChar);
      return true;
    } else {
      return false;
    }
  }

  private boolean ban(StringTokenizer st, Player activeChar) {
    try {
      st.nextToken();
      String player = st.nextToken();
      int time = 0;
      String msg = "";
      if (st.hasMoreTokens()) {
        time = Integer.parseInt(st.nextToken());
      }

      if (st.hasMoreTokens()) {
        for(msg = "admin_ban " + player + " " + time + " "; st.hasMoreTokens(); msg = msg + st.nextToken() + " ") {
        }

        msg.trim();
      }

      Player plyr = World.getPlayer(player);
      if (plyr != null) {
        plyr.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM", plyr));
        plyr.setAccessLevel(-100);
        AutoBan.Banned(plyr, time, msg, activeChar.getName());
        plyr.kick();
        activeChar.sendMessage("You banned " + plyr.getName());
      } else if (AutoBan.Banned(player, -100, time, msg, activeChar.getName())) {
        activeChar.sendMessage("You banned " + player);
      } else {
        activeChar.sendMessage("Can't find char: " + player);
      }
    } catch (Exception var7) {
      activeChar.sendMessage("Command syntax: //ban char_name days reason");
    }

    return true;
  }

  public Enum[] getAdminCommandEnum() {
    return AdminBan.Commands.values();
  }

  private enum Commands {
    admin_ban,
    admin_unban,
    admin_hwidban,
    admin_cban,
    admin_chatban,
    admin_chatunban,
    admin_accban,
    admin_accunban,
    admin_trade_ban,
    admin_trade_unban,
    admin_jail,
    admin_unjail,
    admin_permaban;

    Commands() {
    }
  }
}
