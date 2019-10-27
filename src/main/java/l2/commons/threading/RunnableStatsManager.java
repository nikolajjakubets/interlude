//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class RunnableStatsManager {
    private static final RunnableStatsManager _instance = new RunnableStatsManager();
    private final Map<Class<?>, RunnableStatsManager.ClassStat> classStats = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    public RunnableStatsManager() {
    }

    public static final RunnableStatsManager getInstance() {
        return _instance;
    }

    public void handleStats(Class<?> cl, long runTime) {
        try {
            this.lock.lock();
            RunnableStatsManager.ClassStat stat = (RunnableStatsManager.ClassStat)this.classStats.get(cl);
            if (stat == null) {
                stat = new RunnableStatsManager.ClassStat(cl);
            }

            stat.runCount++;
            stat.runTime = stat.runTime + runTime;
            if (stat.minTime > runTime) {
                stat.minTime = runTime;
            }

            if (stat.maxTime < runTime) {
                stat.maxTime = runTime;
            }
        } finally {
            this.lock.unlock();
        }

    }

    private List<RunnableStatsManager.ClassStat> getSortedClassStats() {
        List result = Collections.emptyList();

        try {
            this.lock.lock();
            result = Arrays.asList(this.classStats.values().toArray(new RunnableStatsManager.ClassStat[this.classStats.size()]));
        } finally {
            this.lock.unlock();
        }

        Collections.sort(result, new Comparator<RunnableStatsManager.ClassStat>() {
            public int compare(RunnableStatsManager.ClassStat c1, RunnableStatsManager.ClassStat c2) {
                if (c1.maxTime < c2.maxTime) {
                    return 1;
                } else {
                    return c1.maxTime == c2.maxTime ? 0 : -1;
                }
            }
        });
        return result;
    }

    public CharSequence getStats() {
        StringBuilder list = new StringBuilder();
        List<RunnableStatsManager.ClassStat> stats = this.getSortedClassStats();
        Iterator var3 = stats.iterator();

        while(var3.hasNext()) {
            RunnableStatsManager.ClassStat stat = (RunnableStatsManager.ClassStat)var3.next();
            list.append(stat.clazz.getName()).append(":\n");
            list.append("\tRun: ............ ").append(stat.runCount).append("\n");
            list.append("\tTime: ........... ").append(stat.runTime).append("\n");
            list.append("\tMin: ............ ").append(stat.minTime).append("\n");
            list.append("\tMax: ............ ").append(stat.maxTime).append("\n");
            list.append("\tAverage: ........ ").append(stat.runTime / stat.runCount).append("\n");
        }

        return list;
    }

    private class ClassStat {
        private final Class<?> clazz;
        private long runCount;
        private long runTime;
        private long minTime;
        private long maxTime;

        private ClassStat(Class<?> cl) {
            this.runCount = 0L;
            this.runTime = 0L;
            this.minTime = 9223372036854775807L;
            this.maxTime = -9223372036854775808L;
            this.clazz = cl;
            RunnableStatsManager.this.classStats.put(cl, this);
        }
    }
}
