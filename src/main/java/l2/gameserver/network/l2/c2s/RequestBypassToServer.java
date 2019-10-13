//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Scripts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBypassToServer extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);
  private DecodedBypass bp = null;
  private String _bypass;

  public RequestBypassToServer() {
  }

  protected void readImpl() {
    this._bypass = this.readS();
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    Player activeChar = client.getActiveChar();

    GameObject object;
    try {
      if (!this._bypass.isEmpty()) {
        this.bp = client.decodeBypass(this._bypass);
      }

      if (this.bp == null) {
        return;
      }

      if (activeChar == null) {
        if (Config.USE_SECOND_PASSWORD_AUTH && this.bp.bypass.startsWith("spa_")) {
          client.getSecondPasswordAuth().getUI().handle(client, this.bp.bypass.substring(4));
        }

        return;
      }

      NpcInstance npc = activeChar.getLastNpc();
      GameObject target = activeChar.getTarget();
      if (npc == null && target != null && target.isNpc()) {
        npc = (NpcInstance)target;
      }

      if (this.bp.bypass.startsWith("admin_")) {
        AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, this.bp.bypass);
      } else if (this.bp.bypass.equals("come_here") && activeChar.isGM()) {
        comeHere((GameClient)this.getClient());
      } else if (this.bp.bypass.startsWith("player_help ")) {
        playerHelp(activeChar, this.bp.bypass.substring(12));
      } else {
        String p;
        if (this.bp.bypass.startsWith("scripts_")) {
          p = this.bp.bypass.substring(8).trim();
          String[] word = p.split("\\s+");
          String[] args = p.substring(word[0].length()).trim().split("\\s+");
          String[] path = word[0].split(":");
          if (path.length != 2) {
            _log.warn("Bad Script bypass!");
            return;
          }

          Map<String, Object> variables = null;
          if (npc != null) {
            variables = new HashMap(1);
            variables.put("npc", npc.getRef());
          }

          if (word.length == 1) {
            Scripts.getInstance().callScripts(activeChar, path[0], path[1], variables);
          } else {
            Scripts.getInstance().callScripts(activeChar, path[0], path[1], new Object[]{args}, variables);
          }
        } else {
          String id;
          if (this.bp.bypass.startsWith("user_")) {
            p = this.bp.bypass.substring(5).trim();
            id = p.split("\\s+")[0];
            String args = p.substring(id.length()).trim();
            IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(id);
            if (vch != null) {
              vch.useVoicedCommand(id, activeChar, args);
            } else {
              _log.warn("Unknown voiced command '" + id + "'");
            }
          } else {
            int stadium_id;
            if (this.bp.bypass.startsWith("npc_")) {
              stadium_id = this.bp.bypass.indexOf(95, 5);
              if (stadium_id > 0) {
                id = this.bp.bypass.substring(4, stadium_id);
              } else {
                id = this.bp.bypass.substring(4);
              }

              GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
              if (object != null && object.isNpc() && stadium_id > 0 && object.isInActingRange(activeChar)) {
                activeChar.setLastNpc((NpcInstance)object);
                ((NpcInstance)object).onBypassFeedback(activeChar, this.bp.bypass.substring(stadium_id + 1));
              }
            } else if (this.bp.bypass.startsWith("_olympiad?command=move_op_field&field=")) {
              if (!Config.OLY_SPECTATION_ALLOWED) {
                return;
              }

              boolean var16 = true;

              try {
                stadium_id = Integer.parseInt(this.bp.bypass.substring(38));
                CompetitionController.getInstance().watchCompetition(activeChar, stadium_id);
              } catch (Exception var10) {
                _log.warn("OlyObserver", var10);
                var10.printStackTrace();
              }
            } else {
              StringTokenizer st;
              int heroclass;
              int heropage;
              if (this.bp.bypass.startsWith("_diary")) {
                p = this.bp.bypass.substring(this.bp.bypass.indexOf("?") + 1);
                st = new StringTokenizer(p, "&");
                heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                HeroController.getInstance().showHeroDiary(activeChar, heroclass, heropage);
              } else if (this.bp.bypass.startsWith("_match")) {
                p = this.bp.bypass.substring(this.bp.bypass.indexOf("?") + 1);
                st = new StringTokenizer(p, "&");
                heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                HeroController.getInstance().showHistory(activeChar, heroclass, heropage);
              } else if (this.bp.bypass.startsWith("manor_menu_select?")) {
                object = activeChar.getTarget();
                if (object != null && object.isNpc()) {
                  ((NpcInstance)object).onBypassFeedback(activeChar, this.bp.bypass);
                }
              } else if (this.bp.bypass.startsWith("multisell ")) {
                MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(this.bp.bypass.substring(10)), activeChar, 0.0D);
              } else if (this.bp.bypass.startsWith("Quest ")) {
                p = this.bp.bypass.substring(6).trim();
                int idx = p.indexOf(32);
                if (idx < 0) {
                  activeChar.processQuestEvent(p, "", npc);
                } else {
                  activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
                }
              } else if (this.bp.bbs) {
                if (!Config.COMMUNITYBOARD_ENABLED) {
                  activeChar.sendPacket(new SystemMessage(938));
                } else {
                  ICommunityBoardHandler communityBoardHandler = CommunityBoardManager.getInstance().getCommunityHandler(this.bp.bypass);
                  communityBoardHandler.onBypassCommand(activeChar, this.bp.bypass);
                }
              }
            }
          }
        }
      }
    } catch (Exception var11) {
      String st = "Bad RequestBypassToServer: " + this.bp.bypass;
      object = activeChar != null ? activeChar.getTarget() : null;
      if (object != null && object.isNpc()) {
        st = st + " via NPC #" + ((NpcInstance)object).getNpcId();
      }

      _log.error(st, var11);
    }

  }

  private static void comeHere(GameClient client) {
    GameObject obj = client.getActiveChar().getTarget();
    if (obj != null && obj.isNpc()) {
      NpcInstance temp = (NpcInstance)obj;
      Player activeChar = client.getActiveChar();
      temp.setTarget(activeChar);
      temp.moveToLocation(activeChar.getLoc(), 0, true);
    }

  }

  private static void playerHelp(Player activeChar, String path) {
    NpcHtmlMessage html = new NpcHtmlMessage(5);
    html.setFile(path);
    activeChar.sendPacket(html);
  }
}
