//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableStatsWrapper implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(RunnableStatsWrapper.class);
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
            _log.error("Exception in a Runnable execution:", var4);
        }

    }
}