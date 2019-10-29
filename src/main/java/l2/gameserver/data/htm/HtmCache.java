//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.htm;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmCache {
  public static final int DISABLED = 0; // все диалоги кешируются при загрузке
  // сервера
  public static final int LAZY = 1; // диалоги кешируются по мере обращения
  public static final int ENABLED = 2; // кеширование отключено (только для тестирования)
  private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);
  private static final HtmCache _instance = new HtmCache();
  List<Cache<String, String>> _cache;
//  private Cache[] _cache  = new Cache[Language.VALUES.length];

  public static HtmCache getInstance() {
    return _instance;
  }

  private HtmCache() {
    _cache = new ArrayList<>();

    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
      .withCache("preConfigured",
        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
          ResourcePoolsBuilder.heap(100))
          .build())
      .build(true);

    for (int i = 0; i < Language.VALUES.length; ++i) {
      String cacheName = this.getClass().getName() + "." + Language.VALUES[i].name();
      Cache<String, String> cache = cacheManager.createCache(cacheName,
        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
          ResourcePoolsBuilder.heap(1000)).build());
      _cache.add(cache);
    }
  }

  public void reload() {
    this.clear();
    switch (Config.HTM_CACHE_MODE) {
      case 0:
        _log.info("HtmCache: disabled.");
        break;
      case 1:
        _log.info("HtmCache: lazy cache mode.");
        break;
      case 2:
        Language[] var1 = Language.VALUES;

        for (Language lang : var1) {
          File root = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName());
          if (!root.exists()) {
            _log.info("HtmCache: Not find html dir for lang: " + lang);
          } else {
            this.load(lang, root, root.getAbsolutePath() + "/");
          }
        }
        for (int i = 0; i < this._cache.size(); ++i) {
          Cache c = _cache.get(i);
          _log.info(String.format("HtmCache: parsing %d documents; lang: %s.", c.hashCode(), Language.VALUES[i]));
        }
    }

  }

  private void load(Language lang, File f, String rootPath) {
    if (!f.exists()) {
      _log.info("HtmCache: dir not exists: " + f);
    } else {
      File[] files = f.listFiles();

      for (File file : files) {
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
    this._cache.get(lang.ordinal()).put(path.toLowerCase(), Strings.bbParse(content));
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
        switch (Config.HTM_CACHE_MODE) {
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
        this._cache.get(lang.ordinal()).put(file, cache);
      } catch (IOException var6) {
        _log.info("HtmCache: File error: " + file + " lang: " + lang);
      }
    }

    return cache;
  }

  private String get(Language lang, String f) {
    String value = this._cache.get(lang.ordinal()).get(f);
    if (value == null) {
      value = this._cache.get(Language.ENGLISH.ordinal()).get(f);
    }
    return value;
  }

  public void clear() {
    this._cache.forEach(Cache::clear);

  }
}
