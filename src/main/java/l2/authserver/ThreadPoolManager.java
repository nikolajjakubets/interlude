package l2.authserver;

import l2.commons.threading.RunnableImpl;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private static final long MAX_DELAY;
    private static final ThreadPoolManager instance;
    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
    private final ThreadPoolExecutor executor;

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    private ThreadPoolManager() {
        this.executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        this.scheduleAtFixedRate(new RunnableImpl() {
            public void runImpl() {
                ThreadPoolManager.this.executor.purge();
                ThreadPoolManager.this.scheduledExecutor.purge();
            }
        }, 600000L, 600000L);
    }

    private long validate(long delay) {
        return Math.max(0L, Math.min(MAX_DELAY, delay));
    }

    public void execute(Runnable r) {
        this.executor.execute(r);
    }

    public void schedule(Runnable r, long delay) {
        this.scheduledExecutor.schedule(r, this.validate(delay), TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
        return this.scheduledExecutor.scheduleAtFixedRate(r, this.validate(initial), this.validate(delay), TimeUnit.MILLISECONDS);
    }

    static {
        MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(9223372036854775807L - System.nanoTime()) / 2L;
        instance = new ThreadPoolManager();
    }
}
