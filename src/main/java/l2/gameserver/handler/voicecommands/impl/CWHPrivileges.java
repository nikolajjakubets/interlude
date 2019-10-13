//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class CWHPrivileges implements IVoicedCommandHandler {
  private String[] _commandList = new String[]{"clan"};

  public CWHPrivileges() {
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }

  public boolean useVoicedCommand(String command, Player activeChar, String args) {
    if (activeChar.getClan() == null) {
      return false;
    } else if (command.equals("clan") && Config.ALT_ALLOW_CLAN_COMMAND_ALLOW_WH) {
      if (Config.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER && !activeChar.isClanLeader()) {
        return false;
      } else if ((activeChar.getClanPrivileges() & 16) != 16) {
        return false;
      } else {
        if (args != null) {
          String[] param = args.split(" ");
          if (param.length > 0) {
            if (param[0].equalsIgnoreCase("allowwh") && param.length > 1) {
              UnitMember cm = activeChar.getClan().getAnyMember(param[1]);
              if (cm != null && cm.getPlayer() != null) {
                if (cm.getPlayer().getVarB("canWhWithdraw")) {
                  cm.getPlayer().unsetVar("canWhWithdraw");
                  activeChar.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeRemoved", activeChar, new Object[0]));
                } else {
                  cm.getPlayer().setVar("canWhWithdraw", "1", -1L);
                  activeChar.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeGiven", activeChar, new Object[0]));
                }
              } else if (cm != null) {
                int state = mysql.simple_get_int("value", "character_variables", "obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw'");
                if (state > 0) {
                  mysql.set("DELETE FROM `character_variables` WHERE obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw' LIMIT 1");
                  activeChar.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeRemoved", activeChar, new Object[0]));
                } else {
                  mysql.set("INSERT INTO character_variables  (obj_id, type, name, value, expire_time) VALUES (" + cm.getObjectId() + ",'user-var','canWhWithdraw','1',-1)");
                  activeChar.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PrivilegeGiven", activeChar, new Object[0]));
                }
              } else {
                activeChar.sendMessage(new CustomMessage("usercommandhandlers.CWHPrivileges.PlayerNotFound", activeChar, new Object[0]));
              }
            } else if (param[0].equalsIgnoreCase("list")) {
              StringBuilder sb = new StringBuilder("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (");
              List<UnitMember> members = activeChar.getClan().getAllMembers();

              for(int i = 0; i < members.size(); ++i) {
                sb.append(((UnitMember)members.get(i)).getObjectId());
                if (i < members.size() - 1) {
                  sb.append(",");
                }
              }

              sb.append(") AND `name`='canWhWithdraw'");
              List<Object> list = mysql.get_array(sb.toString());
              sb = new StringBuilder("<html><body>Clan member Warehouse privilege<br><br><table>");
              Iterator var8 = list.iterator();

              while(var8.hasNext()) {
                Object o_id = var8.next();
                Iterator var10 = members.iterator();

                while(var10.hasNext()) {
                  UnitMember m = (UnitMember)var10.next();
                  if (m.getObjectId() == Integer.parseInt(o_id.toString())) {
                    sb.append("<tr><td width=10></td><td width=60>").append(m.getName()).append("</td><td width=20><button width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h user_clan allowwh ").append(m.getName()).append("\" value=\"Remove\">").append("<br></td></tr>");
                  }
                }
              }

              sb.append("<tr><td width=10></td><td width=20><button width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>");
              Functions.show(sb.toString(), activeChar, (NpcInstance)null, new Object[0]);
              return true;
            }
          }
        }

        String dialog = HtmCache.getInstance().getNotNull("scripts/services/clan.htm", activeChar);
        Functions.show(dialog, activeChar, (NpcInstance)null, new Object[0]);
        return true;
      }
    } else {
      return false;
    }
  }
}
