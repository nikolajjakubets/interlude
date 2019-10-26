//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import gnu.trove.TIntArrayList;
import l2.gameserver.model.Player;

public class Henna {
  private final int _symbolId;
  private final int _dyeId;
  private final long _price;
  private final long _drawCount;
  private final int _statINT;
  private final int _statSTR;
  private final int _statCON;
  private final int _statMEN;
  private final int _statDEX;
  private final int _statWIT;
  private final TIntArrayList _classes;

  public Henna(int symbolId, int dyeId, long price, long drawCount, int wit, int intA, int con, int str, int dex, int men, TIntArrayList classes) {
    this._symbolId = symbolId;
    this._dyeId = dyeId;
    this._price = price;
    this._drawCount = drawCount;
    this._statINT = intA;
    this._statSTR = str;
    this._statCON = con;
    this._statMEN = men;
    this._statDEX = dex;
    this._statWIT = wit;
    this._classes = classes;
  }

  public int getSymbolId() {
    return this._symbolId;
  }

  public int getDyeId() {
    return this._dyeId;
  }

  public long getPrice() {
    return this._price;
  }

  public int getStatINT() {
    return this._statINT;
  }

  public int getStatSTR() {
    return this._statSTR;
  }

  public int getStatCON() {
    return this._statCON;
  }

  public int getStatMEN() {
    return this._statMEN;
  }

  public int getStatDEX() {
    return this._statDEX;
  }

  public int getStatWIT() {
    return this._statWIT;
  }

  public boolean isForThisClass(Player player) {
    return this._classes.contains(player.getActiveClassId());
  }

  public long getDrawCount() {
    return this._drawCount;
  }
}
