//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class ParticipantPool {
  private static ParticipantPool _instance;
  private Map<CompetitionType, ArrayList<ParticipantPool.EntryRec>> _pools;

  public static ParticipantPool getInstance() {
    if (_instance == null) {
      _instance = new ParticipantPool();
    }

    return _instance;
  }

  private ParticipantPool() {
  }

  public void AllocatePools() {
    this._pools = new HashMap<>();
    CompetitionType[] var1 = CompetitionType.values();

    for (CompetitionType type : var1) {
      this._pools.put(type, new ArrayList<>());
    }

    log.info("OlyParticipantPool: Allocated " + this._pools.size() + " particiant pools.");
  }

  public void FreePools() {
    if (this._pools != null) {
      this._pools.clear();
    }

    log.info("OlyParticipantPool: pools cleared.");
  }

  public boolean isEnough(CompetitionType type, int cls_id) {
    switch (type) {
      case CLASS_FREE:
        return this._pools.get(type).size() >= Config.OLY_MIN_CF_START;
      case TEAM_CLASS_FREE:
        return this._pools.get(type).size() >= Config.OLY_MIN_TB_START;
      case CLASS_INDIVIDUAL:
        int cnt = 0;

        for (Object o : this._pools.get(type)) {
          EntryRec er = (EntryRec) o;
          if (er.cls_id == cls_id) {
            ++cnt;
          }
        }

        return cnt >= Config.OLY_MIN_CB_START;
      default:
        return false;
    }
  }

  public int getNearestIndex(CompetitionType type, int idx, int cls_id) {
    ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
    ParticipantPool.EntryRec base_rec = pool.get(idx);
    if (base_rec == null) {
      return -1;
    } else {
      int ndelta = 2147483647;
      int nidx = -2147483648;

      int i;
      ParticipantPool.EntryRec pr;
      int delta;
      for (i = 0; i < idx; ++i) {
        pr = pool.get(i);
        if (pr != null && (type != CompetitionType.CLASS_INDIVIDUAL || cls_id <= 0 || cls_id == pr.cls_id)) {
          delta = Math.abs(base_rec.average - pr.average);
          if (delta < ndelta) {
            nidx = i;
            ndelta = delta;
          }
        }
      }

      for (i = idx + 1; i < pool.size(); ++i) {
        pr = pool.get(i);
        if (pr != null && (type != CompetitionType.CLASS_INDIVIDUAL || cls_id <= 0 || cls_id == pr.cls_id)) {
          delta = Math.abs(base_rec.average - pr.average);
          if (delta < ndelta) {
            nidx = i;
            ndelta = delta;
          }
        }
      }

      return nidx;
    }
  }

  public void createEntry(CompetitionType type, Player[] players) {
    if (players != null && players.length != 0) {
      ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
      synchronized (pool) {
        this._pools.get(type).add(new ParticipantPool.EntryRec(players));
      }
    }
  }

  public Player[][] retrieveEntrys(CompetitionType type, int cls_id) {
    this.cleadInvalidEntrys(type);
    ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
    int oldest_idx = -1;
    long oldest_time = -9223372036854775808L;
    Player[][] ret;
    synchronized (pool) {
      for (int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = pool.get(i);
        if (pr != null && (type != CompetitionType.CLASS_INDIVIDUAL || cls_id <= 0 || cls_id == pr.cls_id) && pr.reg_time > oldest_time) {
          oldest_idx = i;
          oldest_time = pr.reg_time;
        }
      }

      if (oldest_idx < 0) {
        return null;
      } else {
        int pair_idx = this.getNearestIndex(type, oldest_idx, cls_id);
        if (pair_idx < 0) {
          return null;
        } else {
          ret = new Player[][]{Util.GetPlayersFromStoredIds(pool.remove(oldest_idx).sids), Util.GetPlayersFromStoredIds(pool.remove(pair_idx).sids)};
          pool.trimToSize();
          return ret;
        }
      }
    }
  }

  public boolean removeEntryByPlayer(CompetitionType type, Player player) {
    long psid = player.getStoredId();
    ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
    synchronized (pool) {
      for (int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = pool.get(i);
        if (pr != null) {
          long[] var9 = pr.sids;
          int var10 = var9.length;

          for (long sid : var9) {
            if (sid == psid) {
              pool.remove(i);
              return true;
            }
          }
        }
      }

      return false;
    }
  }

  public CompetitionType getCompTypeOf(Player player) {
    long psid = player.getStoredId();

    for (Entry<CompetitionType, ArrayList<EntryRec>> competitionTypeArrayListEntry : this._pools.entrySet()) {
      ArrayList<EntryRec> pool = competitionTypeArrayListEntry.getValue();

      for (EntryRec pr : pool) {
        if (pr != null) {
          long[] var9 = pr.sids;
          int var10 = var9.length;

          for (long sid : var9) {
            if (sid == psid) {
              return competitionTypeArrayListEntry.getKey();
            }
          }
        }
      }
    }

    return null;
  }

  public boolean isRegistred(Player player) {
    if (!OlyController.getInstance().isRegAllowed()) {
      return false;
    } else {
      Iterator var2 = this._pools.keySet().iterator();

      CompetitionType type;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        type = (CompetitionType) var2.next();
      } while (!this.isRegistred(type, player));

      return true;
    }
  }

  public boolean isHWIDRegistred(String hwid) {
    if (!OlyController.getInstance().isRegAllowed()) {
      return false;
    } else {
      List<ParticipantPool.EntryRec> recs = new LinkedList<>();
      Iterator var3 = this._pools.entrySet().iterator();

      while (var3.hasNext()) {
        Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry) var3.next();
        ArrayList<ParticipantPool.EntryRec> entryRecs = e.getValue();
        synchronized (entryRecs) {
          recs.addAll(entryRecs);
        }
      }

      var3 = recs.iterator();

      while (var3.hasNext()) {
        ParticipantPool.EntryRec er = (ParticipantPool.EntryRec) var3.next();

        for (int sidx = 0; sidx < er.sids.length; ++sidx) {
          Player player = GameObjectsStorage.getAsPlayer(er.sids[sidx]);
          if (player != null && player.getNetConnection() != null && player.getNetConnection().getHwid() != null && hwid.equalsIgnoreCase(player.getNetConnection().getHwid())) {
            return true;
          }
        }
      }

      return false;
    }
  }

  public boolean isIPRegistred(String ip) {
    if (!OlyController.getInstance().isRegAllowed()) {
      return false;
    } else {
      List<ParticipantPool.EntryRec> recs = new LinkedList<>();
      Iterator var3 = this._pools.entrySet().iterator();

      while (var3.hasNext()) {
        Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry) var3.next();
        ArrayList<ParticipantPool.EntryRec> entryRecs = e.getValue();
        synchronized (entryRecs) {
          recs.addAll(entryRecs);
        }
      }

      var3 = recs.iterator();

      while (var3.hasNext()) {
        ParticipantPool.EntryRec er = (ParticipantPool.EntryRec) var3.next();

        for (int sidx = 0; sidx < er.sids.length; ++sidx) {
          Player player = GameObjectsStorage.getAsPlayer(er.sids[sidx]);
          if (player != null && player.getNetConnection() != null && player.getNetConnection().getIpAddr() != null && player.getNetConnection().getIpAddr() != "?.?.?.?" && ip.equalsIgnoreCase(player.getNetConnection().getIpAddr())) {
            return true;
          }
        }
      }

      return false;
    }
  }

  public boolean isRegistred(CompetitionType type, Player player) {
    if (!OlyController.getInstance().isRegAllowed()) {
      return false;
    } else {
      long psid = player.getStoredId();
      ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
      Iterator var6 = pool.iterator();

      while (true) {
        ParticipantPool.EntryRec pr;
        do {
          if (!var6.hasNext()) {
            return false;
          }

          pr = (ParticipantPool.EntryRec) var6.next();
        } while (pr == null);

        long[] var8 = pr.sids;
        int var9 = var8.length;

        for (long sid : var8) {
          if (sid == psid) {
            return true;
          }
        }
      }
    }
  }

  public void broadcastToEntrys(CompetitionType type, L2GameServerPacket gsp, int cls_id) {
    ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
    Iterator var5 = pool.iterator();

    while (true) {
      ParticipantPool.EntryRec pr;
      do {
        if (!var5.hasNext()) {
          return;
        }

        pr = (ParticipantPool.EntryRec) var5.next();
      } while (pr == null);

      long[] var7 = pr.sids;
      int var8 = var7.length;

      for (long sid : var7) {
        Player player = GameObjectsStorage.getAsPlayer(sid);
        if (player != null && (cls_id <= 0 || player.getClassId().getId() == cls_id)) {
          player.sendPacket(gsp);
        }
      }
    }
  }

  private void cleadInvalidEntrys(CompetitionType type) {
    ArrayList<ParticipantPool.EntryRec> pool = this._pools.get(type);
    synchronized (pool) {
      ArrayList<Integer> invalid_entrys = new ArrayList<>();

      for (int i = 0; i < pool.size(); ++i) {
        if (!this.isValidEntry(pool.get(i))) {
          invalid_entrys.add(i);
        }
      }

      for (int i : invalid_entrys) {
        pool.remove(i);
      }

    }
  }

  public void onLogout(Player player) {
    if (OlyController.getInstance().isRegAllowed()) {
      CompetitionType ctype = getInstance().getCompTypeOf(player);
      if (ctype != null) {
        this.removeEntryByPlayer(ctype, player);
      }

    }
  }

  private boolean isValidEntry(ParticipantPool.EntryRec pr) {
    return true;
  }

  public int getParticipantCount() {
    int result = 0;

    for (Entry<CompetitionType, ArrayList<EntryRec>> competitionTypeArrayListEntry : this._pools.entrySet()) {
      ArrayList<EntryRec> pool = competitionTypeArrayListEntry.getValue();

      for (EntryRec pr : pool) {
        if (pr != null) {
          result += pr.sids.length;
        }
      }
    }

    return result;
  }

  private static class EntryRec {
    long[] sids;
    int average;
    long reg_time;
    int cls_id;

    public EntryRec(Player[] players) {
      this.sids = new long[players.length];
      this.cls_id = players[0].getClassId().getId();
      int sum = 0;

      for (int i = 0; i < players.length; ++i) {
        this.sids[i] = players[i].getStoredId();
        sum += Math.max(0, NoblesController.getInstance().getPointsOf(players[i].getObjectId()));
        OlyController.getInstance().incPartCount();
      }

      this.average = sum / players.length;
      this.reg_time = System.currentTimeMillis();
    }
  }
}
