//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.GameTimeController;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.s2c.ExFishingEnd;
import l2.gameserver.network.l2.s2c.ExFishingHpRegen;
import l2.gameserver.network.l2.s2c.ExFishingStart;
import l2.gameserver.network.l2.s2c.ExFishingStartCombat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.FishTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class Fishing {
  private final Player _fisher;
  public static final int FISHING_NONE = 0;
  public static final int FISHING_STARTED = 1;
  public static final int FISHING_WAITING = 2;
  public static final int FISHING_COMBAT = 3;
  private AtomicInteger _state;
  private int _time;
  private int _stop;
  private int _gooduse;
  private int _anim;
  private int _combatMode = -1;
  private int _deceptiveMode;
  private int _fishCurHP;
  private FishTemplate _fish;
  private int _lureId;
  private Future<?> _fishingTask;
  private Location _fishLoc = new Location();

  public Fishing(Player fisher) {
    this._fisher = fisher;
    this._state = new AtomicInteger(0);
  }

  public void setFish(FishTemplate fish) {
    this._fish = fish;
  }

  public void setLureId(int lureId) {
    this._lureId = lureId;
  }

  public int getLureId() {
    return this._lureId;
  }

  public void setFishLoc(Location loc) {
    this._fishLoc.x = loc.x;
    this._fishLoc.y = loc.y;
    this._fishLoc.z = loc.z;
  }

  public Location getFishLoc() {
    return this._fishLoc;
  }

  public void startFishing() {
    if (this._state.compareAndSet(0, 1)) {
      this._fisher.setFishing(true);
      this._fisher.broadcastCharInfo();
      this._fisher.broadcastPacket(new L2GameServerPacket[]{new ExFishingStart(this._fisher, this._fish.getType(), this._fisher.getFishLoc(), isNightLure(this._lureId))});
      this._fisher.sendPacket(Msg.STARTS_FISHING);
      this.startLookingForFishTask();
    }
  }

  public void stopFishing() {
    if (this._state.getAndSet(0) != 0) {
      this.stopFishingTask();
      this._fisher.setFishing(false);
      this._fisher.broadcastPacket(new L2GameServerPacket[]{new ExFishingEnd(this._fisher, false)});
      this._fisher.broadcastCharInfo();
      this._fisher.sendPacket(Msg.CANCELS_FISHING);
    }
  }

  public void endFishing(boolean win) {
    if (this._state.compareAndSet(3, 0)) {
      this.stopFishingTask();
      this._fisher.setFishing(false);
      this._fisher.broadcastPacket(new L2GameServerPacket[]{new ExFishingEnd(this._fisher, win)});
      this._fisher.broadcastCharInfo();
      this._fisher.sendPacket(Msg.ENDS_FISHING);
    }
  }

  private void stopFishingTask() {
    if (this._fishingTask != null) {
      this._fishingTask.cancel(false);
      this._fishingTask = null;
    }

  }

  private void startLookingForFishTask() {
    if (this._state.compareAndSet(1, 2)) {
      long checkDelay = 10000L;
      switch(this._fish.getGroup()) {
        case 0:
          checkDelay = Math.round((double)this._fish.getGutsCheckTime() * 1.33D);
          break;
        case 1:
          checkDelay = (long)this._fish.getGutsCheckTime();
          break;
        case 2:
          checkDelay = Math.round((double)this._fish.getGutsCheckTime() * 0.66D);
      }

      this._fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Fishing.LookingForFishTask(), 10000L, checkDelay);
    }
  }

  public boolean isInCombat() {
    return this._state.get() == 3;
  }

  private void startFishCombat() {
    if (this._state.compareAndSet(2, 3)) {
      this._stop = 0;
      this._gooduse = 0;
      this._anim = 0;
      this._time = this._fish.getCombatTime() / 1000;
      this._fishCurHP = this._fish.getHP();
      this._combatMode = Rnd.chance(20) ? 1 : 0;
      switch(getLureGrade(this._lureId)) {
        case 0:
        case 1:
          this._deceptiveMode = 0;
          break;
        case 2:
          this._deceptiveMode = Rnd.chance(10) ? 1 : 0;
      }

      ExFishingStartCombat efsc = new ExFishingStartCombat(this._fisher, this._time, this._fish.getHP(), this._combatMode, this._fish.getGroup(), this._deceptiveMode);
      this._fisher.broadcastPacket(new L2GameServerPacket[]{efsc});
      this._fisher.sendPacket(Msg.SUCCEEDED_IN_GETTING_A_BITE);
      this._fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Fishing.FishCombatTask(), 1000L, 1000L);
    }
  }

  private void changeHp(int hp, int pen) {
    this._fishCurHP -= hp;
    if (this._fishCurHP < 0) {
      this._fishCurHP = 0;
    }

    this._fisher.broadcastPacket(new L2GameServerPacket[]{new ExFishingHpRegen(this._fisher, this._time, this._fishCurHP, this._combatMode, this._gooduse, this._anim, pen, this._deceptiveMode)});
    this._gooduse = 0;
    this._anim = 0;
    if (this._fishCurHP > this._fish.getHP() * 2) {
      this._fishCurHP = this._fish.getHP() * 2;
      this.doDie(false);
    } else if (this._fishCurHP == 0) {
      this.doDie(true);
    }

  }

  private void doDie(boolean win) {
    this.stopFishingTask();
    if (win) {
      if (!this._fisher.isInPeaceZone() && Rnd.chance(5)) {
        win = false;
        this._fisher.sendPacket(Msg.YOU_HAVE_CAUGHT_A_MONSTER);
        spawnPenaltyMonster(this._fisher);
      } else {
        this._fisher.sendPacket(Msg.SUCCEEDED_IN_FISHING);
        ItemFunctions.addItem(this._fisher, this._fish.getId(), 1L, true);
        FishingChampionShipManager.getInstance().newFish(this._fisher, this._lureId);
      }
    }

    this.endFishing(win);
  }

  public void useFishingSkill(int dmg, int pen, SkillType skillType) {
    if (this.isInCombat()) {
      byte mode;
      if (skillType == SkillType.REELING && !GameTimeController.getInstance().isNowNight()) {
        mode = 1;
      } else if (skillType == SkillType.PUMPING && GameTimeController.getInstance().isNowNight()) {
        mode = 1;
      } else {
        mode = 0;
      }

      this._anim = mode + 1;
      if (Rnd.chance(10)) {
        this._fisher.sendPacket(Msg.FISH_HAS_RESISTED);
        this._gooduse = 0;
        this.changeHp(0, pen);
      } else {
        if (this._combatMode == mode) {
          if (this._deceptiveMode == 0) {
            showMessage(this._fisher, dmg, pen, skillType, 1);
            this._gooduse = 1;
            this.changeHp(dmg, pen);
          } else {
            showMessage(this._fisher, dmg, pen, skillType, 2);
            this._gooduse = 2;
            this.changeHp(-dmg, pen);
          }
        } else if (this._deceptiveMode == 0) {
          showMessage(this._fisher, dmg, pen, skillType, 2);
          this._gooduse = 2;
          this.changeHp(-dmg, pen);
        } else {
          showMessage(this._fisher, dmg, pen, skillType, 3);
          this._gooduse = 1;
          this.changeHp(dmg, pen);
        }

      }
    }
  }

  private static void showMessage(Player fisher, int dmg, int pen, SkillType skillType, int messageId) {
    switch(messageId) {
      case 1:
        if (skillType == SkillType.PUMPING) {
          fisher.sendPacket((new SystemMessage(1465)).addNumber(dmg));
          if (pen == 50) {
            fisher.sendPacket((new SystemMessage(1672)).addNumber(pen));
          }
        } else {
          fisher.sendPacket((new SystemMessage(1467)).addNumber(dmg));
          if (pen == 50) {
            fisher.sendPacket((new SystemMessage(1671)).addNumber(pen));
          }
        }
        break;
      case 2:
        if (skillType == SkillType.PUMPING) {
          fisher.sendPacket((new SystemMessage(1466)).addNumber(dmg));
        } else {
          fisher.sendPacket((new SystemMessage(1468)).addNumber(dmg));
        }
        break;
      case 3:
        if (skillType == SkillType.PUMPING) {
          fisher.sendPacket((new SystemMessage(1465)).addNumber(dmg));
          if (pen == 50) {
            fisher.sendPacket((new SystemMessage(1672)).addNumber(pen));
          }
        } else {
          fisher.sendPacket((new SystemMessage(1467)).addNumber(dmg));
          if (pen == 50) {
            fisher.sendPacket((new SystemMessage(1671)).addNumber(pen));
          }
        }
    }

  }

  public static void spawnPenaltyMonster(Player fisher) {
    int npcId = 18319 + Math.min(fisher.getLevel() / 11, 7);
    MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(npcId));
    npc.setSpawnedLoc(Location.findPointToStay(fisher, 100, 120));
    npc.setReflection(fisher.getReflection());
    npc.setHeading(fisher.getHeading() - '耀');
    npc.spawnMe(npc.getSpawnedLoc());
    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, fisher, Rnd.get(1, 100));
  }

  public static int getRandomFishType(int lureId, int fishLvl, int dist) {
    int check = Rnd.get(100);
    byte type;
    switch(lureId) {
      case 6519:
      case 6520:
      case 6521:
      case 8505:
      case 8507:
        if (check <= 54) {
          type = 1;
        } else if (check <= 74) {
          type = 0;
        } else if (check <= 94) {
          type = 2;
        } else {
          type = 3;
        }
        break;
      case 6522:
      case 6523:
      case 6524:
      case 8508:
      case 8510:
        if (check <= 54) {
          type = 0;
        } else if (check <= 74) {
          type = 1;
        } else if (check <= 94) {
          type = 2;
        } else {
          type = 3;
        }
        break;
      case 6525:
      case 6526:
      case 6527:
      case 8511:
      case 8513:
        if (check <= 55) {
          type = 2;
        } else if (check <= 74) {
          type = 1;
        } else if (check <= 94) {
          type = 0;
        } else {
          type = 3;
        }
        break;
      case 7610:
      case 7611:
      case 7612:
      case 7613:
      case 8496:
      case 8497:
      case 8498:
      case 8499:
      case 8500:
      case 8501:
      case 8502:
      case 8503:
      case 8504:
        type = 3;
        break;
      case 7807:
        if (check <= 54) {
          type = 5;
        } else if (check <= 77) {
          type = 4;
        } else {
          type = 6;
        }
        break;
      case 7808:
        if (check <= 54) {
          type = 4;
        } else if (check <= 77) {
          type = 6;
        } else {
          type = 5;
        }
        break;
      case 7809:
        if (check <= 54) {
          type = 6;
        } else if (check <= 77) {
          type = 5;
        } else {
          type = 4;
        }
        break;
      case 8484:
        if (check <= 33) {
          type = 0;
        } else if (check <= 66) {
          type = 1;
        } else {
          type = 2;
        }
        break;
      case 8485:
        if (check <= 33) {
          type = 7;
        } else if (check <= 66) {
          type = 8;
        } else {
          type = 9;
        }
        break;
      case 8486:
        if (check <= 33) {
          type = 4;
        } else if (check <= 66) {
          type = 5;
        } else {
          type = 6;
        }
        break;
      case 8506:
        if (check <= 54) {
          type = 8;
        } else if (check <= 77) {
          type = 7;
        } else {
          type = 9;
        }
        break;
      case 8509:
        if (check <= 54) {
          type = 7;
        } else if (check <= 77) {
          type = 9;
        } else {
          type = 8;
        }
        break;
      case 8512:
        if (check <= 54) {
          type = 9;
        } else if (check <= 77) {
          type = 8;
        } else {
          type = 7;
        }
        break;
      case 8548:
        if (check <= 32) {
          type = 1;
        } else if (check <= 64) {
          type = 2;
        } else if (check <= 96) {
          type = 0;
        } else if (dist == 4 && fishLvl > 19) {
          type = 10;
        } else {
          type = 0;
        }
        break;
      default:
        type = 1;
    }

    return type;
  }

  public static int getRandomFishLvl(Player player) {
    int skilllvl = false;
    Effect effect = player.getEffectList().getEffectByStackType("fishPot");
    int skilllvl;
    if (effect != null) {
      skilllvl = (int)effect.getSkill().getPower();
    } else {
      skilllvl = player.getSkillLevel(1315);
    }

    if (skilllvl <= 0) {
      return 1;
    } else {
      int check = Rnd.get(100);
      int randomlvl;
      if (check < 50) {
        randomlvl = skilllvl;
      } else if (check <= 85) {
        randomlvl = skilllvl - 1;
        if (randomlvl <= 0) {
          randomlvl = 1;
        }
      } else {
        randomlvl = skilllvl + 1;
      }

      randomlvl = Math.min(27, Math.max(1, randomlvl));
      return randomlvl;
    }
  }

  public static int getFishGroup(int lureId) {
    switch(lureId) {
      case 7807:
      case 7808:
      case 7809:
      case 8486:
        return 0;
      case 8485:
      case 8506:
      case 8509:
      case 8512:
        return 2;
      default:
        return 1;
    }
  }

  public static int getLureGrade(int lureId) {
    switch(lureId) {
      case 6519:
      case 6522:
      case 6525:
      case 8505:
      case 8508:
      case 8511:
        return 0;
      case 6520:
      case 6523:
      case 6526:
      case 7610:
      case 7611:
      case 7612:
      case 7613:
      case 7807:
      case 7808:
      case 7809:
      case 8484:
      case 8485:
      case 8486:
      case 8496:
      case 8497:
      case 8498:
      case 8499:
      case 8500:
      case 8501:
      case 8502:
      case 8503:
      case 8504:
      case 8506:
      case 8509:
      case 8512:
      case 8548:
        return 1;
      case 6521:
      case 6524:
      case 6527:
      case 8507:
      case 8510:
      case 8513:
        return 2;
      default:
        return -1;
    }
  }

  public static boolean isNightLure(int lureId) {
    switch(lureId) {
      case 8485:
        return true;
      case 8486:
      case 8487:
      case 8488:
      case 8489:
      case 8490:
      case 8491:
      case 8492:
      case 8493:
      case 8494:
      case 8495:
      case 8507:
      default:
        return false;
      case 8496:
      case 8497:
      case 8498:
      case 8499:
      case 8500:
      case 8501:
      case 8502:
      case 8503:
      case 8504:
        return true;
      case 8505:
      case 8508:
      case 8511:
        return true;
      case 8506:
      case 8509:
      case 8512:
        return true;
      case 8510:
      case 8513:
        return true;
    }
  }

  private class FishCombatTask extends RunnableImpl {
    private FishCombatTask() {
    }

    public void runImpl() throws Exception {
      if (Fishing.this._fishCurHP >= Fishing.this._fish.getHP() * 2) {
        Fishing.this._fisher.sendPacket(Msg.THE_FISH_GOT_AWAY);
        Fishing.this.doDie(false);
      } else if (Fishing.this._time <= 0) {
        Fishing.this._fisher.sendPacket(Msg.TIME_IS_UP_SO_THAT_FISH_GOT_AWAY);
        Fishing.this.doDie(false);
      } else {
        Fishing.this._time--;
        if (Fishing.this._combatMode == 1 && Fishing.this._deceptiveMode == 0 || Fishing.this._combatMode == 0 && Fishing.this._deceptiveMode == 1) {
          Fishing.this._fishCurHP = Fishing.this._fishCurHP + Fishing.this._fish.getHpRegen();
        }

        if (Fishing.this._stop == 0) {
          Fishing.this._stop = 1;
          if (Rnd.chance(30)) {
            Fishing.this._combatMode = Fishing.this._combatMode == 0 ? 1 : 0;
          }

          if (Fishing.this._fish.getGroup() == 2 && Rnd.chance(10)) {
            Fishing.this._deceptiveMode = Fishing.this._deceptiveMode == 0 ? 1 : 0;
          }
        } else {
          Fishing.this._stop--;
        }

        ExFishingHpRegen efhr = new ExFishingHpRegen(Fishing.this._fisher, Fishing.this._time, Fishing.this._fishCurHP, Fishing.this._combatMode, 0, Fishing.this._anim, 0, Fishing.this._deceptiveMode);
        if (Fishing.this._anim != 0) {
          Fishing.this._fisher.broadcastPacket(new L2GameServerPacket[]{efhr});
        } else {
          Fishing.this._fisher.sendPacket(efhr);
        }
      }

    }
  }

  protected class LookingForFishTask extends RunnableImpl {
    private long _endTaskTime;

    protected LookingForFishTask() {
      this._endTaskTime = System.currentTimeMillis() + (long)Fishing.this._fish.getWaitTime() + 10000L;
    }

    public void runImpl() throws Exception {
      if (System.currentTimeMillis() >= this._endTaskTime) {
        Fishing.this._fisher.sendPacket(Msg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
        Fishing.this.stopFishingTask();
        Fishing.this.endFishing(false);
      } else if (!GameTimeController.getInstance().isNowNight() && Fishing.isNightLure(Fishing.this._lureId)) {
        Fishing.this._fisher.sendPacket(Msg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
        Fishing.this.stopFishingTask();
        Fishing.this.endFishing(false);
      } else {
        int check = Rnd.get(1000);
        if (Fishing.this._fish.getFishGuts() > check) {
          Fishing.this.stopFishingTask();
          Fishing.this.startFishCombat();
        }

      }
    }
  }
}
