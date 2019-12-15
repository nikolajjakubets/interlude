//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.StringHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Util;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class SecondPasswordAuth {
  private final String _login;
  private String _secondPassword;
  private int _tryLine;
  private long _blockEndTime;
  private SecondPasswordAuth.SecondPasswordAuthUI _ui;

  public SecondPasswordAuth(String login) {
    this._login = login;
    this._secondPassword = null;
  }

  private String getSecondPassword() {
    if (this._secondPassword != null) {
      return this._secondPassword;
    } else {
      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rset = null;

      try {
        conn = DatabaseFactory.getInstance().getConnection();
        pstmt = conn.prepareStatement("SELECT `password`, `tryLine`, `blockEndTime` FROM `second_auth` WHERE `login` = ?");
        pstmt.setString(1, this._login);
        rset = pstmt.executeQuery();
        if (rset.next()) {
          this._secondPassword = rset.getString("password");
          this._tryLine = Math.min(Config.SECOND_AUTH_MAX_TRYS, rset.getInt("tryLine"));
          this._blockEndTime = rset.getLong("blockEndTime");
        }
      } catch (SQLException var8) {
        log.warn("Database error on retreiving second password for login '" + this._login + "' :", var8);
      } finally {
        DbUtils.closeQuietly(conn, pstmt, rset);
      }

      return this._secondPassword;
    }
  }

  private void store() {
    if (this._secondPassword != null) {
      Connection conn = null;
      PreparedStatement pstmt = null;

      try {
        conn = DatabaseFactory.getInstance().getConnection();
        pstmt = conn.prepareStatement("REPLACE INTO `second_auth`(`login`, `password`, `tryLine`, `blockEndTime`) VALUES (?, ?, ?, ?)");
        pstmt.setString(1, this._login);
        pstmt.setString(2, this._secondPassword);
        pstmt.setInt(3, this.getTrysCount());
        pstmt.setLong(4, this._blockEndTime);
        pstmt.executeUpdate();
      } catch (SQLException var7) {
        log.warn("Database error on storing second password for login '" + this._login + "' :", var7);
      } finally {
        DbUtils.closeQuietly(conn, pstmt);
      }

    }
  }

  public boolean isSecondPasswordSet() {
    return this.getSecondPassword() != null;
  }

  public boolean isBlocked() {
    if (this._blockEndTime == 0L) {
      return false;
    } else if (this._blockEndTime * 1000L < System.currentTimeMillis()) {
      this._blockEndTime = 0L;
      this._tryLine = 0;
      this.store();
      return false;
    } else {
      return true;
    }
  }

  public int getBlockTimeLeft() {
    return (int)Math.max(0L, this._blockEndTime - System.currentTimeMillis() / 1000L);
  }

  public int getTrysCount() {
    return Math.min(Config.SECOND_AUTH_MAX_TRYS, this._tryLine);
  }

  public boolean isValidSecondPassword(String checkSecondPassword) {
    if (checkSecondPassword == null && this.getSecondPassword() == null) {
      return true;
    } else if (checkSecondPassword.equalsIgnoreCase(this.getSecondPassword())) {
      this._blockEndTime = 0L;
      this._tryLine = 0;
      this.store();
      return true;
    } else {
      ++this._tryLine;
      if (this._tryLine >= Config.SECOND_AUTH_MAX_TRYS) {
        this._blockEndTime = System.currentTimeMillis() / 1000L + Config.SECOND_AUTH_BLOCK_TIME;
        this._tryLine = Config.SECOND_AUTH_MAX_TRYS;
      }

      this.store();
      return false;
    }
  }

  public boolean changePassword(String oldSecondPassword, String newSecondPassword) {
    if (!this.isValidSecondPassword(oldSecondPassword)) {
      return false;
    } else {
      this._secondPassword = newSecondPassword;
      this.store();
      return true;
    }
  }

  public SecondPasswordAuth.SecondPasswordAuthUI getUI() {
    return this._ui;
  }

  public void setUI(SecondPasswordAuth.SecondPasswordAuthUI ui) {
    this._ui = ui;
  }

  public static class SecondPasswordAuthUI {
    private static final Language SPA_UI_LANG;
    private static final Random RND = new Random();
    private SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType _type;
    private SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData[] _inputs;
    private SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData _inputFocus;
    private SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult _result;
    private ArrayList<Integer> _numpad = new ArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    private Runnable _runOnVerify;

    public SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType type) {
      Collections.shuffle(this._numpad, RND);
      this._type = type;
      switch(this._type) {
        case CREATE:
          this._inputs = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData[2];
          this._inputs[0] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.CREATE.PIN"), 0);
          this._inputs[1] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.CREATE.PINConfirm"), 1);
          this._inputFocus = this._inputs[0];
          break;
        case VERIFY:
          this._inputs = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData[1];
          this._inputs[0] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.VERIFY.PIN"), 0);
          this._inputFocus = this._inputs[0];
          break;
        case CHANGE:
          this._inputs = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData[3];
          this._inputs[0] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINOld"), 0);
          this._inputs[1] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINNew"), 1);
          this._inputs[2] = new SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINNewConfirm"), 2);
          this._inputFocus = this._inputs[0];
      }

      this._result = null;
    }

    public void setRunOnVerify(Runnable runOnVerify) {
      this._runOnVerify = runOnVerify;
    }

    private void handleArg(GameClient client, String args) {
      if (!args.equals("cnl")) {
        int digit;
        NpcHtmlMessage html;
        if (args.startsWith("af")) {
          digit = args.charAt(2) - 48;
          this._inputFocus = this._inputs[digit];
        } else if (args.startsWith("np")) {
          if (this._inputFocus != null) {
            if (args.equals("npc")) {
              this._inputFocus.clear();
            } else if (args.equals("npb")) {
              this._inputFocus.back();
            } else {
              digit = args.charAt(2) - 48;
              if (digit >= 0 && digit <= 9 && this._inputFocus.getLen() < 8) {
                this._inputFocus.add(digit);
              }
            }
          }
        } else {
          if (args.equals("hlp")) {
            html = new NpcHtmlMessage(5);
            html.setFile("spahelp.htm");
            client.sendPacket(html);
            return;
          }

          if (args.equals("hlb")) {
            client.getSecondPasswordAuth().getUI().handle(client, "");
            return;
          }

          if (args.equals("cgh")) {
            SecondPasswordAuth.SecondPasswordAuthUI changeUI = new SecondPasswordAuth.SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType.CHANGE);
            changeUI.setRunOnVerify(this._runOnVerify);
            client.getSecondPasswordAuth().setUI(changeUI);
            changeUI.handle(client, "");
            return;
          }

          if (args.equals("okk")) {
            SecondPasswordAuth spa = client.getSecondPasswordAuth();
            if (spa == null) {
              return;
            }

            String oldPin;
            String newPin;
            switch(this._type) {
              case CREATE:
                if (this._inputs[0].getLen() >= Config.SECOND_AUTH_MIN_LENG && this._inputs[1].getLen() >= Config.SECOND_AUTH_MIN_LENG) {
                  if (this._inputs[0].getLen() <= Config.SECOND_AUTH_MAX_LENG && this._inputs[1].getLen() <= Config.SECOND_AUTH_MAX_LENG) {
                    if (!this._inputs[0].toString().equals(this._inputs[1].toString())) {
                      this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.NOT_MATCH;
                    } else if (!this._inputs[0].isStrongPin()) {
                      this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_SIMPLE;
                    } else {
                      if (!spa.isSecondPasswordSet()) {
                        oldPin = this._inputs[0].toString();
                        spa.changePassword((String)null, oldPin);
                        SecondPasswordAuth.SecondPasswordAuthUI verifyUI = new SecondPasswordAuth.SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType.VERIFY);
                        verifyUI.setRunOnVerify(this._runOnVerify);
                        client.getSecondPasswordAuth().setUI(verifyUI);
                        verifyUI.handle(client, "");
                        return;
                      }

                      this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.ERROR;
                    }
                  } else {
                    this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_LONG;
                  }
                } else {
                  this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_SHORT;
                }
                break;
              case VERIFY:
                if (spa.isSecondPasswordSet()) {
                  oldPin = this._inputs[0].toString();
                  if (!spa.isBlocked() && spa.isValidSecondPassword(oldPin)) {
                    if (this._runOnVerify != null) {
                      client.setSecondPasswordAuthed(true);
                      ThreadPoolManager.getInstance().execute(this._runOnVerify);
                    }

                    return;
                  }

                  if (spa.isBlocked()) {
                    this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
                    newPin = StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
                    newPin = newPin.replace("%tryCnt%", Integer.toString(spa.getTrysCount()));
                    newPin = newPin.replace("%time%", Util.formatTime(spa.getBlockTimeLeft()));
                    client.close((new ConfirmDlg(SystemMsg.S1, -1)).addString(newPin));
                  } else {
                    this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.FAIL_VERIFY;
                    this.verifyFail(client);
                  }

                  return;
                }

                this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.ERROR;
                break;
              case CHANGE:
                if (!spa.isSecondPasswordSet()) {
                  this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.ERROR;
                } else if (this._inputs[0].getLen() >= Config.SECOND_AUTH_MIN_LENG && this._inputs[1].getLen() >= Config.SECOND_AUTH_MIN_LENG && this._inputs[2].getLen() >= Config.SECOND_AUTH_MIN_LENG) {
                  if (this._inputs[0].getLen() <= Config.SECOND_AUTH_MAX_LENG && this._inputs[1].getLen() <= Config.SECOND_AUTH_MAX_LENG && this._inputs[2].getLen() <= Config.SECOND_AUTH_MAX_LENG) {
                    oldPin = this._inputs[0].toString();
                    if (!this._inputs[1].toString().equals(this._inputs[2].toString())) {
                      this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.NOT_MATCH;
                    } else {
                      if (this._inputs[1].isStrongPin()) {
                        newPin = this._inputs[1].toString();
                        if (!spa.isBlocked() && spa.changePassword(oldPin, newPin)) {
                          SecondPasswordAuth.SecondPasswordAuthUI verifyUI = new SecondPasswordAuth.SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType.VERIFY);
                          verifyUI.setRunOnVerify(this._runOnVerify);
                          client.getSecondPasswordAuth().setUI(verifyUI);
                          verifyUI.handle(client, "");
                          return;
                        }

                        if (spa.isBlocked()) {
                          this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
                          String blockMsg = StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
                          blockMsg = blockMsg.replace("%tryCnt%", Integer.toString(spa.getTrysCount()));
                          blockMsg = blockMsg.replace("%time%", Util.formatTime(spa.getBlockTimeLeft()));
                          client.close((new ConfirmDlg(SystemMsg.S1, -1)).addString(blockMsg));
                        } else {
                          this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.FAIL_VERIFY;
                          this.verifyFail(client);
                        }

                        return;
                      }

                      this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_SIMPLE;
                    }
                  } else {
                    this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_LONG;
                  }
                } else {
                  this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.TOO_SHORT;
                }
            }
          }
        }

        html = new NpcHtmlMessage(5);
        html.setHtml(this.format());
        client.sendPacket(html);
      }
    }

    public void handle(GameClient client, String args) {
      if (args != null) {
        try {
          this.handleArg(client, args);
        } catch (Exception e) {
          log.error("handle: eMessage={}, eClass={}", e.getMessage(), e.getClass());
        }
      }

    }

    public boolean verify(GameClient client, Runnable runOnSuccess) {
      SecondPasswordAuth spa = client.getSecondPasswordAuth();
      if (spa == null) {
        return false;
      } else {
        this.setRunOnVerify(runOnSuccess);
        if (spa.isBlocked()) {
          this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
          String blockMsg = StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
          blockMsg = blockMsg.replace("%tryCnt%", Integer.toString(spa.getTrysCount()));
          blockMsg = blockMsg.replace("%time%", Util.formatTime(spa.getBlockTimeLeft()));
          client.close((new ConfirmDlg(SystemMsg.S1, -1)).addString(blockMsg));
          return false;
        } else {
          this.handle(client, "");
          return true;
        }
      }
    }

    private void verifyFail(GameClient client) {
      SecondPasswordAuth spa = client.getSecondPasswordAuth();
      if (spa != null) {
        this._result = SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIResult.FAIL_VERIFY;
        String blockMsg = StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Result.FAIL_VERIFY");
        blockMsg = blockMsg.replace("%tryCnt%", Integer.toString(Config.SECOND_AUTH_MAX_TRYS - spa.getTrysCount()));
        client.close((new ConfirmDlg(SystemMsg.S1, -1)).addString(blockMsg));
      }
    }

    private String getTitle() {
      return StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth." + this._type.name() + ".Title");
    }

    private String getFormDescription() {
      return StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth." + this._type.name() + ".Description");
    }

    private String getNote() {
      return this._result == null ? StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Note") : StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Result." + this._result.name());
    }

    private String getInputDescription() {
      return StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.InputDescription");
    }

    private String format() {
      StringBuilder sb = new StringBuilder(8192);
      sb.append("<html>");
      sb.append("<head><title>").append(this.getTitle()).append("</title></head>");
      sb.append("<body><center>");
      sb.append("<table width=270 border=0 cellspacing=0 cellpadding=0>");
      sb.append("<br>");
      sb.append("<tr><td fixwidth=270 align=center>");
      this.formatFormContent(sb);
      sb.append("</td></tr>");
      sb.append("<tr><td fixwidth=270 align=center>");
      sb.append("<img src=\"L2UI.SquareBlank\" width=270 height=10>");
      this.formatNote(sb);
      sb.append("</td></tr>");
      sb.append("<tr><td align=center>");
      sb.append("<img src=\"L2UI.SquareBlank\" width=270 height=10>");
      this.formatButtons(sb);
      sb.append("</td></tr>");
      sb.append("</table>");
      sb.append("</center></body>");
      sb.append("</html> ");
      return sb.toString();
    }

    private void formatFormContent(StringBuilder sb) {
      sb.append("<table width=260 height=250 border=0 cellspacing=5 cellpadding=0 bgcolor=000000>");
      sb.append("<tr><td valign=TOP height=80>").append(this.getFormDescription()).append("</td></tr>");
      sb.append("<tr><td align=CENTER>").append(this.getInputDescription()).append("</td></tr>");
      sb.append("<tr><td valign=TOP>");
      this.formatInputs(sb);
      sb.append("<br>");
      sb.append("</td></tr>");
      sb.append("<tr><td valign=TOP align=center height=100>");
      sb.append("<img src=\"L2UI.SquareGray\" width=250 height=1><br>");
      this.formatNumPad(sb);
      sb.append("<br>");
      sb.append("</td></tr>");
      sb.append("</table>");
    }

    private void formatInputs(StringBuilder sb) {
      sb.append("<table width=250 height=60 border=0 cellspacing=5 cellpadding=0>");
      SecondPasswordAuth.SecondPasswordAuthUI.SPAUIPINInputData[] var2 = this._inputs;

      for (SPAUIPINInputData suid : var2) {
        suid.formatPINInput(sb, this._inputFocus == suid);
      }

      sb.append("</table>");
    }

    private void formatNote(StringBuilder sb) {
      sb.append("<table height=20 border=0 cellspacing=0 cellpadding=0 bgcolor=\"000000\">").append("<tr><td align=center valign=center fixwidth=264>").append(this.getNote()).append("</td></tr></table>");
    }

    private void formatButtons(StringBuilder sb) {
      sb.append("<table width=260 border=0 cellspacing=0 cellpadding=0><tr>");
      sb.append("<td fixwidth=50>");
      if (this._type.isCanChange()) {
        sb.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Change")).append("\" action=\"bypass -h spa_cgh\">");
      }

      sb.append("</td>");
      sb.append("<td width=60>&nbsp;</td>");
      sb.append("<td>");
      sb.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Help")).append("\" action=\"bypass -h spa_hlp\">");
      sb.append("</td>");
      sb.append("<td>");
      sb.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.OK")).append("\" action=\"bypass -h spa_okk\">");
      sb.append("</td>");
      sb.append("<td>");
      sb.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(SPA_UI_LANG, "l2.gameserver.network.l2.SecondPasswordAuth.Cancel")).append("\" action=\"bypass -h spa_cnl\">");
      sb.append("</td>");
      sb.append("</tr></table>");
    }

    public void formatNumPad(StringBuilder sb) {
      sb.append("<table width=90 border=0 cellspacing=0 cellpadding=0>");

      int num;
      for(int i = 0; i < 3; ++i) {
        sb.append("<tr>");

        for(int j = 0; j < 3; ++j) {
          int idx = i * 3 + j;
          num = (Integer)this._numpad.get(idx);
          sb.append("<td>");
          sb.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_").append(num).append("\" back=\"L2UI_CH3.calculate2_").append(num).append("_down\" value=\"\" action=\"bypass spa_np").append(num).append("\">");
          sb.append("</td>");
        }

        sb.append("</tr>");
      }

      sb.append("<tr><td>");
      num = (Integer)this._numpad.get(9);
      sb.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_c\" back=\"L2UI_CH3.calculate2_c_down\" action=\"bypass spa_npc\">");
      sb.append("</td><td>");
      sb.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_").append(num).append("\" back=\"L2UI_CH3.calculate2_").append(num).append("_down\" value=\"\" action=\"bypass spa_np").append(num).append("\">");
      sb.append("</td><td>");
      sb.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_bs\" back=\"L2UI_CH3.calculate2_bs_down\" action=\"bypass spa_npb\">");
      sb.append("</td></tr>");
      sb.append("</table>");
    }

    static {
      Language lang = Language.ENGLISH;
      Language[] var1 = Language.VALUES;

      for (Language lang2 : var1) {
        if (lang2.getShortName().equals(Config.DEFAULT_LANG)) {
          lang = lang2;
        }
      }

      SPA_UI_LANG = lang;
    }

    private static class SPAUIPINInputData {
      private final Stack<Integer> _pin = new Stack<>();
      private final String _label;
      private final int _inputFieldIdx;

      public SPAUIPINInputData(String label, int inputFieldIdx) {
        this._label = label;
        this._inputFieldIdx = inputFieldIdx;
      }

      public String getLabel() {
        return this._label;
      }

      public int getInputFieldIdx() {
        return this._inputFieldIdx;
      }

      public void clear() {
        this._pin.clear();
      }

      public void back() {
        if (!this._pin.isEmpty()) {
          this._pin.pop();
        }

      }

      public void add(int digit) {
        this._pin.add(digit);
      }

      public boolean isEmpty() {
        return this._pin.isEmpty();
      }

      public boolean isStrongPin() {
        return !this.isEmpty();
      }

      public int getLen() {
        return this._pin.size();
      }

      private void formatPINInputBox(StringBuilder sb, boolean isActive, int len, String link) {
        int dWidth = 8 * Math.min(8, len);
        int cWidth = isActive ? 1 : 0;
        int eWidth = 65 - (dWidth + cWidth);
        String hTexture = isActive ? "L2UI_CH3.inputbox02_over" : "L2UI_CH3.M_inputbox02";
        String vTexture = isActive ? "L2UI_CH3.inputbox04_over" : "L2UI_CH3.M_inputbox04";
        String dTexture = isActive ? "L2UI_CH3.radar_tutorial1" : "L2UI_CH3.radar_tutorial2";
        sb.append("<table width=67 height=12 border=0 cellspacing=0 cellpadding=0>");
        sb.append("<tr><td>");
        sb.append("<img src=\"").append(hTexture).append("\" width=67 height=1>");
        sb.append("</td></tr>");
        sb.append("<tr><td>");
        sb.append("<table width=67 height=12 border=0 cellspacing=0 cellpadding=0><tr>");
        sb.append("<td><img src=\"").append(vTexture).append("\" width=1 height=12></td>");
        sb.append("<td>");
        sb.append("<img src=\"L2UI.SquareBlank\" width=65 height=4>");
        sb.append("<table border=0 cellspacing=0 cellpadding=0><tr>");
        if (dWidth > 0) {
          sb.append("<td fixwidth=").append(dWidth).append(">");
          sb.append("<button width=").append(dWidth).append(" height=8 ").append("fore=\"").append(dTexture).append("\" back=\"").append(dTexture).append("\" value=\" \"");
          if (link != null) {
            sb.append(" action=\"bypass spa_").append(link).append("\"");
          }

          sb.append(">");
          sb.append("</td>");
        }

        if (cWidth > 0) {
          sb.append("<td valign=TOP><img src=\"L2UI.SquareWhite\" width=1 height=8></td>");
        }

        if (eWidth > 0) {
          sb.append("<td FIXWIDTH=").append(eWidth).append(">");
          sb.append("<button width=").append(eWidth).append(" height=8 ").append("fore=\"L2UI.SquareBlank\" back=\"L2UI.SquareBlank\" value=\" \"");
          if (link != null) {
            sb.append(" action=\"bypass spa_").append(link).append("\"");
          }

          sb.append(">");
          sb.append("</td>");
        }

        sb.append("</tr></table>");
        sb.append("</td>");
        sb.append("<td><img src=\"").append(vTexture).append("\" width=1 height=12></td>");
        sb.append("</tr></table>");
        sb.append("</td></tr>");
        sb.append("<tr><td>");
        sb.append("<img src=\"").append(hTexture).append("\" width=67 height=1>");
        sb.append("</td></tr>");
        sb.append("</table>");
      }

      public void formatPINInput(StringBuilder sb, boolean isActive) {
        sb.append("<tr>");
        sb.append("<td align=right valign=TOP fixwidth=100>");
        if (!isActive) {
          sb.append("<font color=\"a2a0a2\">").append(this.getLabel()).append("</font>");
        } else {
          sb.append(this.getLabel());
        }

        sb.append("</td>");
        sb.append("<td align=left>");
        this.formatPINInputBox(sb, isActive, this.getLen(), String.format("af%d", this.getInputFieldIdx()));
        sb.append("</td>");
        sb.append("</tr>");
      }

      public String toString() {
        StringBuilder pinText = new StringBuilder(8);

        for (Integer digit : this._pin) {
          pinText.append((char) (48 + Math.min(9, Math.max(digit, 0))));
        }

        return pinText.toString();
      }
    }

    public static enum SecondPasswordAuthUIType {
      CREATE(false),
      VERIFY(true),
      CHANGE(false);

      private final boolean _canChange;

      private SecondPasswordAuthUIType(boolean canChange) {
        this._canChange = canChange;
      }

      public boolean isCanChange() {
        return this._canChange;
      }
    }

    public static enum SecondPasswordAuthUIResult {
      TOO_SHORT,
      TOO_LONG,
      NOT_MATCH,
      TOO_SIMPLE,
      FAIL_VERIFY,
      BLOCK_HOMEPAGE,
      ERROR;

      private SecondPasswordAuthUIResult() {
      }
    }
  }
}
