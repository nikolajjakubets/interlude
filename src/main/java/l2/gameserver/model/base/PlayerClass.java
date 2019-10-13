//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import l2.gameserver.Config;

public enum PlayerClass {
  HumanFighter(Race.human, ClassType.Fighter, ClassLevel.First),
  Warrior(Race.human, ClassType.Fighter, ClassLevel.Second),
  Gladiator(Race.human, ClassType.Fighter, ClassLevel.Third),
  Warlord(Race.human, ClassType.Fighter, ClassLevel.Third),
  HumanKnight(Race.human, ClassType.Fighter, ClassLevel.Second),
  Paladin(Race.human, ClassType.Fighter, ClassLevel.Third),
  DarkAvenger(Race.human, ClassType.Fighter, ClassLevel.Third),
  Rogue(Race.human, ClassType.Fighter, ClassLevel.Second),
  TreasureHunter(Race.human, ClassType.Fighter, ClassLevel.Third),
  Hawkeye(Race.human, ClassType.Fighter, ClassLevel.Third),
  HumanMystic(Race.human, ClassType.Mystic, ClassLevel.First),
  HumanWizard(Race.human, ClassType.Mystic, ClassLevel.Second),
  Sorceror(Race.human, ClassType.Mystic, ClassLevel.Third),
  Necromancer(Race.human, ClassType.Mystic, ClassLevel.Third),
  Warlock(Race.human, ClassType.Mystic, ClassLevel.Third),
  Cleric(Race.human, ClassType.Priest, ClassLevel.Second),
  Bishop(Race.human, ClassType.Priest, ClassLevel.Third),
  Prophet(Race.human, ClassType.Priest, ClassLevel.Third),
  ElvenFighter(Race.elf, ClassType.Fighter, ClassLevel.First),
  ElvenKnight(Race.elf, ClassType.Fighter, ClassLevel.Second),
  TempleKnight(Race.elf, ClassType.Fighter, ClassLevel.Third),
  Swordsinger(Race.elf, ClassType.Fighter, ClassLevel.Third),
  ElvenScout(Race.elf, ClassType.Fighter, ClassLevel.Second),
  Plainswalker(Race.elf, ClassType.Fighter, ClassLevel.Third),
  SilverRanger(Race.elf, ClassType.Fighter, ClassLevel.Third),
  ElvenMystic(Race.elf, ClassType.Mystic, ClassLevel.First),
  ElvenWizard(Race.elf, ClassType.Mystic, ClassLevel.Second),
  Spellsinger(Race.elf, ClassType.Mystic, ClassLevel.Third),
  ElementalSummoner(Race.elf, ClassType.Mystic, ClassLevel.Third),
  ElvenOracle(Race.elf, ClassType.Priest, ClassLevel.Second),
  ElvenElder(Race.elf, ClassType.Priest, ClassLevel.Third),
  DarkElvenFighter(Race.darkelf, ClassType.Fighter, ClassLevel.First),
  PalusKnight(Race.darkelf, ClassType.Fighter, ClassLevel.Second),
  ShillienKnight(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
  Bladedancer(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
  Assassin(Race.darkelf, ClassType.Fighter, ClassLevel.Second),
  AbyssWalker(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
  PhantomRanger(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
  DarkElvenMystic(Race.darkelf, ClassType.Mystic, ClassLevel.First),
  DarkElvenWizard(Race.darkelf, ClassType.Mystic, ClassLevel.Second),
  Spellhowler(Race.darkelf, ClassType.Mystic, ClassLevel.Third),
  PhantomSummoner(Race.darkelf, ClassType.Mystic, ClassLevel.Third),
  ShillienOracle(Race.darkelf, ClassType.Priest, ClassLevel.Second),
  ShillienElder(Race.darkelf, ClassType.Priest, ClassLevel.Third),
  OrcFighter(Race.orc, ClassType.Fighter, ClassLevel.First),
  orcRaider(Race.orc, ClassType.Fighter, ClassLevel.Second),
  Destroyer(Race.orc, ClassType.Fighter, ClassLevel.Third),
  orcMonk(Race.orc, ClassType.Fighter, ClassLevel.Second),
  Tyrant(Race.orc, ClassType.Fighter, ClassLevel.Third),
  orcMystic(Race.orc, ClassType.Mystic, ClassLevel.First),
  orcShaman(Race.orc, ClassType.Mystic, ClassLevel.Second),
  Overlord(Race.orc, ClassType.Mystic, ClassLevel.Third),
  Warcryer(Race.orc, ClassType.Mystic, ClassLevel.Third),
  DwarvenFighter(Race.dwarf, ClassType.Fighter, ClassLevel.First),
  DwarvenScavenger(Race.dwarf, ClassType.Fighter, ClassLevel.Second),
  BountyHunter(Race.dwarf, ClassType.Fighter, ClassLevel.Third),
  DwarvenArtisan(Race.dwarf, ClassType.Fighter, ClassLevel.Second),
  Warsmith(Race.dwarf, ClassType.Fighter, ClassLevel.Third),
  DummyEntry1((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry2((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry3((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry4((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry5((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry6((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry7((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry8((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry9((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry10((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry11((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry12((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry13((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry14((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry15((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry16((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry17((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry18((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry19((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry20((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry21((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry22((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry23((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry24((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry25((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry26((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry27((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry28((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry29((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry30((Race)null, (ClassType)null, (ClassLevel)null),
  Duelist(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  Dreadnought(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  PhoenixKnight(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  HellKnight(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  Sagittarius(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  Adventurer(Race.human, ClassType.Fighter, ClassLevel.Fourth),
  Archmage(Race.human, ClassType.Mystic, ClassLevel.Fourth),
  Soultaker(Race.human, ClassType.Mystic, ClassLevel.Fourth),
  ArcanaLord(Race.human, ClassType.Mystic, ClassLevel.Fourth),
  Cardinal(Race.human, ClassType.Priest, ClassLevel.Fourth),
  Hierophant(Race.human, ClassType.Priest, ClassLevel.Fourth),
  EvaTemplar(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
  SwordMuse(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
  WindRider(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
  MoonlightSentinel(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
  MysticMuse(Race.elf, ClassType.Mystic, ClassLevel.Fourth),
  ElementalMaster(Race.elf, ClassType.Mystic, ClassLevel.Fourth),
  EvaSaint(Race.elf, ClassType.Priest, ClassLevel.Fourth),
  ShillienTemplar(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
  SpectralDancer(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
  GhostHunter(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
  GhostSentinel(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
  StormScreamer(Race.darkelf, ClassType.Mystic, ClassLevel.Fourth),
  SpectralMaster(Race.darkelf, ClassType.Mystic, ClassLevel.Fourth),
  ShillienSaint(Race.darkelf, ClassType.Priest, ClassLevel.Fourth),
  Titan(Race.orc, ClassType.Fighter, ClassLevel.Fourth),
  GrandKhauatari(Race.orc, ClassType.Fighter, ClassLevel.Fourth),
  Dominator(Race.orc, ClassType.Mystic, ClassLevel.Fourth),
  Doomcryer(Race.orc, ClassType.Mystic, ClassLevel.Fourth),
  FortuneSeeker(Race.dwarf, ClassType.Fighter, ClassLevel.Fourth),
  Maestro(Race.dwarf, ClassType.Fighter, ClassLevel.Fourth),
  DummyEntry31((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry32((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry33((Race)null, (ClassType)null, (ClassLevel)null),
  DummyEntry34((Race)null, (ClassType)null, (ClassLevel)null);

  public static final PlayerClass[] VALUES = values();
  private Race _race;
  private ClassLevel _level;
  private ClassType _type;
  private static final Set<PlayerClass> mainSubclassSet;
  private static final Set<PlayerClass> neverSubclassed = EnumSet.of(Overlord, Warsmith);
  private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
  private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
  private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
  private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
  private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(Sorceror, Spellsinger, Spellhowler);
  private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap(PlayerClass.class);

  private PlayerClass(Race race, ClassType type, ClassLevel level) {
    this._race = race;
    this._level = level;
    this._type = type;
  }

  public final Set<PlayerClass> getAvailableSubclasses() {
    Set<PlayerClass> subclasses = null;
    if (this._level == ClassLevel.Third || this._level == ClassLevel.Fourth) {
      subclasses = EnumSet.copyOf(mainSubclassSet);
      if (!Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL) {
        subclasses.removeAll(neverSubclassed);
      }

      subclasses.remove(this);
      if (!Config.ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF) {
        switch(this._race) {
          case elf:
            subclasses.removeAll(getSet(Race.darkelf, ClassLevel.Third));
            break;
          case darkelf:
            subclasses.removeAll(getSet(Race.elf, ClassLevel.Third));
        }
      }

      Set<PlayerClass> unavailableClasses = (Set)subclassSetMap.get(this);
      if (unavailableClasses != null) {
        subclasses.removeAll(unavailableClasses);
      }
    }

    return subclasses;
  }

  public static EnumSet<PlayerClass> getSet(Race race, ClassLevel level) {
    EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);
    Iterator var3 = EnumSet.allOf(PlayerClass.class).iterator();

    while(true) {
      PlayerClass playerClass;
      do {
        do {
          if (!var3.hasNext()) {
            return allOf;
          }

          playerClass = (PlayerClass)var3.next();
        } while(race != null && !playerClass.isOfRace(race));
      } while(level != null && !playerClass.isOfLevel(level));

      allOf.add(playerClass);
    }
  }

  public final boolean isOfRace(Race race) {
    return this._race == race;
  }

  public final boolean isOfType(ClassType type) {
    return this._type == type;
  }

  public final boolean isOfLevel(ClassLevel level) {
    return this._level == level;
  }

  public static boolean areClassesComportable(PlayerClass c1, PlayerClass c2) {
    if (Config.ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF || (!c1.isOfRace(Race.elf) || !c2.isOfRace(Race.darkelf)) && (!c1.isOfRace(Race.darkelf) || !c2.isOfRace(Race.elf))) {
      if (!Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL && (c1 == Overlord || c1 == Warsmith || c2 == Overlord || c2 == Warsmith)) {
        return false;
      } else {
        return subclassSetMap.get(c1) != subclassSetMap.get(c2);
      }
    } else {
      return false;
    }
  }

  static {
    Set<PlayerClass> subclasses = getSet((Race)null, ClassLevel.Third);
    if (!Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL) {
      subclasses.removeAll(neverSubclassed);
    }

    mainSubclassSet = subclasses;
    subclassSetMap.put(DarkAvenger, subclasseSet1);
    subclassSetMap.put(HellKnight, subclasseSet1);
    subclassSetMap.put(Paladin, subclasseSet1);
    subclassSetMap.put(PhoenixKnight, subclasseSet1);
    subclassSetMap.put(TempleKnight, subclasseSet1);
    subclassSetMap.put(EvaTemplar, subclasseSet1);
    subclassSetMap.put(ShillienKnight, subclasseSet1);
    subclassSetMap.put(ShillienTemplar, subclasseSet1);
    subclassSetMap.put(TreasureHunter, subclasseSet2);
    subclassSetMap.put(Adventurer, subclasseSet2);
    subclassSetMap.put(AbyssWalker, subclasseSet2);
    subclassSetMap.put(GhostHunter, subclasseSet2);
    subclassSetMap.put(Plainswalker, subclasseSet2);
    subclassSetMap.put(WindRider, subclasseSet2);
    subclassSetMap.put(Hawkeye, subclasseSet3);
    subclassSetMap.put(Sagittarius, subclasseSet3);
    subclassSetMap.put(SilverRanger, subclasseSet3);
    subclassSetMap.put(MoonlightSentinel, subclasseSet3);
    subclassSetMap.put(PhantomRanger, subclasseSet3);
    subclassSetMap.put(GhostSentinel, subclasseSet3);
    subclassSetMap.put(Warlock, subclasseSet4);
    subclassSetMap.put(ArcanaLord, subclasseSet4);
    subclassSetMap.put(ElementalSummoner, subclasseSet4);
    subclassSetMap.put(ElementalMaster, subclasseSet4);
    subclassSetMap.put(PhantomSummoner, subclasseSet4);
    subclassSetMap.put(SpectralMaster, subclasseSet4);
    subclassSetMap.put(Sorceror, subclasseSet5);
    subclassSetMap.put(Archmage, subclasseSet5);
    subclassSetMap.put(Spellsinger, subclasseSet5);
    subclassSetMap.put(MysticMuse, subclasseSet5);
    subclassSetMap.put(Spellhowler, subclasseSet5);
    subclassSetMap.put(StormScreamer, subclasseSet5);
    subclassSetMap.put(Duelist, EnumSet.of(Gladiator));
    subclassSetMap.put(Dreadnought, EnumSet.of(Warlord));
    subclassSetMap.put(Soultaker, EnumSet.of(Necromancer));
    subclassSetMap.put(Cardinal, EnumSet.of(Bishop));
    subclassSetMap.put(Hierophant, EnumSet.of(Prophet));
    subclassSetMap.put(SwordMuse, EnumSet.of(Swordsinger));
    subclassSetMap.put(EvaSaint, EnumSet.of(ElvenElder));
    subclassSetMap.put(SpectralDancer, EnumSet.of(Bladedancer));
    subclassSetMap.put(Titan, EnumSet.of(Destroyer));
    subclassSetMap.put(GrandKhauatari, EnumSet.of(Tyrant));
    subclassSetMap.put(Dominator, EnumSet.of(Overlord));
    subclassSetMap.put(Doomcryer, EnumSet.of(Warcryer));
  }
}
