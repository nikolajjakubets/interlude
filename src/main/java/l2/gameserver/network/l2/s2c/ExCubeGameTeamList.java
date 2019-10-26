//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;

public class ExCubeGameTeamList extends L2GameServerPacket {
  List<Player> _bluePlayers;
  List<Player> _redPlayers;
  int _roomNumber;

  public ExCubeGameTeamList(List<Player> redPlayers, List<Player> bluePlayers, int roomNumber) {
    this._redPlayers = redPlayers;
    this._bluePlayers = bluePlayers;
    this._roomNumber = roomNumber - 1;
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(0);
    this.writeD(this._roomNumber);
    this.writeD(-1);
    this.writeD(this._bluePlayers.size());
    Iterator var1 = this._bluePlayers.iterator();

    Player player;
    while(var1.hasNext()) {
      player = (Player)var1.next();
      this.writeD(player.getObjectId());
      this.writeS(player.getName());
    }

    this.writeD(this._redPlayers.size());
    var1 = this._redPlayers.iterator();

    while(var1.hasNext()) {
      player = (Player)var1.next();
      this.writeD(player.getObjectId());
      this.writeS(player.getName());
    }

  }
}
