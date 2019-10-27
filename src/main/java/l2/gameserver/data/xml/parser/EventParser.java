//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.actions.ActiveDeactiveAction;
import l2.gameserver.model.entity.events.actions.AnnounceAction;
import l2.gameserver.model.entity.events.actions.GiveItemAction;
import l2.gameserver.model.entity.events.actions.IfElseAction;
import l2.gameserver.model.entity.events.actions.InitAction;
import l2.gameserver.model.entity.events.actions.NpcSayAction;
import l2.gameserver.model.entity.events.actions.OpenCloseAction;
import l2.gameserver.model.entity.events.actions.PlaySoundAction;
import l2.gameserver.model.entity.events.actions.RefreshAction;
import l2.gameserver.model.entity.events.actions.SayAction;
import l2.gameserver.model.entity.events.actions.SpawnDespawnAction;
import l2.gameserver.model.entity.events.actions.StartStopAction;
import l2.gameserver.model.entity.events.actions.TeleportPlayersAction;
import l2.gameserver.model.entity.events.objects.BoatPoint;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.events.objects.StaticObjectObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PlaySound.Type;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public final class EventParser extends AbstractDirParser<EventHolder> {
  private static final EventParser _instance = new EventParser();

  public static EventParser getInstance() {
    return _instance;
  }

  protected EventParser() {
    super(EventHolder.getInstance());
  }

  public File getXMLDir() {
    return new File(Config.DATAPACK_ROOT, "data/events/");
  }

  public boolean isIgnored(File f) {
    return false;
  }

  public String getDTDFileName() {
    return "events.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator iterator = rootElement.elementIterator("event");

    while(iterator.hasNext()) {
      Element eventElement = (Element)iterator.next();
      int id = Integer.parseInt(eventElement.attributeValue("id"));
      String name = eventElement.attributeValue("name");
      String impl = eventElement.attributeValue("impl");
      EventType type = EventType.valueOf(eventElement.attributeValue("type"));
      Class eventClass = null;

      try {
        eventClass = Class.forName("l2.gameserver.model.entity.events.impl." + impl + "Event");
      } catch (ClassNotFoundException var17) {
        this.info("Not found impl class: " + impl + "; File: " + this.getCurrentFileName());
        continue;
      }

      Constructor<GlobalEvent> constructor = eventClass.getConstructor(MultiValueSet.class);
      MultiValueSet<String> set = new MultiValueSet();
      set.set("id", id);
      set.set("name", name);
      Iterator parameterIterator = eventElement.elementIterator("parameter");

      Element onTime;
      while(parameterIterator.hasNext()) {
        onTime = (Element)parameterIterator.next();
        set.set(onTime.attributeValue("name"), onTime.attributeValue("value"));
      }

      GlobalEvent event = (GlobalEvent)constructor.newInstance(set);
      event.addOnStartActions(this.parseActions(eventElement.element("on_start"), 2147483647));
      event.addOnStopActions(this.parseActions(eventElement.element("on_stop"), 2147483647));
      event.addOnInitActions(this.parseActions(eventElement.element("on_init"), 2147483647));
      onTime = eventElement.element("on_time");
      Iterator objectIterator;
      Element objectElement;
      List objects;
      if (onTime != null) {
        objectIterator = onTime.elementIterator("on");

        while(objectIterator.hasNext()) {
          objectElement = (Element)objectIterator.next();
          int time = Integer.parseInt(objectElement.attributeValue("time"));
          objects = this.parseActions(objectElement, time);
          event.addOnTimeActions(time, objects);
        }
      }

      objectIterator = eventElement.elementIterator("objects");

      while(objectIterator.hasNext()) {
        objectElement = (Element)objectIterator.next();
        String objectsName = objectElement.attributeValue("name");
        objects = this.parseObjects(objectElement);
        event.addObjects(objectsName, objects);
      }

      ((EventHolder)this.getHolder()).addEvent(type, event);
    }

  }

  private List<Serializable> parseObjects(Element element) {
    if (element == null) {
      return Collections.emptyList();
    } else {
      List<Serializable> objects = new ArrayList(2);
      Iterator objectsIterator = element.elementIterator();

      while(true) {
        while(objectsIterator.hasNext()) {
          Element objectsElement = (Element)objectsIterator.next();
          String nodeName = objectsElement.getName();
          if (nodeName.equalsIgnoreCase("boat_point")) {
            objects.add(BoatPoint.parse(objectsElement));
          } else if (nodeName.equalsIgnoreCase("point")) {
            objects.add(Location.parse(objectsElement));
          } else if (nodeName.equalsIgnoreCase("spawn_ex")) {
            objects.add(new SpawnExObject(objectsElement.attributeValue("name")));
          } else if (nodeName.equalsIgnoreCase("door")) {
            objects.add(new DoorObject(Integer.parseInt(objectsElement.attributeValue("id"))));
          } else if (nodeName.equalsIgnoreCase("static_object")) {
            objects.add(new StaticObjectObject(Integer.parseInt(objectsElement.attributeValue("id"))));
          } else {
            int mobId;
            int flagId;
            if (!nodeName.equalsIgnoreCase("siege_toggle_npc")) {
              if (nodeName.equalsIgnoreCase("castle_zone")) {
                long price = Long.parseLong(objectsElement.attributeValue("price"));
                objects.add(new CastleDamageZoneObject(objectsElement.attributeValue("name"), price));
              } else if (nodeName.equalsIgnoreCase("zone")) {
                objects.add(new ZoneObject(objectsElement.attributeValue("name")));
              } else if (nodeName.equalsIgnoreCase("ctb_team")) {
                mobId = Integer.parseInt(objectsElement.attributeValue("mob_id"));
                flagId = Integer.parseInt(objectsElement.attributeValue("id"));
                Location loc = Location.parse(objectsElement);
                objects.add(new CTBTeamObject(mobId, flagId, loc));
              }
            } else {
              mobId = Integer.parseInt(objectsElement.attributeValue("id"));
              flagId = Integer.parseInt(objectsElement.attributeValue("fake_id"));
              int x = Integer.parseInt(objectsElement.attributeValue("x"));
              int y = Integer.parseInt(objectsElement.attributeValue("y"));
              int z = Integer.parseInt(objectsElement.attributeValue("z"));
              int hp = Integer.parseInt(objectsElement.attributeValue("hp"));
              Set<String> set = Collections.emptySet();

              Element sub;
              for(Iterator oIterator = objectsElement.elementIterator(); oIterator.hasNext(); ((Set)set).add(sub.attributeValue("name"))) {
                sub = (Element)oIterator.next();
                if (((Set)set).isEmpty()) {
                  set = new HashSet<>();
                }
              }

              objects.add(new SiegeToggleNpcObject(mobId, flagId, new Location(x, y, z), hp, (Set)set));
            }
          }
        }

        return objects;
      }
    }
  }

  private List<EventAction> parseActions(Element element, int time) {
    if (element == null) {
      return Collections.emptyList();
    } else {
      IfElseAction lastIf = null;
      List<EventAction> actions = new ArrayList(0);
      Iterator iterator = element.elementIterator();

      while(true) {
        while(iterator.hasNext()) {
          Element actionElement = (Element)iterator.next();
          String name;
          StartStopAction startStopAction;
          if (actionElement.getName().equalsIgnoreCase("start")) {
            name = actionElement.attributeValue("name");
            startStopAction = new StartStopAction(name, true);
            actions.add(startStopAction);
          } else if (actionElement.getName().equalsIgnoreCase("stop")) {
            name = actionElement.attributeValue("name");
            startStopAction = new StartStopAction(name, false);
            actions.add(startStopAction);
          } else {
            SpawnDespawnAction spawnDespawnAction;
            if (actionElement.getName().equalsIgnoreCase("spawn")) {
              name = actionElement.attributeValue("name");
              spawnDespawnAction = new SpawnDespawnAction(name, true);
              actions.add(spawnDespawnAction);
            } else if (actionElement.getName().equalsIgnoreCase("despawn")) {
              name = actionElement.attributeValue("name");
              spawnDespawnAction = new SpawnDespawnAction(name, false);
              actions.add(spawnDespawnAction);
            } else {
              OpenCloseAction a;
              if (actionElement.getName().equalsIgnoreCase("open")) {
                name = actionElement.attributeValue("name");
                a = new OpenCloseAction(true, name);
                actions.add(a);
              } else if (actionElement.getName().equalsIgnoreCase("close")) {
                name = actionElement.attributeValue("name");
                a = new OpenCloseAction(false, name);
                actions.add(a);
              } else {
                ActiveDeactiveAction activeDeactiveAction;
                if (actionElement.getName().equalsIgnoreCase("active")) {
                  name = actionElement.attributeValue("name");
                  activeDeactiveAction = new ActiveDeactiveAction(true, name);
                  actions.add(activeDeactiveAction);
                } else if (actionElement.getName().equalsIgnoreCase("deactive")) {
                  name = actionElement.attributeValue("name");
                  activeDeactiveAction = new ActiveDeactiveAction(false, name);
                  actions.add(activeDeactiveAction);
                } else if (actionElement.getName().equalsIgnoreCase("refresh")) {
                  name = actionElement.attributeValue("name");
                  RefreshAction refreshAction = new RefreshAction(name);
                  actions.add(refreshAction);
                } else if (actionElement.getName().equalsIgnoreCase("init")) {
                  name = actionElement.attributeValue("name");
                  InitAction initAction = new InitAction(name);
                  actions.add(initAction);
                } else {
                  int itemId;
                  if (actionElement.getName().equalsIgnoreCase("npc_say")) {
                    itemId = Integer.parseInt(actionElement.attributeValue("npc"));
                    ChatType chat = ChatType.valueOf(actionElement.attributeValue("chat"));
                    int range = Integer.parseInt(actionElement.attributeValue("range"));
                    NpcString string = NpcString.valueOf(actionElement.attributeValue("text"));
                    NpcSayAction action = new NpcSayAction(itemId, range, chat, string);
                    actions.add(action);
                  } else if (actionElement.getName().equalsIgnoreCase("play_sound")) {
                    itemId = Integer.parseInt(actionElement.attributeValue("range"));
                    String sound = actionElement.attributeValue("sound");
                    Type type = Type.valueOf(actionElement.attributeValue("type"));
                    PlaySoundAction action = new PlaySoundAction(itemId, sound, type);
                    actions.add(action);
                  } else if (actionElement.getName().equalsIgnoreCase("give_item")) {
                    itemId = Integer.parseInt(actionElement.attributeValue("id"));
                    long count = (long)Integer.parseInt(actionElement.attributeValue("count"));
                    GiveItemAction action = new GiveItemAction(itemId, count);
                    actions.add(action);
                  } else {
                    int range;
                    if (actionElement.getName().equalsIgnoreCase("announce")) {
                      name = actionElement.attributeValue("val");
                      if (name == null && time == 2147483647) {
                        this.info("Can't get announce time." + this.getCurrentFileName());
                      } else {
                        range = name == null ? time : Integer.parseInt(name);
                        EventAction action = new AnnounceAction(range);
                        actions.add(action);
                      }
                    } else {
                      IfElseAction action;
                      if (actionElement.getName().equalsIgnoreCase("if")) {
                        name = actionElement.attributeValue("name");
                        action = new IfElseAction(name, false);
                        action.setIfList(this.parseActions(actionElement, time));
                        actions.add(action);
                        lastIf = action;
                      } else if (actionElement.getName().equalsIgnoreCase("ifnot")) {
                        name = actionElement.attributeValue("name");
                        action = new IfElseAction(name, true);
                        action.setIfList(this.parseActions(actionElement, time));
                        actions.add(action);
                        lastIf = action;
                      } else if (actionElement.getName().equalsIgnoreCase("else")) {
                        if (lastIf == null) {
                          this.info("Not find <if> for <else> tag");
                        } else {
                          lastIf.setElseList(this.parseActions(actionElement, time));
                        }
                      } else if (actionElement.getName().equalsIgnoreCase("say")) {
                        ChatType chat = ChatType.valueOf(actionElement.attributeValue("chat"));
                        range = Integer.parseInt(actionElement.attributeValue("range"));
                        String how = actionElement.attributeValue("how");
                        String text = actionElement.attributeValue("text");
                        SysString sysString = SysString.valueOf2(how);
                        SayAction sayAction = null;
                        if (sysString != null) {
                          sayAction = new SayAction(range, chat, sysString, SystemMsg.valueOf(text));
                        } else {
                          sayAction = new SayAction(range, chat, how, NpcString.valueOf(text));
                        }

                        actions.add(sayAction);
                      } else if (actionElement.getName().equalsIgnoreCase("teleport_players")) {
                        name = actionElement.attributeValue("id");
                        TeleportPlayersAction teleportPlayersAction = new TeleportPlayersAction(name);
                        actions.add(teleportPlayersAction);
                      }
                    }
                  }
                }
              }
            }
          }
        }

        return (List)(actions.isEmpty() ? Collections.emptyList() : actions);
      }
    }
  }
}
