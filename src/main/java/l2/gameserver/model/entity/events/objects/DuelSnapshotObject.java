//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.stats.Env;
import l2.gameserver.utils.Location;

public class DuelSnapshotObject implements Serializable {
  private final TeamType _team;
  private final HardReference<Player> _playerRef;
  private final int _activeClass;
  private final List<Effect> _effects;
  private final Location _returnLoc;
  private final double _currentHp;
  private final double _currentMp;
  private final double _currentCp;
  private boolean _isDead;

  public DuelSnapshotObject(Player player, TeamType team) {
    this._playerRef = player.getRef();
    this._team = team;
    this._returnLoc = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();
    this._currentCp = player.getCurrentCp();
    this._currentHp = player.getCurrentHp();
    this._currentMp = player.getCurrentMp();
    this._activeClass = player.getActiveClassId();
    List<Effect> effectList = player.getEffectList().getAllEffects();
    this._effects = new ArrayList(effectList.size());
    Iterator var4 = effectList.iterator();

    while(var4.hasNext()) {
      Effect $effect = (Effect)var4.next();
      Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), $effect.getSkill()));
      if (effect.isSaveable()) {
        effect.setCount($effect.getCount());
        effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());
        this._effects.add(effect);
      }
    }

  }

  public void restore(boolean abnormal) {
    Player player = this.getPlayer();
    if (player != null) {
      if (!abnormal) {
        player.getEffectList().stopAllEffects();
        if (this._activeClass == player.getActiveClassId()) {
          Iterator var3 = this._effects.iterator();

          while(var3.hasNext()) {
            Effect e = (Effect)var3.next();
            if (player.getEffectList().getEffectsBySkill(e.getSkill()) == null) {
              player.getEffectList().addEffect(e);
            }
          }
        }

        player.setCurrentCp(this._currentCp);
        player.setCurrentHpMp(this._currentHp, this._currentMp);
      }

    }
  }

  public void teleport() {
    Player player = this.getPlayer();
    player._stablePoint = null;
    if (player.isFrozen()) {
      player.stopFrozen();
    }

    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        Player player = DuelSnapshotObject.this.getPlayer();
        if (player != null) {
          player.teleToLocation(DuelSnapshotObject.this._returnLoc, ReflectionManager.DEFAULT);
        }
      }
    }, 5000L);
  }

  public Player getPlayer() {
    return (Player)this._playerRef.get();
  }

  public boolean isDead() {
    return this._isDead;
  }

  public void setDead() {
    this._isDead = true;
  }

  public Location getLoc() {
    return this._returnLoc;
  }

  public TeamType getTeam() {
    return this._team;
  }
}
