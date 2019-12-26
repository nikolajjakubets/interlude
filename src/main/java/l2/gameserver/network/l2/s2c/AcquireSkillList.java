//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.base.AcquireType;

import java.util.ArrayList;
import java.util.List;

public class AcquireSkillList extends L2GameServerPacket {
  private AcquireType _type;
  private final List<AcquireSkillList.Skill> _skills;

  public AcquireSkillList(AcquireType type, int size) {
    this._skills = new ArrayList(size);
    this._type = type;
  }

  public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements, int subUnit) {
    this._skills.add(new AcquireSkillList.Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
  }

  public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements) {
    this._skills.add(new AcquireSkillList.Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
  }

  protected final void writeImpl() {
    this.writeC(138);
    this.writeD(this._type.ordinal());
    this.writeD(this._skills.size());

    for (Skill temp : this._skills) {
      this.writeD(temp.id);
      this.writeD(temp.nextLevel);
      this.writeD(temp.maxLevel);
      this.writeD(temp.cost);
      this.writeD(temp.requirements);
    }

  }

  class Skill {
    public int id;
    public int nextLevel;
    public int maxLevel;
    public int cost;
    public int requirements;
    public int subUnit;

    Skill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit) {
      this.id = id;
      this.nextLevel = nextLevel;
      this.maxLevel = maxLevel;
      this.cost = cost;
      this.requirements = requirements;
      this.subUnit = subUnit;
    }
  }
}
