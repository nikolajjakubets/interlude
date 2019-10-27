//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.handler.admincommands.impl.AdminAdmin;
import l2.gameserver.handler.admincommands.impl.AdminAnnouncements;
import l2.gameserver.handler.admincommands.impl.AdminBan;
import l2.gameserver.handler.admincommands.impl.AdminCamera;
import l2.gameserver.handler.admincommands.impl.AdminCancel;
import l2.gameserver.handler.admincommands.impl.AdminChangeAccessLevel;
import l2.gameserver.handler.admincommands.impl.AdminClanHall;
import l2.gameserver.handler.admincommands.impl.AdminCreateItem;
import l2.gameserver.handler.admincommands.impl.AdminCursedWeapons;
import l2.gameserver.handler.admincommands.impl.AdminDelete;
import l2.gameserver.handler.admincommands.impl.AdminDisconnect;
import l2.gameserver.handler.admincommands.impl.AdminDoorControl;
import l2.gameserver.handler.admincommands.impl.AdminEditChar;
import l2.gameserver.handler.admincommands.impl.AdminEffects;
import l2.gameserver.handler.admincommands.impl.AdminEnchant;
import l2.gameserver.handler.admincommands.impl.AdminEvents;
import l2.gameserver.handler.admincommands.impl.AdminGeodata;
import l2.gameserver.handler.admincommands.impl.AdminGm;
import l2.gameserver.handler.admincommands.impl.AdminGmChat;
import l2.gameserver.handler.admincommands.impl.AdminHeal;
import l2.gameserver.handler.admincommands.impl.AdminHelpPage;
import l2.gameserver.handler.admincommands.impl.AdminIP;
import l2.gameserver.handler.admincommands.impl.AdminInstance;
import l2.gameserver.handler.admincommands.impl.AdminKill;
import l2.gameserver.handler.admincommands.impl.AdminLevel;
import l2.gameserver.handler.admincommands.impl.AdminMammon;
import l2.gameserver.handler.admincommands.impl.AdminManor;
import l2.gameserver.handler.admincommands.impl.AdminMenu;
import l2.gameserver.handler.admincommands.impl.AdminMonsterRace;
import l2.gameserver.handler.admincommands.impl.AdminMove;
import l2.gameserver.handler.admincommands.impl.AdminNochannel;
import l2.gameserver.handler.admincommands.impl.AdminOlympiad;
import l2.gameserver.handler.admincommands.impl.AdminPetition;
import l2.gameserver.handler.admincommands.impl.AdminPledge;
import l2.gameserver.handler.admincommands.impl.AdminPolymorph;
import l2.gameserver.handler.admincommands.impl.AdminQuests;
import l2.gameserver.handler.admincommands.impl.AdminReload;
import l2.gameserver.handler.admincommands.impl.AdminRepairChar;
import l2.gameserver.handler.admincommands.impl.AdminRes;
import l2.gameserver.handler.admincommands.impl.AdminRide;
import l2.gameserver.handler.admincommands.impl.AdminSS;
import l2.gameserver.handler.admincommands.impl.AdminScripts;
import l2.gameserver.handler.admincommands.impl.AdminServer;
import l2.gameserver.handler.admincommands.impl.AdminShop;
import l2.gameserver.handler.admincommands.impl.AdminShutdown;
import l2.gameserver.handler.admincommands.impl.AdminSkill;
import l2.gameserver.handler.admincommands.impl.AdminSpawn;
import l2.gameserver.handler.admincommands.impl.AdminTarget;
import l2.gameserver.handler.admincommands.impl.AdminTeleport;
import l2.gameserver.handler.admincommands.impl.AdminTest;
import l2.gameserver.handler.admincommands.impl.AdminZone;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Log;

public class AdminCommandHandler extends AbstractHolder {
  private static final AdminCommandHandler _instance = new AdminCommandHandler();
  private Map<String, IAdminCommandHandler> _datatable = new HashMap<>();

  public static AdminCommandHandler getInstance() {
    return _instance;
  }

