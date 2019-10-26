//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.components;

import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;

public class CustomMessage {
  private String _text;
  private int mark = 0;

  public CustomMessage(String address, Player player, Object... args) {
    this._text = StringHolder.getInstance().getNotNull(player, address);
    this.add(args);
  }

  public CustomMessage addNumber(long number) {
    this._text = this._text.replace("{" + this.mark + "}", String.valueOf(number));
    ++this.mark;
    return this;
  }

  public CustomMessage add(Object... args) {
    Object[] var2 = args;
    int var3 = args.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Object arg = var2[var4];
      if (arg instanceof String) {
        this.addString((String)arg);
      } else if (arg instanceof Integer) {
        this.addNumber((long)(Integer)arg);
      } else if (arg instanceof Long) {
        this.addNumber((Long)arg);
      } else if (arg instanceof ItemTemplate) {
        this.addItemName((ItemTemplate)arg);
      } else if (arg instanceof ItemInstance) {
        this.addItemName((ItemInstance)arg);
      } else if (arg instanceof Creature) {
        this.addCharName((Creature)arg);
      } else if (arg instanceof Skill) {
        this.addSkillName((Skill)arg);
      } else {
        System.out.println("unknown CustomMessage arg type: " + arg);
        Thread.dumpStack();
      }
    }

    return this;
  }

  public CustomMessage addString(String str) {
    this._text = this._text.replace("{" + this.mark + "}", str);
    ++this.mark;
    return this;
  }

  public CustomMessage addSkillName(Skill skill) {
    this._text = this._text.replace("{" + this.mark + "}", skill.getName());
    ++this.mark;
    return this;
  }

  public CustomMessage addSkillName(int skillId, int skillLevel) {
    return this.addSkillName(SkillTable.getInstance().getInfo(skillId, skillLevel));
  }

  public CustomMessage addItemName(ItemTemplate item) {
    this._text = this._text.replace("{" + this.mark + "}", item.getName());
    ++this.mark;
    return this;
  }

  public CustomMessage addItemName(int itemId) {
    return this.addItemName(ItemHolder.getInstance().getTemplate(itemId));
  }

  public CustomMessage addItemName(ItemInstance item) {
    return this.addItemName(item.getTemplate());
  }

  public CustomMessage addCharName(Creature cha) {
    this._text = this._text.replace("{" + this.mark + "}", cha.getName());
    ++this.mark;
    return this;
  }

  public String toString() {
    return this._text;
  }
}
