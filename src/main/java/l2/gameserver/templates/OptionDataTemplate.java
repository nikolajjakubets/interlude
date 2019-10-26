//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.StatTemplate;

public class OptionDataTemplate extends StatTemplate {
  private final List<Skill> _skills = new ArrayList(0);
  private final int _id;

  public OptionDataTemplate(int id) {
    this._id = id;
  }

  public void addSkill(Skill skill) {
    this._skills.add(skill);
  }

  public List<Skill> getSkills() {
    return this._skills;
  }

  public int getId() {
    return this._id;
  }
}
