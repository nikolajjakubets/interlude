//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;

import java.util.Iterator;
import java.util.List;

public class Call extends Skill {
  private final boolean _party;
  private final int _requestWithCrystal;

  public Call(StatsSet set) {
    super(set);
    this._party = set.getBool("party", false);
    this._requestWithCrystal = set.getInteger("requestWithCrystal", -1);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (activeChar.isPlayer()) {
      if (this._party && ((Player)activeChar).getParty() == null) {
        return false;
      }

      SystemMessage msg = canSummonHere((Player)activeChar);
      if (msg != null) {
        activeChar.sendPacket(msg);
        return false;
      }

      if (!this._party) {
        if (activeChar == target) {
          return false;
        }

        msg = canBeSummoned(target);
        if (msg != null) {
          activeChar.sendPacket(msg);
          return false;
        }
      }
    }

    return super.checkCondition(activeChar, target, forceUse, dontMove, first);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      SystemMessage msg = canSummonHere((Player)activeChar);
      if (msg != null) {
        activeChar.sendPacket(msg);
      } else {
        Iterator var4;
        if (!this._party) {
          var4 = targets.iterator();

          while(var4.hasNext()) {
            Creature target = (Creature)var4.next();
            if (target != null && canBeSummoned(target) == null) {
              if (this._requestWithCrystal >= 0) {
                ((Player)target).summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), this._requestWithCrystal);
              } else {
                target.stopMove();
                target.teleToLocation(Location.findPointToStay(activeChar, 100, 150), activeChar.getGeoIndex());
              }

              this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
            }
          }

          if (this.isSSPossible()) {
            activeChar.unChargeShots(this.isMagic());
          }

        } else {
          if (((Player)activeChar).getParty() != null) {
            var4 = ((Player)activeChar).getParty().getPartyMembers().iterator();

            while(var4.hasNext()) {
              Player target = (Player)var4.next();
              if (!target.equals(activeChar) && canBeSummoned(target) == null) {
                if (this._requestWithCrystal >= 0) {
                  target.summonCharacterRequest(activeChar, Location.findPointToStay(activeChar, 100, 150), this._requestWithCrystal);
                } else {
                  target.stopMove();
                  target.teleToLocation(Location.findPointToStay(activeChar, 100, 150), activeChar.getGeoIndex());
                }

                this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
              }
            }
          }

          if (this.isSSPossible()) {
            activeChar.unChargeShots(this.isMagic());
          }

        }
      }
    }
  }

  public static SystemMessage canSummonHere(Player activeChar) {
    if (!activeChar.isAlikeDead() && !activeChar.isOlyParticipant() && !activeChar.isInObserverMode() && !activeChar.isFlying() && !activeChar.isFestivalParticipant()) {
      if (!activeChar.isInZoneBattle() && !activeChar.isInZone(ZoneType.SIEGE) && !activeChar.isInZone(ZoneType.no_restart) && !activeChar.isInZone(ZoneType.no_summon) && !activeChar.isInBoat() && activeChar.getReflection() == ReflectionManager.DEFAULT && !activeChar.isInZone(ZoneType.fun)) {
        return !activeChar.isInStoreMode() && !activeChar.isProcessingRequest() ? null : Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS;
      } else {
        return Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;
      }
    } else {
      return Msg.NOTHING_HAPPENED;
    }
  }

  public static SystemMessage canBeSummoned(Creature target) {
    if (target != null && target.isPlayer() && !target.isFlying() && !target.isInObserverMode() && !target.getPlayer().isFestivalParticipant() && target.getPlayer().getPlayerAccess().UseTeleport) {
      if (target.isOlyParticipant()) {
        return Msg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;
      } else if (!target.isInZoneBattle() && !target.isInZone(ZoneType.SIEGE) && !target.isInZone(ZoneType.no_restart) && !target.isInZone(ZoneType.no_summon) && target.getReflection() == ReflectionManager.DEFAULT && !target.isInBoat() && !target.isInZone(ZoneType.fun)) {
        if (target.isAlikeDead()) {
          return (new SystemMessage(1844)).addString(target.getName());
        } else if (target.getPvpFlag() == 0 && !target.isInCombat()) {
          Player pTarget = (Player)target;
          return pTarget.getPrivateStoreType() == 0 && !pTarget.isProcessingRequest() ? null : (new SystemMessage(1898)).addString(target.getName());
        } else {
          return (new SystemMessage(1843)).addString(target.getName());
        }
      } else {
        return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
      }
    } else {
      return Msg.INVALID_TARGET;
    }
  }
}
ClanGate
