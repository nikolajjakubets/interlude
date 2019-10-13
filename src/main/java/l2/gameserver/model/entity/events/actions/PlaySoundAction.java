//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.PlaySound.Type;

public class PlaySoundAction implements EventAction {
  private int _range;
  private String _sound;
  private Type _type;

  public PlaySoundAction(int range, String s, Type type) {
    this._range = range;
    this._sound = s;
    this._type = type;
  }

  public void call(GlobalEvent event) {
    GameObject object = event.getCenterObject();
    PlaySound packet = null;
    if (object != null) {
      packet = new PlaySound(this._type, this._sound, 1, object.getObjectId(), object.getLoc());
    } else {
      packet = new PlaySound(this._type, this._sound, 0, 0, 0, 0, 0);
    }

    List<Player> players = event.broadcastPlayers(this._range);
    Iterator var5 = players.iterator();

    while(var5.hasNext()) {
      Player player = (Player)var5.next();
      if (player != null) {
        player.sendPacket(packet);
      }
    }

  }
}
