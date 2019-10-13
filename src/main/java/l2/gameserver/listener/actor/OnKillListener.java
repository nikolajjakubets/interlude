//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;

public interface OnKillListener extends CharListener {
  void onKill(Creature var1, Creature var2);

  boolean ignorePetOrSummon();
}
