//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.tables.SkillTable;

public class SiegeUtils {
  public static final int MIN_CLAN_SIEGE_LEVEL = 4;

  public SiegeUtils() {
  }

  public static void addSiegeSkills(Player character) {
    character.addSkill(SkillTable.getInstance().getInfo(246, 1), false);
    character.addSkill(SkillTable.getInstance().getInfo(247, 1), false);
    if (character.isNoble()) {
      character.addSkill(SkillTable.getInstance().getInfo(326, 1), false);
    }

  }

  public static void removeSiegeSkills(Player character) {
    character.removeSkill(SkillTable.getInstance().getInfo(246, 1), false);
    character.removeSkill(SkillTable.getInstance().getInfo(247, 1), false);
    character.removeSkill(SkillTable.getInstance().getInfo(326, 1), false);
  }

  public static boolean getCanRide() {
    Iterator var0 = ResidenceHolder.getInstance().getResidences().iterator();

    Residence residence;
    do {
      if (!var0.hasNext()) {
        return true;
      }

      residence = (Residence)var0.next();
    } while(residence == null || !residence.getSiegeEvent().isInProgress());

    return false;
  }
}
