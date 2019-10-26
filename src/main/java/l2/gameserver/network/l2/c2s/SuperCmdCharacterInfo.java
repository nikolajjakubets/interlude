//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

class SuperCmdCharacterInfo extends L2GameClientPacket {
  private String _characterName;

  SuperCmdCharacterInfo() {
  }

  protected void readImpl() {
    this._characterName = this.readS();
  }

  protected void runImpl() {
  }
}
