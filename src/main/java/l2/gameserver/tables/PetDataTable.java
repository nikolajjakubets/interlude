//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import gnu.trove.TIntObjectHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.PetData;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.items.ItemInstance;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class PetDataTable {
  private static final PetDataTable _instance = new PetDataTable();
  public static final int PET_WOLF_ID = 12077;
  public static final int HATCHLING_WIND_ID = 12311;
  public static final int HATCHLING_STAR_ID = 12312;
  public static final int HATCHLING_TWILIGHT_ID = 12313;
  public static final int STRIDER_WIND_ID = 12526;
  public static final int STRIDER_STAR_ID = 12527;
  public static final int STRIDER_TWILIGHT_ID = 12528;
  public static final int RED_STRIDER_WIND_ID = 16038;
  public static final int RED_STRIDER_STAR_ID = 16039;
  public static final int RED_STRIDER_TWILIGHT_ID = 16040;
  public static final int WYVERN_ID = 12621;
  public static final int BABY_BUFFALO_ID = 12780;
  public static final int BABY_KOOKABURRA_ID = 12781;
  public static final int BABY_COUGAR_ID = 12782;
  public static final int IMPROVED_BABY_BUFFALO_ID = 16034;
  public static final int IMPROVED_BABY_KOOKABURRA_ID = 16035;
  public static final int IMPROVED_BABY_COUGAR_ID = 16036;
  public static final int SIN_EATER_ID = 12564;
  public static final int GREAT_WOLF_ID = 16025;
  public static final int WGREAT_WOLF_ID = 16037;
  public static final int FENRIR_WOLF_ID = 16041;
  public static final int WFENRIR_WOLF_ID = 16042;
  public static final int GUARDIANS_STRIDER_ID = 16068;
  private final TIntObjectHashMap<PetData> _pets = new TIntObjectHashMap<>();

  public static PetDataTable getInstance() {
    return _instance;
  }

  private PetDataTable() {
    this.load();
  }

  public void reload() {
    this.load();
  }

  public PetData getInfo(int petNpcId, int level) {
    PetData result;
    for (result = null; result == null && level < 100; ++level) {
      result = this._pets.get(petNpcId * 100 + level);
    }

    return result;
  }

  private void load() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT id, level, exp, hp, mp, patk, pdef, matk, mdef, acc, evasion, crit, speed, atk_speed, cast_speed, max_meal, battle_meal, normal_meal, loadMax, hpregen, mpregen FROM pet_data");
      rset = statement.executeQuery();

      while (rset.next()) {
        PetData petData = new PetData();
        petData.setID(rset.getInt("id"));
        petData.setLevel(rset.getInt("level"));
        petData.setExp(rset.getLong("exp"));
        petData.setHP(rset.getInt("hp"));
        petData.setMP(rset.getInt("mp"));
        petData.setPAtk(rset.getInt("patk"));
        petData.setPDef(rset.getInt("pdef"));
        petData.setMAtk(rset.getInt("matk"));
        petData.setMDef(rset.getInt("mdef"));
        petData.setAccuracy(rset.getInt("acc"));
        petData.setEvasion(rset.getInt("evasion"));
        petData.setCritical(rset.getInt("crit"));
        petData.setSpeed(rset.getInt("speed"));
        petData.setAtkSpeed(rset.getInt("atk_speed"));
        petData.setCastSpeed(rset.getInt("cast_speed"));
        petData.setFeedMax(rset.getInt("max_meal"));
        petData.setFeedBattle(rset.getInt("battle_meal"));
        petData.setFeedNormal(rset.getInt("normal_meal"));
        petData.setMaxLoad(rset.getInt("loadMax"));
        petData.setHpRegen(rset.getInt("hpregen"));
        petData.setMpRegen(rset.getInt("mpregen"));
        petData.setControlItemId(getControlItemId(petData.getID()));
        petData.setFoodId(getFoodId(petData.getID()));
        petData.setMountable(isMountable(petData.getID()));
        petData.setMinLevel(getMinLevel(petData.getID()));
        petData.setAddFed(getAddFed(petData.getID()));
        this._pets.put(petData.getID() * 100 + petData.getLevel(), petData);
      }
    } catch (Exception e) {
      log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    log.info("PetDataTable: Loaded " + this._pets.size() + " pets.");
  }

  public static void deletePet(ItemInstance item, Creature owner) {
    int petObjectId = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT objId FROM pets WHERE item_obj_id=?");
      statement.setInt(1, item.getObjectId());

      for (rset = statement.executeQuery(); rset.next(); petObjectId = rset.getInt("objId")) {
      }

      DbUtils.close(statement, rset);
      Summon summon = owner.getPet();
      if (summon != null && summon.getObjectId() == petObjectId) {
        summon.unSummon();
      }

      Player player = owner.getPlayer();
      if (player != null && player.isMounted() && player.getMountObjId() == petObjectId) {
        player.setMount(0, 0, 0);
      }

      statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
      statement.setInt(1, item.getObjectId());
      statement.execute();
    } catch (Exception var11) {
      log.error("could not restore pet objectid:", var11);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public static void unSummonPet(ItemInstance oldItem, Creature owner) {
    int petObjectId = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT objId FROM pets WHERE item_obj_id=?");
      statement.setInt(1, oldItem.getObjectId());

      for (rset = statement.executeQuery(); rset.next(); petObjectId = rset.getInt("objId")) {
      }

      if (owner != null) {
        Summon summon = owner.getPet();
        if (summon != null && summon.getObjectId() == petObjectId) {
          summon.unSummon();
        }

        Player player = owner.getPlayer();
        if (player != null && player.isMounted() && player.getMountObjId() == petObjectId) {
          player.setMount(0, 0, 0);
        }

      }
    } catch (Exception var11) {
      log.error("could not restore pet objectid:", var11);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public static int getControlItemId(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getControlItemId();
      }
    }

    return 1;
  }

  public static int getFoodId(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getFoodId();
      }
    }

    return 1;
  }

  public static boolean isMountable(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.isMountable();
      }
    }

    return false;
  }

  public static int getMinLevel(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getMinLevel();
      }
    }

    return 1;
  }

  public static int getAddFed(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getAddFed();
      }
    }

    return 1;
  }

  public static double getExpPenalty(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getExpPenalty();
      }
    }

    return 0.0D;
  }

  public static int getSoulshots(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getSoulshots();
      }
    }

    return 2;
  }

  public static int getSpiritshots(int npcId) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getNpcId() == npcId) {
        return pet.getSpiritshots();
      }
    }

    return 2;
  }

  public static int getSummonId(ItemInstance item) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getControlItemId() == item.getItemId()) {
        return pet.getNpcId();
      }
    }

    return 0;
  }

  public static int[] getPetControlItems() {
    int[] items = new int[PetDataTable.L2Pet.values().length];
    int i = 0;
    PetDataTable.L2Pet[] var2 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var2) {
      items[i++] = pet.getControlItemId();
    }

    return items;
  }

  public static boolean isPetControlItem(ItemInstance item) {
    PetDataTable.L2Pet[] var1 = PetDataTable.L2Pet.values();

    for (L2Pet pet : var1) {
      if (pet.getControlItemId() == item.getItemId()) {
        return true;
      }
    }

    return false;
  }

  public static boolean isBabyPet(int id) {
    switch (id) {
      case 12780:
      case 12781:
      case 12782:
        return true;
      default:
        return false;
    }
  }

  public static boolean isImprovedBabyPet(int id) {
    switch (id) {
      case 16034:
      case 16035:
      case 16036:
        return true;
      default:
        return false;
    }
  }

  public static boolean isWolf(int id) {
    return id == 12077;
  }

  public static boolean isHatchling(int id) {
    switch (id) {
      case 12311:
      case 12312:
      case 12313:
        return true;
      default:
        return false;
    }
  }

  public static boolean isStrider(int id) {
    switch (id) {
      case 12526:
      case 12527:
      case 12528:
      case 16038:
      case 16039:
      case 16040:
      case 16068:
        return true;
      default:
        return false;
    }
  }

  public static boolean isGWolf(int id) {
    switch (id) {
      case 16025:
      case 16037:
      case 16041:
      case 16042:
        return true;
      default:
        return false;
    }
  }

  public enum L2Pet {
    WOLF(12077, 2375, 2515, false, 1, 12, 0.3D, 2, 2),
    HATCHLING_WIND(12311, 3500, 4038, false, 1, 12, 0.3D, 2, 2),
    HATCHLING_STAR(12312, 3501, 4038, false, 1, 12, 0.3D, 2, 2),
    HATCHLING_TWILIGHT(12313, 3502, 4038, false, 1, 100, 0.3D, 2, 2),
    STRIDER_WIND(12526, 4422, 5168, true, 1, 12, 0.3D, 2, 2),
    STRIDER_STAR(12527, 4423, 5168, true, 1, 12, 0.3D, 2, 2),
    STRIDER_TWILIGHT(12528, 4424, 5168, true, 1, 100, 0.3D, 2, 2),
    WYVERN(12621, 5249, 6316, true, 1, 12, 0.0D, 2, 2),
    BABY_BUFFALO(12780, 6648, 7582, false, 1, 12, 0.05D, 2, 2),
    BABY_KOOKABURRA(12781, 6650, 7582, false, 1, 12, 0.05D, 2, 2),
    BABY_COUGAR(12782, 6649, 7582, false, 1, 12, 0.05D, 2, 2),
    SIN_EATER(12564, 4425, 2515, false, 1, 12, 0.0D, 2, 2);

    private final int _npcId;
    private final int _controlItemId;
    private final int _foodId;
    private final boolean _isMountable;
    private final int _minLevel;
    private final int _addFed;
    private final double _expPenalty;
    private final int _soulshots;
    private final int _spiritshots;

    L2Pet(int npcId, int controlItemId, int foodId, boolean isMountabe, int minLevel, int addFed, double expPenalty, int soulshots, int spiritshots) {
      this._npcId = npcId;
      this._controlItemId = controlItemId;
      this._foodId = foodId;
      this._isMountable = isMountabe;
      this._minLevel = minLevel;
      this._addFed = addFed;
      this._expPenalty = expPenalty;
      this._soulshots = soulshots;
      this._spiritshots = spiritshots;
    }

    public int getNpcId() {
      return this._npcId;
    }

    public int getControlItemId() {
      return this._controlItemId;
    }

    public int getFoodId() {
      return this._foodId;
    }

    public boolean isMountable() {
      return this._isMountable;
    }

    public int getMinLevel() {
      return this._minLevel;
    }

    public int getAddFed() {
      return this._addFed;
    }

    public double getExpPenalty() {
      return this._expPenalty;
    }

    public int getSoulshots() {
      return this._soulshots;
    }

    public int getSpiritshots() {
      return this._spiritshots;
    }
  }
}
