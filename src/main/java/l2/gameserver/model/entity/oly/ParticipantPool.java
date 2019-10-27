//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParticipantPool {
  private static final Logger _log = LoggerFactory.getLogger(ParticipantPool.class);
  private static ParticipantPool _instance;
  private Map<CompetitionType, ArrayList<ParticipantPool.EntryRec>> _pools;

  public static final ParticipantPool getInstance() {
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
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      CompetitionType type = var1[var3];
      this._pools.put(type, new ArrayList<>());
    }

    _log.info("OlyParticipantPool: Allocated " + this._pools.size() + " particiant pools.");
  }

  public void FreePools() {
    if (this._pools != null) {
      this._pools.clear();
    }

    _log.info("OlyParticipantPool: pools cleared.");
  }

  public boolean isEnough(CompetitionType type, int cls_id) {
    switch(type) {
      case CLASS_FREE:
        return ((ArrayList)this._pools.get(type)).size() >= Config.OLY_MIN_CF_START;
      case TEAM_CLASS_FREE:
        return ((ArrayList)this._pools.get(type)).size() >= Config.OLY_MIN_TB_START;
      case CLASS_INDIVIDUAL:
        int cnt = 0;
        Iterator var4 = ((ArrayList)this._pools.get(type)).iterator();

        while(var4.hasNext()) {
          ParticipantPool.EntryRec er = (ParticipantPool.EntryRec)var4.next();
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
    ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
    ParticipantPool.EntryRec base_rec = (ParticipantPool.EntryRec)pool.get(idx);
    if (base_rec == null) {
      return -1;
    } else {
      int ndelta = 2147483647;
      int nidx = -2147483648;

      int i;
      ParticipantPool.EntryRec pr;
      int delta;
      for(i = 0; i < idx; ++i) {
        pr = (ParticipantPool.EntryRec)pool.get(i);
        if (pr != null && (type != CompetitionType.CLASS_INDIVIDUAL || cls_id <= 0 || cls_id == pr.cls_id)) {
          delta = Math.abs(base_rec.average - pr.average);
          if (delta < ndelta) {
            nidx = i;
            ndelta = delta;
          }
        }
      }

      for(i = idx + 1; i < pool.size(); ++i) {
        pr = (ParticipantPool.EntryRec)pool.get(i);
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
      ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
      synchronized(pool) {
        ((ArrayList)this._pools.get(type)).add(new ParticipantPool.EntryRec(players));
      }
    }
  }

  public Player[][] retrieveEntrys(CompetitionType type, int cls_id) {
    this.cleadInvalidEntrys(type);
    ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
    int oldest_idx = -1;
    long oldest_time = -9223372036854775808L;
    int pair_idx = true;
    Player[][] ret = (Player[][])null;
    synchronized(pool) {
      for(int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = (ParticipantPool.EntryRec)pool.get(i);
        if (pr != null && (type != CompetitionType.CLASS_INDIVIDUAL || cls_id <= 0 || cls_id == pr.cls_id) && pr.reg_time > oldest_time) {
          oldest_idx = i;
          oldest_time = pr.reg_time;
        }
      }

      if (oldest_idx < 0) {
        return (Player[][])null;
      } else {
        int pair_idx = this.getNearestIndex(type, oldest_idx, cls_id);
        if (pair_idx < 0) {
          return (Player[][])null;
        } else {
          ret = new Player[][]{Util.GetPlayersFromStoredIds(((ParticipantPool.EntryRec)pool.remove(oldest_idx)).sids), Util.GetPlayersFromStoredIds(((ParticipantPool.EntryRec)pool.remove(pair_idx)).sids)};
          pool.trimToSize();
          return ret;
        }
      }
    }
  }

  public boolean removeEntryByPlayer(CompetitionType type, Player player) {
    long psid = player.getStoredId();
    ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
    synchronized(pool) {
      for(int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = (ParticipantPool.EntryRec)pool.get(i);
        if (pr != null) {
          long[] var9 = pr.sids;
          int var10 = var9.length;

          for(int var11 = 0; var11 < var10; ++var11) {
            long sid = var9[var11];
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
    Iterator var4 = this._pools.entrySet().iterator();

    while(var4.hasNext()) {
      Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry)var4.next();
      ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)e.getValue();

      for(int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = (ParticipantPool.EntryRec)pool.get(i);
        if (pr != null) {
          long[] var9 = pr.sids;
          int var10 = var9.length;

          for(int var11 = 0; var11 < var10; ++var11) {
            long sid = var9[var11];
            if (sid == psid) {
              return (CompetitionType)e.getKey();
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

        type = (CompetitionType)var2.next();
      } while(!this.isRegistred(type, player));

      return true;
    }
  }

  public boolean isHWIDRegistred(String hwid) {
    if (!OlyController.getInstance().isRegAllowed()) {
      return false;
    } else {
      List<ParticipantPool.EntryRec> recs = new LinkedList();
      Iterator var3 = this._pools.entrySet().iterator();

      while(var3.hasNext()) {
        Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry)var3.next();
        ArrayList<ParticipantPool.EntryRec> entryRecs = (ArrayList)e.getValue();
        synchronized(entryRecs) {
          recs.addAll(entryRecs);
        }
      }

      var3 = recs.iterator();

      while(var3.hasNext()) {
        ParticipantPool.EntryRec er = (ParticipantPool.EntryRec)var3.next();

        for(int sidx = 0; sidx < er.sids.length; ++sidx) {
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
      List<ParticipantPool.EntryRec> recs = new LinkedList();
      Iterator var3 = this._pools.entrySet().iterator();

      while(var3.hasNext()) {
        Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry)var3.next();
        ArrayList<ParticipantPool.EntryRec> entryRecs = (ArrayList)e.getValue();
        synchronized(entryRecs) {
          recs.addAll(entryRecs);
        }
      }

      var3 = recs.iterator();

      while(var3.hasNext()) {
        ParticipantPool.EntryRec er = (ParticipantPool.EntryRec)var3.next();

        for(int sidx = 0; sidx < er.sids.length; ++sidx) {
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
      ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
      Iterator var6 = pool.iterator();

      while(true) {
        ParticipantPool.EntryRec pr;
        do {
          if (!var6.hasNext()) {
            return false;
          }

          pr = (ParticipantPool.EntryRec)var6.next();
        } while(pr == null);

        long[] var8 = pr.sids;
        int var9 = var8.length;

        for(int var10 = 0; var10 < var9; ++var10) {
          long sid = var8[var10];
          if (sid == psid) {
            return true;
          }
        }
      }
    }
  }

  public void broadcastToEntrys(CompetitionType type, L2GameServerPacket gsp, int cls_id) {
    ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
    Iterator var5 = pool.iterator();

    while(true) {
      ParticipantPool.EntryRec pr;
      do {
        if (!var5.hasNext()) {
          return;
        }

        pr = (ParticipantPool.EntryRec)var5.next();
      } while(pr == null);

      long[] var7 = pr.sids;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
        long sid = var7[var9];
        Player player = GameObjectsStorage.getAsPlayer(sid);
        if (player != null && (cls_id <= 0 || player.getClassId().getId() == cls_id)) {
          player.sendPacket(gsp);
        }
      }
    }
  }

  private void cleadInvalidEntrys(CompetitionType type) {
    ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)this._pools.get(type);
    synchronized(pool) {
      ArrayList<Integer> invalid_entrys = new ArrayList<>();

      for(int i = 0; i < pool.size(); ++i) {
        if (!this.isValidEntry((ParticipantPool.EntryRec)pool.get(i))) {
          invalid_entrys.add(i);
        }
      }

      Iterator var9 = invalid_entrys.iterator();

      while(var9.hasNext()) {
        int i = (Integer)var9.next();
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
    Iterator var2 = this._pools.entrySet().iterator();

    while(var2.hasNext()) {
      Entry<CompetitionType, ArrayList<ParticipantPool.EntryRec>> e = (Entry)var2.next();
      ArrayList<ParticipantPool.EntryRec> pool = (ArrayList)e.getValue();

      for(int i = 0; i < pool.size(); ++i) {
        ParticipantPool.EntryRec pr = (ParticipantPool.EntryRec)pool.get(i);
        if (pr != null) {
          result += pr.sids.length;
        }
      }
    }

    return result;
  }

  private class EntryRec {
    long[] sids;
    int average;
    long reg_time;
    int cls_id;

    public EntryRec(Player[] players) {
      this.sids = new long[players.length];
      this.cls_id = players[0].getClassId().getId();
      int sum = 0;

      for(int i = 0; i < players.length; ++i) {
        this.sids[i] = players[i].getStoredId();
        sum += Math.max(0, NoblesController.getInstance().getPointsOf(players[i].getObjectId()));
        OlyController.getInstance().incPartCount();
      }

      this.average = sum / players.length;
      this.reg_time = System.currentTimeMillis();
    }
  }
}
