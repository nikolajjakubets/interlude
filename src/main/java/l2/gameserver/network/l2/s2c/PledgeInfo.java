//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.Clan;

public class PledgeInfo extends L2GameServerPacket {
  private int clan_id;
  private String clan_name;
  private String ally_name;

  public PledgeInfo(Clan clan) {
    this.clan_id = clan.getClanId();
    this.clan_name = clan.getName();
    this.ally_name = clan.getAlliance() == null ? "" : clan.getAlliance().getAllyName();
  }

  protected final void writeImpl() {
    this.writeC(131);
    this.writeD(this.clan_id);
    this.writeS(this.clan_name);
    this.writeS(this.ally_name);
  }
}
