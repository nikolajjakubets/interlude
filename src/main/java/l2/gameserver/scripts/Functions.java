//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.scripts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.MultiValueSet;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.NpcSay;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.MapUtils;
import l2.gameserver.utils.NpcUtils;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Functions {
  public HardReference<Player> self = HardReferences.emptyRef();
  public HardReference<NpcInstance> npc = HardReferences.emptyRef();
  private static final String ITEM_ID_AMOUNT_LIST_DELIMITERS = ",;/";
  private static final String ITEM_ID_AMOUNT_ITEM_DELIMITERS = "-:_";

  public Functions() {
  }

  public static ScheduledFuture<?> executeTask(final Player caller, final String className, final String methodName, final Object[] args, final Map<String, Object> variables, long delay) {
    return ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        Functions.callScripts(caller, className, methodName, args, variables);
      }
    }, delay);
  }

  public static ScheduledFuture<?> executeTask(String className, String methodName, Object[] args, Map<String, Object> variables, long delay) {
    return executeTask((Player)null, className, methodName, args, variables, delay);
  }

  public static ScheduledFuture<?> executeTask(Player player, String className, String methodName, Object[] args, long delay) {
    return executeTask(player, className, methodName, args, (Map)null, delay);
  }

  public static ScheduledFuture<?> executeTask(String className, String methodName, Object[] args, long delay) {
    return executeTask((String)className, methodName, (Object[])args, (Map)null, delay);
  }

  public static Object callScripts(String className, String methodName, Object[] args) {
    return callScripts(className, methodName, args, (Map)null);
  }

  public static Object callScripts(String className, String methodName, Object[] args, Map<String, Object> variables) {
    return callScripts((Player)null, className, methodName, args, variables);
  }

  public static Object callScripts(Player player, String className, String methodName, Object[] args, Map<String, Object> variables) {
    return Scripts.getInstance().callScripts(player, className, methodName, args, variables);
  }

  public static List<Pair<ItemTemplate, Long>> parseItemIdAmountList(String itemIdAmountListText) {
    List<Pair<ItemTemplate, Long>> result = new ArrayList<>();
    StringTokenizer itemsListTokenizer = new StringTokenizer(itemIdAmountListText, ",;/");

    while(itemsListTokenizer.hasMoreTokens()) {
      String consumeItemTextTok = itemsListTokenizer.nextToken();
      StringTokenizer itemIdAmountTokenizer = new StringTokenizer(consumeItemTextTok, "-:_");
      int itemId = Integer.parseInt(itemIdAmountTokenizer.nextToken());
      ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(itemId);
      long itemCount = Long.parseLong(itemIdAmountTokenizer.nextToken());
      result.add(Pair.of(itemTemplate, itemCount));
    }

    return Collections.unmodifiableList(result);
  }

  public void show(String text, Player self) {
    show(text, self, this.getNpc());
  }

  public static void show(String text, Player self, NpcInstance npc, Object... arg) {
    if (text != null && self != null) {
      NpcHtmlMessage msg = new NpcHtmlMessage(self, npc);
      if (!text.endsWith(".html") && !text.endsWith(".htm")) {
        msg.setHtml(Strings.bbParse(text));
      } else {
        msg.setFile(text);
      }

      if (arg != null && arg.length % 2 == 0) {
        for(byte i = 0; i < arg.length; i = 2) {
          msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
        }
      }

      self.sendPacket(msg);
    }
  }

  public static void show(CustomMessage message, Player self) {
    show(message.toString(), self, (NpcInstance)null);
  }

  public static void sendMessage(String text, Player self) {
    self.sendMessage(text);
  }

  public static void sendMessage(CustomMessage message, Player self) {
    self.sendMessage(message);
  }

  public static void npcSayInRange(NpcInstance npc, String text, int range) {
    npcSayInRange(npc, range, NpcString.NONE, text);
  }

  public static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId, String... params) {
    if (npc != null) {
      NpcSay cs = new NpcSay(npc, ChatType.NPC_NORMAL, fStringId, params);
      Iterator var5 = World.getAroundPlayers(npc, range, Math.max(range / 2, 200)).iterator();

      while(var5.hasNext()) {
        Player player = (Player)var5.next();
        if (npc.getReflection() == player.getReflection()) {
          player.sendPacket(cs);
        }
      }

    }
  }

  public static void npcSay(NpcInstance npc, String text) {
    npcSayInRange(npc, text, 1500);
  }

  public static void npcSay(NpcInstance npc, NpcString npcString, String... params) {
    npcSayInRange(npc, 1500, npcString, params);
  }

  public static void npcSayInRangeCustomMessage(NpcInstance npc, int range, String address, Object... replacements) {
    if (npc != null) {
      Iterator var4 = World.getAroundPlayers(npc, range, Math.max(range / 2, 200)).iterator();

      while(var4.hasNext()) {
        Player player = (Player)var4.next();
        if (npc.getReflection() == player.getReflection()) {
          player.sendPacket(new NpcSay(npc, ChatType.NPC_NORMAL, (new CustomMessage(address, player, replacements)).toString()));
        }
      }

    }
  }

  public static void npcSayInRangeCustomMessage(NpcInstance npc, ChatType chatType, int range, String address, Object... replacements) {
    if (npc != null) {
      Iterator var5 = World.getAroundPlayers(npc, range, Math.max(range / 2, 200)).iterator();

      while(var5.hasNext()) {
        Player player = (Player)var5.next();
        if (npc.getReflection() == player.getReflection()) {
          player.sendPacket(new NpcSay(npc, chatType, (new CustomMessage(address, player, replacements)).toString()));
        }
      }

    }
  }

  public static void npcSayCustomMessage(NpcInstance npc, ChatType chatType, String address, Object... replacements) {
    npcSayInRangeCustomMessage(npc, chatType, 1500, address, replacements);
  }

  public static void npcSayCustomMessage(NpcInstance npc, String address, Object... replacements) {
    npcSayCustomMessage(npc, ChatType.NPC_NORMAL, address, replacements);
  }

  public static void npcSayToPlayer(NpcInstance npc, Player player, String text) {
    npcSayToPlayer(npc, player, NpcString.NONE, text);
  }

  public static void npcSayToPlayer(NpcInstance npc, Player player, NpcString npcString, String... params) {
    if (npc != null) {
      player.sendPacket(new NpcSay(npc, ChatType.TELL, npcString, params));
    }
  }

  public static void npcShout(NpcInstance npc, String text) {
    npcShout(npc, NpcString.NONE, text);
  }

  public static void npcShout(NpcInstance npc, NpcString npcString, String... params) {
    if (npc != null) {
      NpcSay cs = new NpcSay(npc, ChatType.SHOUT, npcString, params);
      int rx = MapUtils.regionX(npc);
      int ry = MapUtils.regionY(npc);
      int offset = Config.SHOUT_OFFSET;
      Iterator var7 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(var7.hasNext()) {
        Player player = (Player)var7.next();
        if (player.getReflection() == npc.getReflection()) {
          int tx = MapUtils.regionX(player);
          int ty = MapUtils.regionY(player);
          if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset) {
            player.sendPacket(cs);
          }
        }
      }

    }
  }

  public static void npcShoutCustomMessage(NpcInstance npc, String address, Object... replacements) {
    if (npc != null) {
      int rx = MapUtils.regionX(npc);
      int ry = MapUtils.regionY(npc);
      int offset = Config.SHOUT_OFFSET;
      Iterator var6 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(true) {
        Player player;
        int tx;
        int ty;
        do {
          do {
            if (!var6.hasNext()) {
              return;
            }

            player = (Player)var6.next();
          } while(player.getReflection() != npc.getReflection());

          tx = MapUtils.regionX(player);
          ty = MapUtils.regionY(player);
        } while((tx < rx - offset || tx > rx + offset || ty < ry - offset || ty > ry + offset) && !npc.isInRange(player, (long)Config.CHAT_RANGE));

        player.sendPacket(new NpcSay(npc, ChatType.SHOUT, (new CustomMessage(address, player, replacements)).toString()));
      }
    }
  }

  public static void npcSay(NpcInstance npc, NpcString address, ChatType type, int range, String... replacements) {
    if (npc != null) {
      Iterator var5 = World.getAroundPlayers(npc, range, Math.max(range / 2, 200)).iterator();

      while(var5.hasNext()) {
        Player player = (Player)var5.next();
        if (player.getReflection() == npc.getReflection()) {
          player.sendPacket(new NpcSay(npc, type, address, replacements));
        }
      }

    }
  }

  public static void addItem(Playable playable, int itemId, long count) {
    ItemFunctions.addItem(playable, itemId, count, true);
  }

  public static long getItemCount(Playable playable, int itemId) {
    return ItemFunctions.getItemCount(playable, itemId);
  }

  public static long removeItem(Playable playable, int itemId, long count) {
    return ItemFunctions.removeItem(playable, itemId, count, true);
  }

  public static boolean ride(Player player, int pet) {
    if (player.isMounted()) {
      player.setMount(0, 0, 0);
    }

    if (player.getPet() != null) {
      player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
      return false;
    } else {
      player.setMount(pet, 0, 0);
      return true;
    }
  }

  public static void unRide(Player player) {
    if (player.isMounted()) {
      player.setMount(0, 0, 0);
    }

  }

  public static void unSummonPet(Player player, boolean onlyPets) {
    Summon pet = player.getPet();
    if (pet != null) {
      if (pet.isPet() || !onlyPets) {
        pet.unSummon();
      }

    }
  }

  /** @deprecated */
  @Deprecated
  public static NpcInstance spawn(Location loc, int npcId) {
    return spawn(loc, npcId, ReflectionManager.DEFAULT);
  }

  /** @deprecated */
  @Deprecated
  public static NpcInstance spawn(Location loc, int npcId, Reflection reflection) {
    return NpcUtils.spawnSingle(npcId, loc, reflection, 0L);
  }

  public Player getSelf() {
    return (Player)this.self.get();
  }

  public NpcInstance getNpc() {
    return (NpcInstance)this.npc.get();
  }

  /** @deprecated */
  @Deprecated
  public static void SpawnNPCs(int npcId, int[][] locations, List<SimpleSpawner> list) {
    NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
    if (template == null) {
      System.out.println("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
      Thread.dumpStack();
    } else {
      int[][] var4 = locations;
      int var5 = locations.length;

      for(int var6 = 0; var6 < var5; ++var6) {
        int[] location = var4[var6];
        SimpleSpawner sp = new SimpleSpawner(template);
        Location loc = new Location(location[0], location[1], location[2]);
        if (location.length > 3) {
          loc.setH(location[3]);
        }

        sp.setLoc(loc);
        sp.setAmount(1);
        sp.setRespawnDelay(0);
        sp.init();
        if (list != null) {
          list.add(sp);
        }
      }

    }
  }

  public static void deSpawnNPCs(List<SimpleSpawner> list) {
    Iterator var1 = list.iterator();

    while(var1.hasNext()) {
      SimpleSpawner sp = (SimpleSpawner)var1.next();
      sp.deleteAll();
    }

    list.clear();
  }

  public static void teleportParty(Party party, Location loc, int radius) {
    Iterator var3 = party.getPartyMembers().iterator();

    while(var3.hasNext()) {
      Player partyMember = (Player)var3.next();
      partyMember.teleToLocation(Location.findPointToStay(loc, radius, partyMember.getGeoIndex()));
    }

  }

  public static boolean IsActive(String name) {
    return ServerVariables.getString(name, "off").equalsIgnoreCase("on");
  }

  public static boolean SetActive(String name, boolean active) {
    if (active == IsActive(name)) {
      return false;
    } else {
      if (active) {
        ServerVariables.set(name, "on");
      } else {
        ServerVariables.unset(name);
      }

      return true;
    }
  }

  public static boolean SimpleCheckDrop(Creature mob, Creature killer) {
    return mob != null && mob.isMonster() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
  }

  public static boolean CheckPlayerConditions(Player player) {
    if (player.isInStoreMode()) {
      player.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
      return false;
    } else if (player.isInTrade()) {
      player.sendActionFailed();
      return false;
    } else if (player.isDead()) {
      player.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
      return false;
    } else if (player.isParalyzed()) {
      player.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
      return false;
    } else if (player.isFishing()) {
      player.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
      return false;
    } else if (player.isSitting()) {
      player.sendPacket(Msg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
      return false;
    } else if (player.isActionsDisabled()) {
      player.sendActionFailed();
      return false;
    } else {
      return true;
    }
  }

  public static boolean isPvPEventStarted() {
    if ((Boolean)callScripts("events.TvT.TvT", "isRunned", new Object[0])) {
      return true;
    } else {
      return (Boolean)callScripts("events.lastHero.LastHero", "isRunned", new Object[0]);
    }
  }

  public static MultiValueSet<String> parseParams(String mapText) {
    MultiValueSet<String> result = new MultiValueSet();
    char[] chs = mapText.toCharArray();
    StringBuilder sb = new StringBuilder();
    String key = null;
    String val = null;

    for(int chIdx = 0; chIdx < chs.length; ++chIdx) {
      char ch = chs[chIdx];
      if (ch == '=' && key == null) {
        key = sb.toString();
        sb.setLength(0);
      } else if (ch == '&') {
        val = sb.toString();
        result.put(key, val);
        sb.setLength(0);
        key = null;
        val = null;
      } else {
        sb.append(ch);
      }
    }

    if (key != null) {
      val = sb.toString();
      result.put(key, val);
    }

    return result;
  }

  public static void sendDebugMessage(Player player, String message) {
    if (player.isGM()) {
      player.sendMessage(message);
    }
  }

  public static void sendSystemMail(Player receiver, String title, String body, Map<Integer, Long> items) {
    if (receiver != null && receiver.isOnline()) {
      if (title != null) {
        if (items.keySet().size() <= 8) {
          Mail mail = new Mail();
          mail.setSenderId(1);
          mail.setSenderName("Admin");
          mail.setReceiverId(receiver.getObjectId());
          mail.setReceiverName(receiver.getName());
          mail.setTopic(title);
          mail.setBody(body);
          Iterator var5 = items.entrySet().iterator();

          while(var5.hasNext()) {
            Entry<Integer, Long> itm = (Entry)var5.next();
            ItemInstance item = ItemFunctions.createItem((Integer)itm.getKey());
            item.setLocation(ItemLocation.MAIL);
            item.setCount((Long)itm.getValue());
            item.setOwnerId(receiver.getObjectId());
            item.save();
            mail.addAttachment(item);
          }

          mail.setType(SenderType.NEWS_INFORMER);
          mail.setUnread(true);
          mail.setExpireTime(2592000 + (int)(System.currentTimeMillis() / 1000L));
          mail.save();
          receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
          receiver.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
        }
      }
    }
  }

  public static final String truncateHtmlTagsSpaces(String srcHtml) {
    StringBuilder dstHtml = new StringBuilder(srcHtml.length());
    StringBuilder buff = new StringBuilder();
    boolean doBuff = false;
    int srcIdx = 0;

    for(int srcLen = srcHtml.length(); srcIdx < srcLen; ++srcIdx) {
      char srcCh = srcHtml.charAt(srcIdx);
      if (srcCh == '<') {
        doBuff = false;
        if (buff.length() > 0) {
          dstHtml.append(StringUtils.trim(buff.toString()));
          buff.setLength(0);
        }
      }

      if (!doBuff) {
        dstHtml.append(srcCh);
      } else {
        buff.append(srcCh);
      }

      if (srcCh == '>') {
        doBuff = true;
      }
    }

    if (buff.length() > 0) {
      dstHtml.append(StringUtils.trim(buff.toString()));
      buff.setLength(0);
    }

    return dstHtml.toString();
  }

  public static Map<String, ScheduledFuture<?>> ScheduleTimeStarts(Runnable r, String[] times) {
    Map<String, ScheduledFuture<?>> result = new HashMap<>();
    if (r != null && times != null && times.length != 0) {
      Calendar currentTime = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      String[] var5 = times;
      int var6 = times.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        String str_time = var5[var7];
        String[] spl_time = str_time.trim().split(":");
        int hour = Integer.parseInt(spl_time[0].trim());
        int minute = Integer.parseInt(spl_time[1].trim());
        Calendar nextStartTime = Calendar.getInstance();
        nextStartTime.set(11, hour);
        nextStartTime.set(12, minute);
        if (nextStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
          nextStartTime.add(5, 1);
        }

        long millsLeft = nextStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (millsLeft > 0L) {
          result.put(sdf.format(nextStartTime.getTime()), ThreadPoolManager.getInstance().schedule(r, millsLeft));
        }
      }

      return result;
    } else {
      return result;
    }
  }
}
