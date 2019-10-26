//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TeleportUtils;
import org.apache.commons.lang3.tuple.Pair;

public class RequestRestartPoint extends L2GameClientPacket {
  private RestartType _restartType;

  public RequestRestartPoint() {
  }

  protected void readImpl() {
    this._restartType = (RestartType)ArrayUtils.valid(RestartType.VALUES, this.readD());
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (this._restartType != null && activeChar != null) {
      if (activeChar.isFakeDeath()) {
        activeChar.breakFakeDeath();
      } else if (!activeChar.isDead() && !activeChar.isGM()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isFestivalParticipant()) {
        activeChar.doRevive();
      } else if (activeChar.isResurectProhibited()) {
        activeChar.sendActionFailed();
      } else {
        switch(this._restartType) {
          case FIXED:
            if (activeChar.getPlayerAccess().ResurectFixed) {
              activeChar.doRevive(100.0D);
            } else if (ItemFunctions.removeItem(activeChar, 9218, 1L, true) == 1L) {
              activeChar.sendMessage(new CustomMessage("YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT", activeChar, new Object[0]));
              activeChar.doRevive(100.0D);
            } else {
              activeChar.sendPacket(new IStaticPacket[]{ActionFail.STATIC, new Die(activeChar)});
            }
            break;
          default:
            Location loc = null;
            Reflection ref = activeChar.getReflection();
            GlobalEvent e;
            if (ref == ReflectionManager.DEFAULT) {
              for(Iterator var4 = activeChar.getEvents().iterator(); var4.hasNext(); loc = e.getRestartLoc(activeChar, this._restartType)) {
                e = (GlobalEvent)var4.next();
              }
            }

            if (loc == null) {
              loc = defaultLoc(this._restartType, activeChar);
            }

            if (loc != null) {
              Pair<Integer, OnAnswerListener> ask = activeChar.getAskListener(false);
              if (ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener)ask.getValue()).isForPet()) {
                activeChar.getAskListener(true);
              }

              activeChar.setPendingRevive(true);
              activeChar.teleToLocation(loc, ReflectionManager.DEFAULT);
            } else {
              activeChar.sendPacket(new IStaticPacket[]{ActionFail.STATIC, new Die(activeChar)});
            }
        }

      }
    }
  }

  public static Location defaultLoc(RestartType restartType, Player activeChar) {
    Location loc = null;
    Clan clan = activeChar.getClan();
    switch(restartType) {
      case TO_CLANHALL:
        if (clan != null && clan.getHasHideout() != 0) {
          ClanHall clanHall = activeChar.getClanHall();
          loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CLANHALL);
          if (clanHall.getFunction(5) != null) {
            activeChar.restoreExp((double)clanHall.getFunction(5).getLevel());
          }
        }
        break;
      case TO_CASTLE:
        if (clan != null && clan.getCastle() != 0) {
          Castle castle = activeChar.getCastle();
          loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CASTLE);
          if (castle.getFunction(5) != null) {
            activeChar.restoreExp((double)castle.getFunction(5).getLevel());
          }
        }
        break;
      default:
        loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_VILLAGE);
    }

    return loc;
  }
}
