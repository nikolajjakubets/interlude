//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RecipeHolder extends AbstractHolder {
  private static final Logger LOG = LoggerFactory.getLogger(RecipeHolder.class);
  private static final RecipeHolder INSTANCE = new RecipeHolder();
  private final Map<Integer, Recipe> _recipesById = new HashMap<>();
  private final Map<Integer, Recipe> _recipesByRecipeItemId = new HashMap<>();

  public static final RecipeHolder getInstance() {
    return INSTANCE;
  }

  public RecipeHolder() {
  }

  public void addRecipe(Recipe recipe) {
    if (this._recipesById.containsKey(recipe.getId())) {
      LOG.warn("Recipe \"" + recipe.getId() + "\" already exists.");
    }

    this._recipesById.put(recipe.getId(), recipe);
    this._recipesByRecipeItemId.put(recipe.getItem().getItemId(), recipe);
  }

  public Recipe getRecipeById(int recipeId) {
    return (Recipe)this._recipesById.get(recipeId);
  }

  public Recipe getRecipeByItem(ItemTemplate itemTemplate) {
    return this.getRecipeByItem(itemTemplate.getItemId());
  }

  public Recipe getRecipeByItem(ItemInstance item) {
    return this.getRecipeByItem(item.getItemId());
  }

  public Recipe getRecipeByItem(int itemId) {
    return (Recipe)this._recipesByRecipeItemId.get(itemId);
  }

  public Collection<Recipe> getRecipes() {
    return Collections.unmodifiableMap(this._recipesById).values();
  }

  public int size() {
    return this._recipesById.size();
  }

  public void clear() {
    this._recipesById.clear();
    this._recipesByRecipeItemId.clear();
  }
}
