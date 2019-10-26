//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;

public class PlayerTemplate extends CharTemplate {
  public final ClassId classId;
  public final Race race;
  public final String className;
  public final Location spawnLoc = new Location();
  public final boolean isMale;
  private List<ItemTemplate> _items = new ArrayList();

  public PlayerTemplate(StatsSet set) {
    super(set);
    this.classId = ClassId.VALUES[set.getInteger("classId")];
    this.race = Race.values()[set.getInteger("raceId")];
    this.className = set.getString("className");
    this.spawnLoc.set(new Location(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ")));
    this.isMale = set.getBool("isMale", true);
  }

  public void addItem(int itemId) {
    ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
    if (item != null) {
      this._items.add(item);
    }

  }

  public ItemTemplate[] getItems() {
    return (ItemTemplate[])this._items.toArray(new ItemTemplate[this._items.size()]);
  }
}
