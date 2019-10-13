//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.util.List;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Log;

public class BypassManager {
  private static final String[] SIMBLE_BEGININGS = new String[]{"_mrsl", "_diary", "_match", "manor_menu_select", "_match", "_olympiad"};
  private static final String[] SIMBLE_BBS_BEGININGS = new String[]{"_bbshome", "_bbsgetfav", "_bbslink", "_bbsloc", "_bbsclan", "_bbsmemo", "_maillist_0_1_0_", "_friendlist_0_", "_bbsaddfav"};

  public BypassManager() {
  }

  private static boolean isSimpleBypass(String bypass, boolean bbs) {
    String[] beginings = bbs ? SIMBLE_BBS_BEGININGS : SIMBLE_BEGININGS;
    String[] var3 = beginings;
    int var4 = beginings.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String begining = var3[var5];
      if (bypass.startsWith(begining)) {
        return true;
      }
    }

    return false;
  }

  public static BypassManager.BypassType getBypassType(String bypass) {
    switch(bypass.charAt(0)) {
      case '0':
        return BypassManager.BypassType.ENCODED;
      case '1':
        return BypassManager.BypassType.ENCODED_BBS;
      default:
        if (isSimpleBypass(bypass, false)) {
          return BypassManager.BypassType.SIMPLE;
        } else {
          return isSimpleBypass(bypass, true) && CommunityBoardManager.getInstance().getCommunityHandler(bypass) != null ? BypassManager.BypassType.SIMPLE_BBS : BypassManager.BypassType.SIMPLE_DIRECT;
        }
    }
  }

  public static String encode(String html_, List<String> bypassStorage, boolean bbs) {
    StringBuffer sb = new StringBuffer();
    char[] html = html_.toCharArray();
    int nextAppendIdx = 0;

    for(int i = 0; i + 7 < html.length; ++i) {
      int bypassPos = 0;
      int bypassLen = 0;
      int j;
      if (html[i] == '"' && Character.toLowerCase(html[i + 1]) == 'b' && Character.toLowerCase(html[i + 2]) == 'y' && Character.toLowerCase(html[i + 3]) == 'p' && Character.toLowerCase(html[i + 4]) == 'a' && Character.toLowerCase(html[i + 5]) == 's' && Character.toLowerCase(html[i + 6]) == 's' && Character.isWhitespace(html[i + 7])) {
        for(j = 8; j + i < html.length && html[j + i] != '"'; ++j) {
        }

        if (j + i == html.length) {
          bypassPos = 0;
          bypassLen = 0;
        } else {
          bypassPos = i + 1;
          bypassLen = j - 1;
        }
      }

      if (bypassLen > 0) {
        for(j = 7; j < bypassLen && Character.isWhitespace(html[bypassPos + j]); ++j) {
        }

        boolean haveMinusH = html[bypassPos + j] == '-' && (html[bypassPos + j + 1] == 'h' || html[bypassPos + j + 1] == 'H');
        if (haveMinusH) {
          for(j += 2; j < bypassLen && (html[bypassPos + j] == ' ' || html[bypassPos + j] == '\t'); ++j) {
          }
        }

        String bypass = new String(html, bypassPos + j, bypassLen - j);
        String code = bypass;
        String params = "";
        int k = bypass.indexOf(" $");
        boolean use_params = k >= 0;
        if (use_params) {
          code = bypass.substring(0, k);
          params = bypass.substring(k);
        }

        sb.append(html, nextAppendIdx, bypassPos - nextAppendIdx);
        nextAppendIdx = bypassPos + bypassLen;
        sb.append("bypass ");
        if (haveMinusH) {
          sb.append("-h ");
        }

        sb.append((char)(bbs ? '1' : '0'));
        synchronized(bypassStorage) {
          sb.append(Integer.toHexString(bypassStorage.size()));
          sb.append(params);
          bypassStorage.add(code);
        }
      }
    }

    sb.append(html, nextAppendIdx, html.length - nextAppendIdx);
    return sb.toString();
  }

  public static BypassManager.DecodedBypass decode(String bypass, List<String> bypassStorage, boolean bbs, GameClient client) {
    synchronized(bypassStorage) {
      String[] bypass_parsed = bypass.split(" ");
      int idx = Integer.parseInt(bypass_parsed[0].substring(1), 16);

      String bp;
      try {
        bp = (String)bypassStorage.get(idx);
      } catch (Exception var11) {
        bp = null;
      }

      if (bp == null) {
        Log.add("Can't decode bypass (bypass not exists): " + (bbs ? "[bbs] " : "") + bypass + " / Client: " + client.toString() + " / Npc: " + (client.getActiveChar() != null && client.getActiveChar().getLastNpc() != null ? client.getActiveChar().getLastNpc().getName() : "null"), "debug_bypass");
        return null;
      } else {
        BypassManager.DecodedBypass result = null;
        result = new BypassManager.DecodedBypass(bp, bbs);

        for(int i = 1; i < bypass_parsed.length; ++i) {
          result.bypass = result.bypass + " " + bypass_parsed[i];
        }

        result.trim();
        return result;
      }
    }
  }

  public static class DecodedBypass {
    public String bypass;
    public boolean bbs;

    public DecodedBypass(String _bypass, boolean _bbs) {
      this.bypass = _bypass;
      this.bbs = _bbs;
    }

    public BypassManager.DecodedBypass trim() {
      this.bypass = this.bypass.trim();
      return this;
    }
  }

  public static enum BypassType {
    ENCODED,
    ENCODED_BBS,
    SIMPLE,
    SIMPLE_BBS,
    SIMPLE_DIRECT;

    private BypassType() {
    }
  }
}
