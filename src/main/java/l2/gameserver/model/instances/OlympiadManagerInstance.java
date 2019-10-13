//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.Arrays;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.oly.NoblesController.NobleRecord;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExHeroList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadManagerInstance extends NpcInstance {
  private static Logger _log = LoggerFactory.getLogger(OlympiadManagerInstance.class);
  private static final int OLYMPIAD_MANAGER_ID = 31688;
  private static final int[] OLYMPIAD_MONUMENT_IDS = new int[]{31690, 31769, 31770, 31771, 31772};
  private static final int HERO_CIRCLE = 6842;
  private static final int[] TOP_RANK_CLOAKS;

  public OlympiadManagerInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  private Player[] createTeam(Player leader) {
    Player[] ret = new Player[3];
    if (leader.getParty() == null) {
      leader.sendMessage(new CustomMessage("THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM", leader, new Object[0]));
      return null;
    } else if (!leader.getParty().isLeader(leader)) {
      leader.sendMessage(new CustomMessage("ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH", leader, new Object[0]));
      return null;
    } else if (!this.checkMatchLimit(leader, CompetitionType.TEAM_CLASS_FREE)) {
      return null;
    } else {
      ret[0] = leader;
      int i = 0;
      Iterator var4 = leader.getParty().getPartyMembers().iterator();

      while(var4.hasNext()) {
        Player pm = (Player)var4.next();
        if (!this.checkMatchLimit(pm, CompetitionType.TEAM_CLASS_FREE)) {
          return null;
        }

        if (pm != leader) {
          ++i;
          if (i >= 3) {
            leader.sendMessage(new CustomMessage("THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM", leader, new Object[0]));
            return null;
          }

          ret[i] = pm;
        }
      }

      return ret;
    }
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (Config.OLY_ENABLED) {
        SystemMessage sm = null;
        int cloakItemId;
        int i;
        int rank;
        int var20;
        int hid;
        if (command.startsWith("oly hwpn_")) {
          boolean already_got = false;
          rank = Integer.parseInt(command.substring(9));
          cloakItemId = 0;
          int[] var19 = HeroController.HERO_WEAPONS;
          var20 = var19.length;

          for(hid = 0; hid < var20; ++hid) {
            i = var19[hid];
            if (player.getInventory().getItemByItemId(i) != null || player.getWarehouse().getCountOf(i) > 0L) {
              already_got = true;
            }

            if (i == rank) {
              cloakItemId = i;
            }
          }

          if (already_got) {
            this.showChatWindow(player, 51);
          }

          if (player.isHero() && cloakItemId > 0) {
            player.getInventory().addItem(cloakItemId, 1L);
            player.sendPacket(SystemMessage2.obtainItems(cloakItemId, 1L, 0));
          }

        } else if (!command.startsWith("oly ")) {
          super.onBypassFeedback(player, command);
        } else {
          int cmdID = Integer.parseInt(command.substring(4));
          if (this.getNpcId() == 31688) {
            NpcHtmlMessage html;
            switch(cmdID) {
              case 100:
                if (OlyController.getInstance().isRegAllowed()) {
                  html = new NpcHtmlMessage(player, this);
                  if (!ParticipantPool.getInstance().isRegistred(player) && !player.isOlyParticipant()) {
                    html.setFile("oly/olympiad_operator100.htm");
                    html.replace("%period%", String.valueOf(OlyController.getInstance().getCurrentPeriod()));
                    html.replace("%season%", String.valueOf(OlyController.getInstance().getCurrentSeason()));
                    html.replace("%particicnt%", String.valueOf(OlyController.getInstance().getPartCount()));
                    html.replace("%currpartcnt%", String.valueOf(ParticipantPool.getInstance().getParticipantCount()));
                  } else {
                    html.setFile("oly/olympiad_operator110.htm");
                  }

                  player.sendPacket(html);
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 101:
                if (OlyController.getInstance().isRegAllowed()) {
                  if (this.checkMatchLimit(player, CompetitionType.CLASS_INDIVIDUAL)) {
                    player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                    sm = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.CLASS_INDIVIDUAL, new Player[]{player});
                    if (sm != null) {
                      player.sendPacket(sm);
                    }
                  }
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 102:
                if (OlyController.getInstance().isRegAllowed()) {
                  if (this.checkMatchLimit(player, CompetitionType.CLASS_FREE)) {
                    sm = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.CLASS_FREE, new Player[]{player});
                    if (sm != null) {
                      player.sendPacket(sm);
                    }
                  }
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 103:
                if (OlyController.getInstance().isRegAllowed()) {
                  Player[] participants = this.createTeam(player);
                  if (participants != null) {
                    sm = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.TEAM_CLASS_FREE, participants);
                    if (sm != null) {
                      player.sendPacket(sm);
                    }
                  }
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 104:
                if (OlyController.getInstance().isRegAllowed()) {
                  CompetitionType ctype = ParticipantPool.getInstance().getCompTypeOf(player);
                  if (ctype != null) {
                    ParticipantPool.getInstance().removeEntryByPlayer(ctype, player);
                    player.sendPacket(Msg.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
                  }
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 200:
                if (OlyController.getInstance().isRegAllowed()) {
                  CompetitionController.getInstance().showCompetitionList(player);
                } else {
                  player.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                }
                break;
              case 301:
                html = new NpcHtmlMessage(player, this);
                cloakItemId = NoblesController.getInstance().getNoblessePasses(player);
                if (cloakItemId > 0) {
                  html.setFile(this.getHtmlPath(this.getNpcId(), 301, player));
                  html.replace("%points%", String.valueOf(cloakItemId));
                  player.getInventory().addItem(Config.OLY_VICTORY_RITEMID, (long)cloakItemId);
                  player.sendPacket(SystemMessage2.obtainItems(Config.OLY_VICTORY_RITEMID, (long)cloakItemId, 0));
                } else {
                  html.setFile(this.getHtmlPath(this.getNpcId(), 302, player));
                }

                player.sendPacket(html);
                break;
              case 1902:
              case 1903:
                MultiSellHolder.getInstance().SeparateAndSend(cmdID, player, 0.0D);
                break;
              default:
                if (cmdID >= 588 && cmdID <= 634) {
                  rank = cmdID - 500;
                  NpcHtmlMessage rhtml = new NpcHtmlMessage(player, this);
                  rhtml.setFile("oly/olympiad_operator_rank_class.htm");
                  String[] rlist = NoblesController.getInstance().getClassLeaders(rank);
                  String Name = "";
                  String Rank = "";

                  for(i = 0; i < 15; ++i) {
                    if (i < rlist.length) {
                      Name = rlist[i];
                      Rank = String.valueOf(i + 1);
                    } else {
                      Name = "";
                      Rank = "";
                    }

                    rhtml.replace("<?Rank" + (i + 1) + "?>", Rank);
                    rhtml.replace("<?Name" + (i + 1) + "?>", Name);
                  }

                  player.sendPacket(rhtml);
                } else {
                  this.showChatWindow(player, cmdID);
                }
            }
          } else if (Arrays.binarySearch(OLYMPIAD_MONUMENT_IDS, this.getNpcId()) >= 0) {
            if (cmdID == 1000) {
              player.sendPacket(new ExHeroList());
            } else if (cmdID == 2000) {
              if (OlyController.getInstance().isCalculationPeriod()) {
                this.showChatWindow(player, 11);
              } else if (HeroController.getInstance().isInactiveHero(player)) {
                HeroController.getInstance().activateHero(player);
                this.showChatWindow(player, 10);
              } else {
                this.showChatWindow(player, 1);
              }
            } else if (cmdID == 3) {
              if (player.isHero()) {
                if (player.getInventory().getItemByItemId(6842) == null && player.getWarehouse().getCountOf(6842) <= 0L) {
                  player.getInventory().addItem(6842, 1L);
                  player.sendPacket(SystemMessage2.obtainItems(6842, 1L, 0));
                } else {
                  this.showChatWindow(player, 55);
                }
              } else {
                this.showChatWindow(player, 3);
              }
            } else if (cmdID == 4) {
              if (player.isHero()) {
                boolean already_got = false;
                int[] var17 = HeroController.HERO_WEAPONS;
                int var18 = var17.length;

                for(var20 = 0; var20 < var18; ++var20) {
                  hid = var17[var20];
                  if (player.getInventory().getItemByItemId(hid) != null || player.getWarehouse().getCountOf(hid) > 0L) {
                    already_got = true;
                  }
                }

                this.showChatWindow(player, already_got ? 51 : 50);
              } else {
                this.showChatWindow(player, 4);
              }
            } else if (cmdID == 5) {
              if (player.isNoble()) {
                rank = NoblesController.getInstance().getPlayerClassRank(player.getBaseClassId(), player.getObjectId());
                if (rank >= 0 && rank < TOP_RANK_CLOAKS.length) {
                  cloakItemId = TOP_RANK_CLOAKS[rank];
                  if (player.getInventory().getItemByItemId(cloakItemId) == null && player.getWarehouse().getCountOf(cloakItemId) <= 0L) {
                    player.getInventory().addItem(cloakItemId, 1L);
                    player.sendPacket(SystemMessage2.obtainItems(cloakItemId, 1L, 0));
                  } else {
                    this.showChatWindow(player, 5);
                  }
                } else {
                  this.showChatWindow(player, 5);
                }
              } else {
                this.showChatWindow(player, 5);
              }
            } else {
              this.showChatWindow(player, cmdID);
            }
          }

        }
      }
    }
  }

  public void showChatWindow(Player player, int val, Object... replace) {
    if ((val == 0 || val == 100) && !player.isNoble()) {
      super.showChatWindow(player, 900, new Object[0]);
    } else {
      super.showChatWindow(player, val, new Object[0]);
    }

  }

  public String getHtmlPath(int npcId, int val, Player player) {
    return String.format(this.getNpcId() == 31688 ? "oly/olympiad_operator%03d.htm" : "oly/olympiad_monument%03d.htm", val);
  }

  private boolean checkMatchLimit(Player player, CompetitionType type) {
    if (player == null) {
      return false;
    } else {
      NobleRecord nr = NoblesController.getInstance().getNobleRecord(player.getObjectId());
      if (nr.class_based_cnt + nr.class_free_cnt + nr.team_cnt > Config.OLY_MAX_TOTAL_MATCHES) {
        player.sendPacket(SystemMsg.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70);
        return false;
      } else {
        switch(type) {
          case CLASS_FREE:
            if (nr.class_free_cnt > Config.OLY_CF_MATCHES) {
              player.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
              return false;
            }
            break;
          case CLASS_INDIVIDUAL:
            if (nr.class_based_cnt > Config.OLY_CB_MATCHES) {
              player.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
              return false;
            }
            break;
          case TEAM_CLASS_FREE:
            if (nr.team_cnt > Config.OLY_TB_MATCHES) {
              player.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
              return false;
            }
        }

        return true;
      }
    }
  }

  static {
    Arrays.sort(OLYMPIAD_MONUMENT_IDS);
    TOP_RANK_CLOAKS = new int[]{31274, 31275, 31275};
  }
}
