//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;

public class AutoSaveManager extends SteppingRunnableQueueManager {
  private static final AutoSaveManager _instance = new AutoSaveManager();

  public static final AutoSaveManager getInstance() {
    return _instance;
  }

  private AutoSaveManager() {
    super(10000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        AutoSaveManager.this.purge();
      }
    }, 60000L, 60000L);
  }

  public Future<?> addAutoSaveTask(final Player player) {
    long delay = (long)Rnd.get(180, 360) * 1000L;
    return this.scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (player.isOnline()) {
          player.store(true);
          if (Config.AUTOSAVE_ITEMS) {
            player.getInventory().store();
          }

        }
      }
    }, delay, delay);
  }
}
