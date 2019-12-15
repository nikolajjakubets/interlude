//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import l2.commons.collections.LazyArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class SteppingRunnableQueueManager implements Runnable {
  protected final long tickPerStepInMillis;
  private final List<SteppingRunnableQueueManager.SteppingScheduledFuture<?>> queue = new CopyOnWriteArrayList<>();
  private final AtomicBoolean isRunning = new AtomicBoolean();

  public SteppingRunnableQueueManager(long tickPerStepInMillis) {
    this.tickPerStepInMillis = tickPerStepInMillis;
  }

  public SteppingRunnableQueueManager.SteppingScheduledFuture<?> schedule(Runnable r, long delay) {
    return this.schedule(r, delay, delay, false);
  }

  public SteppingRunnableQueueManager.SteppingScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
    return this.schedule(r, initial, delay, true);
  }

  private SteppingRunnableQueueManager.SteppingScheduledFuture<?> schedule(Runnable r, long initial, long delay, boolean isPeriodic) {
    long initialStepping = this.getStepping(initial);
    long stepping = this.getStepping(delay);
    SteppingRunnableQueueManager.SteppingScheduledFuture sr;
    this.queue.add(sr = new SteppingRunnableQueueManager.SteppingScheduledFuture(r, initialStepping, stepping, isPeriodic));
    return sr;
  }

  private long getStepping(long delay) {
    delay = Math.max(0L, delay);
    return delay % this.tickPerStepInMillis > this.tickPerStepInMillis / 2L ? delay / this.tickPerStepInMillis + 1L : (delay < this.tickPerStepInMillis ? 1L : delay / this.tickPerStepInMillis);
  }

  public void run() {
    try {
      if (!this.isRunning.compareAndSet(false, true)) {
        log.warn("Slow running queue, managed by " + this + ", queue size : " + this.queue.size() + "!");
      } else {
        try {
          if (!this.queue.isEmpty()) {

            for (SteppingScheduledFuture<?> steppingScheduledFuture : this.queue) {
              if (!steppingScheduledFuture.isDone()) {
                steppingScheduledFuture.run();
              }
            }

          }
        } finally {
          this.isRunning.set(false);
        }

      }
    } catch (Throwable var7) {
      log.error("Exception in stepped queue manager", var7);
    }
  }

  public void purge() {
    LazyArrayList<SteppingRunnableQueueManager.SteppingScheduledFuture<?>> purge = LazyArrayList.newInstance();

    for (SteppingScheduledFuture<?> steppingScheduledFuture : this.queue) {
      if (steppingScheduledFuture.isDone()) {
        purge.add(steppingScheduledFuture);
      }
    }

    this.queue.removeAll(purge);
    LazyArrayList.recycle(purge);
  }

  public CharSequence getStats() {
    StringBuilder list = new StringBuilder();
    Map<String, MutableLong> stats = new TreeMap<>();
    int total = 0;
    int done = 0;
    Iterator var5 = this.queue.iterator();

    while (var5.hasNext()) {
      SteppingRunnableQueueManager.SteppingScheduledFuture<?> sr = (SteppingRunnableQueueManager.SteppingScheduledFuture) var5.next();
      if (sr.isDone()) {
        ++done;
      } else {
        ++total;
        MutableLong count = stats.get(sr.r.getClass().getName());
        if (count == null) {
          stats.put(sr.r.getClass().getName(), new MutableLong(1L));
        } else {
          count.increment();
        }
      }
    }

    var5 = stats.entrySet().iterator();

    while (var5.hasNext()) {
      Entry<String, MutableLong> e = (Entry) var5.next();
      list.append("\t").append(e.getKey()).append(" : ").append(e.getValue().longValue()).append("\n");
    }

    list.append("Scheduled: ....... ").append(total).append("\n");
    list.append("Done/Cancelled: .. ").append(done).append("\n");
    return list;
  }

  public class SteppingScheduledFuture<V> implements RunnableScheduledFuture<V> {
    private final Runnable r;
    private final long stepping;
    private final boolean isPeriodic;
    private long step;
    private boolean isCancelled;

    public SteppingScheduledFuture(Runnable r, long initial, long stepping, boolean isPeriodic) {
      this.r = r;
      this.step = initial;
      this.stepping = stepping;
      this.isPeriodic = isPeriodic;
    }

    public void run() {
      if (--this.step == 0L) {
        try {
          this.r.run();
        } catch (Throwable e) {
          log.error("run: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
          SteppingRunnableQueueManager.log.error("Exception in a Runnable execution:", e);
        } finally {
          if (this.isPeriodic) {
            this.step = this.stepping;
          }

        }
      }

    }

    public boolean isDone() {
      return this.isCancelled || !this.isPeriodic && this.step == 0L;
    }

    public boolean isCancelled() {
      return this.isCancelled;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
      return this.isCancelled = true;
    }

    public V get() throws InterruptedException, ExecutionException {
      return null;
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return null;
    }

    public long getDelay(TimeUnit unit) {
      return unit.convert(this.step * SteppingRunnableQueueManager.this.tickPerStepInMillis, TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
      return 0;
    }

    public boolean isPeriodic() {
      return this.isPeriodic;
    }
  }
}
