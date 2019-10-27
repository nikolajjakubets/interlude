//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.templates.npc.NpcTemplate;

public final class NpcHolder extends AbstractHolder {
  private static final NpcHolder _instance = new NpcHolder();
  private TIntObjectHashMap<NpcTemplate> _npcs = new TIntObjectHashMap<>(20000);
  private TIntObjectHashMap<List<NpcTemplate>> _npcsByLevel;
  private NpcTemplate[] _allTemplates;
  private Map<String, NpcTemplate> _npcsNames;

  public static NpcHolder getInstance() {
    return _instance;
  }

  NpcHolder() {
  }

  public void addTemplate(NpcTemplate template) {
    this._npcs.put(template.npcId, template);
  }

  public NpcTemplate getTemplate(int id) {
    NpcTemplate npc = ArrayUtils.valid(this._allTemplates, id);
    if (npc == null) {
      this.warn("Not defined npc id : " + id + ", or out of range!", new Exception());
      return null;
    } else {
      return this._allTemplates[id];
    }
  }

  public NpcTemplate getTemplateByName(String name) {
    return this._npcsNames.get(name.toLowerCase());
  }

  public List<NpcTemplate> getAllOfLevel(int lvl) {
    return this._npcsByLevel.get(lvl);
  }

  public NpcTemplate[] getAll() {
    return this._npcs.getValues(new NpcTemplate[this._npcs.size()]);
  }

  private void buildFastLookupTable() {
    this._npcsByLevel = new TIntObjectHashMap<>();
    this._npcsNames = new HashMap<>();
    int highestId = 0;
    int[] var2 = this._npcs.keys();
    int npcId = var2.length;

    for(int var4 = 0; var4 < npcId; ++var4) {
      int id = var2[var4];
      if (id > highestId) {
        highestId = id;
      }
    }

    this._allTemplates = new NpcTemplate[highestId + 1];
    TIntObjectIterator iterator = this._npcs.iterator();

    while(iterator.hasNext()) {
      iterator.advance();
      npcId = iterator.key();
      NpcTemplate npc = (NpcTemplate)iterator.value();
      this._allTemplates[npcId] = npc;
      List<NpcTemplate> byLevel = this._npcsByLevel.get(npc.level);
      if (byLevel == null) {
        byLevel = new ArrayList<>();
        this._npcsByLevel.put(npcId, byLevel);
      }

      byLevel.add(npc);
      this._npcsNames.put(npc.name.toLowerCase(), npc);
    }

  }

  protected void process() {
    this.buildFastLookupTable();
  }

  public int size() {
    return this._npcs.size();
  }

  public void clear() {
    this._npcsNames.clear();
    this._npcs.clear();
  }
}
