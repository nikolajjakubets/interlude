//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.NpcString;

public abstract class NpcStringContainer extends L2GameServerPacket {
  private final NpcString _npcString;
  private final String[] _parameters = new String[5];

  protected NpcStringContainer(NpcString npcString, String... arg) {
    this._npcString = npcString;
    System.arraycopy(arg, 0, this._parameters, 0, arg.length);
  }

  protected void writeElements() {
    String[] var1 = this._parameters;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      String st = var1[var3];
      this.writeS(st);
    }

  }
}
