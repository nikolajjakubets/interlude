//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class FestivalMonsterInstance extends MonsterInstance {
  protected int _bonusMultiplier = 1;

  public FestivalMonsterInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this._hasRandomWalk = false;
  }

  public void setOfferingBonus(int bonusMultiplier) {
    this._bonusMultiplier = bonusMultiplier;
  }

  protected void onSpawn() {
    super.onSpawn();
    List<Player> pl = World.getAroundPlayers(this);
    if (!pl.isEmpty()) {
      List<Player> alive = new ArrayList(9);
      Iterator var3 = pl.iterator();

      while(var3.hasNext()) {
        Player p = (Player)var3.next();
        if (!p.isDead()) {
          alive.add(p);
        }
      }

      if (!alive.isEmpty()) {
        Player target = (Player)alive.get(Rnd.get(alive.size()));
        this.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
      }
    }
  }

  public void rollRewards(Entry<RewardType, RewardList> entry, Creature lastAttacker, Creature topDamager) {
    super.rollRewards(entry, lastAttacker, topDamager);
    if (entry.getKey() == RewardType.RATED_GROUPED) {
      if (topDamager.isPlayable()) {
        Player topDamagerPlayer = topDamager.getPlayer();
        Party associatedParty = topDamagerPlayer.getParty();
        if (associatedParty != null) {
          Player partyLeader = associatedParty.getPartyLeader();
          if (partyLeader != null) {
            ItemInstance bloodOfferings = ItemFunctions.createItem(5901);
            bloodOfferings.setCount((long)this._bonusMultiplier);
            partyLeader.getInventory().addItem(bloodOfferings);
            partyLeader.sendPacket(SystemMessage2.obtainItems(5901, (long)this._bonusMultiplier, 0));
          }
        }
      }
    }
  }

  public boolean isAggressive() {
    return true;
  }

  public int getAggroRange() {
    return 1000;
  }

  public boolean hasRandomAnimation() {
    return false;
  }

  public boolean canChampion() {
    return false;
  }
}
