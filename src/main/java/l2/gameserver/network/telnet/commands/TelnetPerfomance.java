//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.telnet.commands;

import com.sun.management.HotSpotDiagnosticMXBean;
import l2.commons.lang.StatsUtils;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.threading.RunnableStatsManager;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.geodata.PathFindBuffers;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.taskmanager.AiTaskManager;
import l2.gameserver.taskmanager.EffectTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.management.MBeanServer;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class TelnetPerfomance implements TelnetCommandHolder {
  private Set<TelnetCommand> _commands = new LinkedHashSet();

  public TelnetPerfomance() {
    this._commands.add(new TelnetCommand("pool", new String[]{"p"}) {
      public String getUsage() {
        return "pool [dump]";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        if (args.length != 0 && !args[0].isEmpty()) {
          if (!args[0].equals("dump") && !args[0].equals("d")) {
            return null;
          }

          try {
            (new File("stats")).mkdir();
            FileUtils.writeStringToFile(new File("stats/RunnableStats-" + (new SimpleDateFormat("MMddHHmmss")).format(System.currentTimeMillis()) + ".txt"), RunnableStatsManager.getInstance().getStats().toString());
            sb.append("Runnable stats saved.\n");
          } catch (IOException e) {
            log.error("addTestTask: eMessage={}, eClass={}, eCause={}", e.getMessage(), e.getClass(), this.getClass().getSimpleName());
            sb.append("Exception: ").append(e.getMessage()).append("!\n");
          }
        } else {
          sb.append(ThreadPoolManager.getInstance().getStats());
        }

        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("mem", new String[]{"m"}) {
      public String getUsage() {
        return "mem";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(StatsUtils.getMemUsage());
        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("heap") {
      public String getUsage() {
        return "heap [dump] <live>";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        if (args.length != 0 && !args[0].isEmpty()) {
          if (!args[0].equals("dump") && !args[0].equals("d")) {
            return null;
          } else {
            try {
              boolean live = args.length == 2 && !args[1].isEmpty() && (args[1].equals("live") || args[1].equals("l"));
              (new File("dumps")).mkdir();
              String filename = "dumps/HeapDump" + (live ? "Live" : "") + "-" + (new SimpleDateFormat("MMddHHmmss")).format(System.currentTimeMillis()) + ".hprof";
              MBeanServer server = ManagementFactory.getPlatformMBeanServer();
              HotSpotDiagnosticMXBean bean = (HotSpotDiagnosticMXBean)ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
              bean.dumpHeap(filename, live);
              sb.append("Heap dumped.\n");
            } catch (IOException e) {
              log.error("addTestTask: eMessage={}, eClass={}, eCause={}", e.getMessage(), e.getClass(), this.getClass().getSimpleName());
              sb.append("Exception: " + e.getMessage() + "!\n");
            }

            return sb.toString();
          }
        } else {
          return null;
        }
      }
    });
    this._commands.add(new TelnetCommand("threads", new String[]{"t"}) {
      public String getUsage() {
        return "threads [dump]";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        if (args.length != 0 && !args[0].isEmpty()) {
          if (!args[0].equals("dump") && !args[0].equals("d")) {
            return null;
          }

          try {
            (new File("stats")).mkdir();
            FileUtils.writeStringToFile(new File("stats/ThreadsDump-" + (new SimpleDateFormat("MMddHHmmss")).format(System.currentTimeMillis()) + ".txt"), StatsUtils.getThreadStats(true, true, true).toString());
            sb.append("Threads stats saved.\n");
          } catch (IOException e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), e.getClass(), this.getClass().getSimpleName());

            sb.append("Exception: ").append(e.getMessage()).append("!\n");
          }
        } else {
          sb.append(StatsUtils.getThreadStats());
        }

        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("gc") {
      public String getUsage() {
        return "gc";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(StatsUtils.getGCStats());
        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("net", new String[]{"ns"}) {
      public String getUsage() {
        return "net";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(SelectorThread.getStats());
        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("pathfind", new String[]{"pfs"}) {
      public String getUsage() {
        return "pathfind";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(PathFindBuffers.getStats());
        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("aistats", new String[]{"as"}) {
      public String getUsage() {
        return "aistats";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < Config.AI_TASK_MANAGER_COUNT; ++i) {
          sb.append("AiTaskManager #").append(i + 1).append("\n");
          sb.append("=================================================\n");
          sb.append(AiTaskManager.getInstance().getStats(i));
          sb.append("=================================================\n");
        }

        return sb.toString();
      }
    });
    this._commands.add(new TelnetCommand("effectstats", new String[]{"es"}) {
      public String getUsage() {
        return "effectstats";
      }

      public String handle(String[] args) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < Config.EFFECT_TASK_MANAGER_COUNT; ++i) {
          sb.append("EffectTaskManager #").append(i + 1).append("\n");
          sb.append("=================================================\n");
          sb.append(EffectTaskManager.getInstance().getStats(i));
          sb.append("=================================================\n");
        }

        return sb.toString();
      }
    });
  }

  public Set<TelnetCommand> getCommands() {
    return this._commands;
  }
}
