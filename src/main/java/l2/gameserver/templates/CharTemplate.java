//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

public class CharTemplate {
  public static final int[] EMPTY_ATTRIBUTES = new int[6];
  public final int baseSTR;
  public final int baseCON;
  public final int baseDEX;
  public final int baseINT;
  public final int baseWIT;
  public final int baseMEN;
  public final double baseHpMax;
  public final double baseCpMax;
  public final double baseMpMax;
  public final double baseHpReg;
  public final double baseMpReg;
  public final double baseCpReg;
  public final int basePAtk;
  public final int baseMAtk;
  public final int basePDef;
  public final int baseMDef;
  public final int basePAtkSpd;
  public final int baseMAtkSpd;
  public final int baseShldDef;
  public final int baseAtkRange;
  public final int baseShldRate;
  public final int baseCritRate;
  public final int baseRunSpd;
  public final int baseWalkSpd;
  public final int[] baseAttributeAttack;
  public final int[] baseAttributeDefence;
  public final double collisionRadius;
  public final double collisionHeight;

  public CharTemplate(StatsSet set) {
    this.baseSTR = set.getInteger("baseSTR");
    this.baseCON = set.getInteger("baseCON");
    this.baseDEX = set.getInteger("baseDEX");
    this.baseINT = set.getInteger("baseINT");
    this.baseWIT = set.getInteger("baseWIT");
    this.baseMEN = set.getInteger("baseMEN");
    this.baseHpMax = set.getDouble("baseHpMax");
    this.baseCpMax = set.getDouble("baseCpMax");
    this.baseMpMax = set.getDouble("baseMpMax");
    this.baseHpReg = set.getDouble("baseHpReg");
    this.baseCpReg = set.getDouble("baseCpReg");
    this.baseMpReg = set.getDouble("baseMpReg");
    this.basePAtk = set.getInteger("basePAtk");
    this.baseMAtk = set.getInteger("baseMAtk");
    this.basePDef = set.getInteger("basePDef");
    this.baseMDef = set.getInteger("baseMDef");
    this.basePAtkSpd = set.getInteger("basePAtkSpd");
    this.baseMAtkSpd = set.getInteger("baseMAtkSpd");
    this.baseShldDef = set.getInteger("baseShldDef");
    this.baseAtkRange = set.getInteger("baseAtkRange");
    this.baseShldRate = set.getInteger("baseShldRate");
    this.baseCritRate = set.getInteger("baseCritRate");
    this.baseRunSpd = set.getInteger("baseRunSpd");
    this.baseWalkSpd = set.getInteger("baseWalkSpd");
    this.baseAttributeAttack = set.getIntegerArray("baseAttributeAttack", EMPTY_ATTRIBUTES);
    this.baseAttributeDefence = set.getIntegerArray("baseAttributeDefence", EMPTY_ATTRIBUTES);
    this.collisionRadius = set.getDouble("collision_radius", 5.0D);
    this.collisionHeight = set.getDouble("collision_height", 5.0D);
  }

  public int getNpcId() {
    return 0;
  }

  public static StatsSet getEmptyStatsSet() {
    StatsSet npcDat = new StatsSet();
    npcDat.set("baseSTR", 0);
    npcDat.set("baseCON", 0);
    npcDat.set("baseDEX", 0);
    npcDat.set("baseINT", 0);
    npcDat.set("baseWIT", 0);
    npcDat.set("baseMEN", 0);
    npcDat.set("baseHpMax", 0);
    npcDat.set("baseCpMax", 0);
    npcDat.set("baseMpMax", 0);
    npcDat.set("baseHpReg", 0.003000000026077032D);
    npcDat.set("baseCpReg", 0);
    npcDat.set("baseMpReg", 0.003000000026077032D);
    npcDat.set("basePAtk", 0);
    npcDat.set("baseMAtk", 0);
    npcDat.set("basePDef", 100);
    npcDat.set("baseMDef", 100);
    npcDat.set("basePAtkSpd", 0);
    npcDat.set("baseMAtkSpd", 0);
    npcDat.set("baseShldDef", 0);
    npcDat.set("baseAtkRange", 0);
    npcDat.set("baseShldRate", 0);
    npcDat.set("baseCritRate", 0);
    npcDat.set("baseRunSpd", 0);
    npcDat.set("baseWalkSpd", 0);
    return npcDat;
  }
}
