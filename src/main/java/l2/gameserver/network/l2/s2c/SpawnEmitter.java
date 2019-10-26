//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class SpawnEmitter extends L2GameServerPacket {
  private int _monsterObjId;
  private int _playerObjId;

  public SpawnEmitter(NpcInstance monster, Player player) {
    this._playerObjId = player.getObjectId();
    this._monsterObjId = monster.getObjectId();
  }

  protected final void writeImpl() {
    this.writeEx(93);
    this.writeD(this._monsterObjId);
    this.writeD(this._playerObjId);
    this.writeD(0);
  }
}
