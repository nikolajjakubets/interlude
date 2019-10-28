//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Scripts;

public class ScriptAnswerListener implements OnAnswerListener {
  private HardReference<Player> _playerRef;
  private String _scriptName;
  private Object[] _arg;
  private long _endTime;

  public ScriptAnswerListener(Player player, String scriptName, Object[] arg, long time) {
    this._scriptName = scriptName;
    this._arg = arg;
    this._playerRef = (HardReference<Player>) player.getRef();
    this._endTime = System.currentTimeMillis() + time;
  }

  public void sayYes() {
    Player player = (Player)this._playerRef.get();
    if (player != null && System.currentTimeMillis() <= this._endTime) {
      Scripts.getInstance().callScripts(player, this._scriptName.split(":")[0], this._scriptName.split(":")[1], this._arg);
    }
  }

  public void sayNo() {
  }
}
