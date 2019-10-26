//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;

public class DecayTaskManager extends SteppingRunnableQueueManager {
  private static final DecayTaskManager _instance = new DecayTaskManager();

  public static final DecayTaskManager getInstance() {
    return _instance;
  }

  private DecayTaskManager() {
    super(500L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 500L, 500L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        DecayTaskManager.this.purge();
      }
    }, 60000L, 60000L);
  }

  public Future<?> addDecayTask(final Creature actor, long delay) {
    return this.schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        actor.doDecay();
      }
    }, delay);
  }
}
