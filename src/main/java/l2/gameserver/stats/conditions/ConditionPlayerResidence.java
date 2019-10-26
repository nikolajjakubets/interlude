//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.stats.Env;

public class ConditionPlayerResidence extends Condition {
  private final int _id;
  private final ResidenceType _type;

  public ConditionPlayerResidence(int id, ResidenceType type) {
    this._id = id;
    this._type = type;
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else {
      Player player = (Player)env.character;
      Clan clan = player.getClan();
      if (clan == null) {
        return false;
      } else {
        int residenceId = clan.getResidenceId(this._type);
        return this._id > 0 ? residenceId == this._id : residenceId > 0;
      }
    }
  }
}
