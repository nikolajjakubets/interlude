//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class PartySmallWindowDelete extends L2GameServerPacket {
  private final int _objId;
  private final String _name;

  public PartySmallWindowDelete(Player member) {
    this._objId = member.getObjectId();
    this._name = member.getName();
  }

  protected final void writeImpl() {
    this.writeC(81);
    this.writeD(this._objId);
    this.writeS(this._name);
  }
}
