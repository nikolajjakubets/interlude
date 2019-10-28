//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.Location;

public class SummonAnswerListener implements OnAnswerListener {
  private HardReference<Player> _playerRef;
  private Location _location;
  private long _count;
  private final long _timeStamp;

  public SummonAnswerListener(Player player, Location loc, long count, int expiration) {
    this._playerRef = (HardReference<Player>) player.getRef();
    this._location = loc;
    this._count = count;
    this._timeStamp = expiration > 0 ? System.currentTimeMillis() + (long)expiration : 9223372036854775807L;
  }

  public void sayYes() {
    Player player = (Player)this._playerRef.get();
    if (player != null) {
      if (System.currentTimeMillis() <= this._timeStamp) {
        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.stopMove();
        if (this._count > 0L) {
          if (player.getInventory().destroyItemByItemId(8615, this._count)) {
            player.sendPacket(SystemMessage2.removeItems(8615, this._count));
            player.teleToLocation(this._location);
          } else {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
          }
        } else {
          player.teleToLocation(this._location);
        }

      }
    }
  }

  public void sayNo() {
  }
}
