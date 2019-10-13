//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.AcquireSkillInfo;
import l2.gameserver.tables.SkillTable;

public class RequestAquireSkillInfo extends L2GameClientPacket {
  private int _id;
  private int _level;
  private AcquireType _type;

  public RequestAquireSkillInfo() {
  }

  protected void readImpl() {
    this._id = this.readD();
    this._level = this.readD();
    this._type = (AcquireType)ArrayUtils.valid(AcquireType.VALUES, this.readD());
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null && player.getTransformation() == 0 && !player.isCursedWeaponEquipped() && SkillTable.getInstance().getInfo(this._id, this._level) != null && this._type != null) {
      NpcInstance trainer = player.getLastNpc();
      if (trainer != null && trainer.isInActingRange(player)) {
        int clsId = player.getVarInt("AcquireSkillClassId", player.getClassId().getId());
        ClassId classId = clsId >= 0 && clsId < ClassId.VALUES.length ? ClassId.VALUES[clsId] : null;
        SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, classId, this._id, this._level, this._type);
        if (skillLearn != null) {
          if (Config.ALT_DISABLE_SPELLBOOKS && this._type == AcquireType.NORMAL) {
            this.sendPacket(new AcquireSkillInfo(this._type, skillLearn, 0, 0));
          } else {
            this.sendPacket(new AcquireSkillInfo(this._type, skillLearn, skillLearn.getItemId(), (int)skillLearn.getItemCount()));
          }

        }
      }
    }
  }
}
