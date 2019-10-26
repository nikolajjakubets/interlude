//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.npc;

import gnu.trove.TIntHashSet;

public class AbsorbInfo {
  private final boolean _skill;
  private final AbsorbInfo.AbsorbType _absorbType;
  private final int _chance;
  private final int _cursedChance;
  private final TIntHashSet _levels;

  public AbsorbInfo(boolean skill, AbsorbInfo.AbsorbType absorbType, int chance, int cursedChance, int min, int max) {
    this._skill = skill;
    this._absorbType = absorbType;
    this._chance = chance;
    this._cursedChance = cursedChance;
    this._levels = new TIntHashSet(max - min);

    for(int i = min; i <= max; ++i) {
      this._levels.add(i);
    }

  }

  public boolean isSkill() {
    return this._skill;
  }

  public AbsorbInfo.AbsorbType getAbsorbType() {
    return this._absorbType;
  }

  public int getChance() {
    return this._chance;
  }

  public int getCursedChance() {
    return this._cursedChance;
  }

  public boolean canAbsorb(int le) {
    return this._levels.contains(le);
  }

  public static enum AbsorbType {
    LAST_HIT,
    PARTY_ONE,
    PARTY_ALL,
    PARTY_RANDOM;

    private AbsorbType() {
    }
  }
}
