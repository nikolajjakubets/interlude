//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.Recipe.ERecipeType;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeParser extends AbstractFileParser<RecipeHolder> {
  private static final Logger LOG = LoggerFactory.getLogger(RecipeParser.class);
  private static final RecipeParser INSTANCE = new RecipeParser(RecipeHolder.getInstance());

  protected RecipeParser(RecipeHolder holder) {
    super(holder);
  }

  public static RecipeParser getInstance() {
    return INSTANCE;
  }

  public File getXMLFile() {
    return new File(Config.DATAPACK_ROOT, "data/recipe.xml");
  }

  public String getDTDFileName() {
    return "recipes.dtd";
  }

  protected void readData(Element rootElement) throws Exception {
    Iterator recipeListElementIt = rootElement.elementIterator();

    while(true) {
      Element recipeListElement;
      do {
        if (!recipeListElementIt.hasNext()) {
          return;
        }

        recipeListElement = (Element)recipeListElementIt.next();
      } while(!"recipe".equalsIgnoreCase(recipeListElement.getName()));

      int recipeId = Integer.parseInt(recipeListElement.attributeValue("id"));
      int minCraftSkillLvl = Integer.parseInt(recipeListElement.attributeValue("level"));
      int craftMpConsume = Integer.parseInt(recipeListElement.attributeValue("mp_consume"));
      int successRate = Integer.parseInt(recipeListElement.attributeValue("success_rate"));
      int recipeItemId = Integer.parseInt(recipeListElement.attributeValue("item_id"));
      ItemTemplate recipeItem = ItemHolder.getInstance().getTemplate(recipeItemId);
      ERecipeType recipeType = Boolean.parseBoolean(recipeListElement.attributeValue("is_common")) ? ERecipeType.ERT_COMMON : ERecipeType.ERT_DWARF;
      List<Pair<ItemTemplate, Long>> materials = new ArrayList<>();
      List<Pair<ItemTemplate, Long>> products = new ArrayList<>();
      List<Pair<ItemTemplate, Long>> npcFees = new ArrayList<>();
      Iterator recipeElementIt = recipeListElement.elementIterator();

      while(true) {
        while(recipeElementIt.hasNext()) {
          Element recipeElement = (Element)recipeElementIt.next();
          Iterator npcFeeElementIt;
          Element npcFeeElement;
          Pair npcFee;
          if ("materials".equalsIgnoreCase(recipeElement.getName())) {
            npcFeeElementIt = recipeElement.elementIterator();

            while(npcFeeElementIt.hasNext()) {
              npcFeeElement = (Element)npcFeeElementIt.next();
              npcFee = this.parseItem(npcFeeElement);
              if (npcFee != null) {
                materials.add(npcFee);
              }
            }
          } else if ("products".equalsIgnoreCase(recipeElement.getName())) {
            npcFeeElementIt = recipeElement.elementIterator();

            while(npcFeeElementIt.hasNext()) {
              npcFeeElement = (Element)npcFeeElementIt.next();
              npcFee = this.parseItem(npcFeeElement);
              if (npcFee != null) {
                products.add(npcFee);
              }
            }
          } else if ("npc_fee".equalsIgnoreCase(recipeElement.getName())) {
            npcFeeElementIt = recipeElement.elementIterator();

            while(npcFeeElementIt.hasNext()) {
              npcFeeElement = (Element)npcFeeElementIt.next();
              npcFee = this.parseItem(npcFeeElement);
              if (npcFee != null) {
                npcFees.add(npcFee);
              }
            }
          }
        }

        if (recipeItem == null) {
          LOG.warn("Skip recipe " + recipeId);
        } else if (products.isEmpty()) {
          LOG.warn("Recipe " + recipeId + " have empty product list. Skip");
        } else {
          if (products.size() > 1) {
            LOG.warn("Recipe " + recipeId + " have more than one product. Skip");
          }

          if (materials.isEmpty()) {
            LOG.warn("Recipe " + recipeId + " have empty material list. Skip");
          } else {
            Recipe recipe = new Recipe(recipeId, recipeItem, recipeType, minCraftSkillLvl, craftMpConsume, successRate, Collections.unmodifiableList(materials), Collections.unmodifiableList(products), Collections.unmodifiableList(npcFees));
            ((RecipeHolder)this.getHolder()).addRecipe(recipe);
          }
        }
        break;
      }
    }
  }

  private Pair<ItemTemplate, Long> parseItem(Element itemElement) {
    if (!"item".equalsIgnoreCase(itemElement.getName())) {
      return null;
    } else {
      int itemId = Integer.parseInt(itemElement.attributeValue("id"));
      ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(itemId);
      if (itemElement == null) {
        return null;
      } else {
        long itemCount = Long.parseLong(itemElement.attributeValue("count"));
        return Pair.of(itemTemplate, itemCount);
      }
    }
  }
}
