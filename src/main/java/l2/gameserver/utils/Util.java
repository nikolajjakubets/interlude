//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
public class Util {
  static final String PATTERN = "0.0000000000E00";
  static final DecimalFormat df;
  private static NumberFormat adenaFormatter;
  private static Pattern _pattern;

  public Util() {
  }

  public static boolean isMatchingRegexp(String text, String template) {
    Pattern pattern = null;

    try {
      pattern = Pattern.compile(template);
    } catch (PatternSyntaxException e) {
      log.error("Exception: eMessage={}, eClass={},", e.getMessage(), e.getClass());
      e.printStackTrace();
    }

    if (pattern == null) {
      return false;
    } else {
      Matcher regexp = pattern.matcher(text);
      return regexp.matches();
    }
  }

  public static String formatDouble(double x, String nanString, boolean forceExponents) {
    if (Double.isNaN(x)) {
      return nanString;
    } else if (forceExponents) {
      return df.format(x);
    } else {
      return (double)((long)x) == x ? String.valueOf((long)x) : String.valueOf(x);
    }
  }

  public static String formatAdena(long amount) {
    return adenaFormatter.format(amount);
  }

  public static String formatTime(int time) {
    if (time == 0) {
      return "now";
    } else {
      time = Math.abs(time);
      String ret = "";
      long numDays = (long)(time / 86400);
      time = (int)((long)time - numDays * 86400L);
      long numHours = (long)(time / 3600);
      time = (int)((long)time - numHours * 3600L);
      long numMins = (long)(time / 60);
      time = (int)((long)time - numMins * 60L);
      long numSeconds = (long)time;
      if (numDays > 0L) {
        ret = ret + numDays + "d ";
      }

      if (numHours > 0L) {
        ret = ret + numHours + "h ";
      }

      if (numMins > 0L) {
        ret = ret + numMins + "m ";
      }

      if (numSeconds > 0L) {
        ret = ret + numSeconds + "s";
      }

      return ret.trim();
    }
  }

  public static String getCfgDirect() {
    StringBuilder result = new StringBuilder();
    result.append("Auth: ").append(Config.GAME_SERVER_LOGIN_HOST).append('\n');
    result.append("Game:\n");

    try {
      Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

      while(interfaces.hasMoreElements()) {
        NetworkInterface iface = (NetworkInterface)interfaces.nextElement();
        if (!iface.isLoopback() && iface.isUp()) {
          Enumeration addresses = iface.getInetAddresses();

          while(addresses.hasMoreElements()) {
            InetAddress addr = (InetAddress)addresses.nextElement();
            String tmp = addr.getHostAddress();
            result.append(" ").append(tmp).append('\n');
          }
        }
      }
    } catch (SocketException e) {
      log.error("Exception: eMessage={}, eClass={}", e.getMessage(), e.getClass());
      return "none";
    }

    return result.toString();
  }

  public static long rollDrop(long min, long max, double calcChance, boolean rate) {
    if (calcChance > 0.0D && min > 0L && max > 0L) {
      int dropmult = 1;
      if (rate) {
        calcChance *= Config.RATE_DROP_ITEMS;
      }

      if (calcChance > 1000000.0D) {
        if (calcChance % 1000000.0D == 0.0D) {
          dropmult = (int)(calcChance / 1000000.0D);
        } else {
          dropmult = (int)Math.ceil(calcChance / 1000000.0D);
          calcChance /= (double)dropmult;
        }
      }

      return Rnd.chance(calcChance / 10000.0D) ? Rnd.get(min * (long)dropmult, max * (long)dropmult) : 0L;
    } else {
      return 0L;
    }
  }

  public static int packInt(int[] a, int bits) throws Exception {
    int m = 32 / bits;
    if (a.length > m) {
      throw new Exception("Overflow");
    } else {
      int result = 0;
      int mval = (int)Math.pow(2.0D, (double)bits);

      for(int i = 0; i < m; ++i) {
        result <<= bits;
        int next;
        if (a.length > i) {
          next = a[i];
          if (next >= mval || next < 0) {
            throw new Exception("Overload, value is out of range");
          }
        } else {
          next = 0;
        }

        result += next;
      }

      return result;
    }
  }

