//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.CharSelectInfoPackage;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.tables.CharTemplateTable;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.utils.AutoBan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionInfo extends L2GameServerPacket {
  private static final Logger _log = LoggerFactory.getLogger(CharacterSelectionInfo.class);
  private String _loginName;
  private int _sessionId;
  private CharSelectInfoPackage[] _characterPackages;

  public CharacterSelectionInfo(String loginName, int sessionId) {
    this._sessionId = sessionId;
    this._loginName = loginName;
    this._characterPackages = loadCharacterSelectInfo(loginName);
  }

  public CharSelectInfoPackage[] getCharInfo() {
    return this._characterPackages;
  }

  protected final void writeImpl() {
    int size = this._characterPackages != null ? this._characterPackages.length : 0;
    this.writeC(19);
    this.writeD(size);
    long lastAccess = -1L;
    int lastUsed = -1;

    int i;
    for (i = 0; i < size; ++i) {
      if (lastAccess < this._characterPackages[i].getLastAccess()) {
        lastAccess = this._characterPackages[i].getLastAccess();
        lastUsed = i;
      }
    }

    for (i = 0; i < size; ++i) {
      CharSelectInfoPackage charInfoPackage = this._characterPackages[i];
      this.writeS(charInfoPackage.getName());
      this.writeD(charInfoPackage.getCharId());
      this.writeS(this._loginName);
      this.writeD(this._sessionId);
      this.writeD(charInfoPackage.getClanId());
      this.writeD(0);
      this.writeD(charInfoPackage.getSex());
      this.writeD(charInfoPackage.getRace());
      this.writeD(charInfoPackage.getBaseClassId());
      this.writeD(1);
      this.writeD(charInfoPackage.getX());
      this.writeD(charInfoPackage.getY());
      this.writeD(charInfoPackage.getZ());
      this.writeF(charInfoPackage.getCurrentHp());
      this.writeF(charInfoPackage.getCurrentMp());
      this.writeD(charInfoPackage.getSp());
      this.writeQ(charInfoPackage.getExp());
      int lvl = charInfoPackage.getLevel();
      this.writeD(lvl);
      this.writeD(charInfoPackage.getKarma());
      this.writeD(charInfoPackage.getPk());
      this.writeD(charInfoPackage.getPvP());
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      int[] var8 = Inventory.PAPERDOLL_ORDER;
      int var9 = var8.length;

      int var10;
      int PAPERDOLL_ID;
      for (var10 = 0; var10 < var9; ++var10) {
        PAPERDOLL_ID = var8[var10];
        this.writeD(charInfoPackage.getPaperdollObjectId(PAPERDOLL_ID));
      }

      var8 = Inventory.PAPERDOLL_ORDER;
      var9 = var8.length;

      for (var10 = 0; var10 < var9; ++var10) {
        PAPERDOLL_ID = var8[var10];
        this.writeD(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));
      }

      this.writeD(charInfoPackage.getHairStyle());
      this.writeD(charInfoPackage.getHairColor());
      this.writeD(charInfoPackage.getFace());
      this.writeF(charInfoPackage.getMaxHp());
      this.writeF(charInfoPackage.getMaxMp());
      this.writeD(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
      this.writeD(charInfoPackage.getClassId());
      this.writeD(i == lastUsed ? 1 : 0);
      this.writeC(Math.min(charInfoPackage.getPaperdollEnchantEffect(7), 127));
      this.writeD(charInfoPackage.getPaperdollAugmentationId(7));
    }

  }

  public static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName) {
    List<CharSelectInfoPackage> characterList = new ArrayList<>();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id AND cs.active=1) WHERE account_name=? LIMIT 7");
      statement.setString(1, loginName);
      rset = statement.executeQuery();

      while (rset.next()) {
        CharSelectInfoPackage charInfopackage = restoreChar(rset);
        if (charInfopackage != null) {
          characterList.add(charInfopackage);
        }
      }
    } catch (Exception var10) {
      _log.error("could not restore charinfo:", var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return characterList.toArray(new CharSelectInfoPackage[0]);
  }

  private static int restoreBaseClassId(int objId) {
    int classId = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT class_id FROM character_subclasses WHERE char_obj_id=? AND isBase=1");
      statement.setInt(1, objId);

      for (rset = statement.executeQuery(); rset.next(); classId = rset.getInt("class_id")) {
      }
    } catch (Exception var9) {
      _log.error("could not restore base class id:", var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return classId;
  }

  private static CharSelectInfoPackage restoreChar(ResultSet chardata) {
    CharSelectInfoPackage charInfopackage = null;

    try {
      int objectId = chardata.getInt("obj_Id");
      int classid = chardata.getInt("class_id");
      int baseClassId = classid;
      boolean useBaseClass = chardata.getInt("isBase") > 0;
      if (!useBaseClass) {
        baseClassId = restoreBaseClassId(objectId);
      }

      boolean female = chardata.getInt("sex") == 1;
      PlayerTemplate templ = CharTemplateTable.getInstance().getTemplate(baseClassId, female);
      if (templ == null) {
        _log.error("restoreChar fail | templ == null | objectId: " + objectId + " | classid: " + baseClassId + " | female: " + female);
        return null;
      }

      String name = chardata.getString("char_name");
      charInfopackage = new CharSelectInfoPackage(objectId, name);
      charInfopackage.setLevel(chardata.getInt("level"));
      charInfopackage.setMaxHp(chardata.getInt("maxHp"));
      charInfopackage.setCurrentHp(chardata.getDouble("curHp"));
      charInfopackage.setMaxMp(chardata.getInt("maxMp"));
      charInfopackage.setCurrentMp(chardata.getDouble("curMp"));
      charInfopackage.setX(chardata.getInt("x"));
      charInfopackage.setY(chardata.getInt("y"));
      charInfopackage.setZ(chardata.getInt("z"));
      charInfopackage.setPk(chardata.getInt("pkkills"));
      charInfopackage.setPvP(chardata.getInt("pvpkills"));
      charInfopackage.setFace(chardata.getInt("face"));
      charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
      charInfopackage.setHairColor(chardata.getInt("haircolor"));
      charInfopackage.setSex(female ? 1 : 0);
      charInfopackage.setExp(chardata.getLong("exp"));
      charInfopackage.setSp(chardata.getInt("sp"));
      charInfopackage.setClanId(chardata.getInt("clanid"));
      charInfopackage.setKarma(chardata.getInt("karma"));
      charInfopackage.setRace(templ.race.ordinal());
      charInfopackage.setClassId(classid);
      charInfopackage.setBaseClassId(baseClassId);
      long deletetime = chardata.getLong("deletetime");
//      int deletedays = false;
      if (Config.DELETE_DAYS > 0) {
        if (deletetime > 0L) {
          deletetime = (int) (System.currentTimeMillis() / 1000L - deletetime);
          int deletedays = (int) (deletetime / 3600L / 24L);
          if (deletedays >= Config.DELETE_DAYS) {
            CharacterDAO.getInstance().deleteCharacterDataByObjId(objectId);
            return null;
          }

          deletetime = (long) (Config.DELETE_DAYS * 3600 * 24) - deletetime;
        } else {
          deletetime = 0L;
        }
      }

      charInfopackage.setDeleteTimer((int) deletetime);
      charInfopackage.setLastAccess(chardata.getLong("lastAccess") * 1000L);
      charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));
      int points = chardata.getInt("vitality") + (int) ((double) (System.currentTimeMillis() - charInfopackage.getLastAccess()) / 15.0D);
      if (points > 20000) {
        points = 20000;
      } else if (points < 0) {
        points = 0;
      }

      charInfopackage.setVitalityPoints(points);
      if (charInfopackage.getAccessLevel() < 0 && !AutoBan.isBanned(objectId)) {
        charInfopackage.setAccessLevel(0);
      }
    } catch (Exception var13) {
      _log.error("", var13);
    }

    return charInfopackage;
  }
}
