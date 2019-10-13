//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Language;

public final class StringHolder extends AbstractHolder {
  private static final StringHolder _instance = new StringHolder();
  private Map<Language, Map<String, String>> _strings = new HashMap();

  public static StringHolder getInstance() {
    return _instance;
  }

  private StringHolder() {
  }

  public String getNullable(Player player, String name) {
    Language lang = player == null ? Language.ENGLISH : player.getLanguage();
    return this.get(lang, name);
  }

  public String getNotNull(Player player, String name) {
    Language lang = player == null ? Language.ENGLISH : player.getLanguage();
    String text = this.get(lang, name);
    if (text == null && player != null) {
      text = "Not find string: " + name + "; for lang: " + lang;
      ((Map)this._strings.get(lang)).put(name, text);
    }

    return text;
  }

  public String getNotNull(Language lang, String name) {
    String text = this.get(lang, name);
    if (text == null && lang != null) {
      text = "Not find string: " + name + "; for lang: " + lang;
      ((Map)this._strings.get(lang)).put(name, text);
    }

    return text;
  }

  private String get(Language lang, String address) {
    Map<String, String> strings = (Map)this._strings.get(lang);
    return (String)strings.get(address);
  }

  public void load() {
    Language[] var1 = Language.VALUES;
    int var2 = var1.length;

    label134:
    for(int var3 = 0; var3 < var2; ++var3) {
      Language lang = var1[var3];
      this._strings.put(lang, new HashMap());
      File f = new File(Config.DATAPACK_ROOT, "data/string/strings_" + lang.getShortName() + ".properties");
      if (!f.exists()) {
        this.warn("Not find file: " + f.getAbsolutePath());
      } else {
        LineNumberReader reader = null;

        try {
          reader = new LineNumberReader(new FileReader(f));
          String line = null;

          while(true) {
            while(true) {
              do {
                if ((line = reader.readLine()) == null) {
                  continue label134;
                }
              } while(line.startsWith("#"));

              StringTokenizer token = new StringTokenizer(line, "=");
              if (token.countTokens() < 2) {
                this.error("Error on line: " + line + "; file: " + f.getName());
              } else {
                String name = token.nextToken();

                String value;
                for(value = token.nextToken(); token.hasMoreTokens(); value = value + "=" + token.nextToken()) {
                }

                Map<String, String> strings = (Map)this._strings.get(lang);
                strings.put(name, value);
              }
            }
          }
        } catch (Exception var20) {
          this.error("Exception: " + var20, var20);
        } finally {
          try {
            reader.close();
          } catch (Exception var19) {
          }

        }
      }
    }

    this.log();
  }

  public void reload() {
    this.clear();
    this.load();
  }

  public void log() {
    Iterator var1 = this._strings.entrySet().iterator();

    while(var1.hasNext()) {
      Entry<Language, Map<String, String>> entry = (Entry)var1.next();
      this.info("load strings: " + ((Map)entry.getValue()).size() + " for lang: " + entry.getKey());
    }

  }

  public int size() {
    return 0;
  }

  public void clear() {
    this._strings.clear();
  }
}
