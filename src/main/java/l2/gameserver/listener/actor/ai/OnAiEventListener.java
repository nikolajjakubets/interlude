//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.ai;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.listener.AiListener;
import l2.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener {
  void onAiEvent(Creature var1, CtrlEvent var2, Object[] var3);
}
