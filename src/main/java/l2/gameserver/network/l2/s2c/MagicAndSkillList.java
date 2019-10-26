//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;

public class MagicAndSkillList extends L2GameServerPacket {
  private int _chaId;
  private int _unk1;
  private int _unk2;

  public MagicAndSkillList(Creature cha, int unk1, int unk2) {
    this._chaId = cha.getObjectId();
    this._unk1 = unk1;
    this._unk2 = unk2;
  }

  protected final void writeImpl() {
    this.writeC(64);
    this.writeD(this._chaId);
    this.writeD(this._unk1);
    this.writeD(this._unk2);
  }
}
