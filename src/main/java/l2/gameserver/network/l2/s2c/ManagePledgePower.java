//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.RankPrivs;

public class ManagePledgePower extends L2GameServerPacket {
  private int _action;
  private int _clanId;
  private int privs;

  public ManagePledgePower(Player player, int action, int rank) {
    this._clanId = player.getClanId();
    this._action = action;
    RankPrivs temp = player.getClan().getRankPrivs(rank);
    this.privs = temp == null ? 0 : temp.getPrivs();
    player.sendPacket(new PledgeReceiveUpdatePower(this.privs));
  }

  protected final void writeImpl() {
    this.writeC(48);
    this.writeD(this._clanId);
    this.writeD(this._action);
    this.writeD(this.privs);
  }
}
