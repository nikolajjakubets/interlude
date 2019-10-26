//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExSubPledgeSkillAdd extends L2GameServerPacket {
  private int _type;
  private int _id;
  private int _level;

  public ExSubPledgeSkillAdd(int type, int id, int level) {
    this._type = type;
    this._id = id;
    this._level = level;
  }

  protected void writeImpl() {
    this.writeEx(118);
    this.writeD(this._type);
    this.writeD(this._id);
    this.writeD(this._level);
  }
}
