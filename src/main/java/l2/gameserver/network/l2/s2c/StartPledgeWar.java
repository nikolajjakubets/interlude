//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class StartPledgeWar extends L2GameServerPacket {
  private String _pledgeName;
  private String _char;

  public StartPledgeWar(String pledge, String charName) {
    this._pledgeName = pledge;
    this._char = charName;
  }

  protected final void writeImpl() {
    this.writeC(101);
    this.writeS(this._char);
    this.writeS(this._pledgeName);
  }
}
