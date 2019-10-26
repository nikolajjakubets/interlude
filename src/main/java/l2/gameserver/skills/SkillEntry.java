//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills;

import java.util.AbstractMap.SimpleImmutableEntry;
import l2.gameserver.model.Skill;

public class SkillEntry extends SimpleImmutableEntry<SkillEntryType, Skill> {
  private boolean _disabled;

  public SkillEntry(SkillEntryType key, Skill value) {
    super(key, value);
  }

  public boolean isDisabled() {
    return this._disabled;
  }

  public void setDisabled(boolean disabled) {
    this._disabled = disabled;
  }
}
