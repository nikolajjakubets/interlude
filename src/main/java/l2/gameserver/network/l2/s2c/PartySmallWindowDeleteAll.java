//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PartySmallWindowDeleteAll extends L2GameServerPacket {
  public static final L2GameServerPacket STATIC = new PartySmallWindowDeleteAll();

  public PartySmallWindowDeleteAll() {
  }

  protected final void writeImpl() {
    this.writeC(80);
  }
}
