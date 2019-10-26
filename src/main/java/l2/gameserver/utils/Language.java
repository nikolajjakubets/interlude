//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

public enum Language {
  ENGLISH("en"),
  RUSSIAN("ru");

  public static final Language[] VALUES = values();
  private String _shortName;

  private Language(String shortName) {
    this._shortName = shortName;
  }

  public String getShortName() {
    return this._shortName;
  }
}
