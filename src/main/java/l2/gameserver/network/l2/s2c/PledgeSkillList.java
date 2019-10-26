//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;

public class PledgeSkillList extends L2GameServerPacket {
  private List<PledgeSkillList.SkillInfo> _allSkills = Collections.emptyList();
  private List<PledgeSkillList.UnitSkillInfo> _unitSkills = new ArrayList();

  public PledgeSkillList(Clan clan) {
    Collection<Skill> skills = clan.getSkills();
    this._allSkills = new ArrayList(skills.size());
    Iterator var3 = skills.iterator();

    while(var3.hasNext()) {
      Skill sk = (Skill)var3.next();
      this._allSkills.add(new PledgeSkillList.SkillInfo(sk.getId(), sk.getLevel()));
    }

    var3 = clan.getAllSubUnits().iterator();

    while(var3.hasNext()) {
      SubUnit subUnit = (SubUnit)var3.next();
      Iterator var5 = subUnit.getSkills().iterator();

      while(var5.hasNext()) {
        Skill sk = (Skill)var5.next();
        this._unitSkills.add(new PledgeSkillList.UnitSkillInfo(subUnit.getType(), sk.getId(), sk.getLevel()));
      }
    }

  }

  protected final void writeImpl() {
    this.writeEx(57);
    this.writeD(this._allSkills.size() + this._unitSkills.size());
    Iterator var1 = this._allSkills.iterator();

    while(var1.hasNext()) {
      PledgeSkillList.SkillInfo info = (PledgeSkillList.SkillInfo)var1.next();
      this.writeD(info._id);
      this.writeD(info._level);
    }

    var1 = this._unitSkills.iterator();

    while(var1.hasNext()) {
      PledgeSkillList.UnitSkillInfo info = (PledgeSkillList.UnitSkillInfo)var1.next();
      this.writeD(info._id);
      this.writeD(info._level);
    }

  }

  static class UnitSkillInfo extends PledgeSkillList.SkillInfo {
    private int _type;

    public UnitSkillInfo(int type, int id, int level) {
      super(id, level);
      this._type = type;
    }
  }

  static class SkillInfo {
    public int _id;
    public int _level;

    public SkillInfo(int id, int level) {
      this._id = id;
      this._level = level;
    }
  }
}
