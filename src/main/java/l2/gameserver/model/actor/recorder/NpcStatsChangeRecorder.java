//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.instances.NpcInstance;

public class NpcStatsChangeRecorder extends CharStatsChangeRecorder<NpcInstance> {
  public NpcStatsChangeRecorder(NpcInstance actor) {
    super(actor);
  }

  protected void onSendChanges() {
    super.onSendChanges();
    if ((this._changes & 1) == 1) {
      ((NpcInstance)this._activeChar).broadcastCharInfo();
    }

  }
}
