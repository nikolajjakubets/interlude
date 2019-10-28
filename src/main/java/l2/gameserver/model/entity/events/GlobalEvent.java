//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events;

import l2.commons.collections.MultiValueSet;
import l2.commons.listener.Listener;
import l2.commons.listener.ListenerList;
import l2.commons.logging.LoggerObject;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.event.OnStartStopListener;
import l2.gameserver.model.*;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.events.objects.InitableObject;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.taskmanager.actionrunner.ActionRunner;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TimeUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.IntObjectMap.Entry;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GlobalEvent extends LoggerObject {
  public static final String EVENT = "event";
  protected final IntObjectMap<List<EventAction>> _onTimeActions;
  protected final List<EventAction> _onStartActions;
  protected final List<EventAction> _onStopActions;
  protected final List<EventAction> _onInitActions;
  protected final Map<String, List<Serializable>> _objects;
  protected final int _id;
  protected final String _name;
  protected final String _timerName;
  protected final GlobalEvent.ListenerListImpl _listenerList;
  protected IntObjectMap<ItemInstance> _banishedItems;

  protected GlobalEvent(MultiValueSet<String> set) {
    this(set.getInteger("id"), set.getString("name"));
  }

  protected GlobalEvent(int id, String name) {
    this._onTimeActions = new TreeIntObjectMap();
    this._onStartActions = new ArrayList(0);
    this._onStopActions = new ArrayList(0);
    this._onInitActions = new ArrayList(0);
    this._objects = new HashMap(0);
    this._listenerList = new GlobalEvent.ListenerListImpl();
    this._banishedItems = Containers.emptyIntObjectMap();
    this._id = id;
    this._name = name;
    this._timerName = id + "_" + name.toLowerCase().replace(" ", "_");
  }

  public void initEvent() {
    this.callActions(this._onInitActions);
    this.reCalcNextTime(true);
    this.printInfo();
  }

  public void startEvent() {
    this.callActions(this._onStartActions);
    this._listenerList.onStart();
  }

  public void stopEvent() {
    this.callActions(this._onStopActions);
    this._listenerList.onStop();
  }

  protected void printInfo() {
    this.info(this.getName() + " time - " + TimeUtils.toSimpleFormat(this.startTimeMillis()));
  }

  public String toString() {
    return this.getClass().getSimpleName() + "[" + this.getId() + ";" + this.getName() + "]";
  }

  protected void callActions(List<EventAction> actions) {
    Iterator var2 = actions.iterator();

    while(var2.hasNext()) {
      EventAction action = (EventAction)var2.next();
      action.call(this);
    }

  }

  public void addOnStartActions(List<EventAction> start) {
    this._onStartActions.addAll(start);
  }

  public void addOnStopActions(List<EventAction> start) {
    this._onStopActions.addAll(start);
  }

  public void addOnInitActions(List<EventAction> start) {
    this._onInitActions.addAll(start);
  }

  public void addOnTimeAction(int time, EventAction action) {
    List<EventAction> list = this._onTimeActions.get(time);
    if (list != null) {
      list.add(action);
    } else {
      List<EventAction> actions = new ArrayList(1);
      actions.add(action);
      this._onTimeActions.put(time, actions);
    }

  }

  public void addOnTimeActions(int time, List<EventAction> actions) {
    if (!actions.isEmpty()) {
      List<EventAction> list = this._onTimeActions.get(time);
      if (list != null) {
        list.addAll(actions);
      } else {
        this._onTimeActions.put(time, new ArrayList(actions));
      }

    }
  }

  public void timeActions(int time) {
    List<EventAction> actions = this._onTimeActions.get(time);
    if (actions == null) {
      this.info("Undefined time : " + time + " for " + this.toString());
    } else {
      this.callActions(actions);
    }
  }

  public int[] timeActions() {
    return this._onTimeActions.keySet().toArray();
  }

  public void registerActions() {
    long t = this.startTimeMillis();
    if (t != 0L) {
      int[] var3 = this._onTimeActions.keySet().toArray();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        int key = var3[var5];
        ActionRunner.getInstance().register(t + (long)key * 1000L, new EventWrapper(this._timerName, this, key));
      }

    }
  }

  public void clearActions() {
    ActionRunner.getInstance().clear(this._timerName);
  }

  public <O extends Serializable> List<O> getObjects(String name) {
    List<Serializable> objects = this._objects.get(name);
    return objects == null ? Collections.emptyList() : (List<O>) objects;
  }

  public <O extends Serializable> O getFirstObject(String name) {
    List<O> objects = this.getObjects(name);
    return objects.size() > 0 ? objects.get(0) : null;
  }

  public void addObject(String name, Serializable object) {

    if (object != null) {
      List<Serializable> list = this._objects.get(name);
      if (list != null) {
        list.add(object);
      } else {
        list = new CopyOnWriteArrayList<>();
        list.add(object);
        this._objects.put(name, list);
      }

    }
  }

  public void removeObject(String name, Serializable o) {
    if (o != null) {
      List<Serializable> list = this._objects.get(name);
      if (list != null) {
        list.remove(o);
      }

    }
  }

  public <O extends Serializable> List<O> removeObjects(String name) {
    List<Serializable> objects = this._objects.remove(name);
    return objects == null ? Collections.emptyList() : (List<O>) objects;
  }

  public void addObjects(String name, List<? extends Serializable> objects) {
    if (!objects.isEmpty()) {
      List<Serializable> list = this._objects.get(name);
      if (list != null) {
        list.addAll(objects);
      } else {
        this._objects.put(name, (List<Serializable>) objects);
      }

    }
  }

  public Map<String, List<Serializable>> getObjects() {
    return this._objects;
  }

  public void spawnAction(String name, boolean spawn) {
    List<Serializable> objects = this.getObjects(name);
    if (objects.isEmpty()) {
      this.info("Undefined objects: " + name);
    } else {
      Iterator var4 = objects.iterator();

      while(var4.hasNext()) {
        Serializable object = (Serializable)var4.next();
        if (object instanceof SpawnableObject) {
          if (spawn) {
            ((SpawnableObject)object).spawnObject(this);
          } else {
            ((SpawnableObject)object).despawnObject(this);
          }
        }
      }

    }
  }

  public void doorAction(String name, boolean open) {
    List<Serializable> objects = this.getObjects(name);
    if (objects.isEmpty()) {
      this.info("Undefined objects: " + name);
    } else {
      Iterator var4 = objects.iterator();

      while(var4.hasNext()) {
        Serializable object = (Serializable)var4.next();
        if (object instanceof DoorObject) {
          if (open) {
            ((DoorObject)object).open(this);
          } else {
            ((DoorObject)object).close(this);
          }
        }
      }

    }
  }

  public void zoneAction(String name, boolean active) {
    List<Serializable> objects = this.getObjects(name);
    if (objects.isEmpty()) {
      this.info("Undefined objects: " + name);
    } else {
      Iterator var4 = objects.iterator();

      while(var4.hasNext()) {
        Serializable object = (Serializable)var4.next();
        if (object instanceof ZoneObject) {
          ((ZoneObject)object).setActive(active, this);
        }

        if (object instanceof String) {
          this.zoneAction((String)object, active);
        }
      }

    }
  }

  public void initAction(String name) {
    List<Serializable> objects = this.getObjects(name);
    if (objects.isEmpty()) {
      this.info("Undefined objects: " + name);
    } else {
      Iterator var3 = objects.iterator();

      while(var3.hasNext()) {
        Serializable object = (Serializable)var3.next();
        if (object instanceof InitableObject) {
          ((InitableObject)object).initObject(this);
        }
      }

    }
  }

  public void action(String name, boolean start) {
    if (name.equalsIgnoreCase("event")) {
      if (start) {
        this.startEvent();
      } else {
        this.stopEvent();
      }
    }

  }

  public void refreshAction(String name) {
    List<Serializable> objects = this.getObjects(name);
    if (objects.isEmpty()) {
      this.info("Undefined objects: " + name);
    } else {
      Iterator var3 = objects.iterator();

      while(var3.hasNext()) {
        Serializable object = (Serializable)var3.next();
        if (object instanceof SpawnableObject) {
          ((SpawnableObject)object).refreshObject(this);
        }
      }

    }
  }

  public abstract void reCalcNextTime(boolean var1);

  protected abstract long startTimeMillis();

  public void broadcastToWorld(IStaticPacket packet) {
    Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (player != null) {
        player.sendPacket(packet);
      }
    }

  }

  public void broadcastToWorld(L2GameServerPacket packet) {
    Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      if (player != null) {
        player.sendPacket(packet);
      }
    }

  }

  public int getId() {
    return this._id;
  }

  public String getName() {
    return this._name;
  }

  public GameObject getCenterObject() {
    return null;
  }

  public Reflection getReflection() {
    return ReflectionManager.DEFAULT;
  }

  public int getRelation(Player thisPlayer, Player target, int oldRelation) {
    return oldRelation;
  }

  public int getUserRelation(Player thisPlayer, int oldRelation) {
    return oldRelation;
  }

  public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
  }

  public Location getRestartLoc(Player player, RestartType type) {
    return null;
  }

  public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force) {
    return false;
  }

  public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
    return null;
  }

  public boolean isInProgress() {
    return false;
  }

  public boolean isParticle(Player player) {
    return false;
  }

  public void announce(int a) {
    throw new UnsupportedOperationException();
  }

  public void teleportPlayers(String teleportWho) {
    throw new UnsupportedOperationException();
  }

  public boolean ifVar(String name) {
    throw new UnsupportedOperationException();
  }

  public List<Player> itemObtainPlayers() {
    throw new UnsupportedOperationException();
  }

  public void giveItem(Player player, int itemId, long count) {
    Functions.addItem(player, itemId, count);
  }

  public List<Player> broadcastPlayers(int range) {
    throw new UnsupportedOperationException();
  }

  public boolean canResurrect(Player resurrectPlayer, Creature creature, boolean force) {
    return true;
  }

  public void onAddEvent(GameObject o) {
  }

  public void onRemoveEvent(GameObject o) {
  }

  public void addBanishItem(ItemInstance item) {
    if (this._banishedItems.isEmpty()) {
      this._banishedItems = new CHashIntObjectMap();
    }

    this._banishedItems.put(item.getObjectId(), item);
  }

  public void removeBanishItems() {
    ItemInstance item;
    for(Iterator iterator = this._banishedItems.entrySet().iterator(); iterator.hasNext(); item.deleteMe()) {
      Entry<ItemInstance> entry = (Entry)iterator.next();
      iterator.remove();
      item = ItemsDAO.getInstance().load(entry.getKey());
      if (item != null) {
        if (item.getOwnerId() > 0) {
          GameObject object = GameObjectsStorage.findObject(item.getOwnerId());
          if (object != null && object.isPlayable()) {
            ((Playable)object).getInventory().destroyItem(item);
            object.getPlayer().sendPacket(SystemMessage2.removeItems(item));
          }
        }

        item.delete();
      } else {
        item = entry.getValue();
      }
    }

  }

  public void addListener(Listener<GlobalEvent> l) {
    this._listenerList.add(l);
  }

  public void removeListener(Listener<GlobalEvent> l) {
    this._listenerList.remove(l);
  }

  public void cloneTo(GlobalEvent e) {
    Iterator var2 = this._onInitActions.iterator();

    EventAction a;
    while(var2.hasNext()) {
      a = (EventAction)var2.next();
      e._onInitActions.add(a);
    }

    var2 = this._onStartActions.iterator();

    while(var2.hasNext()) {
      a = (EventAction)var2.next();
      e._onStartActions.add(a);
    }

    var2 = this._onStopActions.iterator();

    while(var2.hasNext()) {
      a = (EventAction)var2.next();
      e._onStopActions.add(a);
    }

    var2 = this._onTimeActions.entrySet().iterator();

    while(var2.hasNext()) {
      Entry<List<EventAction>> entry = (Entry)var2.next();
      e.addOnTimeActions(entry.getKey(), entry.getValue());
    }

  }

  private class ListenerListImpl extends ListenerList<GlobalEvent> {
    private ListenerListImpl() {
    }

    public void onStart() {
      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener<GlobalEvent> listener = (Listener)var1.next();
        if (listener instanceof OnStartStopListener) {
          ((OnStartStopListener)listener).onStart(GlobalEvent.this);
        }
      }

    }

    public void onStop() {
      Iterator var1 = this.getListeners().iterator();

      while(var1.hasNext()) {
        Listener<GlobalEvent> listener = (Listener)var1.next();
        if (listener instanceof OnStartStopListener) {
          ((OnStartStopListener)listener).onStop(GlobalEvent.this);
        }
      }

    }
  }
}