  private AdminCommandHandler() {
    this.registerAdminCommandHandler(new AdminAdmin());
    this.registerAdminCommandHandler(new AdminAnnouncements());
    this.registerAdminCommandHandler(new AdminBan());
    this.registerAdminCommandHandler(new AdminCamera());
    this.registerAdminCommandHandler(new AdminCancel());
    this.registerAdminCommandHandler(new AdminChangeAccessLevel());
    this.registerAdminCommandHandler(new AdminClanHall());
    this.registerAdminCommandHandler(new AdminCreateItem());
    this.registerAdminCommandHandler(new AdminCursedWeapons());
    this.registerAdminCommandHandler(new AdminDelete());
    this.registerAdminCommandHandler(new AdminDisconnect());
    this.registerAdminCommandHandler(new AdminDoorControl());
    this.registerAdminCommandHandler(new AdminEditChar());
    this.registerAdminCommandHandler(new AdminEffects());
    this.registerAdminCommandHandler(new AdminEnchant());
    this.registerAdminCommandHandler(new AdminEvents());
    this.registerAdminCommandHandler(new AdminGeodata());
    this.registerAdminCommandHandler(new AdminGm());
    this.registerAdminCommandHandler(new AdminGmChat());
    this.registerAdminCommandHandler(new AdminHeal());
    this.registerAdminCommandHandler(new AdminHelpPage());
    this.registerAdminCommandHandler(new AdminInstance());
    this.registerAdminCommandHandler(new AdminIP());
    this.registerAdminCommandHandler(new AdminLevel());
    this.registerAdminCommandHandler(new AdminMammon());
    this.registerAdminCommandHandler(new AdminManor());
    this.registerAdminCommandHandler(new AdminMenu());
    this.registerAdminCommandHandler(new AdminMonsterRace());
    this.registerAdminCommandHandler(new AdminMove());
    this.registerAdminCommandHandler(new AdminNochannel());
    this.registerAdminCommandHandler(new AdminOlympiad());
    this.registerAdminCommandHandler(new AdminPetition());
    this.registerAdminCommandHandler(new AdminPledge());
    this.registerAdminCommandHandler(new AdminPolymorph());
    this.registerAdminCommandHandler(new AdminQuests());
    this.registerAdminCommandHandler(new AdminReload());
    this.registerAdminCommandHandler(new AdminRepairChar());
    this.registerAdminCommandHandler(new AdminRes());
    this.registerAdminCommandHandler(new AdminRide());
    this.registerAdminCommandHandler(new AdminServer());
    this.registerAdminCommandHandler(new AdminShop());
    this.registerAdminCommandHandler(new AdminShutdown());
    this.registerAdminCommandHandler(new AdminSkill());
    this.registerAdminCommandHandler(new AdminScripts());
    this.registerAdminCommandHandler(new AdminSpawn());
    this.registerAdminCommandHandler(new AdminSS());
    this.registerAdminCommandHandler(new AdminTarget());
    this.registerAdminCommandHandler(new AdminTeleport());
    this.registerAdminCommandHandler(new AdminZone());
    this.registerAdminCommandHandler(new AdminKill());
    this.registerAdminCommandHandler(new AdminTest());
  }

  public void registerAdminCommandHandler(IAdminCommandHandler handler) {
    Enum[] var2 = handler.getAdminCommandEnum();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Enum<?> e = var2[var4];
      this._datatable.put(e.toString().toLowerCase(), handler);
    }

  }

  public IAdminCommandHandler getAdminCommandHandler(String adminCommand) {
    String command = adminCommand;
    if (adminCommand.indexOf(" ") != -1) {
      command = adminCommand.substring(0, adminCommand.indexOf(" "));
    }

    return (IAdminCommandHandler)this._datatable.get(command);
  }

  public void useAdminCommandHandler(Player activeChar, String adminCommand) {
    if (!activeChar.isGM() && !activeChar.getPlayerAccess().CanUseGMCommand) {
      activeChar.sendMessage((new CustomMessage("l2p.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", activeChar, new Object[0])).addString(adminCommand));
    } else {
      String[] wordList = adminCommand.split(" ");
      IAdminCommandHandler handler = (IAdminCommandHandler)this._datatable.get(wordList[0]);
      if (handler != null) {
        boolean success = false;

        try {
          Enum[] var6 = handler.getAdminCommandEnum();
          int var7 = var6.length;

          for(int var8 = 0; var8 < var7; ++var8) {
            Enum<?> e = var6[var8];
            if (e.toString().equalsIgnoreCase(wordList[0])) {
              success = handler.useAdminCommand(e, wordList, adminCommand, activeChar);
              break;
            }
          }
        } catch (Exception var10) {
          this.error("", var10);
        }

        Log.LogCommand(activeChar, activeChar.getTarget(), adminCommand, success);
      }

    }
  }

  public void process() {
  }

  public int size() {
    return this._datatable.size();
  }

  public void clear() {
    this._datatable.clear();
  }

  public Set<String> getAllCommands() {
    return this._datatable.keySet();
  }
}
