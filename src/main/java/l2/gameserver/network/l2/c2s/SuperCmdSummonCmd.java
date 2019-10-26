//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

class SuperCmdSummonCmd extends L2GameClientPacket {
  private String _summonName;

  SuperCmdSummonCmd() {
  }

  protected void readImpl() {
    this._summonName = this.readS();
  }

  protected void runImpl() {
  }
}