  public static long packLong(int[] a, int bits) throws Exception {
    int m = 64 / bits;
    if (a.length > m) {
      throw new Exception("Overflow");
    } else {
      long result = 0L;
      int mval = (int)Math.pow(2.0D, (double)bits);

      for(int i = 0; i < m; ++i) {
        result <<= bits;
        int next;
        if (a.length > i) {
          next = a[i];
          if (next >= mval || next < 0) {
            throw new Exception("Overload, value is out of range");
          }
        } else {
          next = 0;
        }

        result += (long)next;
      }

      return result;
    }
  }

  public static int[] unpackInt(int a, int bits) {
    int m = 32 / bits;
    int mval = (int)Math.pow(2.0D, (double)bits);
    int[] result = new int[m];

    for(int i = m; i > 0; --i) {
      int next = a;
      a >>= bits;
      result[i - 1] = next - a * mval;
    }

    return result;
  }

  public static int[] unpackLong(long a, int bits) {
    int m = 64 / bits;
    int mval = (int)Math.pow(2.0D, (double)bits);
    int[] result = new int[m];

    for(int i = m; i > 0; --i) {
      long next = a;
      a >>= bits;
      result[i - 1] = (int)(next - a * (long)mval);
    }

    return result;
  }

  public static String joinStrings(String glueStr, String[] strings, int startIdx, int maxCount) {
    return Strings.joinStrings(glueStr, strings, startIdx, maxCount);
  }

  public static String joinStrings(String glueStr, String[] strings, int startIdx) {
    return Strings.joinStrings(glueStr, strings, startIdx, -1);
  }

  public static boolean isNumber(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), null, e.getCause());
      return false;
    }
  }

  public static String dumpObject(Object o, boolean simpleTypes, boolean parentFields, boolean ignoreStatics) {
    Class<?> cls = o.getClass();
    String result = "[" + (simpleTypes ? cls.getSimpleName() : cls.getName()) + "\n";
    ArrayList fields = new ArrayList<>();

    while(cls != null) {
      Field[] var10 = cls.getDeclaredFields();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
        Field fld = var10[var12];
        if (!fields.contains(fld) && (!ignoreStatics || !Modifier.isStatic(fld.getModifiers()))) {
          fields.add(fld);
        }
      }

      cls = cls.getSuperclass();
      if (!parentFields) {
        break;
      }
    }

    String val;
    String type;
    Field fld;
    for(Iterator var15 = fields.iterator(); var15.hasNext(); result = result + String.format("\t%s [%s] = %s;\n", fld.getName(), type, val)) {
      fld = (Field)var15.next();
      fld.setAccessible(true);

      try {
        Object fldObj = fld.get(o);
        if (fldObj == null) {
          val = "NULL";
        } else {
          val = fldObj.toString();
        }
      } catch (Throwable e) {
        log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), "this.getClass().getSimpleName()", e.getCause());
        e.printStackTrace();
        val = "<ERROR>";
      }

      type = simpleTypes ? fld.getType().getSimpleName() : fld.getType().toString();
    }

    result = result + "]\n";
    return result;
  }

  public static HashMap<Integer, String> parseTemplate(String html) {
    Matcher m = _pattern.matcher(html);

    HashMap tpls;
    for(tpls = new HashMap<>(); m.find(); html = html.replace(m.group(0), "")) {
      tpls.put(Integer.parseInt(m.group(1)), m.group(2));
    }

    tpls.put(0, html);
    return tpls;
  }

  public static int fibonacci(int n) {
    int x = 0;
    int y = 1;

    for(int i = 0; i < n; ++i) {
      int z = x;
      x = y;
      y += z;
    }

    return x;
  }

  public static double padovan(int n) {
    return n != 0 && n != 1 && n != 2 ? padovan(n - 2) + padovan(n - 3) : 1.0D;
  }

  public static Player[] GetPlayersFromStoredIds(long[] sids) {
    ArrayList<Player> result = new ArrayList<>();
    long[] var2 = sids;
    int var3 = sids.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      long sid = var2[var4];
      Player player = GameObjectsStorage.getAsPlayer(sid);
      if (player != null) {
        result.add(player);
      }
    }

    return (Player[])result.toArray(new Player[result.size()]);
  }

  static {
    adenaFormatter = NumberFormat.getIntegerInstance(Locale.FRANCE);
    df = (DecimalFormat)NumberFormat.getNumberInstance(Locale.ENGLISH);
    df.applyPattern("0.0000000000E00");
    df.setPositivePrefix("+");
    _pattern = Pattern.compile("<!--TEMPLET(\\d+)(.*?)TEMPLET-->", 32);
  }
}
