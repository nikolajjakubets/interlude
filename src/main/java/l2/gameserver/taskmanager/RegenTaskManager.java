//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.gameserver.ThreadPoolManager;

public class RegenTaskManager extends SteppingRunnableQueueManager {
  private static final RegenTaskManager _instance = new RegenTaskManager();

  public static final RegenTaskManager getInstance() {
    return _instance;
  }

  private RegenTaskManager() {
    super(1000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        RegenTaskManager.this.purge();
      }
    }, 10000L, 10000L);
  }
}
