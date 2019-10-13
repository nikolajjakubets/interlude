//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Util;

public class AdminEffects implements IAdminCommandHandler {
  public AdminEffects() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminEffects.Commands command = (AdminEffects.Commands)comm;
    if (!activeChar.getPlayerAccess().GodMode) {
      return false;
    } else {
      AbnormalEffect ae = AbnormalEffect.NULL;
      GameObject target = activeChar.getTarget();
      int val;
      int sh_level;
      switch(command) {
        case admin_invis:
        case admin_vis:
          if (activeChar.isInvisible()) {
            activeChar.setInvisibleType(InvisibleType.NONE);
            activeChar.broadcastCharInfo();
            if (activeChar.getPet() != null) {
              activeChar.getPet().broadcastCharInfo();
            }

            activeChar.setVar("gm_vis", "true", -1L);
          } else {
            activeChar.setInvisibleType(InvisibleType.NORMAL);
            activeChar.sendUserInfo(true);
            World.removeObjectFromPlayers(activeChar);
            activeChar.unsetVar("gm_vis");
          }
          break;
        case admin_gmspeed:
          if (wordList.length < 2) {
            val = 0;
          } else {
            try {
              val = Integer.parseInt(wordList[1]);
            } catch (Exception var18) {
              activeChar.sendMessage("USAGE: //gmspeed value=[0~4]");
              return false;
            }
          }

          List<Effect> superhaste = activeChar.getEffectList().getEffectsBySkillId(7029);
          sh_level = superhaste == null ? 0 : (superhaste.isEmpty() ? 0 : ((Effect)superhaste.get(0)).getSkill().getLevel());
          if (val == 0) {
            if (sh_level != 0) {
              activeChar.doCast(SkillTable.getInstance().getInfo(7029, sh_level), activeChar, true);
            }

            activeChar.unsetVar("gm_gmspeed");
          } else if (val >= 1 && val <= 4) {
            if (Config.SAVE_GM_EFFECTS) {
              activeChar.setVar("gm_gmspeed", String.valueOf(val), -1L);
            }

            if (val != sh_level) {
              if (sh_level != 0) {
                activeChar.doCast(SkillTable.getInstance().getInfo(7029, sh_level), activeChar, true);
              }

              activeChar.doCast(SkillTable.getInstance().getInfo(7029, val), activeChar, true);
            }
          } else {
            activeChar.sendMessage("USAGE: //gmspeed value=[0..4]");
          }
          break;
        case admin_invul:
          this.handleInvul(activeChar, activeChar);
          if (activeChar.isInvul()) {
            if (Config.SAVE_GM_EFFECTS) {
              activeChar.setVar("gm_invul", "true", -1L);
            }
          } else {
            activeChar.unsetVar("gm_invul");
          }
      }

      if (!activeChar.isGM()) {
        return false;
      } else {
        Iterator var22;
        Player player;
        switch(command) {
          case admin_offline_vis:
            var22 = GameObjectsStorage.getAllPlayers().iterator();

            while(var22.hasNext()) {
              player = (Player)var22.next();
              if (player != null && player.isInOfflineMode()) {
                player.setInvisibleType(InvisibleType.NONE);
                player.decayMe();
                player.spawnMe();
              }
            }

            return true;
          case admin_offline_invis:
            var22 = GameObjectsStorage.getAllPlayers().iterator();

            while(var22.hasNext()) {
              player = (Player)var22.next();
              if (player != null && player.isInOfflineMode()) {
                player.setInvisibleType(InvisibleType.NORMAL);
                player.decayMe();
              }
            }

            return true;
          case admin_earthquake:
            try {
              int intensity = Integer.parseInt(wordList[1]);
              sh_level = Integer.parseInt(wordList[2]);
              activeChar.broadcastPacket(new L2GameServerPacket[]{new Earthquake(activeChar.getLoc(), intensity, sh_level)});
              break;
            } catch (Exception var17) {
              activeChar.sendMessage("USAGE: //earthquake intensity duration");
              return false;
            }
          case admin_block:
            if (target != null && ((GameObject)target).isCreature()) {
              if (((Creature)target).isBlocked()) {
                return false;
              }

              ((Creature)target).abortAttack(true, false);
              ((Creature)target).abortCast(true, false);
              ((Creature)target).block();
              activeChar.sendMessage("Target blocked.");
              ((Creature)target).sendMessage("You have been paralyzed by a GM " + activeChar.getName());
              break;
            }

            activeChar.sendPacket(Msg.INVALID_TARGET);
            return false;
          case admin_unblock:
            if (target == null || !((GameObject)target).isCreature()) {
              activeChar.sendPacket(Msg.INVALID_TARGET);
              return false;
            }

            if (!((Creature)target).isBlocked()) {
              return false;
            }

            ((Creature)target).unblock();
            activeChar.sendMessage("Target unblocked.");
            ((Creature)target).sendMessage("You have been unblocked by a GM " + activeChar.getName());
            break;
          case admin_changename:
            if (wordList.length < 2) {
              activeChar.sendMessage("USAGE: //changename newName");
              return false;
            }

            if (target == null) {
              target = activeChar;
            }

            if (!((GameObject)target).isCreature()) {
              activeChar.sendPacket(Msg.INVALID_TARGET);
              return false;
            }

            String oldName = ((Creature)target).getName();
            String newName = Util.joinStrings(" ", wordList, 1);
            ((Creature)target).setName(newName);
            ((Creature)target).broadcastCharInfo();
            activeChar.sendMessage("Changed name from " + oldName + " to " + newName + ".");
            break;
          case admin_setinvul:
            if (target != null && ((GameObject)target).isPlayer()) {
              this.handleInvul(activeChar, (Player)target);
              break;
            }

            activeChar.sendPacket(Msg.INVALID_TARGET);
            return false;
          case admin_getinvul:
            if (target != null && ((GameObject)target).isCreature()) {
              activeChar.sendMessage("Target " + ((GameObject)target).getName() + "(object ID: " + ((GameObject)target).getObjectId() + ") is " + (!((Creature)target).isInvul() ? "NOT " : "") + "invul");
            }
            break;
          case admin_social:
            if (wordList.length < 2) {
              val = Rnd.get(1, 7);
            } else {
              try {
                val = Integer.parseInt(wordList[1]);
              } catch (NumberFormatException var16) {
                activeChar.sendMessage("USAGE: //social value");
                return false;
              }
            }

            if (target != null && target != activeChar) {
              if (((GameObject)target).isCreature()) {
                ((Creature)target).broadcastPacket(new L2GameServerPacket[]{new SocialAction(((GameObject)target).getObjectId(), val)});
              }
            } else {
              activeChar.broadcastPacket(new L2GameServerPacket[]{new SocialAction(activeChar.getObjectId(), val)});
            }
            break;
          case admin_abnormal:
            try {
              if (wordList.length > 1) {
                ae = AbnormalEffect.getByName(wordList[1]);
              }
            } catch (Exception var19) {
              activeChar.sendMessage("USAGE: //abnormal name");
              activeChar.sendMessage("//abnormal - Clears all abnormal effects");
              return false;
            }

            Creature effectTarget = target == null ? activeChar : (Creature)target;
            if (ae == AbnormalEffect.NULL) {
              ((Creature)effectTarget).startAbnormalEffect(AbnormalEffect.NULL);
              ((Creature)effectTarget).sendMessage("Abnormal effects clearned by admin.");
              if (effectTarget != activeChar) {
                ((Creature)effectTarget).sendMessage("Abnormal effects clearned.");
              }
            } else {
              ((Creature)effectTarget).startAbnormalEffect(ae);
              ((Creature)effectTarget).sendMessage("Admin added abnormal effect: " + ae.getName());
              if (effectTarget != activeChar) {
                ((Creature)effectTarget).sendMessage("Added abnormal effect: " + ae.getName());
              }
            }
            break;
          case admin_transform:
            try {
              val = Integer.parseInt(wordList[1]);
            } catch (Exception var15) {
              activeChar.sendMessage("USAGE: //transform transform_id");
              return false;
            }

            activeChar.setTransformation(val);
            break;
          case admin_showmovie:
            if (wordList.length < 2) {
              activeChar.sendMessage("USAGE: //showmovie id");
              return false;
            }

            int id;
            try {
              id = Integer.parseInt(wordList[1]);
            } catch (NumberFormatException var14) {
              activeChar.sendMessage("You must specify id");
              return false;
            }

            activeChar.showQuestMovie(id);
        }

        return true;
      }
    }
  }

  private void handleInvul(Player activeChar, Player target) {
    if (target.isInvul()) {
      target.setIsInvul(false);
      target.stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
      if (target.getPet() != null) {
        target.getPet().setIsInvul(false);
        target.getPet().stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
      }

      activeChar.sendMessage(target.getName() + " is now mortal.");
    } else {
      target.setIsInvul(true);
      target.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
      if (target.getPet() != null) {
        target.getPet().setIsInvul(true);
        target.getPet().startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
      }

      activeChar.sendMessage(target.getName() + " is now immortal.");
    }

  }

  public Enum[] getAdminCommandEnum() {
    return AdminEffects.Commands.values();
  }

  private static enum Commands {
    admin_invis,
    admin_vis,
    admin_offline_vis,
    admin_offline_invis,
    admin_earthquake,
    admin_block,
    admin_unblock,
    admin_changename,
    admin_gmspeed,
    admin_invul,
    admin_setinvul,
    admin_getinvul,
    admin_social,
    admin_abnormal,
    admin_transform,
    admin_showmovie;

    private Commands() {
    }
  }
}
