//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

import java.util.Collection;

public class GMViewSkillInfo extends L2GameServerPacket {
  private String _charName;
  private Collection<Skill> _skills;
  private Player _targetChar;

  public GMViewSkillInfo(Player cha) {
    this._charName = cha.getName();
    this._skills = cha.getAllSkills();
    this._targetChar = cha;
  }

  protected final void writeImpl() {
    this.writeC(145);
    this.writeS(this._charName);
    this.writeD(this._skills.size());

    for (Skill skill : this._skills) {
      this.writeD(skill.isPassive() ? 1 : 0);
      this.writeD(skill.getDisplayLevel());
      this.writeD(skill.getId());
      this.writeC(this._targetChar.isUnActiveSkill(skill.getId()) ? 1 : 0);
    }

  }
}
