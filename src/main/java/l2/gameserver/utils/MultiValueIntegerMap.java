//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiValueIntegerMap {
  private Map<Integer, List<Integer>> map = new ConcurrentHashMap();

  public MultiValueIntegerMap() {
  }

  public Set<Integer> keySet() {
    return this.map.keySet();
  }

  public Collection<List<Integer>> values() {
    return this.map.values();
  }

  public List<Integer> allValues() {
    List<Integer> result = new ArrayList();
    Iterator var2 = this.map.entrySet().iterator();

    while(var2.hasNext()) {
      Entry<Integer, List<Integer>> entry = (Entry)var2.next();
      result.addAll((Collection)entry.getValue());
    }

    return result;
  }

  public Set<Entry<Integer, List<Integer>>> entrySet() {
    return this.map.entrySet();
  }

  public List<Integer> remove(Integer key) {
    return (List)this.map.remove(key);
  }

  public List<Integer> get(Integer key) {
    return (List)this.map.get(key);
  }

  public boolean containsKey(Integer key) {
    return this.map.containsKey(key);
  }

  public void clear() {
    this.map.clear();
  }

  public int size() {
    return this.map.size();
  }

  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  public Integer remove(Integer key, Integer value) {
    List<Integer> valuesForKey = (List)this.map.get(key);
    if (valuesForKey == null) {
      return null;
    } else {
      boolean removed = valuesForKey.remove(value);
      if (!removed) {
        return null;
      } else {
        if (valuesForKey.isEmpty()) {
          this.remove(key);
        }

        return value;
      }
    }
  }

  public Integer removeValue(Integer value) {
    List<Integer> toRemove = new ArrayList(1);
    Iterator var3 = this.map.entrySet().iterator();

    while(var3.hasNext()) {
      Entry<Integer, List<Integer>> entry = (Entry)var3.next();
      ((List)entry.getValue()).remove(value);
      if (((List)entry.getValue()).isEmpty()) {
        toRemove.add(entry.getKey());
      }
    }

    var3 = toRemove.iterator();

    while(var3.hasNext()) {
      Integer key = (Integer)var3.next();
      this.remove(key);
    }

    return value;
  }

  public Integer put(Integer key, Integer value) {
    List<Integer> coll = (List)this.map.get(key);
    if (coll == null) {
      coll = new CopyOnWriteArrayList();
      this.map.put(key, coll);
    }

    ((List)coll).add(value);
    return value;
  }

  public boolean containsValue(Integer value) {
    Iterator var2 = this.map.entrySet().iterator();

    Entry entry;
    do {
      if (!var2.hasNext()) {
        return false;
      }

      entry = (Entry)var2.next();
    } while(!((List)entry.getValue()).contains(value));

    return true;
  }

  public boolean containsValue(Integer key, Integer value) {
    List<Integer> coll = (List)this.map.get(key);
    return coll == null ? false : coll.contains(value);
  }

  public int size(Integer key) {
    List<Integer> coll = (List)this.map.get(key);
    return coll == null ? 0 : coll.size();
  }

  public boolean putAll(Integer key, Collection<? extends Integer> values) {
    if (values != null && values.size() != 0) {
      boolean result = false;
      List<Integer> coll = (List)this.map.get(key);
      if (coll == null) {
        List<Integer> coll = new CopyOnWriteArrayList();
        coll.addAll(values);
        if (coll.size() > 0) {
          this.map.put(key, coll);
          result = true;
        }
      } else {
        result = coll.addAll(values);
      }

      return result;
    } else {
      return false;
    }
  }

  public int totalSize() {
    int total = 0;

    Entry entry;
    for(Iterator var2 = this.map.entrySet().iterator(); var2.hasNext(); total += ((List)entry.getValue()).size()) {
      entry = (Entry)var2.next();
    }

    return total;
  }
}
