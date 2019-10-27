//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.text.StrTable;
import l2.gameserver.Config;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameObjectsStorage {
  private static final Logger _log = LoggerFactory.getLogger(GameObjectsStorage.class);
  private static final int STORAGE_PLAYERS = 0;
  private static final int STORAGE_SUMMONS = 1;
  private static final int STORAGE_NPCS = 2;
  private static final int STORAGE_OTHER = 30;
  private static final int STORAGE_NONE = 31;
  private static final GameObjectArray[] storages = new GameObjectArray[31];
  private static long offline_refresh;
  private static int offline_count;

  public GameObjectsStorage() {
  }

  private static GameObjectArray<Player> getStoragePlayers() {
    return storages[0];
  }

  private static GameObjectArray<Playable> getStorageSummons() {
    return storages[1];
  }

  private static GameObjectArray<NpcInstance> getStorageNpcs() {
    return storages[2];
  }

  private static int selectStorageID(GameObject o) {
    if (o.isNpc()) {
      return 2;
    } else if (o.isPlayable()) {
      return o.isPlayer() ? 0 : 1;
    } else {
      return 30;
    }
  }

  public static GameObject get(long storedId) {
    int STORAGE_ID;
    if (storedId != 0L && (STORAGE_ID = getStorageID(storedId)) != 31) {
      GameObject result = storages[STORAGE_ID].get(getStoredIndex(storedId));
      return result != null && result.getObjectId() == getStoredObjectId(storedId) ? result : null;
    } else {
      return null;
    }
  }

  public static GameObject get(Long storedId) {
    int STORAGE_ID;
    if (storedId != null && storedId != 0L && (STORAGE_ID = getStorageID(storedId)) != 31) {
      GameObject result = storages[STORAGE_ID].get(getStoredIndex(storedId));
      return result != null && result.getObjectId() == getStoredObjectId(storedId) ? result : null;
    } else {
      return null;
    }
  }

  public static boolean isStored(long storedId) {
    int STORAGE_ID;
    if (storedId != 0L && (STORAGE_ID = getStorageID(storedId)) != 31) {
      GameObject o = storages[STORAGE_ID].get(getStoredIndex(storedId));
      return o != null && o.getObjectId() == getStoredObjectId(storedId);
    } else {
      return false;
    }
  }

  public static NpcInstance getAsNpc(long storedId) {
    return (NpcInstance)get(storedId);
  }

  public static NpcInstance getAsNpc(Long storedId) {
    return (NpcInstance)get(storedId);
  }

  public static Player getAsPlayer(long storedId) {
    return (Player)get(storedId);
  }

  public static Playable getAsPlayable(long storedId) {
    return (Playable)get(storedId);
  }

  public static Creature getAsCharacter(long storedId) {
    return (Creature)get(storedId);
  }

  public static MonsterInstance getAsMonster(long storedId) {
    return (MonsterInstance)get(storedId);
  }

  public static PetInstance getAsPet(long storedId) {
    return (PetInstance)get(storedId);
  }

  public static ItemInstance getAsItem(long storedId) {
    return (ItemInstance)get(storedId);
  }

  public static boolean contains(long storedId) {
    return get(storedId) != null;
  }

  public static Player getPlayer(String name) {
    return (Player)getStoragePlayers().findByName(name);
  }

  public static Player getPlayer(int objId) {
    return (Player)getStoragePlayers().findByObjectId(objId);
  }

  public static List<Player> getAllPlayers() {
    return getStoragePlayers().getAll();
  }

  public static Iterable<Player> getAllPlayersForIterate() {
    return getStoragePlayers();
  }

  public static int getAllPlayersCount() {
    return getStoragePlayers().getRealSize();
  }

  public static int getAllObjectsCount() {
    int result = 0;
    GameObjectArray[] var1 = storages;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      GameObjectArray<?> storage = var1[var3];
      if (storage != null) {
        result += storage.getRealSize();
      }
    }

    return result;
  }

  public static List<GameObject> getAllObjects() {
    List<GameObject> result = new ArrayList(getAllObjectsCount());
    GameObjectArray[] var1 = storages;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      GameObjectArray storage = var1[var3];
      if (storage != null) {
        storage.getAll(result);
      }
    }

    return result;
  }

  public static GameObject findObject(int objId) {
    GameObject result = null;
    GameObjectArray[] var2 = storages;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      GameObjectArray<?> storage = var2[var4];
      if (storage != null && (result = storage.findByObjectId(objId)) != null) {
        return result;
      }
    }

    return null;
  }

  public static int getAllOfflineCount() {
    if (!Config.SERVICES_OFFLINE_TRADE_ALLOW) {
      return 0;
    } else {
      long now = System.currentTimeMillis();
      if (now > offline_refresh) {
        offline_refresh = now + 10000L;
        offline_count = 0;
        Iterator var2 = getStoragePlayers().iterator();

        while(var2.hasNext()) {
          Player player = (Player)var2.next();
          if (player.isInOfflineMode()) {
            ++offline_count;
          }
        }
      }

      return offline_count;
    }
  }

  public static List<NpcInstance> getAllNpcs() {
    return getStorageNpcs().getAll();
  }

  public static Iterable<NpcInstance> getAllNpcsForIterate() {
    return getStorageNpcs();
  }

  public static NpcInstance getByNpcId(int npc_id) {
    NpcInstance result = null;
    Iterator var2 = getStorageNpcs().iterator();

    while(var2.hasNext()) {
      NpcInstance temp = (NpcInstance)var2.next();
      if (npc_id == temp.getNpcId()) {
        if (!temp.isDead()) {
          return temp;
        }

        result = temp;
      }
    }

    return result;
  }

  public static List<NpcInstance> getAllByNpcId(int npc_id, boolean justAlive) {
    List<NpcInstance> result = new ArrayList<>();
    Iterator var3 = getStorageNpcs().iterator();

    while(true) {
      NpcInstance temp;
      do {
        do {
          do {
            if (!var3.hasNext()) {
              return result;
            }

            temp = (NpcInstance)var3.next();
          } while(temp.getTemplate() == null);
        } while(npc_id != temp.getTemplate().getNpcId());
      } while(justAlive && temp.isDead());

      result.add(temp);
    }
  }

  public static List<NpcInstance> getAllByNpcId(int[] npc_ids, boolean justAlive) {
    List<NpcInstance> result = new ArrayList<>();
    Iterator var3 = getStorageNpcs().iterator();

    while(true) {
      NpcInstance temp;
      do {
        if (!var3.hasNext()) {
          return result;
        }

        temp = (NpcInstance)var3.next();
      } while(justAlive && temp.isDead());

      int[] var5 = npc_ids;
      int var6 = npc_ids.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        int npc_id = var5[var7];
        if (npc_id == temp.getNpcId()) {
          result.add(temp);
        }
      }
    }
  }

  public static NpcInstance getNpc(String s) {
    List<NpcInstance> npcs = getStorageNpcs().findAllByName(s);
    if (npcs.size() == 0) {
      return null;
    } else {
      Iterator var2 = npcs.iterator();

      NpcInstance temp;
      do {
        if (!var2.hasNext()) {
          if (npcs.size() > 0) {
            return (NpcInstance)npcs.remove(npcs.size() - 1);
          }

          return null;
        }

        temp = (NpcInstance)var2.next();
      } while(temp.isDead());

      return temp;
    }
  }

  public static NpcInstance getNpc(int objId) {
    return (NpcInstance)getStorageNpcs().findByObjectId(objId);
  }

  public static long put(GameObject o) {
    int STORAGE_ID = selectStorageID(o);
    return (long)o.getObjectId() & 4294967295L | ((long)STORAGE_ID & 31L) << 32 | ((long)storages[STORAGE_ID].add(o) & 4294967295L) << 37;
  }

  public static long putDummy(GameObject o) {
    return objIdNoStore(o.getObjectId());
  }

  public static long objIdNoStore(int objId) {
    return (long)objId & 4294967295L | 133143986176L;
  }

  public static long refreshId(Creature o) {
    return (long)o.getObjectId() & 4294967295L | o.getStoredId() >> 32 << 32;
  }

  public static GameObject remove(long storedId) {
    int STORAGE_ID = getStorageID(storedId);
    return STORAGE_ID == 31 ? null : storages[STORAGE_ID].remove(getStoredIndex(storedId), getStoredObjectId(storedId));
  }

  private static int getStorageID(long storedId) {
    return (int)(storedId >> 32) & 31;
  }

  private static int getStoredIndex(long storedId) {
    return (int)(storedId >> 37);
  }

  public static int getStoredObjectId(long storedId) {
    return (int)storedId;
  }

  public static StrTable getStats() {
    StrTable table = new StrTable("L2 Objects Storage Stats");

    for(int i = 0; i < storages.length; ++i) {
      GameObjectArray storage;
      if ((storage = storages[i]) != null) {
        synchronized(storage) {
          table.set(i, "Name", storage.name);
          table.set(i, "Size / Real", storage.size() + " / " + storage.getRealSize());
          table.set(i, "Capacity / init", storage.capacity() + " / " + storage.initCapacity);
        }
      }
    }

    return table;
  }

  static {
    storages[0] = new GameObjectArray("PLAYERS", Config.MAXIMUM_ONLINE_USERS, 1);
    storages[1] = new GameObjectArray("SUMMONS", Config.MAXIMUM_ONLINE_USERS, 1);
    storages[2] = new GameObjectArray("NPCS", '\uea60' * Config.RATE_MOB_SPAWN, 5000);
    storages[30] = new GameObjectArray("OTHER", 2000, 1000);
    offline_refresh = 0L;
    offline_count = 0;
  }
}
