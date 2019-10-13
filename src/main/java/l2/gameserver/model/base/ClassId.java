//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

public enum ClassId {
  fighter(0, false, Race.human, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  warrior(1, false, Race.human, fighter, (ClassId)null, 2, (ClassType2)null),
  gladiator(2, false, Race.human, warrior, (ClassId)null, 3, ClassType2.Warrior),
  warlord(3, false, Race.human, warrior, (ClassId)null, 3, ClassType2.Warrior),
  knight(4, false, Race.human, fighter, (ClassId)null, 2, (ClassType2)null),
  paladin(5, false, Race.human, knight, (ClassId)null, 3, ClassType2.Knight),
  darkAvenger(6, false, Race.human, knight, (ClassId)null, 3, ClassType2.Knight),
  rogue(7, false, Race.human, fighter, (ClassId)null, 2, (ClassType2)null),
  treasureHunter(8, false, Race.human, rogue, (ClassId)null, 3, ClassType2.Rogue),
  hawkeye(9, false, Race.human, rogue, (ClassId)null, 3, ClassType2.Rogue),
  mage(10, true, Race.human, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  wizard(11, true, Race.human, mage, (ClassId)null, 2, (ClassType2)null),
  sorceror(12, true, Race.human, wizard, (ClassId)null, 3, ClassType2.Wizard),
  necromancer(13, true, Race.human, wizard, (ClassId)null, 3, ClassType2.Wizard),
  warlock(14, true, Race.human, wizard, (ClassId)null, 3, ClassType2.Summoner),
  cleric(15, true, Race.human, mage, (ClassId)null, 2, (ClassType2)null),
  bishop(16, true, Race.human, cleric, (ClassId)null, 3, ClassType2.Healer),
  prophet(17, true, Race.human, cleric, (ClassId)null, 3, ClassType2.Enchanter),
  elvenFighter(18, false, Race.elf, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  elvenKnight(19, false, Race.elf, elvenFighter, (ClassId)null, 2, (ClassType2)null),
  templeKnight(20, false, Race.elf, elvenKnight, (ClassId)null, 3, ClassType2.Knight),
  swordSinger(21, false, Race.elf, elvenKnight, (ClassId)null, 3, ClassType2.Enchanter),
  elvenScout(22, false, Race.elf, elvenFighter, (ClassId)null, 2, (ClassType2)null),
  plainsWalker(23, false, Race.elf, elvenScout, (ClassId)null, 3, ClassType2.Rogue),
  silverRanger(24, false, Race.elf, elvenScout, (ClassId)null, 3, ClassType2.Rogue),
  elvenMage(25, true, Race.elf, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  elvenWizard(26, true, Race.elf, elvenMage, (ClassId)null, 2, (ClassType2)null),
  spellsinger(27, true, Race.elf, elvenWizard, (ClassId)null, 3, ClassType2.Wizard),
  elementalSummoner(28, true, Race.elf, elvenWizard, (ClassId)null, 3, ClassType2.Summoner),
  oracle(29, true, Race.elf, elvenMage, (ClassId)null, 2, (ClassType2)null),
  elder(30, true, Race.elf, oracle, (ClassId)null, 3, ClassType2.Healer),
  darkFighter(31, false, Race.darkelf, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  palusKnight(32, false, Race.darkelf, darkFighter, (ClassId)null, 2, (ClassType2)null),
  shillienKnight(33, false, Race.darkelf, palusKnight, (ClassId)null, 3, ClassType2.Knight),
  bladedancer(34, false, Race.darkelf, palusKnight, (ClassId)null, 3, ClassType2.Enchanter),
  assassin(35, false, Race.darkelf, darkFighter, (ClassId)null, 2, (ClassType2)null),
  abyssWalker(36, false, Race.darkelf, assassin, (ClassId)null, 3, ClassType2.Rogue),
  phantomRanger(37, false, Race.darkelf, assassin, (ClassId)null, 3, ClassType2.Rogue),
  darkMage(38, true, Race.darkelf, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  darkWizard(39, true, Race.darkelf, darkMage, (ClassId)null, 2, (ClassType2)null),
  spellhowler(40, true, Race.darkelf, darkWizard, (ClassId)null, 3, ClassType2.Wizard),
  phantomSummoner(41, true, Race.darkelf, darkWizard, (ClassId)null, 3, ClassType2.Summoner),
  shillienOracle(42, true, Race.darkelf, darkMage, (ClassId)null, 2, (ClassType2)null),
  shillienElder(43, true, Race.darkelf, shillienOracle, (ClassId)null, 3, ClassType2.Healer),
  orcFighter(44, false, Race.orc, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  orcRaider(45, false, Race.orc, orcFighter, (ClassId)null, 2, (ClassType2)null),
  destroyer(46, false, Race.orc, orcRaider, (ClassId)null, 3, ClassType2.Warrior),
  orcMonk(47, false, Race.orc, orcFighter, (ClassId)null, 2, (ClassType2)null),
  tyrant(48, false, Race.orc, orcMonk, (ClassId)null, 3, ClassType2.Warrior),
  orcMage(49, true, Race.orc, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  orcShaman(50, true, Race.orc, orcMage, (ClassId)null, 2, (ClassType2)null),
  overlord(51, true, Race.orc, orcShaman, (ClassId)null, 3, ClassType2.Enchanter),
  warcryer(52, true, Race.orc, orcShaman, (ClassId)null, 3, ClassType2.Enchanter),
  dwarvenFighter(53, false, Race.dwarf, (ClassId)null, (ClassId)null, 1, (ClassType2)null),
  scavenger(54, false, Race.dwarf, dwarvenFighter, (ClassId)null, 2, (ClassType2)null),
  bountyHunter(55, false, Race.dwarf, scavenger, (ClassId)null, 3, ClassType2.Warrior),
  artisan(56, false, Race.dwarf, dwarvenFighter, (ClassId)null, 2, (ClassType2)null),
  warsmith(57, false, Race.dwarf, artisan, (ClassId)null, 3, ClassType2.Warrior),
  dummyEntry1(58, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry2(59, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry3(60, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry4(61, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry5(62, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry6(63, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry7(64, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry8(65, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry9(66, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry10(67, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry11(68, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry12(69, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry13(70, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry14(71, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry15(72, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry16(73, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry17(74, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry18(75, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry19(76, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry20(77, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry21(78, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry22(79, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry23(80, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry24(81, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry25(82, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry26(83, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry27(84, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry28(85, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry29(86, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry30(87, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  duelist(88, false, Race.human, gladiator, (ClassId)null, 4, ClassType2.Warrior),
  dreadnought(89, false, Race.human, warlord, (ClassId)null, 4, ClassType2.Warrior),
  phoenixKnight(90, false, Race.human, paladin, (ClassId)null, 4, ClassType2.Knight),
  hellKnight(91, false, Race.human, darkAvenger, (ClassId)null, 4, ClassType2.Knight),
  sagittarius(92, false, Race.human, hawkeye, (ClassId)null, 4, ClassType2.Rogue),
  adventurer(93, false, Race.human, treasureHunter, (ClassId)null, 4, ClassType2.Rogue),
  archmage(94, true, Race.human, sorceror, (ClassId)null, 4, ClassType2.Wizard),
  soultaker(95, true, Race.human, necromancer, (ClassId)null, 4, ClassType2.Wizard),
  arcanaLord(96, true, Race.human, warlock, (ClassId)null, 4, ClassType2.Summoner),
  cardinal(97, true, Race.human, bishop, (ClassId)null, 4, ClassType2.Healer),
  hierophant(98, true, Race.human, prophet, (ClassId)null, 4, ClassType2.Enchanter),
  evaTemplar(99, false, Race.elf, templeKnight, (ClassId)null, 4, ClassType2.Knight),
  swordMuse(100, false, Race.elf, swordSinger, (ClassId)null, 4, ClassType2.Enchanter),
  windRider(101, false, Race.elf, plainsWalker, (ClassId)null, 4, ClassType2.Rogue),
  moonlightSentinel(102, false, Race.elf, silverRanger, (ClassId)null, 4, ClassType2.Rogue),
  mysticMuse(103, true, Race.elf, spellsinger, (ClassId)null, 4, ClassType2.Wizard),
  elementalMaster(104, true, Race.elf, elementalSummoner, (ClassId)null, 4, ClassType2.Summoner),
  evaSaint(105, true, Race.elf, elder, (ClassId)null, 4, ClassType2.Healer),
  shillienTemplar(106, false, Race.darkelf, shillienKnight, (ClassId)null, 4, ClassType2.Knight),
  spectralDancer(107, false, Race.darkelf, bladedancer, (ClassId)null, 4, ClassType2.Enchanter),
  ghostHunter(108, false, Race.darkelf, abyssWalker, (ClassId)null, 4, ClassType2.Rogue),
  ghostSentinel(109, false, Race.darkelf, phantomRanger, (ClassId)null, 4, ClassType2.Rogue),
  stormScreamer(110, true, Race.darkelf, spellhowler, (ClassId)null, 4, ClassType2.Wizard),
  spectralMaster(111, true, Race.darkelf, phantomSummoner, (ClassId)null, 4, ClassType2.Summoner),
  shillienSaint(112, true, Race.darkelf, shillienElder, (ClassId)null, 4, ClassType2.Healer),
  titan(113, false, Race.orc, destroyer, (ClassId)null, 4, ClassType2.Warrior),
  grandKhauatari(114, false, Race.orc, tyrant, (ClassId)null, 4, ClassType2.Warrior),
  dominator(115, true, Race.orc, overlord, (ClassId)null, 4, ClassType2.Enchanter),
  doomcryer(116, true, Race.orc, warcryer, (ClassId)null, 4, ClassType2.Enchanter),
  fortuneSeeker(117, false, Race.dwarf, bountyHunter, (ClassId)null, 4, ClassType2.Warrior),
  maestro(118, false, Race.dwarf, warsmith, (ClassId)null, 4, ClassType2.Warrior),
  dummyEntry31(119, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry32(120, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry33(121, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null),
  dummyEntry34(122, false, (Race)null, (ClassId)null, (ClassId)null, 0, (ClassType2)null);

  public static final ClassId[] VALUES = values();
  private final int _id;
  private final boolean _isMage;
  private final Race _race;
  private final ClassId _parent;
  private final ClassId _parent2;
  private final ClassType2 _type2;
  private final int _level;

  private ClassId(int id, boolean isMage, Race race, ClassId parent, ClassId parent2, int level, ClassType2 classType2) {
    this._id = id;
    this._isMage = isMage;
    this._race = race;
    this._parent = parent;
    this._parent2 = parent2;
    this._level = level;
    this._type2 = classType2;
  }

  public final int getId() {
    return this._id;
  }

  public final boolean isMage() {
    return this._isMage;
  }

  public final Race getRace() {
    return this._race;
  }

  public final boolean childOf(ClassId cid) {
    if (this._parent == null) {
      return false;
    } else {
      return this._parent != cid && this._parent2 != cid ? this._parent.childOf(cid) : true;
    }
  }

  public final boolean equalsOrChildOf(ClassId cid) {
    return this == cid || this.childOf(cid);
  }

  public final int level() {
    return this._parent == null ? 0 : 1 + this._parent.level();
  }

  public final ClassId getParent(int sex) {
    return sex != 0 && this._parent2 != null ? this._parent2 : this._parent;
  }

  public final int getLevel() {
    return this._level;
  }

  public ClassType2 getType2() {
    return this._type2;
  }
}
