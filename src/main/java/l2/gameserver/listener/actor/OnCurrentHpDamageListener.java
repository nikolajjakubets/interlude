//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener {
  void onCurrentHpDamage(Creature var1, double var2, Creature var4, Skill var5);
}
