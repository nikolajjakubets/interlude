//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item;

import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;

public final class WeaponTemplate extends ItemTemplate {
  private final int _soulShotCount;
  private final int _spiritShotCount;
  private final int _rndDam;
  private final int _atkReuse;
  private final int _mpConsume;
  private final boolean _isMageItem;
  private final int _attackRange;
  private int _critical;

  public WeaponTemplate(StatsSet set) {
    super(set);
    this.type = (ItemType)set.getEnum("type", WeaponTemplate.WeaponType.class);
    this._soulShotCount = set.getInteger("soulshots", 0);
    this._spiritShotCount = set.getInteger("spiritshots", 0);
    this._isMageItem = set.getBool("is_magic_weapon", false);
    this._rndDam = set.getInteger("rnd_dam", 0);
    this._atkReuse = set.getInteger("atk_reuse", this.type == WeaponTemplate.WeaponType.BOW ? 1500 : 0);
    this._mpConsume = set.getInteger("mp_consume", 0);
    switch(this.getItemType()) {
      case BOW:
        this._attackRange = set.getInteger("attack_range", 500);
        break;
      case POLE:
        this._attackRange = set.getInteger("attack_range", 80);
        break;
      default:
        this._attackRange = set.getInteger("attack_range", 40);
    }

    if (this.getItemType() == WeaponTemplate.WeaponType.NONE) {
      this._type1 = 1;
      this._type2 = 1;
    } else {
      this._type1 = 0;
      this._type2 = 0;
    }

    if (this.getItemType() == WeaponTemplate.WeaponType.PET) {
      this._type1 = 0;
      if (this._bodyPart == -100) {
        this._type2 = 6;
      } else if (this._bodyPart == -104) {
        this._type2 = 10;
      } else if (this._bodyPart == -101) {
        this._type2 = 7;
      } else {
        this._type2 = 8;
      }

      this._bodyPart = 128;
    }

  }

  public WeaponTemplate.WeaponType getItemType() {
    return (WeaponTemplate.WeaponType)this.type;
  }

  public long getItemMask() {
    return this.getItemType().mask();
  }

  public int getSoulShotCount() {
    return this._soulShotCount;
  }

  public int getSpiritShotCount() {
    return this._spiritShotCount;
  }

  public int getCritical() {
    return this._critical;
  }

  public int getRandomDamage() {
    return this._rndDam;
  }

  public int getAttackReuseDelay() {
    return this._atkReuse;
  }

  public int getMpConsume() {
    return this._mpConsume;
  }

  public int getAttackRange() {
    return this._attackRange;
  }

  public void attachFunc(FuncTemplate f) {
    if (f._stat == Stats.CRITICAL_BASE && f._order == 8) {
      this._critical = (int)Math.round(f._value / 10.0D);
    }

    super.attachFunc(f);
  }

  public boolean isMageItem() {
    return this._isMageItem;
  }

  public static enum WeaponType implements ItemType {
    NONE(1, "Shield", (Stats)null),
    SWORD(2, "Sword", Stats.SWORD_WPN_VULNERABILITY),
    BLUNT(3, "Blunt", Stats.BLUNT_WPN_VULNERABILITY),
    DAGGER(4, "Dagger", Stats.DAGGER_WPN_VULNERABILITY),
    BOW(5, "Bow", Stats.BOW_WPN_VULNERABILITY),
    POLE(6, "Pole", Stats.POLE_WPN_VULNERABILITY),
    ETC(7, "Etc", (Stats)null),
    FIST(8, "Fist", Stats.FIST_WPN_VULNERABILITY),
    DUAL(9, "Dual Sword", Stats.DUAL_WPN_VULNERABILITY),
    DUALFIST(10, "Dual Fist", Stats.FIST_WPN_VULNERABILITY),
    BIGSWORD(11, "Big Sword", Stats.SWORD_WPN_VULNERABILITY),
    PET(12, "Pet", Stats.FIST_WPN_VULNERABILITY),
    ROD(13, "Rod", (Stats)null),
    BIGBLUNT(14, "Big Blunt", Stats.BLUNT_WPN_VULNERABILITY),
    CROSSBOW(15, "Crossbow", Stats.CROSSBOW_WPN_VULNERABILITY),
    RAPIER(16, "Rapier", Stats.DAGGER_WPN_VULNERABILITY),
    ANCIENTSWORD(17, "Ancient Sword", Stats.SWORD_WPN_VULNERABILITY),
    DUALDAGGER(18, "Dual Dagger", Stats.DAGGER_WPN_VULNERABILITY);

    public static final WeaponTemplate.WeaponType[] VALUES = values();
    private final long _mask;
    private final String _name;
    private final Stats _defence;

    private WeaponType(int id, String name, Stats defence) {
      this._mask = 1L << id;
      this._name = name;
      this._defence = defence;
    }

    public long mask() {
      return this._mask;
    }

    public Stats getDefence() {
      return this._defence;
    }

    public String toString() {
      return this._name;
    }
  }
}
