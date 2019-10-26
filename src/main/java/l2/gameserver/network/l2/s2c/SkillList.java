//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

public class SkillList extends L2GameServerPacket {
  private List<SkillList.SkillListRecord> _skillRecords;

  public SkillList(Player player) {
    Collection<Skill> playerSkills = player.getAllSkills();
    this._skillRecords = new ArrayList(playerSkills.size());
    Iterator var3 = playerSkills.iterator();

    while(var3.hasNext()) {
      Skill skill = (Skill)var3.next();
      this._skillRecords.add(new SkillList.SkillListRecord(player, skill));
    }

  }

  protected final void writeImpl() {
    this.writeC(88);
    this.writeD(this._skillRecords.size());
    Iterator var1 = this._skillRecords.iterator();

    while(var1.hasNext()) {
      SkillList.SkillListRecord skr = (SkillList.SkillListRecord)var1.next();
      skr.writeRecord();
    }

  }

  private class SkillListRecord implements Comparable<SkillList.SkillListRecord> {
    private final int _id;
    private final int _lvl;
    private final boolean _disabled;
    private final int _order;

    public SkillListRecord(Player player, Skill skill) {
      this._id = skill.getDisplayId();
      this._lvl = skill.getDisplayLevel();
      this._disabled = player.isUnActiveSkill(skill.getId());
      this._order = !skill.isActive() && !skill.isToggle() ? 1 : 0;
    }

    public void writeRecord() {
      SkillList.this.writeD(this._order);
      SkillList.this.writeD(this._lvl);
      SkillList.this.writeD(this._id);
      SkillList.this.writeC(this._disabled ? 1 : 0);
    }

    public int compareTo(SkillList.SkillListRecord o) {
      return this._id - o._id;
    }
  }
}
