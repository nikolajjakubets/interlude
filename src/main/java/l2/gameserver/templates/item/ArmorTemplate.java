//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item;

import l2.gameserver.Config;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class ArmorTemplate extends ItemTemplate {
  public static final double EMPTY_RING = 5.0D;
  public static final double EMPTY_EARRING = 9.0D;
  public static final double EMPTY_NECKLACE = 13.0D;
  public static final double EMPTY_HELMET = 12.0D;
  public static final double EMPTY_BODY_FIGHTER = 31.0D;
  public static final double EMPTY_LEGS_FIGHTER = 18.0D;
  public static final double EMPTY_BODY_MYSTIC = 15.0D;
  public static final double EMPTY_LEGS_MYSTIC = 8.0D;
  public static final double EMPTY_GLOVES = 8.0D;
  public static final double EMPTY_BOOTS = 7.0D;

  public ArmorTemplate(StatsSet set) {
    super(set);
    this.type = (ItemType)set.getEnum("type", ArmorTemplate.ArmorType.class);
    if (this._bodyPart != 8 && (this._bodyPart & 4) == 0 && (this._bodyPart & 32) == 0) {
      if (this._bodyPart != 65536 && this._bodyPart != 262144 && this._bodyPart != 524288) {
        this._type1 = 1;
        this._type2 = 1;
      } else {
        this._type1 = 2;
        this._type2 = Config.ALT_HAIR_TO_ACC_SLOT ? 2 : 5;
      }
    } else {
      this._type1 = 0;
      this._type2 = 2;
    }

    if (this.getItemType() == ArmorTemplate.ArmorType.PET) {
      this._type1 = 1;
      switch(this._bodyPart) {
        case -105:
          this._type2 = 11;
          this._bodyPart = 8;
          break;
        case -104:
          this._type2 = 10;
          this._bodyPart = 1024;
          break;
        case -103:
          this._type2 = 12;
          this._bodyPart = 1024;
          break;
        case -102:
        default:
          this._type2 = 8;
          this._bodyPart = 1024;
          break;
        case -101:
          this._type2 = 7;
          this._bodyPart = 1024;
          break;
        case -100:
          this._type2 = 6;
          this._bodyPart = 1024;
      }
    }

  }

  public ArmorTemplate.ArmorType getItemType() {
    return (ArmorTemplate.ArmorType)super.type;
  }

  public final long getItemMask() {
    return this.getItemType().mask();
  }

  public static enum ArmorType implements ItemType {
    NONE(1, "None"),
    LIGHT(2, "Light"),
    HEAVY(3, "Heavy"),
    MAGIC(4, "Magic"),
    PET(5, "Pet"),
    SIGIL(6, "Sigil");

    public static final ArmorTemplate.ArmorType[] VALUES = values();
    private final long _mask;
    private final String _name;

    private ArmorType(int id, String name) {
      this._mask = 1L << id + WeaponType.VALUES.length;
      this._name = name;
    }

    public long mask() {
      return this._mask;
    }

    public String toString() {
      return this._name;
    }
  }
}
