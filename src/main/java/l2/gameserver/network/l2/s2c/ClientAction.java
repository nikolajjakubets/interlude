//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ClientAction extends L2GameServerPacket {
  public ClientAction() {
  }

  protected void writeImpl() {
    this.writeC(143);
  }
}
