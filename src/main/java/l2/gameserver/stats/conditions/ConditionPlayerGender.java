//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;

public class ConditionPlayerGender extends Condition {
  private final ConditionPlayerGender.Gender _gender;

  public ConditionPlayerGender(ConditionPlayerGender.Gender gender) {
    this._gender = gender;
  }

  public ConditionPlayerGender(String gender) {
    this(ConditionPlayerGender.Gender.valueOf(gender.toUpperCase()));
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else {
      return ((Player)env.character).getSex() == this._gender.getPlayerGenderId();
    }
  }

  public static enum Gender {
    MALE(0),
    FEMALE(1);

    private final int _playerGenderId;

    private Gender(int playerGenderId) {
      this._playerGenderId = playerGenderId;
    }

    public int getPlayerGenderId() {
      return this._playerGenderId;
    }
  }
}
