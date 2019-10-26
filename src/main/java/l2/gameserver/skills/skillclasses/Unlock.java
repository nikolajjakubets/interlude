//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.templates.StatsSet;

public class Unlock extends Skill {
  private final int _unlockPower;

  public Unlock(StatsSet set) {
    super(set);
    this._unlockPower = set.getInteger("unlockPower", 0) + 100;
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (target != null && (!(target instanceof ChestInstance) || !target.isDead())) {
      if (target instanceof ChestInstance && activeChar.isPlayer()) {
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
      } else if (target.isDoor() && this._unlockPower != 0) {
        DoorInstance door = (DoorInstance)target;
        if (door.isOpen()) {
          activeChar.sendPacket(Msg.IT_IS_NOT_LOCKED);
          return false;
        } else if (!door.isUnlockable()) {
          activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
          return false;
        } else if (door.getKey() > 0) {
          activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
          return false;
        } else if (this._unlockPower - door.getLevel() * 100 < 0) {
          activeChar.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
          return false;
        } else {
          return super.checkCondition(activeChar, target, forceUse, dontMove, first);
        }
      } else {
        activeChar.sendPacket(Msg.INVALID_TARGET);
        return false;
      }
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(true) {
      while(true) {
        while(true) {
          Creature targ;
          do {
            if (!var3.hasNext()) {
              return;
            }

            targ = (Creature)var3.next();
          } while(targ == null);

          if (targ.isDoor()) {
            DoorInstance target = (DoorInstance)targ;
            if (!target.isOpen() && (target.getKey() > 0 || Rnd.chance(this._unlockPower - target.getLevel() * 100))) {
              target.openMe((Player)activeChar, true);
            } else {
              activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
            }
          } else if (targ instanceof ChestInstance) {
            ChestInstance target = (ChestInstance)targ;
            if (!target.isDead()) {
              target.tryOpen((Player)activeChar, this);
            }
          }
        }
      }
    }
  }
}
