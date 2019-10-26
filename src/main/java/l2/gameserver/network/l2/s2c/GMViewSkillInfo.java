//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

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
    Iterator var1 = this._skills.iterator();

    while(var1.hasNext()) {
      Skill skill = (Skill)var1.next();
      this.writeD(skill.isPassive() ? 1 : 0);
      this.writeD(skill.getDisplayLevel());
      this.writeD(skill.getId());
      this.writeC(this._targetChar.isUnActiveSkill(skill.getId()) ? 1 : 0);
    }

  }
}
