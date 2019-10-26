//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.Player.EPledgeRank;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.stats.Env;

public class ConditionClanPlayerMinPledgeRank extends Condition {
  private final EPledgeRank _minPledgeRank;

  private static EPledgeRank parsePledgeRank(String pledgeRankText) {
    EPledgeRank pledgeRank = EPledgeRank.valueOf(pledgeRankText.toUpperCase());
    if (pledgeRank == null) {
      throw new IllegalArgumentException("Unknown pledge rank \"" + pledgeRankText + "\"");
    } else {
      return pledgeRank;
    }
  }

  public ConditionClanPlayerMinPledgeRank(String minPledgeRankName) {
    this(parsePledgeRank(minPledgeRankName));
  }

  public ConditionClanPlayerMinPledgeRank(EPledgeRank minPledgeRank) {
    this._minPledgeRank = minPledgeRank;
  }

  protected boolean testImpl(Env env) {
    if (env.character == null) {
      return false;
    } else {
      Player player = env.character.getPlayer();
      if (player == null) {
        return false;
      } else {
        Clan clan = player.getClan();
        if (clan == null) {
          return false;
        } else {
          return player.getPledgeRank().getRankId() >= this._minPledgeRank.getRankId();
        }
      }
    }
  }
}
