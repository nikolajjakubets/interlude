//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PledgeSkillListAdd extends L2GameServerPacket {
  private int _skillId;
  private int _skillLevel;

  public PledgeSkillListAdd(int skillId, int skillLevel) {
    this._skillId = skillId;
    this._skillLevel = skillLevel;
  }

  protected final void writeImpl() {
    this.writeEx(58);
    this.writeD(this._skillId);
    this.writeD(this._skillLevel);
  }
}

