//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;

public class ReviveAnswerListener implements OnAnswerListener {
  private HardReference<Player> _playerRef;
  private double _power;
  private boolean _forPet;
  private final long _timeStamp;

  public ReviveAnswerListener(Player player, double power, boolean forPet, int expireResurrectTime) {
    this._playerRef = player.getRef();
    this._forPet = forPet;
    this._power = power;
    this._timeStamp = expireResurrectTime > 0 ? System.currentTimeMillis() + (long)expireResurrectTime : 9223372036854775807L;
  }

  public void sayYes() {
    Player player = (Player)this._playerRef.get();
    if (player != null) {
      if (System.currentTimeMillis() <= this._timeStamp) {
        if ((player.isDead() || this._forPet) && (!this._forPet || player.getPet() == null || player.getPet().isDead())) {
          if (!this._forPet) {
            player.doRevive(this._power);
          } else if (player.getPet() != null) {
            ((PetInstance)player.getPet()).doRevive(this._power);
          }

        }
      }
    }
  }

  public void sayNo() {
  }

  public double getPower() {
    return this._power;
  }

  public boolean isForPet() {
    return this._forPet;
  }
}
