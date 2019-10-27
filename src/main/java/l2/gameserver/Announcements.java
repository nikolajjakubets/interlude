//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Announcements {
  private static final Logger _log = LoggerFactory.getLogger(Announcements.class);
  private static final Announcements _instance = new Announcements();
  private List<Announcements.Announce> _announcements = new ArrayList<>();

  public static final Announcements getInstance() {
    return _instance;
  }

  private Announcements() {
    this.loadAnnouncements();
  }

  public List<Announcements.Announce> getAnnouncements() {
    return this._announcements;
  }

  public void loadAnnouncements() {
    this._announcements.clear();

    try {
      List<String> lines = Arrays.asList(FileUtils.readFileToString(new File("config/announcements.txt"), "UTF-8").split("\n"));
      Iterator var2 = lines.iterator();

      while(var2.hasNext()) {
        String line = (String)var2.next();
        if (!StringUtils.isEmpty(line)) {
          StringTokenizer token = new StringTokenizer(line, "\t");
          if (token.countTokens() > 1) {
            this.addAnnouncement(Integer.parseInt(token.nextToken()), token.nextToken(), false);
          } else {
            this.addAnnouncement(0, line, false);
          }
        }
      }
    } catch (Exception var5) {
      _log.error("Error while loading config/announcements.txt!");
    }

  }

  public void showAnnouncements(Player activeChar) {
    Iterator var2 = this._announcements.iterator();

    while(var2.hasNext()) {
      Announcements.Announce announce = (Announcements.Announce)var2.next();
      announce.showAnnounce(activeChar);
    }

  }

  public void addAnnouncement(int val, String text, boolean save) {
    Announcements.Announce announce = new Announcements.Announce(val, text);
    announce.start();
    this._announcements.add(announce);
    if (save) {
      this.saveToDisk();
    }

  }

  public void delAnnouncement(int line) {
    Announcements.Announce announce = (Announcements.Announce)this._announcements.remove(line);
    if (announce != null) {
      announce.stop();
    }

    this.saveToDisk();
  }

  private void saveToDisk() {
    try {
      File f = new File("config/announcements.txt");
      FileWriter writer = new FileWriter(f, false);
      Iterator var3 = this._announcements.iterator();

      while(var3.hasNext()) {
        Announcements.Announce announce = (Announcements.Announce)var3.next();
        writer.write(announce.getTime() + "\t" + announce.getAnnounce() + "\n");
      }

      writer.close();
    } catch (Exception var5) {
      _log.error("Error while saving config/announcements.txt!", var5);
    }

  }

  public void announceToAll(String text) {
    this.announceToAll(text, ChatType.ANNOUNCEMENT);
  }

  public static void shout(Creature activeChar, String text, ChatType type) {
    Say2 cs = new Say2(activeChar.getObjectId(), type, activeChar.getName(), text);
    int rx = MapUtils.regionX(activeChar);
    int ry = MapUtils.regionY(activeChar);
    int offset = Config.SHOUT_OFFSET;
    Iterator var7 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(true) {
      Player player;
      int tx;
      int ty;
      do {
        do {
          do {
            if (!var7.hasNext()) {
              activeChar.sendPacket(cs);
              return;
            }

            player = (Player)var7.next();
          } while(player == activeChar);
        } while(activeChar.getReflection() != player.getReflection());

        tx = MapUtils.regionX(player);
        ty = MapUtils.regionY(player);
      } while((tx < rx - offset || tx > rx + offset || ty < ry - offset || ty > ry + offset) && !activeChar.isInRangeZ(player, (long)Config.CHAT_RANGE));

      player.sendPacket(cs);
    }
  }

  public void announceToAll(String text, ChatType type) {
    Say2 cs = new Say2(0, type, "", text);
    Iterator var4 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var4.hasNext()) {
      Player player = (Player)var4.next();
      player.sendPacket(cs);
    }

  }

  public void announceByCustomMessage(String address, String[] replacements) {
    Iterator var3 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var3.hasNext()) {
      Player player = (Player)var3.next();
      this.announceToPlayerByCustomMessage(player, address, replacements);
    }

  }

  public void announceByCustomMessage(String address, String[] replacements, ChatType type) {
    Iterator var4 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var4.hasNext()) {
      Player player = (Player)var4.next();
      this.announceToPlayerByCustomMessage(player, address, replacements, type);
    }

  }

  public void announceToPlayerByCustomMessage(Player player, String address, String[] replacements) {
    CustomMessage cm = new CustomMessage(address, player, new Object[0]);
    if (replacements != null) {
      String[] var5 = replacements;
      int var6 = replacements.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        String s = var5[var7];
        cm.addString(s);
      }
    }

    player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", cm.toString()));
  }

  public void announceToPlayerByCustomMessage(Player player, String address, String[] replacements, ChatType type) {
    CustomMessage cm = new CustomMessage(address, player, new Object[0]);
    if (replacements != null) {
      String[] var6 = replacements;
      int var7 = replacements.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        String s = var6[var8];
        cm.addString(s);
      }
    }

    player.sendPacket(new Say2(0, type, "", cm.toString()));
  }

  public void announceToAll(SystemMessage sm) {
    Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.sendPacket(sm);
    }

  }

  public class Announce extends RunnableImpl {
    private Future<?> _task;
    private final int _time;
    private final String _announce;

    public Announce(int t, String announce) {
      this._time = t;
      this._announce = announce;
    }

    public void runImpl() throws Exception {
      Announcements.this.announceToAll(this._announce);
    }

    public void showAnnounce(Player player) {
      Say2 cs = new Say2(0, ChatType.ANNOUNCEMENT, player.getName(), this._announce);
      player.sendPacket(cs);
    }

    public void start() {
      if (this._time > 0) {
        this._task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, (long)this._time * 1000L, (long)this._time * 1000L);
      }

    }

    public void stop() {
      if (this._task != null) {
        this._task.cancel(false);
        this._task = null;
      }

    }

    public int getTime() {
      return this._time;
    }

    public String getAnnounce() {
      return this._announce;
    }
  }
}
