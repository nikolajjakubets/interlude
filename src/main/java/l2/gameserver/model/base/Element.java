//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import java.util.Arrays;
import l2.gameserver.stats.Stats;

public enum Element {
  FIRE(0, Stats.ATTACK_FIRE, Stats.DEFENCE_FIRE),
  WATER(1, Stats.ATTACK_WATER, Stats.DEFENCE_WATER),
  WIND(2, Stats.ATTACK_WIND, Stats.DEFENCE_WIND),
  EARTH(3, Stats.ATTACK_EARTH, Stats.DEFENCE_EARTH),
  HOLY(4, Stats.ATTACK_HOLY, Stats.DEFENCE_HOLY),
  UNHOLY(5, Stats.ATTACK_UNHOLY, Stats.DEFENCE_UNHOLY),
  NONE(-2, (Stats)null, (Stats)null);

  public static final Element[] VALUES = (Element[])Arrays.copyOf(values(), 6);
  private final byte id;
  private final Stats attack;
  private final Stats defence;

  private Element(int id, Stats attack, Stats defence) {
    this.id = (byte)id;
    this.attack = attack;
    this.defence = defence;
  }

  public byte getId() {
    return this.id;
  }

  public Stats getAttack() {
    return this.attack;
  }

  public Stats getDefence() {
    return this.defence;
  }

  public static Element getElementById(int id) {
    Element[] var1 = VALUES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Element e = var1[var3];
      if (e.getId() == id) {
        return e;
      }
    }

    return NONE;
  }

  public static Element getReverseElement(Element element) {
    switch(element) {
      case WATER:
        return FIRE;
      case FIRE:
        return WATER;
      case WIND:
        return EARTH;
      case EARTH:
        return WIND;
      case HOLY:
        return UNHOLY;
      case UNHOLY:
        return HOLY;
      default:
        return NONE;
    }
  }

  public static Element getElementByName(String name) {
    Element[] var1 = VALUES;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Element e = var1[var3];
      if (e.name().equalsIgnoreCase(name)) {
        return e;
      }
    }

    return NONE;
  }
}
