//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.Calendar;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;

public final class FestivalGuideInstance extends NpcInstance {
  protected int _festivalType;
  protected int _festivalOracle;

  public FestivalGuideInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    switch(this.getNpcId()) {
      case 31127:
      case 31132:
        this._festivalType = 0;
        this._festivalOracle = 2;
        break;
      case 31128:
      case 31133:
        this._festivalType = 1;
        this._festivalOracle = 2;
        break;
      case 31129:
      case 31134:
        this._festivalType = 2;
        this._festivalOracle = 2;
        break;
      case 31130:
      case 31135:
        this._festivalType = 3;
        this._festivalOracle = 2;
        break;
      case 31131:
      case 31136:
        this._festivalType = 4;
        this._festivalOracle = 2;
        break;
      case 31137:
      case 31142:
        this._festivalType = 0;
        this._festivalOracle = 1;
        break;
      case 31138:
      case 31143:
        this._festivalType = 1;
        this._festivalOracle = 1;
        break;
      case 31139:
      case 31144:
        this._festivalType = 2;
        this._festivalOracle = 1;
        break;
      case 31140:
      case 31145:
        this._festivalType = 3;
        this._festivalOracle = 1;
        break;
      case 31141:
      case 31146:
        this._festivalType = 4;
        this._festivalOracle = 1;
    }

  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (SevenSigns.getInstance().getPlayerCabal(player) == 0) {
        player.sendMessage(new CustomMessage("FestivalGuide.YouMustBeParticipant", player, new Object[0]));
      } else {
        if (command.startsWith("FestivalDesc")) {
          int val = Integer.parseInt(command.substring(13));
          this.showChatWindow(player, val, (String)null, true);
        } else if (command.startsWith("Festival")) {
          Party playerParty = player.getParty();
          int val = Integer.parseInt(command.substring(9, 10));
          Reflection r;
          switch(val) {
            case 1:
              this.showChatWindow(player, 1, (String)null, false);
              break;
            case 2:
              if (SevenSigns.getInstance().getCurrentPeriod() != 1) {
                this.showChatWindow(player, 2, "a", false);
                return;
              }

              if (SevenSignsFestival.getInstance().isFestivalInitialized()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.InProgress", player, new Object[0]));
                return;
              }

              if (playerParty == null || playerParty.getMemberCount() < Config.FESTIVAL_MIN_PARTY_SIZE) {
                this.showChatWindow(player, 2, "b", false);
                return;
              }

              if (!playerParty.isLeader(player)) {
                this.showChatWindow(player, 2, "c", false);
                return;
              }

              int maxlevel = SevenSignsFestival.getMaxLevelForFestival(this._festivalType);
              Iterator var6 = playerParty.getPartyMembers().iterator();

              while(var6.hasNext()) {
                Player p = (Player)var6.next();
                if (p.getLevel() > maxlevel) {
                  this.showChatWindow(player, 2, "d", false);
                  return;
                }

                if (SevenSigns.getInstance().getPlayerCabal(p) == 0) {
                  this.showChatWindow(player, 2, "g", false);
                  return;
                }
              }

              if (player.isFestivalParticipant()) {
                this.showChatWindow(player, 2, "f", false);
                return;
              }

              int stoneType = Integer.parseInt(command.substring(11));
              long stonesNeeded = (long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, stoneType) * Config.FESTIVAL_RATE_PRICE);
              if (!player.getInventory().destroyItemByItemId(stoneType, stonesNeeded)) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.NotEnoughSSType", player, new Object[0]));
                return;
              }

              player.sendPacket(SystemMessage2.removeItems(stoneType, stonesNeeded));
              SevenSignsFestival.getInstance().addAccumulatedBonus(this._festivalType, stoneType, stonesNeeded);
              new DarknessFestival(player.getParty(), SevenSigns.getInstance().getPlayerCabal(player), this._festivalType);
              this.showChatWindow(player, 2, "e", false);
              break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
              this.showChatWindow(player, val, (String)null, false);
              break;
            case 4:
              StringBuilder strBuffer = new StringBuilder("<html><body>Festival Guide:<br>These are the top scores of the week, for the ");
              StatsSet dawnData = SevenSignsFestival.getInstance().getHighestScoreData(2, this._festivalType);
              StatsSet duskData = SevenSignsFestival.getInstance().getHighestScoreData(1, this._festivalType);
              StatsSet overallData = SevenSignsFestival.getInstance().getOverallHighestScoreData(this._festivalType);
              int dawnScore = dawnData.getInteger("score");
              int duskScore = duskData.getInteger("score");
              int overallScore = 0;
              if (overallData != null) {
                overallScore = overallData.getInteger("score");
              }

              strBuffer.append(SevenSignsFestival.getFestivalName(this._festivalType) + " festival.<br>");
              if (dawnScore > 0) {
                strBuffer.append("Dawn: " + this.calculateDate(dawnData.getString("date")) + ". Score " + dawnScore + "<br>" + dawnData.getString("names").replaceAll(",", ", ") + "<br>");
              } else {
                strBuffer.append("Dawn: No record exists. Score 0<br>");
              }

              if (duskScore > 0) {
                strBuffer.append("Dusk: " + this.calculateDate(duskData.getString("date")) + ". Score " + duskScore + "<br>" + duskData.getString("names").replaceAll(",", ", ") + "<br>");
              } else {
                strBuffer.append("Dusk: No record exists. Score 0<br>");
              }

              if (overallScore > 0 && overallData != null) {
                String cabalStr = "Children of Dusk";
                if (overallData.getInteger("cabal") == 2) {
                  cabalStr = "Children of Dawn";
                }

                strBuffer.append("Consecutive top scores: " + this.calculateDate(overallData.getString("date")) + ". Score " + overallScore + "<br>Affilated side: " + cabalStr + "<br>" + overallData.getString("names").replaceAll(",", ", ") + "<br>");
              } else {
                strBuffer.append("Consecutive top scores: No record exists. Score 0<br>");
              }

              strBuffer.append("<a action=\"bypass -h npc_" + this.getObjectId() + "_Chat 0\">Go back.</a></body></html>");
              NpcHtmlMessage html = new NpcHtmlMessage(player, this);
              html.setHtml(strBuffer.toString());
              player.sendPacket(html);
              break;
            case 8:
              if (playerParty == null) {
                return;
              }

              if (!playerParty.isLeader(player)) {
                this.showChatWindow(player, 8, "a", false);
              } else {
                r = this.getReflection();
                if (r instanceof DarknessFestival) {
                  if (((DarknessFestival)r).increaseChallenge()) {
                    this.showChatWindow(player, 8, "b", false);
                  } else {
                    this.showChatWindow(player, 8, "c", false);
                  }
                }
              }
              break;
            case 9:
              if (playerParty == null) {
                return;
              }

              r = this.getReflection();
              if (!(r instanceof DarknessFestival)) {
                return;
              }

              if (playerParty.isLeader(player)) {
                ((DarknessFestival)r).collapse();
              } else if (playerParty.getMemberCount() > Config.FESTIVAL_MIN_PARTY_SIZE) {
                player.leaveParty();
              } else {
                player.sendMessage(new CustomMessage("FestivalGuide.OnlyPartyLeader", player, new Object[0]));
              }
          }
        } else {
          super.onBypassFeedback(player, command);
        }

      }
    }
  }

  private void showChatWindow(Player player, int val, String suffix, boolean isDescription) {
    String filename = "seven_signs/festival/";
    filename = filename + (isDescription ? "desc_" : "festival_");
    filename = filename + (suffix != null ? val + suffix + ".htm" : val + ".htm");
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    html.setFile(filename);
    html.replace("%festivalType%", SevenSignsFestival.getFestivalName(this._festivalType));
    html.replace("%min%", String.valueOf(Config.FESTIVAL_MIN_PARTY_SIZE));
    if (val == 1) {
      html.replace("%price1%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6362) * Config.FESTIVAL_RATE_PRICE)));
      html.replace("%price2%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6361) * Config.FESTIVAL_RATE_PRICE)));
      html.replace("%price3%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6360) * Config.FESTIVAL_RATE_PRICE)));
    }

    if (val == 5) {
      html.replace("%statsTable%", this.getStatsTable());
    }

    if (val == 6) {
      html.replace("%bonusTable%", this.getBonusTable());
    }

    player.sendPacket(html);
    player.sendActionFailed();
  }

  public void showChatWindow(Player player, int val, Object... arg) {
    String filename = "seven_signs/";
    switch(this.getNpcId()) {
      case 31127:
      case 31128:
      case 31129:
      case 31130:
      case 31131:
        filename = filename + "festival/dawn_guide.htm";
        break;
      case 31132:
      case 31133:
      case 31134:
      case 31135:
      case 31136:
      case 31142:
      case 31143:
      case 31144:
      case 31145:
      case 31146:
        filename = filename + "festival/festival_witch.htm";
        break;
      case 31137:
      case 31138:
      case 31139:
      case 31140:
      case 31141:
        filename = filename + "festival/dusk_guide.htm";
        break;
      default:
        filename = this.getHtmlPath(this.getNpcId(), val, player);
    }

    player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
  }

  private String getStatsTable() {
    StringBuilder tableHtml = new StringBuilder();

    for(int i = 0; i < 5; ++i) {
      long dawnScore = SevenSignsFestival.getInstance().getHighestScore(2, i);
      long duskScore = SevenSignsFestival.getInstance().getHighestScore(1, i);
      String festivalName = SevenSignsFestival.getFestivalName(i);
      String winningCabal = "Children of Dusk";
      if (dawnScore > duskScore) {
        winningCabal = "Children of Dawn";
      } else if (dawnScore == duskScore) {
        winningCabal = "None";
      }

      tableHtml.append("<tr><td width=\"100\" align=\"center\">" + festivalName + "</td><td align=\"center\" width=\"35\">" + duskScore + "</td><td align=\"center\" width=\"35\">" + dawnScore + "</td><td align=\"center\" width=\"130\">" + winningCabal + "</td></tr>");
    }

    return tableHtml.toString();
  }

  private String getBonusTable() {
    StringBuilder tableHtml = new StringBuilder();

    for(int i = 0; i < 5; ++i) {
      long accumScore = SevenSignsFestival.getInstance().getAccumulatedBonus(i);
      String festivalName = SevenSignsFestival.getFestivalName(i);
      tableHtml.append("<tr><td align=\"center\" width=\"150\">" + festivalName + "</td><td align=\"center\" width=\"150\">" + accumScore + "</td></tr>");
    }

    return tableHtml.toString();
  }

  private String calculateDate(String milliFromEpoch) {
    long numMillis = Long.valueOf(milliFromEpoch);
    Calendar calCalc = Calendar.getInstance();
    calCalc.setTimeInMillis(numMillis);
    return calCalc.get(1) + "/" + calCalc.get(2) + "/" + calCalc.get(5);
  }
}
