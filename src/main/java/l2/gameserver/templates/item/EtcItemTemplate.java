//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item;

import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class EtcItemTemplate extends ItemTemplate {
  public EtcItemTemplate(StatsSet set) {
    super(set);
    this.type = (ItemType)set.getEnum("type", EtcItemTemplate.EtcItemType.class);
    this._type1 = 4;
    switch(this.getItemType()) {
      case QUEST:
        this._type2 = 3;
        break;
      case MONEY:
        this._type2 = 4;
        break;
      default:
        this._type2 = 5;
    }

  }

  public EtcItemTemplate.EtcItemType getItemType() {
    return (EtcItemTemplate.EtcItemType)super.type;
  }

  public long getItemMask() {
    return this.getItemType().mask();
  }

  public final boolean isShadowItem() {
    return false;
  }

  public final boolean canBeEnchanted(boolean gradeCheck) {
    return false;
  }

  public static enum EtcItemType implements ItemType {
    ARROW(1, "Arrow"),
    MATERIAL(2, "Material"),
    PET_COLLAR(3, "PetCollar"),
    POTION(4, "Potion"),
    RECIPE(5, "Recipe"),
    SCROLL(6, "Scroll"),
    QUEST(7, "Quest"),
    MONEY(8, "Money"),
    OTHER(9, "Other"),
    SPELLBOOK(10, "Spellbook"),
    SEED(11, "Seed"),
    BAIT(12, "Bait"),
    SHOT(13, "Shot"),
    BOLT(14, "Bolt"),
    RUNE(15, "Rune"),
    HERB(16, "Herb"),
    MERCENARY_TICKET(17, "Mercenary Ticket");

    private final long _mask;
    private final String _name;

    private EtcItemType(int id, String name) {
      this._mask = 1L << id + WeaponType.VALUES.length + ArmorType.VALUES.length;
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
