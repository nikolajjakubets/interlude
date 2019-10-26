//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AcquireSkillDone extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC = new AcquireSkillDone();

  public AcquireSkillDone() {
  }

  protected void writeImpl() {
    this.writeC(37);
  }
}
