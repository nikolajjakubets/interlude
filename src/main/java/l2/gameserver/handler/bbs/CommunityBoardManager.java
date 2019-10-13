//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.bbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.Config;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardManager {
  private static final Logger _log = LoggerFactory.getLogger(CommunityBoardManager.class);
  private static final CommunityBoardManager _instance = new CommunityBoardManager();
  private final Map<String, ICommunityBoardHandler> _handlers = new HashMap();
  private final StatsSet _properties = new StatsSet();

  public static CommunityBoardManager getInstance() {
    return _instance;
  }

  private CommunityBoardManager() {
  }

  public void registerHandler(ICommunityBoardHandler commHandler) {
    String[] var2 = commHandler.getBypassCommands();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String bypass = var2[var4];
      if (this._handlers.containsKey(bypass)) {
        _log.warn("CommunityBoard: dublicate bypass registered! First handler: " + ((ICommunityBoardHandler)this._handlers.get(bypass)).getClass().getSimpleName() + " second: " + commHandler.getClass().getSimpleName());
      }

      this._handlers.put(bypass, commHandler);
    }

  }

  public void removeHandler(ICommunityBoardHandler handler) {
    String[] var2 = handler.getBypassCommands();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String bypass = var2[var4];
      this._handlers.remove(bypass);
    }

    _log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
  }

  public ICommunityBoardHandler getCommunityHandler(String bypass) {
    if (Config.COMMUNITYBOARD_ENABLED && !this._handlers.isEmpty()) {
      Iterator var2 = this._handlers.entrySet().iterator();

      Entry entry;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        entry = (Entry)var2.next();
      } while(!bypass.contains((CharSequence)entry.getKey()));

      return (ICommunityBoardHandler)entry.getValue();
    } else {
      return null;
    }
  }

  public void setProperty(String name, String val) {
    this._properties.set(name, val);
  }

  public void setProperty(String name, int val) {
    this._properties.set(name, val);
  }

  public int getIntProperty(String name) {
    return this._properties.getInteger(name, 0);
  }
}
