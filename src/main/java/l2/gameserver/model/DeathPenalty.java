//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;

import java.util.Iterator;

public class DeathPenalty {
  private static final int _skillId = 5076;
  private static final int _fortuneOfNobleseSkillId = 1325;
  private static final int _charmOfLuckSkillId = 2168;
  private HardReference<Player> _playerRef;
  private int _level;
  private boolean _hasCharmOfLuck;

  public DeathPenalty(Player player, int level) {
    this._playerRef = (HardReference<Player>) player.getRef();
    this._level = level;
  }

  public Player getPlayer() {
    return (Player)this._playerRef.get();
  }

  public int getLevel() {
    if (this._level > 15) {
      this._level = 15;
    }

    if (this._level < 0) {
      this._level = 0;
    }

    return Config.ALLOW_DEATH_PENALTY_C5 ? this._level : 0;
  }

  public int getLevelOnSaveDB() {
    if (this._level > 15) {
      this._level = 15;
    }

    if (this._level < 0) {
      this._level = 0;
    }

    return this._level;
  }

  public void notifyDead(Creature killer) {
    if (Config.ALLOW_DEATH_PENALTY_C5) {
      if (this._hasCharmOfLuck) {
        this._hasCharmOfLuck = false;
      } else if (killer != null && !killer.isPlayable()) {
        Player player = this.getPlayer();
        if (player != null && player.getLevel() > 9) {
          int karmaBonus = player.getKarma() / Config.ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
          if (karmaBonus < 0) {
            karmaBonus = 0;
          }

          if (Rnd.chance(Config.ALT_DEATH_PENALTY_C5_CHANCE + karmaBonus)) {
            this.addLevel();
          }

        }
      }
    }
  }

  public void restore(Player player) {
    Skill remove = player.getKnownSkill(5076);
    if (remove != null) {
      player.removeSkill(remove, true);
    }

    if (Config.ALLOW_DEATH_PENALTY_C5) {
      if (this.getLevel() > 0) {
        player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
        player.sendPacket((new SystemMessage(1916)).addNumber(this.getLevel()));
      }

      player.sendEtcStatusUpdate();
      player.updateStats();
    }
  }

  public void addLevel() {
    Player player = this.getPlayer();
    if (player != null && this.getLevel() < 15 && !player.isGM()) {
      if (this.getLevel() != 0) {
        Skill remove = player.getKnownSkill(5076);
        if (remove != null) {
          player.removeSkill(remove, true);
        }
      }

      ++this._level;
      player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
      player.sendPacket((new SystemMessage(1916)).addNumber(this.getLevel()));
      player.sendEtcStatusUpdate();
      player.updateStats();
    }
  }

  public void reduceLevel() {
    Player player = this.getPlayer();
    if (player != null && this.getLevel() > 0) {
      Skill remove = player.getKnownSkill(5076);
      if (remove != null) {
        player.removeSkill(remove, true);
      }

      --this._level;
      if (this.getLevel() > 0) {
        player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
        player.sendPacket((new SystemMessage(1916)).addNumber(this.getLevel()));
      } else {
        player.sendPacket(Msg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);
      }

      player.sendEtcStatusUpdate();
      player.updateStats();
    }
  }

  public void checkCharmOfLuck() {
    Player player = this.getPlayer();
    if (player != null) {
      Iterator var2 = player.getEffectList().getAllEffects().iterator();

      while(var2.hasNext()) {
        Effect e = (Effect)var2.next();
        if (e.getSkill().getId() == 2168 || e.getSkill().getId() == 1325) {
          this._hasCharmOfLuck = true;
          return;
        }
      }
    }

    this._hasCharmOfLuck = false;
  }
}
