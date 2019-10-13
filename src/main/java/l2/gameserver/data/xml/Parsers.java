//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml;

import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.parser.ArmorSetsParser;
import l2.gameserver.data.xml.parser.CapsuleItemParser;
import l2.gameserver.data.xml.parser.CubicParser;
import l2.gameserver.data.xml.parser.DomainParser;
import l2.gameserver.data.xml.parser.DoorParser;
import l2.gameserver.data.xml.parser.EnchantItemParser;
import l2.gameserver.data.xml.parser.EnchantSkillParser;
import l2.gameserver.data.xml.parser.EventParser;
import l2.gameserver.data.xml.parser.FishDataParser;
import l2.gameserver.data.xml.parser.HennaParser;
import l2.gameserver.data.xml.parser.InstantZoneParser;
import l2.gameserver.data.xml.parser.ItemParser;
import l2.gameserver.data.xml.parser.NpcParser;
import l2.gameserver.data.xml.parser.OptionDataParser;
import l2.gameserver.data.xml.parser.RecipeParser;
import l2.gameserver.data.xml.parser.ResidenceParser;
import l2.gameserver.data.xml.parser.RestartPointParser;
import l2.gameserver.data.xml.parser.SkillAcquireParser;
import l2.gameserver.data.xml.parser.SoulCrystalParser;
import l2.gameserver.data.xml.parser.SpawnParser;
import l2.gameserver.data.xml.parser.StaticObjectParser;
import l2.gameserver.data.xml.parser.VariationChanceParser;
import l2.gameserver.data.xml.parser.VariationGroupParser;
import l2.gameserver.data.xml.parser.ZoneParser;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.tables.SkillTable;

public abstract class Parsers {
  public Parsers() {
  }

  public static void parseAll() {
    HtmCache.getInstance().reload();
    StringHolder.getInstance().load();
    EnchantSkillParser.getInstance().load();
    SkillTable.getInstance().load();
    OptionDataParser.getInstance().load();
    ItemParser.getInstance().load();
    VariationGroupParser.getInstance().load();
    VariationChanceParser.getInstance().load();
    NpcParser.getInstance().load();
    DomainParser.getInstance().load();
    RestartPointParser.getInstance().load();
    StaticObjectParser.getInstance().load();
    DoorParser.getInstance().load();
    ZoneParser.getInstance().load();
    SpawnParser.getInstance().load();
    InstantZoneParser.getInstance().load();
    ReflectionManager.getInstance();
    SkillAcquireParser.getInstance().load();
    ResidenceParser.getInstance().load();
    EventParser.getInstance().load();
    CubicParser.getInstance().load();
    RecipeParser.getInstance().load();
    BuyListHolder.getInstance();
    MultiSellHolder.getInstance();
    HennaParser.getInstance().load();
    EnchantItemParser.getInstance().load();
    SoulCrystalParser.getInstance().load();
    ArmorSetsParser.getInstance().load();
    FishDataParser.getInstance().load();
    CapsuleItemParser.getInstance().load();
  }
}
