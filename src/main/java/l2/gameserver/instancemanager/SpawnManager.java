//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.listener.game.OnSSPeriodListener;
import l2.gameserver.model.HardSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.templates.spawn.SpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnManager {
  private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);
  private static SpawnManager _instance = new SpawnManager();
  private static final String SPAWN_EVENT_NAME_SSQ_EVENT = "[ssq_event]";
  private static final String SPAWN_EVENT_NAME_AVARICE_NONE = "[ssq_seal1_none]";
  private static final String SPAWN_EVENT_NAME_AVARICE_DUSK = "[ssq_seal1_twilight]";
  private static final String SPAWN_EVENT_NAME_AVARICE_DAWN = "[ssq_seal1_dawn]";
  private static final String SPAWN_EVENT_NAME_GNOSIS_NONE = "[ssq_seal2_none]";
  private static final String SPAWN_EVENT_NAME_GNOSIS_DUSK = "[ssq_seal2_twilight]";
  private static final String SPAWN_EVENT_NAME_GNOSIS_DAWN = "[ssq_seal2_dawn]";
  private static final String DAWN_GROUP = "dawn_spawn";
  private static final String DUSK_GROUP = "dusk_spawn";
  private Map<String, List<Spawner>> _spawns = new ConcurrentHashMap();
  private SpawnManager.Listeners _listeners = new SpawnManager.Listeners();

  public static SpawnManager getInstance() {
    return _instance;
  }

  private SpawnManager() {
    Iterator var1 = SpawnHolder.getInstance().getSpawns().entrySet().iterator();

    while(var1.hasNext()) {
      Entry<String, List<SpawnTemplate>> entry = (Entry)var1.next();
      this.fillSpawn((String)entry.getKey(), (List)entry.getValue());
    }

    GameTimeController.getInstance().addListener(this._listeners);
    SevenSigns.getInstance().addListener(this._listeners);
  }

  public List<Spawner> fillSpawn(String group, List<SpawnTemplate> templateList) {
    if (Config.DONTLOADSPAWN) {
      return Collections.emptyList();
    } else {
      List<Spawner> spawnerList = (List)this._spawns.get(group);
      if (spawnerList == null) {
        this._spawns.put(group, spawnerList = new ArrayList(templateList.size()));
      }

      Iterator var4 = templateList.iterator();

      while(var4.hasNext()) {
        SpawnTemplate template = (SpawnTemplate)var4.next();

        try {
          HardSpawner spawner = new HardSpawner(template);
          ((List)spawnerList).add(spawner);
          NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(spawner.getCurrentNpcId());
          if (Config.RATE_MOB_SPAWN > 1 && npcTemplate.getInstanceClass() == MonsterInstance.class && npcTemplate.level >= Config.RATE_MOB_SPAWN_MIN_LEVEL && npcTemplate.level <= Config.RATE_MOB_SPAWN_MAX_LEVEL) {
            spawner.setAmount(template.getCount() * Config.RATE_MOB_SPAWN);
          } else {
            spawner.setAmount(template.getCount());
          }

          spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
          spawner.setRespawnCron(template.getRespawnCron());
          spawner.setReflection(ReflectionManager.DEFAULT);
          spawner.setRespawnTime(0);
          if (npcTemplate.isRaid && group.equals(PeriodOfDay.ALL.name())) {
            RaidBossSpawnManager.getInstance().addNewSpawn(npcTemplate.getNpcId(), spawner);
          }
        } catch (Exception var8) {
          var8.printStackTrace();
        }
      }

      return (List)spawnerList;
    }
  }

  public void spawnAll() {
    this.spawn(PeriodOfDay.ALL.name());
    if (Config.ALLOW_EVENT_GATEKEEPER) {
      this.spawn("[event_gate]");
    }

    if (Config.ALLOW_GLOBAL_GK) {
      this.spawn("[global_gatekeeper]");
    }

    if (Config.ALLOW_BUFFER) {
      this.spawn("[npc_buffer]");
    }

    if (Config.ALLOW_GMSHOP) {
      this.spawn("[gm_shop]");
    }

    if (Config.ALLOW_AUCTIONER) {
      this.spawn("[auctioner]");
    }

    if (Config.ALLOW_GLOBAL_SERVICES) {
      this.spawn("[global_services]");
    }

    if (Config.ALLOW_PVP_EVENT_MANAGER) {
      this.spawn("[pvp_event_manager]");
    }

    if (Config.ALLOW_TREASURE_BOX) {
      this.spawn("[treasure_box]");
    }

    if (Config.SERVICES_ALLOW_LOTTERY) {
      this.spawn("[lotto_manager]");
    }

    if (!Config.CLASS_MASTERS_CLASSES.isEmpty()) {
      this.spawn("class_master");
    }

  }

  public void spawn(String group) {
    List<Spawner> spawnerList = this.getSpawners(group);
    if (spawnerList != null) {
      int npcSpawnCount = 0;
      Iterator var4 = spawnerList.iterator();

      while(var4.hasNext()) {
        Spawner spawner = (Spawner)var4.next();
        npcSpawnCount += spawner.init();
        if (npcSpawnCount % 1000 == 0 && npcSpawnCount != 0) {
          _log.info("SpawnManager: spawned " + npcSpawnCount + " npc for group: " + group);
        }
      }

      _log.info("SpawnManager: spawned " + npcSpawnCount + " npc; spawns: " + spawnerList.size() + "; group: " + group);
    }
  }

  public void despawn(String group) {
    List<Spawner> spawnerList = (List)this._spawns.get(group);
    if (spawnerList != null) {
      Iterator var3 = spawnerList.iterator();

      while(var3.hasNext()) {
        Spawner spawner = (Spawner)var3.next();
        spawner.deleteAll();
      }

    }
  }

  public List<Spawner> getSpawners(String group) {
    List<Spawner> list = (List)this._spawns.get(group);
    return list == null ? Collections.emptyList() : list;
  }

  public void reloadAll() {
    RaidBossSpawnManager.getInstance().cleanUp();
    Iterator var1 = this._spawns.values().iterator();

    while(var1.hasNext()) {
      List<Spawner> spawnerList = (List)var1.next();
      Iterator var3 = spawnerList.iterator();

      while(var3.hasNext()) {
        Spawner spawner = (Spawner)var3.next();
        spawner.deleteAll();
      }
    }

    RaidBossSpawnManager.getInstance().reloadBosses();
    this.spawnAll();
    if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
      SevenSigns.getInstance().getCabalHighestScore();
    }

    this._listeners.onPeriodChange(SevenSigns.getInstance().getCurrentPeriod());
    if (GameTimeController.getInstance().isNowNight()) {
      this._listeners.onNight();
    } else {
      this._listeners.onDay();
    }

  }

  private class Listeners implements OnDayNightChangeListener, OnSSPeriodListener {
    private Listeners() {
    }

    public void onDay() {
      SpawnManager.this.despawn(PeriodOfDay.NIGHT.name());
      SpawnManager.this.spawn(PeriodOfDay.DAY.name());
    }

    public void onNight() {
      SpawnManager.this.despawn(PeriodOfDay.DAY.name());
      SpawnManager.this.spawn(PeriodOfDay.NIGHT.name());
    }

    public void onPeriodChange(int mode) {
      SpawnManager.this.despawn("[ssq_event]");
      SpawnManager.this.despawn("[ssq_seal1_none]");
      SpawnManager.this.despawn("[ssq_seal1_twilight]");
      SpawnManager.this.despawn("[ssq_seal1_dawn]");
      SpawnManager.this.despawn("[ssq_seal2_none]");
      SpawnManager.this.despawn("[ssq_seal2_twilight]");
      SpawnManager.this.despawn("[ssq_seal2_dawn]");
      switch(SevenSigns.getInstance().getCurrentPeriod()) {
        case 0:
        case 2:
        default:
          break;
        case 1:
          SpawnManager.this.spawn("[ssq_event]");
          break;
        case 3:
          switch(SevenSigns.getInstance().getSealOwner(1)) {
            case 0:
              SpawnManager.this.spawn("[ssq_seal1_none]");
              break;
            case 1:
              SpawnManager.this.spawn("[ssq_seal1_twilight]");
              break;
            case 2:
              SpawnManager.this.spawn("[ssq_seal1_dawn]");
          }

          switch(SevenSigns.getInstance().getSealOwner(2)) {
            case 0:
              SpawnManager.this.spawn("[ssq_seal2_none]");
              break;
            case 1:
              SpawnManager.this.spawn("[ssq_seal2_twilight]");
              break;
            case 2:
              SpawnManager.this.spawn("[ssq_seal2_dawn]");
          }
      }

    }
  }
}
