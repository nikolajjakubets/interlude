//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import l2.commons.threading.LoggingRejectedExecutionHandler;
import l2.commons.threading.PriorityThreadFactory;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.RunnableStatsWrapper;

public class ThreadPoolManager {
  private static final long MAX_DELAY;
  private static final ThreadPoolManager _instance;
  private final ScheduledThreadPoolExecutor _scheduledExecutor;
  private final ThreadPoolExecutor _executor;
  private boolean _shutdown;

  public static ThreadPoolManager getInstance() {
    return _instance;
  }

  private ThreadPoolManager() {
    this._scheduledExecutor = new ScheduledThreadPoolExecutor(Config.SCHEDULED_THREAD_POOL_SIZE, new PriorityThreadFactory("ScheduledThreadPool", 5), new LoggingRejectedExecutionHandler());
    this._executor = new ThreadPoolExecutor(Config.EXECUTOR_THREAD_POOL_SIZE, 2147483647, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new PriorityThreadFactory("ThreadPoolExecutor", 5), new LoggingRejectedExecutionHandler());
    this.scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() {
        ThreadPoolManager.this._scheduledExecutor.purge();
        ThreadPoolManager.this._executor.purge();
      }
    }, 300000L, 300000L);
  }

  private long validate(long delay) {
    return Math.max(0L, Math.min(MAX_DELAY, delay));
  }

  public boolean isShutdown() {
    return this._shutdown;
  }

  public Runnable wrap(Runnable r) {
    return Config.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap(r) : r;
  }

  public ScheduledFuture<?> schedule(Runnable r, long delay) {
    return this._scheduledExecutor.schedule(this.wrap(r), this.validate(delay), TimeUnit.MILLISECONDS);
  }

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
    return this._scheduledExecutor.scheduleAtFixedRate(this.wrap(r), this.validate(initial), this.validate(delay), TimeUnit.MILLISECONDS);
  }

  public ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay) {
    return this._scheduledExecutor.scheduleWithFixedDelay(this.wrap(r), this.validate(initial), this.validate(delay), TimeUnit.MILLISECONDS);
  }

  public void execute(Runnable r) {
    this._executor.execute(this.wrap(r));
  }

  public void shutdown() throws InterruptedException {
    this._shutdown = true;

    try {
      this._scheduledExecutor.shutdown();
      this._scheduledExecutor.awaitTermination(10L, TimeUnit.SECONDS);
    } finally {
      this._executor.shutdown();
      this._executor.awaitTermination(1L, TimeUnit.MINUTES);
    }

  }

  public CharSequence getStats() {
    StringBuilder list = new StringBuilder();
    list.append("ScheduledThreadPool\n");
    list.append("=================================================\n");
    list.append("\tgetActiveCount: ...... ").append(this._scheduledExecutor.getActiveCount()).append("\n");
    list.append("\tgetCorePoolSize: ..... ").append(this._scheduledExecutor.getCorePoolSize()).append("\n");
    list.append("\tgetPoolSize: ......... ").append(this._scheduledExecutor.getPoolSize()).append("\n");
    list.append("\tgetLargestPoolSize: .. ").append(this._scheduledExecutor.getLargestPoolSize()).append("\n");
    list.append("\tgetMaximumPoolSize: .. ").append(this._scheduledExecutor.getMaximumPoolSize()).append("\n");
    list.append("\tgetCompletedTaskCount: ").append(this._scheduledExecutor.getCompletedTaskCount()).append("\n");
    list.append("\tgetQueuedTaskCount: .. ").append(this._scheduledExecutor.getQueue().size()).append("\n");
    list.append("\tgetTaskCount: ........ ").append(this._scheduledExecutor.getTaskCount()).append("\n");
    list.append("ThreadPoolExecutor\n");
    list.append("=================================================\n");
    list.append("\tgetActiveCount: ...... ").append(this._executor.getActiveCount()).append("\n");
    list.append("\tgetCorePoolSize: ..... ").append(this._executor.getCorePoolSize()).append("\n");
    list.append("\tgetPoolSize: ......... ").append(this._executor.getPoolSize()).append("\n");
    list.append("\tgetLargestPoolSize: .. ").append(this._executor.getLargestPoolSize()).append("\n");
    list.append("\tgetMaximumPoolSize: .. ").append(this._executor.getMaximumPoolSize()).append("\n");
    list.append("\tgetCompletedTaskCount: ").append(this._executor.getCompletedTaskCount()).append("\n");
    list.append("\tgetQueuedTaskCount: .. ").append(this._executor.getQueue().size()).append("\n");
    list.append("\tgetTaskCount: ........ ").append(this._executor.getTaskCount()).append("\n");
    return list;
  }

  static {
    MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(9223372036854775807L - System.nanoTime()) / 2L;
    _instance = new ThreadPoolManager();
  }
}
