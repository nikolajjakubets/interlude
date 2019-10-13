//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.Summon;

public class SummonStatsChangeRecorder extends CharStatsChangeRecorder<Summon> {
  public SummonStatsChangeRecorder(Summon actor) {
    super(actor);
  }

  protected void onSendChanges() {
    super.onSendChanges();
    if ((this._changes & 2) == 2) {
      ((Summon)this._activeChar).sendPetInfo();
    } else if ((this._changes & 1) == 1) {
      ((Summon)this._activeChar).broadcastCharInfo();
    }

  }
}
