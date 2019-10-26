//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class RequestExMagicSkillUseGround extends L2GameClientPacket {
  private Location _loc = new Location();
  private int _skillId;
  private boolean _ctrlPressed;
  private boolean _shiftPressed;

  public RequestExMagicSkillUseGround() {
  }

  protected void readImpl() {
    this._loc.x = this.readD();
    this._loc.y = this.readD();
    this._loc.z = this.readD();
    this._skillId = this.readD();
    this._ctrlPressed = this.readD() != 0;
    this._shiftPressed = this.readC() != 0;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else {
        Skill skill = SkillTable.getInstance().getInfo(this._skillId, activeChar.getSkillLevel(this._skillId));
        if (skill != null) {
          if (skill.getAddedSkills().length == 0) {
            return;
          }

          if ((activeChar.getTransformation() != 0 || activeChar.isCursedWeaponEquipped()) && !activeChar.getAllSkills().contains(skill)) {
            return;
          }

          if (!activeChar.isInRange(this._loc, (long)skill.getCastRange())) {
            activeChar.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            activeChar.sendActionFailed();
            return;
          }

          Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());
          if (skill.checkCondition(activeChar, target, this._ctrlPressed, this._shiftPressed, true)) {
            activeChar.setGroundSkillLoc(this._loc);
            activeChar.getAI().Cast(skill, target, this._ctrlPressed, this._shiftPressed);
          } else {
            activeChar.sendActionFailed();
          }
        } else {
          activeChar.sendActionFailed();
        }

      }
    }
  }
}
