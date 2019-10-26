//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

public class RequestPetUseItem extends L2GameClientPacket {
  private int _objectId;

  public RequestPetUseItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
      } else {
        activeChar.setActive();
        PetInstance pet = (PetInstance)activeChar.getPet();
        if (pet != null) {
          ItemInstance item = pet.getInventory().getItemByObjectId(this._objectId);
          if (item != null && item.getCount() >= 1L) {
            if (!activeChar.isAlikeDead() && !pet.isDead() && !pet.isOutOfControl()) {
              if (!pet.tryFeedItem(item)) {
                if (!ArrayUtils.contains(Config.ALT_ALLOWED_PET_POTIONS, item.getItemId())) {
                  SystemMessage sm = ItemFunctions.checkIfCanEquip(pet, item);
                  if (sm == null) {
                    if (item.isEquipped()) {
                      pet.getInventory().unEquipItem(item);
                    } else {
                      pet.getInventory().equipItem(item);
                    }

                    pet.broadcastCharInfo();
                  } else {
                    activeChar.sendPacket(sm);
                  }
                } else {
                  Skill[] skills = item.getTemplate().getAttachedSkills();
                  if (skills.length > 0) {
                    Skill[] var5 = skills;
                    int var6 = skills.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                      Skill skill = var5[var7];
                      Creature aimingTarget = skill.getAimingTarget(pet, pet.getTarget());
                      if (skill.checkCondition(pet, aimingTarget, false, false, true)) {
                        pet.getAI().Cast(skill, aimingTarget, false, false);
                      }
                    }
                  }

                }
              }
            } else {
              activeChar.sendPacket((new SystemMessage(113)).addItemName(item.getItemId()));
            }
          }
        }
      }
    }
  }
}
