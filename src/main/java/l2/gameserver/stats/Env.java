//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;

public final class Env {
  public Creature character;
  public Creature target;
  public ItemInstance item;
  public Skill skill;
  public double value;

  public Env() {
  }

  public Env(Creature cha, Creature tar, Skill sk) {
    this.character = cha;
    this.target = tar;
    this.skill = sk;
  }
}
