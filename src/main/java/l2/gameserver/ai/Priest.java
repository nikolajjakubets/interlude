//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.ai;

import l2.gameserver.model.instances.NpcInstance;

public class Priest extends DefaultAI {
  public Priest(NpcInstance actor) {
    super(actor);
  }

  protected boolean thinkActive() {
    return super.thinkActive() || this.defaultThinkBuff(10, 5);
  }

  protected boolean createNewTask() {
    return this.defaultFightTask();
  }

  public int getRatePHYS() {
    return 10;
  }

  public int getRateDOT() {
    return 15;
  }

  public int getRateDEBUFF() {
    return 15;
  }

  public int getRateDAM() {
    return 30;
  }

  public int getRateSTUN() {
    return 3;
  }

  public int getRateBUFF() {
    return 10;
  }

  public int getRateHEAL() {
    return 40;
  }
}
