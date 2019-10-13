//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.geodata;

import l2.commons.geometry.Shape;

public interface GeoCollision {
  Shape getShape();

  byte[][] getGeoAround();

  void setGeoAround(byte[][] var1);

  boolean isConcrete();
}
