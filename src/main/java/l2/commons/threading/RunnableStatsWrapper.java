//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableStatsWrapper implements Runnable {
  private final Runnable _runnable;

  RunnableStatsWrapper(Runnable runnable) {
    this._runnable = runnable;
  }

  public static Runnable wrap(Runnable runnable) {
    return new RunnableStatsWrapper(runnable);
  }

  public void run() {
    execute(this._runnable);
  }

  public static void execute(Runnable runnable) {
    long begin = System.nanoTime();

    try {
      runnable.run();
      RunnableStatsManager.getInstance().handleStats(runnable.getClass(), System.nanoTime() - begin);
    } catch (Exception var4) {
      log.error("Exception in a Runnable execution:", var4);
    }

  }
}
