//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.residence.Residence;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import java.util.*;
import java.util.Map.Entry;

public final class ResidenceHolder extends AbstractHolder {
  private static ResidenceHolder _instance = new ResidenceHolder();
  private IntObjectMap<Residence> _residences = new TreeIntObjectMap<>();
  private Map<Class, List<Residence>> _fastResidencesByType = new HashMap<>(4);

  public static ResidenceHolder getInstance() {
    return _instance;
  }

  private ResidenceHolder() {
  }

  public void addResidence(Residence r) {
    this._residences.put(r.getId(), r);
  }

  public <R extends Residence> Residence getResidence(int id) {
    return this._residences.get(id);
  }

  public <R extends Residence> Residence getResidence(Class<R> type, int id) {
    Residence residence = this.getResidence(id);
    return residence != null && residence.getClass() == type ? residence : null;
  }

  public <R extends Residence> List<R> getResidenceList(Class<R> t) {
    return (List) this._fastResidencesByType.get(t);
  }

  public Collection<Residence> getResidences() {
    return this._residences.values();
  }

  public <R extends Residence> Residence getResidenceByObject(Class<? extends Residence> type, GameObject object) {
    return this.getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
  }

  public <R extends Residence> Residence getResidenceByCoord(Class<R> type, int x, int y, int z, Reflection ref) {
    Collection<Residence> residences = type == null ? this.getResidences() : this.getResidenceList((Class<Residence>) type);
    Iterator var7 = ((Collection)residences).iterator();

    Residence residence;
    do {
      if (!var7.hasNext()) {
        return null;
      }

      residence = (Residence)var7.next();
    } while(!residence.checkIfInZone(x, y, z, ref));

    return residence;
  }

  public <R extends Residence> Residence findNearestResidence(Class<R> clazz, int x, int y, int z, Reflection ref, int offset) {
    Residence residence = this.getResidenceByCoord(clazz, x, y, z, ref);
    if (residence == null) {
      double closestDistance = offset;

      for (Residence value : this.getResidenceList(clazz)) {
        double distance = value.getZone().findDistanceToZone(x, y, z, false);
        if (closestDistance > distance) {
          closestDistance = distance;
          residence = value;
        }
      }
    }

    return residence;
  }

  public void callInit() {

    for (Residence r : this.getResidences()) {
      r.init();
    }

  }

  private void buildFastLook() {
    Residence residence;
    Object list;
    for(Iterator var1 = this._residences.values().iterator(); var1.hasNext(); ((List)list).add(residence)) {
      residence = (Residence)var1.next();
      list = this._fastResidencesByType.get(residence.getClass());
      if (list == null) {
        this._fastResidencesByType.put(residence.getClass(),  new ArrayList<>());
      }
    }

  }

  public void log() {
    this.buildFastLook();
    this.info("total size: " + this._residences.size());

    for (Entry<Class, List<Residence>> classListEntry : this._fastResidencesByType.entrySet()) {
      this.info(" - load " + classListEntry.getValue().size() + " " + classListEntry.getKey().getSimpleName().toLowerCase() + "(s).");
    }

  }

  public int size() {
    return 0;
  }

  public void clear() {
    this._residences.clear();
    this._fastResidencesByType.clear();
  }
}
