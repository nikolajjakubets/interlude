//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

public class ManufactureItem {
  private final int _recipeId;
  private final long _cost;

  public ManufactureItem(int recipeId, long cost) {
    this._recipeId = recipeId;
    this._cost = cost;
  }

  public int getRecipeId() {
    return this._recipeId;
  }

  public long getCost() {
    return this._cost;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != this.getClass()) {
      return false;
    } else {
      return ((ManufactureItem)o).getRecipeId() == this.getRecipeId();
    }
  }
}
