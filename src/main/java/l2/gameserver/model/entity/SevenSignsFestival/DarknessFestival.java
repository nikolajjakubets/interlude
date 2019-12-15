//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.SevenSignsFestival;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.instances.FestivalMonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

@Slf4j
public class DarknessFestival extends Reflection {
  private static final Logger _log = LoggerFactory.getLogger(DarknessFestival.class);
  public static final int FESTIVAL_LENGTH = 1080000;
  public static final int FESTIVAL_FIRST_SPAWN = 60000;
  public static final int FESTIVAL_SECOND_SPAWN = 540000;
  public static final int FESTIVAL_CHEST_SPAWN = 900000;
  private FestivalSpawn _witchSpawn;
  private FestivalSpawn _startLocation;
  private int currentState = 0;
  private boolean _challengeIncreased = false;
  private final int _levelRange;
  private final int _cabal;
  private Future<?> _spawnTimerTask;

  public DarknessFestival(Party party, int cabal, int level) {
    this.onCreate();
    this.setName("Darkness Festival");
    this.setParty(party);
    this._levelRange = level;
    this._cabal = cabal;
    this.startCollapseTimer(1140000L);
    if (cabal == 2) {
      this._witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS[this._levelRange]);
      this._startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS[this._levelRange]);
    } else {
      this._witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS[this._levelRange]);
      this._startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS[this._levelRange]);
    }

    party.setReflection(this);
    this.setReturnLoc(party.getPartyLeader().getLoc());

    for (Player p : party.getPartyMembers()) {
      p.setVar("backCoords", p.getLoc().toXYZString(), -1L);
      p.getEffectList().stopAllEffects();
      p.teleToLocation(Location.findPointToStay(this._startLocation.loc, 20, 100, this.getGeoIndex()), this);
    }

    this.scheduleNext();
    NpcTemplate witchTemplate = NpcHolder.getInstance().getTemplate(this._witchSpawn.npcId);

    try {
      SimpleSpawner npcSpawn = new SimpleSpawner(witchTemplate);
      npcSpawn.setLoc(this._witchSpawn.loc);
      npcSpawn.setReflection(this);
      this.addSpawn(npcSpawn);
      npcSpawn.doSpawn(true);
    } catch (Exception e) {
      log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    }

    this.sendMessageToParticipants("The festival will begin in 1 minute.");
  }

  private void scheduleNext() {
    switch (this.currentState) {
      case 0:
        this.currentState = 60000;
        this._spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            DarknessFestival.this.spawnFestivalMonsters(60, 0);
            DarknessFestival.this.sendMessageToParticipants("Go!");
            DarknessFestival.this.scheduleNext();
          }
        }, 60000L);
        break;
      case 60000:
        this.currentState = 540000;
        this._spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            DarknessFestival.this.spawnFestivalMonsters(60, 2);
            DarknessFestival.this.sendMessageToParticipants("Next wave arrived!");
            DarknessFestival.this.scheduleNext();
          }
        }, 480000L);
        break;
      case 540000:
        this.currentState = 900000;
        this._spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            DarknessFestival.this.spawnFestivalMonsters(60, 3);
            DarknessFestival.this.sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
          }
        }, 360000L);
    }

  }

  public void spawnFestivalMonsters(int respawnDelay, int spawnType) {
    int[][] spawns = null;
    switch (spawnType) {
      case 0:
      case 1:
        spawns = this._cabal == 2 ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS[this._levelRange] : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS[this._levelRange];
        break;
      case 2:
        spawns = this._cabal == 2 ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS[this._levelRange] : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS[this._levelRange];
        break;
      case 3:
        spawns = this._cabal == 2 ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS[this._levelRange] : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS[this._levelRange];
    }

    if (spawns != null) {

      for (int[] element : spawns) {
        FestivalSpawn currSpawn = new FestivalSpawn(element);
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(currSpawn.npcId);
        SimpleSpawner npcSpawn = new SimpleSpawner(npcTemplate);
        npcSpawn.setReflection(this);
        npcSpawn.setLoc(currSpawn.loc);
        npcSpawn.setHeading(Rnd.get(65536));
        npcSpawn.setAmount(1);
        npcSpawn.setRespawnDelay(respawnDelay);
        npcSpawn.startRespawn();
        FestivalMonsterInstance festivalMob = (FestivalMonsterInstance) npcSpawn.doSpawn(true);
        if (spawnType == 1) {
          festivalMob.setOfferingBonus(2);
        } else if (spawnType == 3) {
          festivalMob.setOfferingBonus(5);
        }

        this.addSpawn(npcSpawn);
      }
    }

  }

  public boolean increaseChallenge() {
    if (this._challengeIncreased) {
      return false;
    } else {
      this._challengeIncreased = true;
      this.spawnFestivalMonsters(60, 1);
      return true;
    }
  }

  public void collapse() {
    if (!this.isCollapseStarted()) {
      if (this._spawnTimerTask != null) {
        this._spawnTimerTask.cancel(false);
        this._spawnTimerTask = null;
      }

      if (SevenSigns.getInstance().getCurrentPeriod() == 1 && this.getParty() != null) {
        Player player = this.getParty().getPartyLeader();
        ItemInstance bloodOfferings = player.getInventory().getItemByItemId(5901);
        long offeringCount = bloodOfferings == null ? 0L : bloodOfferings.getCount();
        if (player.getInventory().destroyItem(bloodOfferings)) {
          boolean isHighestScore = SevenSignsFestival.getInstance().setFinalScore(this.getParty(), this._cabal, this._levelRange, offeringCount);
          player.sendPacket((new SystemMessage(1267)).addNumber(offeringCount));
          this.sendCustomMessageToParticipants("l2p.gameserver.model.entity.SevenSignsFestival.Ended");
          if (isHighestScore) {
            this.sendMessageToParticipants("Your score is highest!");
          }
        } else {
          player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings", player));
        }
      }

      super.collapse();
    }
  }

  private void sendMessageToParticipants(String s) {

    for (Player p : this.getPlayers()) {
      p.sendMessage(s);
    }

  }

  private void sendCustomMessageToParticipants(String s) {

    for (Player p : this.getPlayers()) {
      p.sendMessage(new CustomMessage(s, p));
    }

  }

  public void partyMemberExited() {
    if (this.getParty() == null || this.getParty().getMemberCount() <= 1) {
      this.collapse();
    }

  }

  public boolean canChampions() {
    return true;
  }

  public boolean isAutolootForced() {
    return true;
  }
}
