//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.model.GWAutoAnnounce;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AutoAnnounce implements Runnable {
  private static AutoAnnounce _instance;
  static Map<Integer, GWAutoAnnounce> _lists;

  public static AutoAnnounce getInstance() {
    if (_instance == null) {
      _instance = new AutoAnnounce();
    }

    return _instance;
  }

  public void reload() {
    _instance = new AutoAnnounce();
  }

  public AutoAnnounce() {
    _lists = new HashMap<>();
    log.info("AutoAnnounce: Initializing");
    this.load();
    log.info("AutoAnnounce: Loaded " + (_lists.size() - 1) + " announce.");
  }

  private void load() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringComments(true);
      File file = new File("./config/autoannounce.xml");
      if (!file.exists()) {
        if (Config.DEBUG) {
          System.out.println("AutoAnnounce: NO FILE");
        }

        return;
      }

      Document doc = factory.newDocumentBuilder().parse(file);
      int counterAnnounce = 0;
      {
        ArrayList<String> msg = new ArrayList<>();
        GWAutoAnnounce aa = new GWAutoAnnounce(counterAnnounce);
        int revision = 0;
        msg.add("" + revision);
        String name = "Own1";
        String name2 = "Own2";
        msg.add(name + name2);
        aa.setAnnounce(0, 0, msg);
        _lists.put(counterAnnounce, aa);
        ++counterAnnounce;
      }

      for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
        if ("list".equalsIgnoreCase(n.getNodeName())) {
          for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("announce".equalsIgnoreCase(d.getNodeName())) {
              ArrayList<String> msg = new ArrayList<>();
              NamedNodeMap attrs = d.getAttributes();
              int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
              int repeat = Integer.parseInt(attrs.getNamedItem("repeat").getNodeValue());

              boolean isScreenMessage;
              try {
                isScreenMessage = Boolean.parseBoolean(attrs.getNamedItem("is_screen_message").getNodeValue());
              } catch (Exception var14) {
                isScreenMessage = false;
              }

              GWAutoAnnounce aa = new GWAutoAnnounce(counterAnnounce);
              aa.setScreenAnnounce(isScreenMessage);

              for(Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                if ("message".equalsIgnoreCase(cd.getNodeName())) {
                  msg.add(String.valueOf(cd.getAttributes().getNamedItem("text").getNodeValue()));
                }
              }

              aa.setAnnounce(delay, repeat, msg);
              _lists.put(counterAnnounce, aa);
              ++counterAnnounce;
            }
          }
        }
      }

      if (Config.DEBUG) {
        System.out.println("AutoAnnounce: OK");
      }
    } catch (Exception var15) {
      log.error("AutoAnnounce: Error parsing autoannounce.xml file. " + var15);
    }

  }

  public void run() {
    if (_lists.size() > 1) {
      for(int i = 1; i < _lists.size(); ++i) {
        GWAutoAnnounce item = _lists.get(i);
        if (item.canAnnounce()) {
          ArrayList<String> msg = item.getMessage();
          Iterator var4 = msg.iterator();

          while(true) {
            while(var4.hasNext()) {
              String text = (String)var4.next();
              if (!item.isScreenAnnounce()) {
                Announcements.getInstance().announceToAll(text);
              } else {
                int _time = 3000 + text.length() * 100;
                ExShowScreenMessage sm = new ExShowScreenMessage(text, _time, ScreenMessageAlign.TOP_CENTER, false);

                for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                  player.sendPacket(sm);
                }
              }
            }

            _lists.get(i).updateRepeat();
            break;
          }
        }
      }

    }
  }

  public static String getRevision() {
    return _lists.size() == 0 ? "" : _lists.get(0).getMessage().get(0);
  }

  public static String getOwnerName() {
    return _lists.size() == 0 ? "" : _lists.get(0).getMessage().get(1);
  }
}
