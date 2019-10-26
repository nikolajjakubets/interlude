//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;

public final class SendStatus extends L2GameServerPacket {
  private static final long MIN_UPDATE_PERIOD = 30000L;
  private static int online_players = 0;
  private static int max_online_players = 0;
  private static int online_priv_store = 0;
  private static long last_update = 0L;

  public SendStatus() {
    int i = 0;
    int j = 0;
    Iterator var3 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(true) {
      Player player;
      do {
        do {
          do {
            if (!var3.hasNext()) {
              online_players = (int)((double)i * Config.MUL_PLAYERS_ONLINE);
              online_priv_store = (int)Math.floor((double)j * Config.SENDSTATUS_TRADE_MOD);
              max_online_players = Math.max(max_online_players, online_players);
              return;
            }

            player = (Player)var3.next();
          } while(player == null);

          ++i;
        } while(!player.isInStoreMode());
      } while(Config.SENDSTATUS_TRADE_JUST_OFFLINE && !player.isInOfflineMode());

      ++j;
    }
  }

  protected final void writeImpl() {
    if (System.currentTimeMillis() - last_update >= 30000L) {
      last_update = System.currentTimeMillis();
      this.writeC(0);
      this.writeD(1);
      this.writeD(max_online_players);
      this.writeD(online_players);
      this.writeD(online_players);
      this.writeD(online_priv_store);
      this.writeH(48);
      this.writeH(44);
      this.writeH(53);
      this.writeH(49);
      this.writeH(48);
      this.writeH(44);
      this.writeH(55);
      this.writeH(55);
      this.writeH(55);
      this.writeH(53);
      this.writeH(56);
      this.writeH(44);
      this.writeH(54);
      this.writeH(53);
      this.writeH(48);
      this.writeD(54);
      this.writeD(119);
      this.writeD(183);
      this.writeQ(159L);
      this.writeD(0);
      this.writeH(65);
      this.writeH(117);
      this.writeH(103);
      this.writeH(32);
      this.writeH(50);
      this.writeH(57);
      this.writeH(32);
      this.writeH(50);
      this.writeH(48);
      this.writeH(48);
      this.writeD(57);
      this.writeH(48);
      this.writeH(50);
      this.writeH(58);
      this.writeH(52);
      this.writeH(48);
      this.writeH(58);
      this.writeH(52);
      this.writeD(51);
      this.writeD(87);
      this.writeC(17);
      this.writeC(93);
      this.writeC(31);
      this.writeC(96);
    }
  }
}
