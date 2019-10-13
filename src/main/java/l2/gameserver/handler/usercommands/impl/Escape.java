//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.SkillTable;

public class Escape implements IUserCommandHandler {
  private static final int[] COMMAND_IDS = new int[]{52};

  public Escape() {
  }

  public boolean useUserCommand(int id, Player activeChar) {
    if (id != COMMAND_IDS[0]) {
      return false;
    } else if (!activeChar.isMovementDisabled() && !activeChar.isOlyParticipant()) {
      if (activeChar.getTeleMode() == 0 && activeChar.getPlayerAccess().UseTeleport && !this.isEventParticipant(activeChar)) {
        if (!activeChar.isInDuel() && activeChar.getTeam() == TeamType.NONE) {
          activeChar.abortAttack(true, true);
          activeChar.abortCast(true, true);
          activeChar.stopMove();
          Skill skill;
          if (activeChar.getPlayerAccess().FastUnstuck) {
            skill = SkillTable.getInstance().getInfo(1050, 2);
          } else {
            skill = SkillTable.getInstance().getInfo(2099, 1);
          }

          if (skill != null && skill.checkCondition(activeChar, activeChar, false, false, true)) {
            activeChar.getAI().Cast(skill, activeChar, false, true);
          }

          return true;
        } else {
          activeChar.sendMessage(new CustomMessage("common.RecallInDuel", activeChar, new Object[0]));
          return false;
        }
      } else {
        activeChar.sendMessage(new CustomMessage("common.TryLater", activeChar, new Object[0]));
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean isEventParticipant(Player player) {
    return (Boolean)Scripts.getInstance().callScripts(player, "events.TvT2.PvPEvent", "isEventPartisipant");
  }

  public final int[] getUserCommandList() {
    return COMMAND_IDS;
  }
}
