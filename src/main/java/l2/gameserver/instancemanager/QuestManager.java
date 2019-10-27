//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.model.quest.Quest;

public class QuestManager {
  public static final int TUTORIAL_QUEST = 255;
  private static Map<String, Quest> _questsByName = new ConcurrentHashMap();
  private static Map<Integer, Quest> _questsById = new ConcurrentHashMap();

  public QuestManager() {
  }

  public static Quest getQuest(String name) {
    return (Quest)_questsByName.get(name);
  }

  public static Quest getQuest(Class<?> quest) {
    return getQuest(quest.getSimpleName());
  }

  public static Quest getQuest(int questId) {
    return (Quest)_questsById.get(questId);
  }

  public static Quest getQuest2(String nameOrId) {
    if (_questsByName.containsKey(nameOrId)) {
      return (Quest)_questsByName.get(nameOrId);
    } else {
      try {
        int questId = Integer.parseInt(nameOrId);
        return (Quest)_questsById.get(questId);
      } catch (Exception var2) {
        return null;
      }
    }
  }

  public static void addQuest(Quest newQuest) {
    _questsByName.put(newQuest.getName(), newQuest);
    _questsById.put(newQuest.getQuestIntId(), newQuest);
  }

  public static Collection<Quest> getQuests() {
    return _questsByName.values();
  }
}
