//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.utils.Location;
import org.dom4j.Element;

public class BoatPoint extends Location {
  private int _speed1;
  private int _speed2;
  private final int _fuel;
  private boolean _teleport;

  public BoatPoint(int x, int y, int z, int h, int speed1, int speed2, int fuel, boolean teleport) {
    super(x, y, z, h);
    this._speed1 = speed1;
    this._speed2 = speed2;
    this._fuel = fuel;
    this._teleport = teleport;
  }

  public int getSpeed1() {
    return this._speed1;
  }

  public int getSpeed2() {
    return this._speed2;
  }

  public int getFuel() {
    return this._fuel;
  }

  public boolean isTeleport() {
    return this._teleport;
  }

  public static BoatPoint parse(Element element) {
    int speed1 = element.attributeValue("speed1") == null ? 0 : Integer.parseInt(element.attributeValue("speed1"));
    int speed2 = element.attributeValue("speed2") == null ? 0 : Integer.parseInt(element.attributeValue("speed2"));
    int x = Integer.parseInt(element.attributeValue("x"));
    int y = Integer.parseInt(element.attributeValue("y"));
    int z = Integer.parseInt(element.attributeValue("z"));
    int h = element.attributeValue("h") == null ? 0 : Integer.parseInt(element.attributeValue("h"));
    int fuel = element.attributeValue("fuel") == null ? 0 : Integer.parseInt(element.attributeValue("fuel"));
    boolean teleport = Boolean.parseBoolean(element.attributeValue("teleport"));
    return new BoatPoint(x, y, z, h, speed1, speed2, fuel, teleport);
  }

  public void setSpeed1(int speed1) {
    this._speed1 = speed1;
  }

  public void setSpeed2(int speed2) {
    this._speed2 = speed2;
  }

  public void setTeleport(boolean teleport) {
    this._teleport = teleport;
  }
}
