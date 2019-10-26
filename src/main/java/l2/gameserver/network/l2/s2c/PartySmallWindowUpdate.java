//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class PartySmallWindowUpdate extends L2GameServerPacket {
  private int obj_id;
  private int class_id;
  private int level;
  private int curCp;
  private int maxCp;
  private int curHp;
  private int maxHp;
  private int curMp;
  private int maxMp;
  private String obj_name;

  public PartySmallWindowUpdate(Player member) {
    this.obj_id = member.getObjectId();
    this.obj_name = member.getName();
    this.curCp = (int)member.getCurrentCp();
    this.maxCp = member.getMaxCp();
    this.curHp = (int)member.getCurrentHp();
    this.maxHp = member.getMaxHp();
    this.curMp = (int)member.getCurrentMp();
    this.maxMp = member.getMaxMp();
    this.level = member.getLevel();
    this.class_id = member.getClassId().getId();
  }

  protected final void writeImpl() {
    this.writeC(82);
    this.writeD(this.obj_id);
    this.writeS(this.obj_name);
    this.writeD(this.curCp);
    this.writeD(this.maxCp);
    this.writeD(this.curHp);
    this.writeD(this.maxHp);
    this.writeD(this.curMp);
    this.writeD(this.maxMp);
    this.writeD(this.level);
    this.writeD(this.class_id);
  }
}
