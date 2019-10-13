//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.io.Serializable;
import l2.gameserver.model.base.Element;

public class ItemAttributes implements Serializable {
  private static final long serialVersionUID = 401594188363005415L;
  private int fire;
  private int water;
  private int wind;
  private int earth;
  private int holy;
  private int unholy;

  public ItemAttributes() {
    this(0, 0, 0, 0, 0, 0);
  }

  public ItemAttributes(int fire, int water, int wind, int earth, int holy, int unholy) {
    this.fire = fire;
    this.water = water;
    this.wind = wind;
    this.earth = earth;
    this.holy = holy;
    this.unholy = unholy;
  }

  public int getFire() {
    return this.fire;
  }

  public void setFire(int fire) {
    this.fire = fire;
  }

  public int getWater() {
    return this.water;
  }

  public void setWater(int water) {
    this.water = water;
  }

  public int getWind() {
    return this.wind;
  }

  public void setWind(int wind) {
    this.wind = wind;
  }

  public int getEarth() {
    return this.earth;
  }

  public void setEarth(int earth) {
    this.earth = earth;
  }

  public int getHoly() {
    return this.holy;
  }

  public void setHoly(int holy) {
    this.holy = holy;
  }

  public int getUnholy() {
    return this.unholy;
  }

  public void setUnholy(int unholy) {
    this.unholy = unholy;
  }

  public Element getElement() {
    if (this.fire > 0) {
      return Element.FIRE;
    } else if (this.water > 0) {
      return Element.WATER;
    } else if (this.wind > 0) {
      return Element.WIND;
    } else if (this.earth > 0) {
      return Element.EARTH;
    } else if (this.holy > 0) {
      return Element.HOLY;
    } else {
      return this.unholy > 0 ? Element.UNHOLY : Element.NONE;
    }
  }

  public int getValue() {
    if (this.fire > 0) {
      return this.fire;
    } else if (this.water > 0) {
      return this.water;
    } else if (this.wind > 0) {
      return this.wind;
    } else if (this.earth > 0) {
      return this.earth;
    } else if (this.holy > 0) {
      return this.holy;
    } else {
      return this.unholy > 0 ? this.unholy : 0;
    }
  }

  public void setValue(Element element, int value) {
    switch(element) {
      case FIRE:
        this.fire = value;
        break;
      case WATER:
        this.water = value;
        break;
      case WIND:
        this.wind = value;
        break;
      case EARTH:
        this.earth = value;
        break;
      case HOLY:
        this.holy = value;
        break;
      case UNHOLY:
        this.unholy = value;
    }

  }

  public int getValue(Element element) {
    switch(element) {
      case FIRE:
        return this.fire;
      case WATER:
        return this.water;
      case WIND:
        return this.wind;
      case EARTH:
        return this.earth;
      case HOLY:
        return this.holy;
      case UNHOLY:
        return this.unholy;
      default:
        return 0;
    }
  }

  public ItemAttributes clone() {
    return new ItemAttributes(this.fire, this.water, this.wind, this.earth, this.holy, this.unholy);
  }
}
