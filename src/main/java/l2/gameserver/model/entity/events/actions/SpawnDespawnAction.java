//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class SpawnDespawnAction implements EventAction {
  private final boolean _spawn;
  private final String _name;

  public SpawnDespawnAction(String name, boolean spawn) {
    this._spawn = spawn;
    this._name = name;
  }

  public void call(GlobalEvent event) {
    event.spawnAction(this._name, this._spawn);
  }
}
