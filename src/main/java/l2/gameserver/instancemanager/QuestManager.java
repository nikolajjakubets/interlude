//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import l2.gameserver.model.quest.Quest;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class QuestManager {
  public static final int TUTORIAL_QUEST = 255;
  private static Map<String, Quest> _questsByName = new ConcurrentHashMap<>();
  private static Map<Integer, Quest> _questsById = new ConcurrentHashMap<>();

  public QuestManager() {
  }

  public static Quest getQuest(String name) {
    return _questsByName.get(name);
  }

  public static Quest getQuest(Class<?> quest) {
    return getQuest(quest.getSimpleName());
  }

  public static Quest getQuest(int questId) {
    return _questsById.get(questId);
  }

  public static Quest getQuest2(String nameOrId) {
    if (_questsByName.containsKey(nameOrId)) {
      return _questsByName.get(nameOrId);
    } else {
      try {
        int questId = Integer.parseInt(nameOrId);
        return _questsById.get(questId);
      } catch (Exception e) {
        log.error("getQuest2: eMessage={}, eClass={}", e.getMessage(), e.getClass());
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
