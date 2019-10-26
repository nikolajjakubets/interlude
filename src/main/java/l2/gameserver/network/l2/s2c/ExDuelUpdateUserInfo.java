//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExDuelUpdateUserInfo extends L2GameServerPacket {
  private String _name;
  private int obj_id;
  private int class_id;
  private int level;
  private int curHp;
  private int maxHp;
  private int curMp;
  private int maxMp;
  private int curCp;
  private int maxCp;

  public ExDuelUpdateUserInfo(Player attacker) {
    this._name = attacker.getName();
    this.obj_id = attacker.getObjectId();
    this.class_id = attacker.getClassId().getId();
    this.level = attacker.getLevel();
    this.curHp = (int)attacker.getCurrentHp();
    this.maxHp = attacker.getMaxHp();
    this.curMp = (int)attacker.getCurrentMp();
    this.maxMp = attacker.getMaxMp();
    this.curCp = (int)attacker.getCurrentCp();
    this.maxCp = attacker.getMaxCp();
  }

  protected final void writeImpl() {
    this.writeEx(79);
    this.writeS(this._name);
    this.writeD(this.obj_id);
    this.writeD(this.class_id);
    this.writeD(this.level);
    this.writeD(this.curHp);
    this.writeD(this.maxHp);
    this.writeD(this.curMp);
    this.writeD(this.maxMp);
    this.writeD(this.curCp);
    this.writeD(this.maxCp);
  }
}
