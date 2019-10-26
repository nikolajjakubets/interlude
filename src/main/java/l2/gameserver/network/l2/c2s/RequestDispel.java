//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestDispel extends L2GameClientPacket {
  private int _objectId;
  private int _id;
  private int _level;

  public RequestDispel() {
  }

  protected void readImpl() throws Exception {
    this._objectId = this.readD();
    this._id = this.readD();
    this._level = this.readD();
  }

  protected void runImpl() throws Exception {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && (activeChar.getObjectId() == this._objectId || activeChar.getPet() != null)) {
      Creature target = activeChar;
      if (activeChar.getObjectId() != this._objectId) {
        target = activeChar.getPet();
      }

      Iterator var3 = ((Creature)target).getEffectList().getAllEffects().iterator();

      while(true) {
        Effect e;
        do {
          do {
            if (!var3.hasNext()) {
              activeChar.sendPacket((new SystemMessage(92)).addSkillName(this._id, this._level));
              return;
            }

            e = (Effect)var3.next();
          } while(e.getDisplayId() != this._id);
        } while(e.getDisplayLevel() != this._level);

        if (e.isOffensive() || e.getSkill().isMusic() || !e.getSkill().isSelfDispellable() || e.getSkill().getSkillType() == SkillType.TRANSFORMATION) {
          return;
        }

        e.exit();
      }
    }
  }
}
