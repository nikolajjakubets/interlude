//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.CharacterCreateFail;
import l2.gameserver.network.l2.s2c.CharacterCreateSuccess;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.CharTemplateTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.AutoBan;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;

public class CharacterCreate extends L2GameClientPacket {
  private String _name;
  private int _sex;
  private int _classId;
  private int _hairStyle;
  private int _hairColor;
  private int _face;

  public CharacterCreate() {
  }

  protected void readImpl() {
    this._name = this.readS();
    this.readD();
    this._sex = this.readD();
    this._classId = this.readD();
    this.readD();
    this.readD();
    this.readD();
    this.readD();
    this.readD();
    this.readD();
    this._hairStyle = this.readD();
    this._hairColor = this.readD();
    this._face = this.readD();
  }

  protected void runImpl() {
    ClassId[] var1 = ClassId.VALUES;
    int var2 = var1.length;

    int var3;
    for(var3 = 0; var3 < var2; ++var3) {
      ClassId cid = var1[var3];
      if (cid.getId() == this._classId && cid.getLevel() != 1) {
        return;
      }
    }

    GameClient client = (GameClient)this.getClient();
    if (client != null) {
      if (CharacterDAO.getInstance().accountCharNumber(((GameClient)this.getClient()).getLogin()) >= 8) {
        this.sendPacket(CharacterCreateFail.REASON_TOO_MANY_CHARACTERS);
      } else if (!Util.isMatchingRegexp(this._name, Config.CNAME_TEMPLATE)) {
        this.sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
      } else if (!Util.isMatchingRegexp(this._name, Config.CNAME_FORBIDDEN_PATTERN) && CharacterDAO.getInstance().getObjectIdByName(this._name) <= 0) {
        String[] var7 = Config.CNAME_FORBIDDEN_NAMES;
        var3 = var7.length;

        for(int var10 = 0; var10 < var3; ++var10) {
          String forbiddenName = var7[var10];
          if (forbiddenName.equalsIgnoreCase(this._name)) {
            this.sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
            return;
          }
        }

        if (this._hairStyle < 0 || this._sex == 0 && this._hairStyle > 4 || this._sex != 0 && this._hairStyle > 6) {
          this.sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
        } else if (this._face <= 2 && this._face >= 0) {
          if (this._hairColor <= 3 && this._hairColor >= 0) {
            Player newChar = Player.create(this._classId, this._sex, ((GameClient)this.getClient()).getLogin(), this._name, this._hairStyle, this._hairColor, this._face);
            if (newChar != null) {
              if (Config.ALT_DEFAULT_ACCESS_LEVEL < 0) {
                newChar.setAccessLevel(-100);
                AutoBan.Banned(newChar, 1000, "GSBan", "Config");
              }

              CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
              this.sendPacket(new L2GameServerPacket[]{CharacterCreateSuccess.STATIC, csi});
              this.initNewChar((GameClient)this.getClient(), newChar);
            }
          } else {
            this.sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
          }
        } else {
          this.sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
        }
      } else {
        this.sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
      }
    }
  }

  private void initNewChar(GameClient client, Player newChar) {
    PlayerTemplate template = newChar.getTemplate();
    Player.restoreCharSubClasses(newChar);
    if (Config.STARTING_ADENA > 0) {
      newChar.addAdena((long)Config.STARTING_ADENA);
    }

    newChar.setLoc(template.spawnLoc);
    if (Config.ALT_NEW_CHARACTER_LEVEL > 0) {
      newChar.getActiveClass().setExp(Experience.getExpForLevel(Config.ALT_NEW_CHARACTER_LEVEL));
    }

    if (Config.CHAR_TITLE) {
      newChar.setTitle(Config.ADD_CHAR_TITLE);
    } else {
      newChar.setTitle("");
    }

    ItemTemplate[] var4 = template.getItems();
    int itemId = var4.length;

    int lvl;
    for(lvl = 0; lvl < itemId; ++lvl) {
      ItemTemplate i = var4[lvl];
      ItemInstance item = ItemFunctions.createItem(i.getItemId());
      newChar.getInventory().addItem(item);
      if (item.isEquipable() && (newChar.getActiveWeaponItem() == null || item.getTemplate().getType2() != 0)) {
        newChar.getInventory().equipItem(item);
      }
    }

    int i;
    long count;
    if (!newChar.isMageClass()) {
      for(i = 0; i < Config.STARTING_ITEMS_FIGHTER.length; i += 2) {
        itemId = Config.STARTING_ITEMS_FIGHTER[i];
        count = (long)Config.STARTING_ITEMS_FIGHTER[i + 1];
        ItemFunctions.addItem(newChar, itemId, count, false);
      }
    } else {
      for(i = 0; i < Config.STARTING_ITEMS_MAGE.length; i += 2) {
        itemId = Config.STARTING_ITEMS_MAGE[i];
        count = (long)Config.STARTING_ITEMS_MAGE[i + 1];
        ItemFunctions.addItem(newChar, itemId, count, false);
      }
    }

    Iterator var10 = SkillAcquireHolder.getInstance().getAvailableSkills(newChar, AcquireType.NORMAL).iterator();

    while(var10.hasNext()) {
      SkillLearn skill = (SkillLearn)var10.next();
      newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);
    }

    var10 = CharTemplateTable.getInstance().getShortCuts(newChar.getClassId()).iterator();

    while(var10.hasNext()) {
      ShortCut shortCut = (ShortCut)var10.next();
      ShortCut skillShortCut;
      switch(shortCut.getType()) {
        case 1:
          ItemInstance shortCutItem = newChar.getInventory().getItemByItemId(shortCut.getId());
          if (shortCutItem != null) {
            skillShortCut = new ShortCut(shortCut.getSlot(), shortCut.getPage(), shortCut.getType(), shortCutItem.getObjectId(), shortCut.getLevel(), shortCut.getCharacterType());
            newChar.registerShortCut(skillShortCut);
          }
          break;
        case 2:
          lvl = newChar.getSkillLevel(shortCut.getId());
          if (lvl > 0) {
            skillShortCut = new ShortCut(shortCut.getSlot(), shortCut.getPage(), shortCut.getType(), shortCut.getId(), lvl, shortCut.getCharacterType());
            newChar.registerShortCut(skillShortCut);
          }
          break;
        default:
          newChar.registerShortCut(shortCut);
      }
    }

    startInitialQuests(newChar);
    newChar.setCurrentHpMp((double)newChar.getMaxHp(), (double)newChar.getMaxMp());
    newChar.setCurrentCp(0.0D);
    newChar.setOnlineStatus(false);
    newChar.store(false);
    newChar.getInventory().store();
    newChar.deleteMe();
    client.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(client.getLogin()));
  }

  public static void startInitialQuests(Player player) {
    for(int startQuestIdx = 0; startQuestIdx < Config.ALT_INITIAL_QUESTS.length; ++startQuestIdx) {
      int questId = Config.ALT_INITIAL_QUESTS[startQuestIdx];
      Quest q = QuestManager.getQuest(questId);
      if (q != null) {
        q.newQuestState(player, 1);
      }
    }

  }
}
