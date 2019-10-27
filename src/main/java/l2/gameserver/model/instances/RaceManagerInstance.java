//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.MonsterRace;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MonRaceInfo;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.PlaySound.Type;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class RaceManagerInstance extends NpcInstance {
  public static final int LANES = 8;
  public static final int WINDOW_START = 0;
  private static List<RaceManagerInstance.Race> history;
  private static Set<RaceManagerInstance> managers;
  private static int _raceNumber = 1;
  private static final long SECOND = 1000L;
  private static final long MINUTE = 60000L;
  private static int minutes = 5;
  private static final int ACCEPTING_BETS = 0;
  private static final int WAITING = 1;
  private static final int STARTING_RACE = 2;
  private static final int RACE_END = 3;
  private static int state = 3;
  protected static final int[][] codes = new int[][]{{-1, 0}, {0, 15322}, {13765, -1}};
  private static boolean notInitialized = true;
  protected static MonRaceInfo packet;
  protected static int[] cost = new int[]{100, 500, 1000, 5000, 10000, 20000, 50000, 100000};

  public RaceManagerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    if (notInitialized) {
      notInitialized = false;
      _raceNumber = ServerVariables.getInt("monster_race", 1);
      history = new ArrayList<>();
      managers = new CopyOnWriteArraySet();
      ThreadPoolManager s = ThreadPoolManager.getInstance();
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(816), 0L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(817), 30000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(816), 60000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(817), 90000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(818), 120000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(818), 180000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(818), 240000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(818), 300000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(819), 360000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(819), 420000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(820), 420000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(820), 480000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(821), 510000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(822), 530000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(823), 535000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(823), 536000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(823), 537000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(823), 538000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(823), 539000L, 600000L);
      s.scheduleAtFixedRate(new RaceManagerInstance.Announcement(824), 540000L, 600000L);
    }

    managers.add(this);
  }

  public void removeKnownPlayer(Player player) {
    for(int i = 0; i < 8; ++i) {
      player.sendPacket(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
    }

  }

  public void makeAnnouncement(int type) {
    SystemMessage sm = new SystemMessage(type);
    switch(type) {
      case 816:
      case 817:
        if (state != 0) {
          state = 0;
          this.startRace();
        }

        sm.addNumber(_raceNumber);
        break;
      case 818:
      case 820:
      case 823:
        sm.addNumber(minutes);
        sm.addNumber(_raceNumber);
        --minutes;
        break;
      case 819:
        sm.addNumber(_raceNumber);
        state = 1;
        minutes = 2;
      case 821:
      case 824:
      default:
        break;
      case 822:
      case 825:
        sm.addNumber(_raceNumber);
        minutes = 5;
        break;
      case 826:
        state = 3;
        sm.addNumber(MonsterRace.getInstance().getFirstPlace() + 1);
        sm.addNumber(MonsterRace.getInstance().getSecondPlace() + 1);
    }

    this.broadcast(sm);
    if (type == 824) {
      state = 2;
      this.startRace();
      minutes = 5;
    }

  }

  protected void broadcast(L2GameServerPacket pkt) {
    Iterator var2 = managers.iterator();

    while(var2.hasNext()) {
      RaceManagerInstance manager = (RaceManagerInstance)var2.next();
      if (!manager.isDead()) {
        manager.broadcastPacketToOthers(new L2GameServerPacket[]{pkt});
      }
    }

  }

  public void sendMonsterInfo() {
    this.broadcast(packet);
  }

  private void startRace() {
    MonsterRace race = MonsterRace.getInstance();
    if (state == 2) {
      PlaySound SRace = new PlaySound("S_Race");
      this.broadcast(SRace);
      PlaySound SRace2 = new PlaySound(Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559));
      this.broadcast(SRace2);
      packet = new MonRaceInfo(codes[1][0], codes[1][1], race.getMonsters(), race.getSpeeds());
      this.sendMonsterInfo();
      ThreadPoolManager.getInstance().schedule(new RaceManagerInstance.RunRace(), 5000L);
    } else {
      race.newRace();
      race.newSpeeds();
      packet = new MonRaceInfo(codes[0][0], codes[0][1], race.getMonsters(), race.getSpeeds());
      this.sendMonsterInfo();
    }

  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (command.startsWith("BuyTicket") && state != 0) {
        player.sendPacket(Msg.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
        command = "Chat 0";
      }

      if (command.startsWith("ShowOdds") && state == 0) {
        player.sendPacket(Msg.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
        command = "Chat 0";
      }

      if (command.startsWith("BuyTicket")) {
        int val = Integer.parseInt(command.substring(10));
        if (val == 0) {
          player.setRace(0, 0);
          player.setRace(1, 0);
        }

        if (val == 10 && player.getRace(0) == 0 || val == 20 && player.getRace(0) == 0 && player.getRace(1) == 0) {
          val = 0;
        }

        this.showBuyTicket(player, val);
      } else if (command.equals("ShowOdds")) {
        this.showOdds(player);
      } else if (command.equals("ShowInfo")) {
        this.showMonsterInfo(player);
      } else if (!command.equals("calculateWin") && !command.equals("viewHistory")) {
        super.onBypassFeedback(player, command);
      }

    }
  }

  public void showOdds(Player player) {
    if (state != 0) {
      int npcId = this.getTemplate().npcId;
      NpcHtmlMessage html = new NpcHtmlMessage(player, this);
      String filename = this.getHtmlPath(npcId, 5, player);
      html.setFile(filename);

      for(int i = 0; i < 8; ++i) {
        int n = i + 1;
        String search = "Mob" + n;
        html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
      }

      html.replace("1race", String.valueOf(_raceNumber));
      player.sendPacket(html);
      player.sendActionFailed();
    }
  }

  public void showMonsterInfo(Player player) {
    int npcId = this.getTemplate().npcId;
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    String filename = this.getHtmlPath(npcId, 6, player);
    html.setFile(filename);

    for(int i = 0; i < 8; ++i) {
      int n = i + 1;
      String search = "Mob" + n;
      html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
    }

    player.sendPacket(html);
    player.sendActionFailed();
  }

  public void showBuyTicket(Player player, int val) {
    if (state == 0) {
      int npcId = this.getTemplate().npcId;
      NpcHtmlMessage html = new NpcHtmlMessage(player, this);
      String filename;
      String search;
      int ticket;
      int priceId;
      if (val < 10) {
        filename = this.getHtmlPath(npcId, 2, player);
        html.setFile(filename);

        for(ticket = 0; ticket < 8; ++ticket) {
          priceId = ticket + 1;
          search = "Mob" + priceId;
          html.replace(search, MonsterRace.getInstance().getMonsters()[ticket].getTemplate().name);
        }

        search = "No1";
        if (val == 0) {
          html.replace(search, "");
        } else {
          html.replace(search, "" + val);
          player.setRace(0, val);
        }
      } else {
        String replace;
        if (val < 20) {
          if (player.getRace(0) == 0) {
            return;
          }

          filename = this.getHtmlPath(npcId, 3, player);
          html.setFile(filename);
          html.replace("0place", "" + player.getRace(0));
          search = "Mob1";
          replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
          html.replace(search, replace);
          search = "0adena";
          if (val == 10) {
            html.replace(search, "");
          } else {
            html.replace(search, "" + cost[val - 11]);
            player.setRace(1, val - 10);
          }
        } else {
          if (val != 20) {
            if (player.getRace(0) != 0 && player.getRace(1) != 0) {
              if (player.getAdena() < (long)cost[player.getRace(1) - 1]) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
              }

              ticket = player.getRace(0);
              priceId = player.getRace(1);
              player.setRace(0, 0);
              player.setRace(1, 0);
              player.reduceAdena((long)cost[priceId - 1], true);
              SystemMessage sm = new SystemMessage(371);
              sm.addNumber(_raceNumber);
              sm.addItemName(4443);
              player.sendPacket(sm);
              ItemInstance item = ItemFunctions.createItem(4443);
              item.setEnchantLevel(_raceNumber);
              item.setBlessed(ticket);
              item.setDamaged(cost[priceId - 1] / 100);
              player.getInventory().addItem(item);
              return;
            }

            return;
          }

          if (player.getRace(0) == 0 || player.getRace(1) == 0) {
            return;
          }

          filename = this.getHtmlPath(npcId, 4, player);
          html.setFile(filename);
          html.replace("0place", "" + player.getRace(0));
          search = "Mob1";
          replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
          html.replace(search, replace);
          search = "0adena";
          ticket = cost[player.getRace(1) - 1];
          html.replace(search, "" + ticket);
          search = "0tax";
          int tax = 0;
          html.replace(search, "" + tax);
          search = "0total";
          int total = ticket + tax;
          html.replace(search, "" + total);
        }
      }

      html.replace("1race", String.valueOf(_raceNumber));
      player.sendPacket(html);
      player.sendActionFailed();
    }
  }

  public MonRaceInfo getPacket() {
    return packet;
  }

  class RunEnd extends RunnableImpl {
    RunEnd() {
    }

    public void runImpl() throws Exception {
      RaceManagerInstance.this.makeAnnouncement(826);
      RaceManagerInstance.this.makeAnnouncement(825);
      RaceManagerInstance._raceNumber++;
      ServerVariables.set("monster_race", RaceManagerInstance._raceNumber);

      for(int i = 0; i < 8; ++i) {
        RaceManagerInstance.this.broadcast(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
      }

    }
  }

  class RunRace extends RunnableImpl {
    RunRace() {
    }

    public void runImpl() throws Exception {
      RaceManagerInstance.packet = new MonRaceInfo(RaceManagerInstance.codes[2][0], RaceManagerInstance.codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
      RaceManagerInstance.this.sendMonsterInfo();
      ThreadPoolManager.getInstance().schedule(RaceManagerInstance.this.new RunEnd(), 30000L);
    }
  }

  public class Race {
    private RaceManagerInstance.Race.Info[] info;

    public Race(RaceManagerInstance.Race.Info[] info) {
      this.info = info;
    }

    public RaceManagerInstance.Race.Info getLaneInfo(int lane) {
      return this.info[lane];
    }

    public class Info {
      private int id;
      private int place;
      private int odds;
      private int payout;

      public Info(int id, int place, int odds, int payout) {
        this.id = id;
        this.place = place;
        this.odds = odds;
        this.payout = payout;
      }

      public int getId() {
        return this.id;
      }

      public int getOdds() {
        return this.odds;
      }

      public int getPayout() {
        return this.payout;
      }

      public int getPlace() {
        return this.place;
      }
    }
  }

  class Announcement extends RunnableImpl {
    private int type;

    public Announcement(int type) {
      this.type = type;
    }

    public void runImpl() throws Exception {
      RaceManagerInstance.this.makeAnnouncement(this.type);
    }
  }
}
