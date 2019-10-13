//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

public enum TeamType {
  NONE,
  BLUE,
  RED;

  private TeamType() {
  }

  public TeamType revert() {
    return this == BLUE ? RED : (this == RED ? BLUE : NONE);
  }
}
