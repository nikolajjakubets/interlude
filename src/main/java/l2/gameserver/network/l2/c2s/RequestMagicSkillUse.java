//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class RequestMagicSkillUse extends L2GameClientPacket {
  private Integer _magicId;
  private boolean _ctrlPressed;
  private boolean _shiftPressed;

  public RequestMagicSkillUse() {
  }

  protected void readImpl() {
    this._magicId = this.readD();
    this._ctrlPressed = this.readD() != 0;
    this._shiftPressed = this.readC() != 0;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setActive();
      if (activeChar.isOutOfControl()) {
        activeChar.sendActionFailed();
      } else {
        Skill skill = SkillTable.getInstance().getInfo(this._magicId, activeChar.getSkillLevel(this._magicId));
        if (skill != null) {
          if (!skill.isActive() && !skill.isToggle()) {
            return;
          }

          FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
          if (attachment != null && !attachment.canCast(activeChar, skill)) {
            activeChar.sendActionFailed();
            return;
          }

          if ((activeChar.getTransformation() != 0 || activeChar.isCursedWeaponEquipped()) && !activeChar.getAllSkills().contains(skill)) {
            return;
          }

          if (skill.isToggle() && activeChar.getEffectList().getEffectsBySkill(skill) != null) {
            activeChar.getEffectList().stopEffect(skill.getId());
            activeChar.sendPacket((new SystemMessage(335)).addSkillName(skill.getId(), skill.getLevel()));
            activeChar.sendActionFailed();
            return;
          }

          Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());
          activeChar.setGroundSkillLoc((Location)null);
          activeChar.getAI().Cast(skill, target, this._ctrlPressed, this._shiftPressed);
        } else {
          activeChar.sendActionFailed();
        }

      }
    }
  }
}
