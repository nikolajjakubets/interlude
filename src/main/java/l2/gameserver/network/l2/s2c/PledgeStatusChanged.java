//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.Clan;

public class PledgeStatusChanged extends L2GameServerPacket {
  private int leader_id;
  private int clan_id;
  private int level;

  public PledgeStatusChanged(Clan clan) {
    this.leader_id = clan.getLeaderId();
    this.clan_id = clan.getClanId();
    this.level = clan.getLevel();
  }

  protected final void writeImpl() {
    this.writeC(205);
    this.writeD(this.leader_id);
    this.writeD(this.clan_id);
    this.writeD(0);
    this.writeD(this.level);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
  }
}
