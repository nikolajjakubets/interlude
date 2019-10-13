//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.model.instances.NpcInstance;

public class Balanced extends DefaultAI {
  public Balanced(NpcInstance actor) {
    super(actor);
  }

  protected boolean thinkActive() {
    return super.thinkActive() || this.defaultThinkBuff(2);
  }

  protected boolean createNewTask() {
    return this.defaultFightTask();
  }

  public int getRatePHYS() {
    return 35;
  }

  public int getRateDOT() {
    return 10;
  }

  public int getRateDEBUFF() {
    return 5;
  }

  public int getRateDAM() {
    return 10;
  }

  public int getRateSTUN() {
    return 3;
  }

  public int getRateBUFF() {
    return 3;
  }

  public int getRateHEAL() {
    return 5;
  }
}
