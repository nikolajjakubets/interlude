//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;

public class AdminPolymorph implements IAdminCommandHandler {
  public AdminPolymorph() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminPolymorph.Commands command = (AdminPolymorph.Commands)comm;
    if (!activeChar.getPlayerAccess().CanPolymorph) {
      return false;
    } else {
      GameObject target = activeChar.getTarget();
      switch(command) {
        case admin_polyself:
          target = activeChar;
        case admin_polymorph:
        case admin_poly:
          if (target != null && ((GameObject)target).isPlayer()) {
            try {
              int id = Integer.parseInt(wordList[1]);
              if (NpcHolder.getInstance().getTemplate(id) != null) {
                ((Player)target).setPolyId(id);
                ((Player)target).broadcastCharInfo();
              }
              break;
            } catch (Exception var8) {
              activeChar.sendMessage("USAGE: //poly id [type:npc|item]");
              return false;
            }
          }

          activeChar.sendPacket(Msg.INVALID_TARGET);
          return false;
        case admin_unpolyself:
          target = activeChar;
        case admin_unpolymorph:
        case admin_unpoly:
          if (target == null || !((GameObject)target).isPlayer()) {
            activeChar.sendPacket(Msg.INVALID_TARGET);
            return false;
          }

          ((Player)target).setPolyId(0);
          ((Player)target).broadcastCharInfo();
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminPolymorph.Commands.values();
  }

  private static enum Commands {
    admin_polyself,
    admin_polymorph,
    admin_poly,
    admin_unpolyself,
    admin_unpolymorph,
    admin_unpoly;

    private Commands() {
    }
  }
}
