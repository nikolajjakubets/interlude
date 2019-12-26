//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.skills.TimeStamp;

import java.util.*;

public class SkillCoolTime extends L2GameServerPacket {
  private List<SkillCoolTime.Skill> _list = Collections.emptyList();

  public SkillCoolTime(Player player) {
    Collection<TimeStamp> list = player.getSkillReuses();
    this._list = new ArrayList(list.size());
    Iterator var3 = list.iterator();

    while(var3.hasNext()) {
      TimeStamp stamp = (TimeStamp)var3.next();
      if (stamp.hasNotPassed()) {
        l2.gameserver.model.Skill skill = player.getKnownSkill(stamp.getId());
        if (skill != null) {
          SkillCoolTime.Skill sk = new SkillCoolTime.Skill();
          sk.skillId = skill.getId();
          sk.level = skill.getLevel();
          sk.reuseBase = (int)Math.floor((double)stamp.getReuseBasic() / 1000.0D);
          sk.reuseCurrent = (int)Math.floor((double)stamp.getReuseCurrent() / 1000.0D);
          this._list.add(sk);
        }
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(193);
    this.writeD(this._list.size());

    for (Skill skill : this._list) {
      this.writeD(skill.skillId);
      this.writeD(skill.level);
      this.writeD(skill.reuseBase);
      this.writeD(skill.reuseCurrent);
    }

  }

  private static class Skill {
    public int skillId;
    public int level;
    public int reuseBase;
    public int reuseCurrent;

    private Skill() {
    }
  }
}
