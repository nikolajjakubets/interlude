//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.htm;

import java.io.File;
import java.io.IOException;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Strings;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmCache {
  public static final int DISABLED = 0;
  public static final int LAZY = 1;
  public static final int ENABLED = 2;
  private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);
  private static final HtmCache _instance = new HtmCache();
  private Cache[] _cache;

  public static HtmCache getInstance() {
    return _instance;
  }

  private HtmCache() {
    this._cache = new Cache[Language.VALUES.length];

    for(int i = 0; i < this._cache.length; ++i) {
      this._cache[i] = CacheManager.getInstance().getCache(this.getClass().getName() + "." + Language.VALUES[i].name());
    }

  }

  public void reload() {
    this.clear();
    switch(Config.HTM_CACHE_MODE) {
      case 0:
        _log.info("HtmCache: disabled.");
        break;
      case 1:
        _log.info("HtmCache: lazy cache mode.");
        break;
      case 2:
        Language[] var1 = Language.VALUES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
          Language lang = var1[var3];
          File root = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName());
          if (!root.exists()) {
            _log.info("HtmCache: Not find html dir for lang: " + lang);
          } else {
            this.load(lang, root, root.getAbsolutePath() + "/");
          }
        }

        for(int i = 0; i < this._cache.length; ++i) {
          Cache c = this._cache[i];
          _log.info(String.format("HtmCache: parsing %d documents; lang: %s.", c.getSize(), Language.VALUES[i]));
        }
    }

  }

  private void load(Language lang, File f, String rootPath) {
    if (!f.exists()) {
      _log.info("HtmCache: dir not exists: " + f);
    } else {
      File[] files = f.listFiles();
      File[] var5 = files;
      int var6 = files.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        File file = var5[var7];
        if (file.isDirectory()) {
          this.load(lang, file, rootPath);
        } else if (file.getName().endsWith(".htm")) {
          try {
            this.putContent(lang, file, rootPath);
          } catch (IOException var10) {
            _log.info("HtmCache: file error" + var10, var10);
          }
        }
      }

    }
  }

  public void putContent(Language lang, File f, String rootPath) throws IOException {
    String content = FileUtils.readFileToString(f, "UTF-8");
    String path = f.getAbsolutePath().substring(rootPath.length()).replace("\\", "/");
    this._cache[lang.ordinal()].put(new Element(path.toLowerCase(), Strings.bbParse(content)));
  }

  public String getNotNull(String fileName, Player player) {
    Language lang = player == null ? Language.ENGLISH : player.getLanguage();
    String cache = this.getCache(fileName, lang);
    if (StringUtils.isEmpty(cache)) {
      cache = "Dialog not found: " + fileName + "; Lang: " + lang;
    }

    return cache;
  }

  public String getNullable(String fileName, Player player) {
    Language lang = player == null ? Language.ENGLISH : player.getLanguage();
    String cache = this.getCache(fileName, lang);
    return StringUtils.isEmpty(cache) ? null : cache;
  }

  private String getCache(String file, Language lang) {
    if (file == null) {
      return null;
    } else {
      String fileLower = file.toLowerCase();
      String cache = this.get(lang, fileLower);
      if (cache == null) {
        switch(Config.HTM_CACHE_MODE) {
          case 0:
            cache = this.loadDisabled(lang, file);
            if (cache == null && lang != Language.ENGLISH) {
              cache = this.loadDisabled(Language.ENGLISH, file);
            }
            break;
          case 1:
            cache = this.loadLazy(lang, file);
            if (cache == null && lang != Language.ENGLISH) {
              cache = this.loadLazy(Language.ENGLISH, file);
            }
          case 2:
        }
      }

      return cache;
    }
  }

  private String loadDisabled(Language lang, String file) {
    String cache = null;
    File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
    if (f.exists()) {
      try {
        cache = FileUtils.readFileToString(f, "UTF-8");
        cache = Strings.bbParse(cache);
      } catch (IOException var6) {
        _log.info("HtmCache: File error: " + file + " lang: " + lang);
      }
    }

    return cache;
  }

  private String loadLazy(Language lang, String file) {
    String cache = null;
    File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
    if (f.exists()) {
      try {
        cache = FileUtils.readFileToString(f, "UTF-8");
        cache = Strings.bbParse(cache);
        this._cache[lang.ordinal()].put(new Element(file, cache));
      } catch (IOException var6) {
        _log.info("HtmCache: File error: " + file + " lang: " + lang);
      }
    }

    return cache;
  }

  private String get(Language lang, String f) {
    Element element = this._cache[lang.ordinal()].get(f);
    if (element == null) {
      element = this._cache[Language.ENGLISH.ordinal()].get(f);
    }

    return element == null ? null : (String)element.getObjectValue();
  }

  public void clear() {
    for(int i = 0; i < this._cache.length; ++i) {
      this._cache[i].removeAll();
    }

  }
}
