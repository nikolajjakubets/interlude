//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.instances.residences.SiegeFlagInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncMul;
import l2.gameserver.templates.StatsSet;

public class SummonSiegeFlag extends Skill {
  private final SummonSiegeFlag.FlagType _flagType;
  private final double _advancedMult;

  public SummonSiegeFlag(StatsSet set) {
    super(set);
    this._flagType = (SummonSiegeFlag.FlagType)set.getEnum("flagType", SummonSiegeFlag.FlagType.class);
    this._advancedMult = set.getDouble("advancedMultiplier", 1.0D);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (!activeChar.isPlayer()) {
      return false;
    } else if (!super.checkCondition(activeChar, target, forceUse, dontMove, first)) {
      return false;
    } else {
      Player player = (Player)activeChar;
      if (player.getClan() != null && player.isClanLeader()) {
        switch(this._flagType) {
          case OUTPOST:
          case NORMAL:
          case ADVANCED:
            if (player.isInZone(ZoneType.RESIDENCE)) {
              player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, (new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this)});
              return false;
            } else {
              SiegeEvent siegeEvent = (SiegeEvent)activeChar.getEvent(SiegeEvent.class);
              if (siegeEvent == null) {
                player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, (new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this)});
                return false;
              } else {
                boolean inZone = false;
                List<ZoneObject> zones = siegeEvent.getObjects("flag_zones");
                Iterator var10 = zones.iterator();

                while(var10.hasNext()) {
                  ZoneObject zone = (ZoneObject)var10.next();
                  if (player.isInZone(zone.getZone())) {
                    inZone = true;
                  }
                }

                if (!inZone) {
                  player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, (new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this)});
                  return false;
                } else {
                  SiegeClanObject siegeClan = siegeEvent.getSiegeClan("attackers", player.getClan());
                  if (siegeClan == null) {
                    player.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE, (new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this)});
                    return false;
                  } else if (siegeClan.getFlag() != null) {
                    player.sendPacket(new IStaticPacket[]{SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS, (new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this)});
                    return false;
                  }
                }
              }
            }
          case DESTROY:
          default:
            return true;
        }
      } else {
        return false;
      }
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Player player = (Player)activeChar;
    Clan clan = player.getClan();
    if (clan != null && player.isClanLeader()) {
      SiegeEvent siegeEvent = (SiegeEvent)activeChar.getEvent(SiegeEvent.class);
      if (siegeEvent != null) {
        SiegeClanObject siegeClan = siegeEvent.getSiegeClan("attackers", clan);
        if (siegeClan != null) {
          switch(this._flagType) {
            case DESTROY:
              siegeClan.deleteFlag();
              break;
            default:
              if (siegeClan.getFlag() != null) {
                return;
              }

              SiegeFlagInstance flag = (SiegeFlagInstance)NpcHolder.getInstance().getTemplate(this._flagType == SummonSiegeFlag.FlagType.OUTPOST ? '軮' : '裶').getNewInstance();
              flag.setClan(siegeClan);
              flag.addEvent(siegeEvent);
              if (this._flagType == SummonSiegeFlag.FlagType.ADVANCED) {
                flag.addStatFunc(new FuncMul(Stats.MAX_HP, 80, flag, this._advancedMult));
              }

              flag.setCurrentHpMp((double)flag.getMaxHp(), (double)flag.getMaxMp(), true);
              flag.setHeading(player.getHeading());
              int x = (int)((double)player.getX() + 100.0D * Math.cos(player.headingToRadians(player.getHeading() - '耀')));
              int y = (int)((double)player.getY() + 100.0D * Math.sin(player.headingToRadians(player.getHeading() - '耀')));
              flag.spawnMe(GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), x, y, player.getGeoIndex()));
              siegeClan.setFlag(flag);
          }

        }
      }
    }
  }

  public static enum FlagType {
    DESTROY,
    NORMAL,
    ADVANCED,
    OUTPOST;

    private FlagType() {
    }
  }
}
