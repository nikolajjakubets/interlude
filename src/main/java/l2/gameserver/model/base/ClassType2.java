//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

public enum ClassType2 {
  None(10280, 10612),
  Warrior(10281, 10289),
  Knight(10282, 10288),
  Rogue(10283, 10290),
  Healer(10285, 10291),
  Enchanter(10287, 10293),
  Summoner(10286, 10294),
  Wizard(10284, 10292);

  public static final ClassType2[] VALUES = values();
  private final int _certificate;
  private final int _transformation;

  private ClassType2(int cer, int tra) {
    this._certificate = cer;
    this._transformation = tra;
  }

  public int getCertificateId() {
    return this._certificate;
  }

  public int getTransformationId() {
    return this._transformation;
  }
}
