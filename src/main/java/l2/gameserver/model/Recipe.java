//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.List;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class Recipe {
  private final int _id;
  private final ItemTemplate _item;
  private final Recipe.ERecipeType _type;
  private final int _requiredSkillLvl;
  private final int _mpConsume;
  private final int _successRate;
  private final List<Pair<ItemTemplate, Long>> _materials;
  private final List<Pair<ItemTemplate, Long>> _products;
  private final List<Pair<ItemTemplate, Long>> _npcFees;

  public Recipe(int id, ItemTemplate item, Recipe.ERecipeType type, int requiredSkillLvl, int mpConsume, int successRate, List<Pair<ItemTemplate, Long>> materials, List<Pair<ItemTemplate, Long>> products, List<Pair<ItemTemplate, Long>> npcFees) {
    this._id = id;
    this._item = item;
    this._type = type;
    this._requiredSkillLvl = requiredSkillLvl;
    this._mpConsume = mpConsume;
    this._successRate = successRate;
    this._materials = materials;
    this._products = products;
    this._npcFees = npcFees;
  }

  public int getId() {
    return this._id;
  }

  public ItemTemplate getItem() {
    return this._item;
  }

  public Recipe.ERecipeType getType() {
    return this._type;
  }

  public int getRequiredSkillLvl() {
    return this._requiredSkillLvl;
  }

  public int getMpConsume() {
    return this._mpConsume;
  }

  public int getSuccessRate() {
    return this._successRate;
  }

  public List<Pair<ItemTemplate, Long>> getMaterials() {
    return this._materials;
  }

  public List<Pair<ItemTemplate, Long>> getProducts() {
    return this._products;
  }

  public List<Pair<ItemTemplate, Long>> getNpcFees() {
    return this._npcFees;
  }

  public int hashCode() {
    return this._id;
  }

  public String toString() {
    return "Recipe{_id=" + this._id + ", _item=" + this._item + ", _type=" + this._type + ", _requiredSkillLvl=" + this._requiredSkillLvl + ", _mpConsume=" + this._mpConsume + ", _successRate=" + this._successRate + ", _materials=" + this._materials + ", _products=" + this._products + ", _npcFees=" + this._npcFees + '}';
  }

  public static enum ERecipeType {
    ERT_DWARF,
    ERT_COMMON;

    private ERecipeType() {
    }

    public boolean isApplicableBy(Player player) {
      return this != ERT_DWARF || player.getRace() == Race.dwarf;
    }
  }
}
