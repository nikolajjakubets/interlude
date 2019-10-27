//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.CropProcure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Manor {
  private static final Logger _log = LoggerFactory.getLogger(Manor.class);
  private static Manor _instance;
  private static Map<Integer, Manor.SeedData> _seeds;

  public Manor() {
    _seeds = new ConcurrentHashMap();
    this.parseData();
  }

  public static Manor getInstance() {
    if (_instance == null) {
      _instance = new Manor();
    }

    return _instance;
  }

  public List<Integer> getAllCrops() {
    List<Integer> crops = new ArrayList<>();
    Iterator var2 = _seeds.values().iterator();

    while(var2.hasNext()) {
      Manor.SeedData seed = (Manor.SeedData)var2.next();
      if (!crops.contains(seed.getCrop()) && seed.getCrop() != 0 && !crops.contains(seed.getCrop())) {
        crops.add(seed.getCrop());
      }
    }

    return crops;
  }

  public Map<Integer, Manor.SeedData> getAllSeeds() {
    return _seeds;
  }

  public int getSeedBasicPrice(int seedId) {
    ItemTemplate seedItem = ItemHolder.getInstance().getTemplate(seedId);
    return seedItem != null ? seedItem.getReferencePrice() : 0;
  }

  public int getSeedBasicPriceByCrop(int cropId) {
    Iterator var2 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var2.hasNext()) {
        return 0;
      }

      seed = (Manor.SeedData)var2.next();
    } while(seed.getCrop() != cropId);

    return this.getSeedBasicPrice(seed.getId());
  }

  public int getCropBasicPrice(int cropId) {
    ItemTemplate cropItem = ItemHolder.getInstance().getTemplate(cropId);
    return cropItem != null ? cropItem.getReferencePrice() : 0;
  }

  public int getMatureCrop(int cropId) {
    Iterator var2 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var2.hasNext()) {
        return 0;
      }

      seed = (Manor.SeedData)var2.next();
    } while(seed.getCrop() != cropId);

    return seed.getMature();
  }

  public long getSeedBuyPrice(int seedId) {
    long buyPrice = (long)(this.getSeedBasicPrice(seedId) / 10);
    return buyPrice >= 0L ? buyPrice : 1L;
  }

  public int getSeedMinLevel(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getLevel() - 5 : -1;
  }

  public int getSeedMaxLevel(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getLevel() + 5 : -1;
  }

  public int getSeedLevelByCrop(int cropId) {
    Iterator var2 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var2.hasNext()) {
        return 0;
      }

      seed = (Manor.SeedData)var2.next();
    } while(seed.getCrop() != cropId);

    return seed.getLevel();
  }

  public int getSeedLevel(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getLevel() : -1;
  }

  public boolean isAlternative(int seedId) {
    Iterator var2 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var2.hasNext()) {
        return false;
      }

      seed = (Manor.SeedData)var2.next();
    } while(seed.getId() != seedId);

    return seed.isAlternative();
  }

  public int getCropType(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getCrop() : -1;
  }

  public synchronized int getRewardItem(int cropId, int type) {
    Iterator var3 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var3.hasNext()) {
        return -1;
      }

      seed = (Manor.SeedData)var3.next();
    } while(seed.getCrop() != cropId);

    return seed.getReward(type);
  }

  public synchronized long getRewardAmountPerCrop(int castle, int cropId, int type) {
    CropProcure cs = (CropProcure)((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, castle)).getCropProcure(0).get(cropId);
    Iterator var5 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var5.hasNext()) {
        return -1L;
      }

      seed = (Manor.SeedData)var5.next();
    } while(seed.getCrop() != cropId);

    return cs.getPrice() / (long)this.getCropBasicPrice(seed.getReward(type));
  }

  public synchronized int getRewardItemBySeed(int seedId, int type) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getReward(type) : 0;
  }

  public List<Integer> getCropsForCastle(int castleId) {
    List<Integer> crops = new ArrayList<>();
    Iterator var3 = _seeds.values().iterator();

    while(var3.hasNext()) {
      Manor.SeedData seed = (Manor.SeedData)var3.next();
      if (seed.getManorId() == castleId && !crops.contains(seed.getCrop())) {
        crops.add(seed.getCrop());
      }
    }

    return crops;
  }

  public List<Integer> getSeedsForCastle(int castleId) {
    List<Integer> seedsID = new ArrayList<>();
    Iterator var3 = _seeds.values().iterator();

    while(var3.hasNext()) {
      Manor.SeedData seed = (Manor.SeedData)var3.next();
      if (seed.getManorId() == castleId && !seedsID.contains(seed.getId())) {
        seedsID.add(seed.getId());
      }
    }

    return seedsID;
  }

  public int getCastleIdForSeed(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getManorId() : 0;
  }

  public long getSeedSaleLimit(int seedId) {
    Manor.SeedData seed = (Manor.SeedData)_seeds.get(seedId);
    return seed != null ? seed.getSeedLimit() : 0L;
  }

  public long getCropPuchaseLimit(int cropId) {
    Iterator var2 = _seeds.values().iterator();

    Manor.SeedData seed;
    do {
      if (!var2.hasNext()) {
        return 0L;
      }

      seed = (Manor.SeedData)var2.next();
    } while(seed.getCrop() != cropId);

    return seed.getCropLimit();
  }

  private void parseData() {
    LineNumberReader lnr = null;

    try {
      File seedData = new File(Config.DATAPACK_ROOT, "data/seeds.csv");
      lnr = new LineNumberReader(new BufferedReader(new FileReader(seedData)));
      String line = null;

      while((line = lnr.readLine()) != null) {
        if (line.trim().length() != 0 && !line.startsWith("#")) {
          Manor.SeedData seed = this.parseList(line);
          _seeds.put(seed.getId(), seed);
        }
      }

      _log.info("ManorManager: Loaded " + _seeds.size() + " seeds");
    } catch (FileNotFoundException var15) {
      _log.info("seeds.csv is missing in data folder");
    } catch (Exception var16) {
      _log.error("Error while loading seeds!", var16);
    } finally {
      try {
        if (lnr != null) {
          lnr.close();
        }
      } catch (Exception var14) {
      }

    }

  }

  private Manor.SeedData parseList(String line) {
    StringTokenizer st = new StringTokenizer(line, ";");
    int seedId = Integer.parseInt(st.nextToken());
    int level = Integer.parseInt(st.nextToken());
    int cropId = Integer.parseInt(st.nextToken());
    int matureId = Integer.parseInt(st.nextToken());
    int type1R = Integer.parseInt(st.nextToken());
    int type2R = Integer.parseInt(st.nextToken());
    int manorId = Integer.parseInt(st.nextToken());
    int isAlt = Integer.parseInt(st.nextToken());
    long limitSeeds = Math.round((double)Integer.parseInt(st.nextToken()) * Config.RATE_MANOR);
    long limitCrops = Math.round((double)Integer.parseInt(st.nextToken()) * Config.RATE_MANOR);
    Manor.SeedData seed = new Manor.SeedData(level, cropId, matureId);
    seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);
    return seed;
  }

  public class SeedData {
    private int _id;
    private int _level;
    private int _crop;
    private int _mature;
    private int _type1;
    private int _type2;
    private int _manorId;
    private int _isAlternative;
    private long _limitSeeds;
    private long _limitCrops;

    public SeedData(int level, int crop, int mature) {
      this._level = level;
      this._crop = crop;
      this._mature = mature;
    }

    public void setData(int id, int t1, int t2, int manorId, int isAlt, long lim1, long lim2) {
      this._id = id;
      this._type1 = t1;
      this._type2 = t2;
      this._manorId = manorId;
      this._isAlternative = isAlt;
      this._limitSeeds = lim1;
      this._limitCrops = lim2;
    }

    public int getManorId() {
      return this._manorId;
    }

    public int getId() {
      return this._id;
    }

    public int getCrop() {
      return this._crop;
    }

    public int getMature() {
      return this._mature;
    }

    public int getReward(int type) {
      return type == 1 ? this._type1 : this._type2;
    }

    public int getLevel() {
      return this._level;
    }

    public boolean isAlternative() {
      return this._isAlternative == 1;
    }

    public long getSeedLimit() {
      return this._limitSeeds;
    }

    public long getCropLimit() {
      return this._limitCrops;
    }
  }
}
