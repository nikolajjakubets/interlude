//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.PartySmallWindowAll.PartySmallWindowMemberInfo;

public class PartySmallWindowAdd extends L2GameServerPacket {
  private int objectId;
  private final PartySmallWindowMemberInfo member;

  public PartySmallWindowAdd(Player player, Player member) {
    this.objectId = player.getObjectId();
    this.member = new PartySmallWindowMemberInfo(member);
  }

  protected final void writeImpl() {
    this.writeC(79);
    this.writeD(this.objectId);
    this.writeD(0);
    this.writeD(this.member._id);
    this.writeS(this.member._name);
    this.writeD(this.member.curCp);
    this.writeD(this.member.maxCp);
    this.writeD(this.member.curHp);
    this.writeD(this.member.maxHp);
    this.writeD(this.member.curMp);
    this.writeD(this.member.maxMp);
    this.writeD(this.member.level);
    this.writeD(this.member.class_id);
    this.writeD(0);
    this.writeD(this.member.race_id);
  }
}
