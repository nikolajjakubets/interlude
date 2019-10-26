//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExOlympiadUserInfo extends L2GameServerPacket {
  private int _side;
  private int class_id;
  private int curHp;
  private int maxHp;
  private int curCp;
  private int maxCp;
  private int obj_id = 0;
  private String _name;

  public ExOlympiadUserInfo(Player player, int side) {
    this._side = side;
    this.obj_id = player.getObjectId();
    this.class_id = player.getClassId().getId();
    this._name = player.getName();
    this.curHp = (int)player.getCurrentHp();
    this.maxHp = player.getMaxHp();
    this.curCp = (int)player.getCurrentCp();
    this.maxCp = player.getMaxCp();
  }

  public ExOlympiadUserInfo(Player player) {
    this._side = player.getOlyParticipant().getSide();
    this.obj_id = player.getObjectId();
    this.class_id = player.getClassId().getId();
    this._name = player.getName();
    this.curHp = (int)player.getCurrentHp();
    this.maxHp = player.getMaxHp();
    this.curCp = (int)player.getCurrentCp();
    this.maxCp = player.getMaxCp();
  }

  protected final void writeImpl() {
    this.writeEx(41);
    this.writeC(this._side);
    this.writeD(this.obj_id);
    this.writeS(this._name);
    this.writeD(this.class_id);
    this.writeD(this.curHp);
    this.writeD(this.maxHp);
    this.writeD(this.curCp);
    this.writeD(this.maxCp);
  }
}
