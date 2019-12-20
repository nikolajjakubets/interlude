package l2.commons.lang;

import java.lang.management.*;
import java.util.Iterator;

public final class StatsUtils {
    private static final MemoryMXBean memMXbean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadMXbean = ManagementFactory.getThreadMXBean();

    public StatsUtils() {
    }

    public static long getMemUsed() {
        return memMXbean.getHeapMemoryUsage().getUsed();
    }

    public static String getMemUsedMb() {
        return getMemUsed() / 1048576L + " Mb";
    }

    public static long getMemMax() {
        return memMXbean.getHeapMemoryUsage().getMax();
    }

    public static String getMemMaxMb() {
        return getMemMax() / 1048576L + " Mb";
    }

    public static long getMemFree() {
        MemoryUsage heapMemoryUsage = memMXbean.getHeapMemoryUsage();
        return heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
    }

    public static String getMemFreeMb() {
        return getMemFree() / 1048576L + " Mb";
    }

    public static CharSequence getMemUsage() {
        double maxMem = (double)memMXbean.getHeapMemoryUsage().getMax() / 1024.0D;
        double allocatedMem = (double)memMXbean.getHeapMemoryUsage().getCommitted() / 1024.0D;
        double usedMem = (double)memMXbean.getHeapMemoryUsage().getUsed() / 1024.0D;
        double nonAllocatedMem = maxMem - allocatedMem;
        double cachedMem = allocatedMem - usedMem;
        double useableMem = maxMem - usedMem;
        StringBuilder list = new StringBuilder();
        list.append("AllowedMemory: ........... ").append((int)maxMem).append(" KB").append("\n");
        list.append("     Allocated: .......... ").append((int)allocatedMem).append(" KB (").append((double)Math.round(allocatedMem / maxMem * 1000000.0D) / 10000.0D).append("%)").append("\n");
        list.append("     Non-Allocated: ...... ").append((int)nonAllocatedMem).append(" KB (").append((double)Math.round(nonAllocatedMem / maxMem * 1000000.0D) / 10000.0D).append("%)").append("\n");
        list.append("AllocatedMemory: ......... ").append((int)allocatedMem).append(" KB").append("\n");
        list.append("     Used: ............... ").append((int)usedMem).append(" KB (").append((double)Math.round(usedMem / maxMem * 1000000.0D) / 10000.0D).append("%)").append("\n");
        list.append("     Unused (cached): .... ").append((int)cachedMem).append(" KB (").append((double)Math.round(cachedMem / maxMem * 1000000.0D) / 10000.0D).append("%)").append("\n");
        list.append("UseableMemory: ........... ").append((int)useableMem).append(" KB (").append((double)Math.round(useableMem / maxMem * 1000000.0D) / 10000.0D).append("%)").append("\n");
        return list;
    }

    public static CharSequence getThreadStats() {
        StringBuilder list = new StringBuilder();
        int threadCount = threadMXbean.getThreadCount();
        int daemonCount = threadMXbean.getThreadCount();
        int nonDaemonCount = threadCount - daemonCount;
        int peakCount = threadMXbean.getPeakThreadCount();
        long totalCount = threadMXbean.getTotalStartedThreadCount();
        list.append("Live: .................... ").append(threadCount).append(" threads").append("\n");
        list.append("     Non-Daemon: ......... ").append(nonDaemonCount).append(" threads").append("\n");
        list.append("     Daemon: ............. ").append(daemonCount).append(" threads").append("\n");
        list.append("Peak: .................... ").append(peakCount).append(" threads").append("\n");
        list.append("Total started: ........... ").append(totalCount).append(" threads").append("\n");
        list.append("=================================================").append("\n");
        return list;
    }

    public static CharSequence getThreadStats(boolean lockedMonitors, boolean lockedSynchronizers, boolean stackTrace) {
        StringBuilder list = new StringBuilder();
        ThreadInfo[] var4 = threadMXbean.dumpAllThreads(lockedMonitors, lockedSynchronizers);
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ThreadInfo info = var4[var6];
            list.append("Thread #").append(info.getThreadId()).append(" (").append(info.getThreadName()).append(")").append("\n");
            list.append("=================================================\n");
            list.append("\tgetThreadState: ...... ").append(info.getThreadState()).append("\n");
            MonitorInfo[] var8 = info.getLockedMonitors();
            int var9 = var8.length;

            int var10;
            for(var10 = 0; var10 < var9; ++var10) {
                MonitorInfo monitorInfo = var8[var10];
                list.append("\tLocked monitor: ....... ").append(monitorInfo).append("\n");
                list.append("\t\t[").append(monitorInfo.getLockedStackDepth()).append(".]: at ").append(monitorInfo.getLockedStackFrame()).append("\n");
            }

            LockInfo[] var12 = info.getLockedSynchronizers();
            var9 = var12.length;

            for(var10 = 0; var10 < var9; ++var10) {
                LockInfo lockInfo = var12[var10];
                list.append("\tLocked synchronizer: ...").append(lockInfo).append("\n");
            }

            if (stackTrace) {
                list.append("\tgetStackTace: ..........\n");
                StackTraceElement[] var13 = info.getStackTrace();
                var9 = var13.length;

                for(var10 = 0; var10 < var9; ++var10) {
                    StackTraceElement trace = var13[var10];
                    list.append("\t\tat ").append(trace).append("\n");
                }
            }

            list.append("=================================================\n");
        }

        return list;
    }

    public static CharSequence getGCStats() {
        StringBuilder list = new StringBuilder();
        Iterator var1 = ManagementFactory.getGarbageCollectorMXBeans().iterator();

        while(var1.hasNext()) {
            GarbageCollectorMXBean gcBean = (GarbageCollectorMXBean)var1.next();
            list.append("GarbageCollector (").append(gcBean.getName()).append(")\n");
            list.append("=================================================\n");
            list.append("getCollectionCount: ..... ").append(gcBean.getCollectionCount()).append("\n");
            list.append("getCollectionTime: ...... ").append(gcBean.getCollectionTime()).append(" ms").append("\n");
            list.append("=================================================\n");
        }

        return list;
    }
}
