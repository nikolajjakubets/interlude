//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.StringTokenizer;

@Slf4j
public class SignsPriestInstance extends NpcInstance {

  public SignsPriestInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  private void showChatWindow(Player player, int val, String suffix, boolean isDescription) {
    String filename = "seven_signs/";
    filename = filename + (isDescription ? "desc_" + val : "signs_" + val);
    filename = filename + (suffix != null ? "_" + suffix + ".htm" : ".htm");
    this.showChatWindow(player, filename);
  }

  private boolean getPlayerAllyHasCastle(Player player) {
    Clan playerClan = player.getClan();
    if (playerClan == null) {
      return false;
    } else {
      if (!Config.ALT_GAME_REQUIRE_CLAN_CASTLE) {
        int allyId = playerClan.getAllyId();
        if (allyId != 0) {
          Clan[] clanList = ClanTable.getInstance().getClans();

          for (Clan clan : clanList) {
            if (clan.getAllyId() == allyId && clan.getCastle() > 0) {
              return true;
            }
          }
        }
      }

      return playerClan.getCastle() > 0;
    }
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      if (this.getNpcId() != 31113 && this.getNpcId() != 31126 || SevenSigns.getInstance().getPlayerCabal(player) != 0 || player.isGM() || !Config.ALT_MAMONS_CHECK_SEVEN_SING_STATUS) {
        super.onBypassFeedback(player, command);
        if (command.startsWith("SevenSignsDesc")) {
          int val = Integer.parseInt(command.substring(15));
          this.showChatWindow(player, val, null, true);
        } else if (command.startsWith("SevenSigns")) {
          int cabal = 0;
//          int stoneType = false;
          ItemInstance ancientAdena = player.getInventory().getItemByItemId(5575);
          long ancientAdenaAmount = ancientAdena == null ? 0L : ancientAdena.getCount();
          int val = Integer.parseInt(command.substring(11, 12).trim());
          if (command.length() > 12) {
            val = Integer.parseInt(command.substring(11, 13).trim());
          }

          if (command.length() > 13) {
            try {
              cabal = Integer.parseInt(command.substring(14, 15).trim());
            } catch (Exception var54) {
              try {
                cabal = Integer.parseInt(command.substring(13, 14).trim());
              } catch (Exception e) {
                log.error("onBypassFeedback: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
              }
            }
          }

          long redContribCount;
          long ancientAdenaReward;
          int stoneType;
          switch (val) {
            case 2:
              if (!player.getInventory().validateCapacity(1L)) {
                player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                return;
              }

              if (500L > player.getAdena()) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
              }

              player.reduceAdena(500L, true);
              player.getInventory().addItem(ItemFunctions.createItem(5707));
              player.sendPacket(SystemMessage2.obtainItems(5707, 1L, 0));
              break;
            case 3:
            case 8:
              cabal = SevenSigns.getInstance().getPriestCabal(this.getNpcId());
              this.showChatWindow(player, val, SevenSigns.getCabalShortName(cabal), false);
              break;
            case 4:
              int newSeal = Integer.parseInt(command.substring(15));
              int oldCabal = SevenSigns.getInstance().getPlayerCabal(player);
              if (oldCabal != 0) {
                player.sendMessage((new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.AlreadyMember", player)).addString(SevenSigns.getCabalName(cabal)));
                return;
              }

              if (player.getClassId().level() == 0) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.YouAreNewbie", player));
              } else {
                if (Config.ALT_GAME_REQUIRE_CASTLE_DAWN) {
                  if (this.getPlayerAllyHasCastle(player)) {
                    if (cabal == 1) {
                      player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwning", player));
                      return;
                    }
                  } else if (cabal == 2) {
                    boolean allowJoinDawn = false;
                    if (Functions.getItemCount(player, 6388) > 0L) {
                      Functions.removeItem(player, 6388, 1L);
                      allowJoinDawn = true;
                    } else if (Config.ALT_GAME_ALLOW_ADENA_DAWN && player.getAdena() >= 50000L) {
                      player.reduceAdena(50000L, true);
                      allowJoinDawn = true;
                    }

                    if (!allowJoinDawn) {
                      if (Config.ALT_GAME_ALLOW_ADENA_DAWN) {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate", player));
                      } else {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate2", player));
                      }

                      return;
                    }
                  }
                }

                SevenSigns.getInstance().setPlayerInfo(player.getObjectId(), cabal, newSeal);
                if (cabal == 2) {
                  player.sendPacket(Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN);
                } else {
                  player.sendPacket(Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK);
                }

                switch (newSeal) {
                  case 1:
                    player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD);
                    break;
                  case 2:
                    player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD);
                    break;
                  case 3:
                    player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD);
                }

                this.showChatWindow(player, 4, SevenSigns.getCabalShortName(cabal), false);
              }
              break;
            case 5:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            default:
              this.showChatWindow(player, val, null, false);
              break;
            case 6:
              stoneType = Integer.parseInt(command.substring(13));
              ItemInstance redStones = player.getInventory().getItemByItemId(6362);
              long redStoneCount = redStones == null ? 0L : redStones.getCount();
              ItemInstance greenStones = player.getInventory().getItemByItemId(6361);
              long greenStoneCount = greenStones == null ? 0L : greenStones.getCount();
              ItemInstance blueStones = player.getInventory().getItemByItemId(6360);
              long blueStoneCount = blueStones == null ? 0L : blueStones.getCount();
              long contribScore = SevenSigns.getInstance().getPlayerContribScore(player);
              boolean stonesFound = false;
              if (contribScore == SevenSigns.MAXIMUM_PLAYER_CONTRIB) {
                player.sendPacket(Msg.CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT_YOU_MAY_NOT_CONTINUE);
              } else {
                redContribCount = 0L;
                long greenContribCount = 0L;
                long blueContribCount = 0L;
                switch (stoneType) {
                  case 1:
                    blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / 3L;
                    if (blueContribCount > blueStoneCount) {
                      blueContribCount = blueStoneCount;
                    }
                    break;
                  case 2:
                    greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / 5L;
                    if (greenContribCount > greenStoneCount) {
                      greenContribCount = greenStoneCount;
                    }
                    break;
                  case 3:
                    redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / 10L;
                    if (redContribCount > redStoneCount) {
                      redContribCount = redStoneCount;
                    }
                    break;
                  case 4:
                    redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / 10L;
                    if (redContribCount > redStoneCount) {
                      redContribCount = redStoneCount;
                    }

                    long tempContribScore = contribScore + redContribCount * 10L;
                    greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / 5L;
                    if (greenContribCount > greenStoneCount) {
                      greenContribCount = greenStoneCount;
                    }

                    tempContribScore += greenContribCount * 5L;
                    blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / 3L;
                    if (blueContribCount > blueStoneCount) {
                      blueContribCount = blueStoneCount;
                    }
                }

                if (redContribCount > 0L && player.getInventory().destroyItemByItemId(6362, redContribCount)) {
                  stonesFound = true;
                }

                if (greenContribCount > 0L && player.getInventory().destroyItemByItemId(6361, greenContribCount)) {
                  stonesFound = true;
                }

                if (blueContribCount > 0L) {
                  ItemInstance temp = player.getInventory().getItemByItemId(6360);
                  if (player.getInventory().destroyItemByItemId(6360, blueContribCount)) {
                    stonesFound = true;
                  }
                }

                if (!stonesFound) {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player));
                  return;
                }

                contribScore = SevenSigns.getInstance().addPlayerStoneContrib(player, blueContribCount, greenContribCount, redContribCount);
                SystemMessage sm = new SystemMessage(1267);
                sm.addNumber(contribScore);
                player.sendPacket(sm);
                this.showChatWindow(player, 6, null, false);
              }
              break;
            case 7:

              try {
                redContribCount = Long.parseLong(command.substring(13).trim());
              } catch (NumberFormatException var51) {
                player.sendMessage(new CustomMessage("common.IntegerAmount", player));
                return;
              } catch (StringIndexOutOfBoundsException e) {
                player.sendMessage(new CustomMessage("common.IntegerAmount", player));
                return;
              }

              if (ancientAdenaAmount >= redContribCount && redContribCount >= 1L) {
                if (player.getInventory().destroyItemByItemId(5575, redContribCount)) {
                  player.addAdena(redContribCount);
                  player.sendPacket(SystemMessage2.removeItems(5575, redContribCount));
                  player.sendPacket(SystemMessage2.obtainItems(57, redContribCount, 0));
                }
                break;
              }

              player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
              return;
            case 9:
              int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
              int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
              if (SevenSigns.getInstance().isSealValidationPeriod() && playerCabal == winningCabal) {
                ancientAdenaReward = SevenSigns.getInstance().getAncientAdenaReward(player, true);
                if (ancientAdenaReward < 3) {
                  this.showChatWindow(player, 9, "b", false);
                  return;
                }

                ancientAdena = ItemFunctions.createItem(5575);
                ancientAdena.setCount(ancientAdenaReward);
                player.getInventory().addItem(ancientAdena);
                player.sendPacket(SystemMessage2.obtainItems(5575, ancientAdenaReward, 0));
                this.showChatWindow(player, 9, "a", false);
              }
              break;
            case 10:
              cabal = SevenSigns.getInstance().getPriestCabal(this.getNpcId());
              if (SevenSigns.getInstance().isSealValidationPeriod()) {
                this.showChatWindow(player, val, "", false);
              } else {
                this.showChatWindow(player, val, this.getParameters().getString("town", "no"), false);
              }
              break;
            case 11:
              try {
                String portInfo = command.substring(14).trim();
                StringTokenizer st = new StringTokenizer(portInfo);
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                int z = Integer.parseInt(st.nextToken());
                long ancientAdenaCost = Long.parseLong(st.nextToken());
                if (ancientAdenaCost > 0L && !player.getInventory().destroyItemByItemId(5575, ancientAdenaCost)) {
                  player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                  return;
                }

                player.teleToLocation(x, y, z);
              } catch (Exception var50) {
                log.warn("SevenSigns: Error occurred while teleporting player: " + var50);
              }
              break;
            case 17:
              stoneType = Integer.parseInt(command.substring(14));
              int stoneId = 0;
              long stoneCount = 0L;
              int stoneValue = 0;
              String stoneColor = null;
              ItemInstance stoneInstance;
              if (stoneType == 4) {
                stoneInstance = player.getInventory().getItemByItemId(6360);
                long bcount = stoneInstance != null ? stoneInstance.getCount() : 0L;
                ItemInstance GreenStoneInstance = player.getInventory().getItemByItemId(6361);
                long gcount = GreenStoneInstance != null ? GreenStoneInstance.getCount() : 0L;
                ItemInstance RedStoneInstance = player.getInventory().getItemByItemId(6362);
                ancientAdenaReward = RedStoneInstance != null ? RedStoneInstance.getCount() : 0L;
                long adenaReward = SevenSigns.calcAncientAdenaReward(bcount, gcount, ancientAdenaReward);
                if (adenaReward > 0L) {
                  if (stoneInstance != null) {
                    player.getInventory().destroyItem(stoneInstance, bcount);
                    player.sendPacket(SystemMessage2.removeItems(6360, bcount));
                  }

                  if (GreenStoneInstance != null) {
                    player.getInventory().destroyItem(GreenStoneInstance, gcount);
                    player.sendPacket(SystemMessage2.removeItems(6361, gcount));
                  }

                  if (RedStoneInstance != null) {
                    player.getInventory().destroyItem(RedStoneInstance, adenaReward);
                    player.sendPacket(SystemMessage2.removeItems(6362, adenaReward));
                  }

                  ancientAdena = ItemFunctions.createItem(5575);
                  ancientAdena.setCount(adenaReward);
                  player.getInventory().addItem(ancientAdena);
                  player.sendPacket(SystemMessage2.obtainItems(5575, adenaReward, 0));
                } else {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySS", player));
                }
              } else {
                switch (stoneType) {
                  case 1:
                    stoneColor = "blue";
                    stoneId = 6360;
                    stoneValue = 3;
                    break;
                  case 2:
                    stoneColor = "green";
                    stoneId = 6361;
                    stoneValue = 5;
                    break;
                  case 3:
                    stoneColor = "red";
                    stoneId = 6362;
                    stoneValue = 10;
                }

                stoneInstance = player.getInventory().getItemByItemId(stoneId);
                if (stoneInstance != null) {
                  stoneCount = stoneInstance.getCount();
                }

                String path = "seven_signs/signs_17.htm";
                String content = HtmCache.getInstance().getNotNull(path, player);
                if (content != null) {
                  content = content.replaceAll("%stoneColor%", stoneColor);
                  content = content.replaceAll("%stoneValue%", String.valueOf(stoneValue));
                  content = content.replaceAll("%stoneCount%", String.valueOf(stoneCount));
                  content = content.replaceAll("%stoneItemId%", String.valueOf(stoneId));
                  NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                  html.setHtml(content);
                  player.sendPacket(html);
                } else {
                  log.warn("Problem with HTML text seven_signs/signs_17.htm: " + path);
                }
              }
              break;
            case 18:
              int convertStoneId = Integer.parseInt(command.substring(14, 18));
              long convertCount = 0L;

              try {
                convertCount = Long.parseLong(command.substring(19).trim());
              } catch (Exception var55) {
                player.sendMessage(new CustomMessage("common.IntegerAmount", player));
                break;
              }

              ItemInstance convertItem = player.getInventory().getItemByItemId(convertStoneId);
              if (convertItem == null) {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player));
              } else {
                long totalCount = convertItem.getCount();
                ancientAdenaReward = 0L;
                if (convertCount <= totalCount && convertCount > 0L) {
                  switch (convertStoneId) {
                    case 6360:
                      ancientAdenaReward = SevenSigns.calcAncientAdenaReward(convertCount, 0L, 0L);
                      break;
                    case 6361:
                      ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0L, convertCount, 0L);
                      break;
                    case 6362:
                      ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0L, 0L, convertCount);
                  }

                  if (player.getInventory().destroyItemByItemId(convertStoneId, convertCount)) {
                    ancientAdena = ItemFunctions.createItem(5575);
                    ancientAdena.setCount(ancientAdenaReward);
                    player.getInventory().addItem(ancientAdena);
                    player.sendPacket(SystemMessage2.removeItems(convertStoneId, convertCount), SystemMessage2.obtainItems(5575, ancientAdenaReward, 0));
                  }
                } else {
                  player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveSSAmount", player));
                }
              }
              break;
            case 19:
              int chosenSeal = Integer.parseInt(command.substring(16));
              String fileSuffix = SevenSigns.getSealName(chosenSeal, true) + "_" + SevenSigns.getCabalShortName(cabal);
              this.showChatWindow(player, val, fileSuffix, false);
              break;
            case 20:
              StringBuilder contentBuffer = new StringBuilder("<html><body><font color=\"LEVEL\">[Seal Status]</font><br>");

              for (int i = 1; i < 4; ++i) {
                int sealOwner = SevenSigns.getInstance().getSealOwner(i);
                if (sealOwner != 0) {
                  contentBuffer.append("[").append(SevenSigns.getSealName(i, false)).append(": ").append(SevenSigns.getCabalName(sealOwner)).append("]<br>");
                } else {
                  contentBuffer.append("[").append(SevenSigns.getSealName(i, false)).append(": Nothingness]<br>");
                }
              }

              contentBuffer.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_SevenSigns 3 ").append(cabal).append("\">Go back.</a></body></html>");
              NpcHtmlMessage html2 = new NpcHtmlMessage(player, this);
              html2.setHtml(contentBuffer.toString());
              player.sendPacket(html2);
              break;
            case 21:
              if (player.getLevel() < 60) {
                this.showChatWindow(player, 20, null, false);
                return;
              }

              if (player.getVarInt("bmarketadena", 0) >= 500000) {
                this.showChatWindow(player, 21, null, false);
                return;
              }

              Calendar sh = Calendar.getInstance();
              sh.set(11, 20);
              sh.set(12, 0);
              sh.set(13, 0);
              Calendar eh = Calendar.getInstance();
              eh.set(11, 23);
              eh.set(12, 59);
              eh.set(13, 59);
              if (System.currentTimeMillis() > sh.getTimeInMillis() && System.currentTimeMillis() < eh.getTimeInMillis()) {
                this.showChatWindow(player, 23, null, false);
              } else {
                this.showChatWindow(player, 22, null, false);
              }
          }
        }

      }
    }
  }

  public void showChatWindow(Player player, int val, Object... arg) {
    String filename;
    int npcId = this.getTemplate().npcId;
    filename = "seven_signs/";
    int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(1);
    int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(2);
    int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
    boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
    int compWinner = SevenSigns.getInstance().getCabalHighestScore();
    label86:
    switch (npcId) {
      case 31078:
      case 31079:
      case 31080:
      case 31081:
      case 31082:
      case 31083:
      case 31084:
      case 31168:
      case 31692:
      case 31694:
      case 31997:
        switch (playerCabal) {
          case 1:
            if (isSealValidationPeriod) {
              filename = filename + "dawn_priest_3b.htm";
            } else {
              filename = filename + "dawn_priest_3a.htm";
            }
            break label86;
          case 2:
            if (isSealValidationPeriod) {
              if (compWinner == 2) {
                if (compWinner != sealGnosisOwner) {
                  filename = filename + "dawn_priest_2c.htm";
                } else {
                  filename = filename + "dawn_priest_2a.htm";
                }
              } else {
                filename = filename + "dawn_priest_2b.htm";
              }
            } else {
              filename = filename + "dawn_priest_1b.htm";
            }
            break label86;
          default:
            if (isSealValidationPeriod) {
              if (compWinner == 2) {
                filename = filename + "dawn_priest_4.htm";
              } else {
                filename = filename + "dawn_priest_2b.htm";
              }
            } else {
              filename = filename + "dawn_priest_1a.htm";
            }
            break label86;
        }
      case 31085:
      case 31086:
      case 31087:
      case 31088:
      case 31089:
      case 31090:
      case 31091:
      case 31169:
      case 31693:
      case 31695:
      case 31998:
        switch (playerCabal) {
          case 1:
            if (isSealValidationPeriod) {
              if (compWinner == 1) {
                if (compWinner != sealGnosisOwner) {
                  filename = filename + "dusk_priest_2c.htm";
                } else {
                  filename = filename + "dusk_priest_2a.htm";
                }
              } else {
                filename = filename + "dusk_priest_2b.htm";
              }
            } else {
              filename = filename + "dusk_priest_1b.htm";
            }
            break label86;
          case 2:
            if (isSealValidationPeriod) {
              filename = filename + "dusk_priest_3b.htm";
            } else {
              filename = filename + "dusk_priest_3a.htm";
            }
            break label86;
          default:
            if (isSealValidationPeriod) {
              if (compWinner == 1) {
                filename = filename + "dusk_priest_4.htm";
              } else {
                filename = filename + "dusk_priest_2b.htm";
              }
            } else {
              filename = filename + "dusk_priest_1a.htm";
            }
            break label86;
        }
      case 31092:
        filename = filename + "blkmrkt_1.htm";
        break;
      case 31113:
        if (!player.isGM()) {
          switch (compWinner) {
            case 1:
              if (playerCabal != compWinner || playerCabal != sealAvariceOwner) {
                filename = filename + "mammmerch_2.htm";
                return;
              }
              break;
            case 2:
              if (playerCabal != compWinner || playerCabal != sealAvariceOwner) {
                filename = filename + "mammmerch_2.htm";
                return;
              }
          }
        }

        filename = filename + "mammmerch_1.htm";
        break;
      case 31126:
        if (!player.isGM()) {
          switch (compWinner) {
            case 1:
              if (playerCabal != compWinner || playerCabal != sealGnosisOwner) {
                filename = filename + "mammblack_2.htm";
                return;
              }
              break;
            case 2:
              if (playerCabal != compWinner || playerCabal != sealGnosisOwner) {
                filename = filename + "mammblack_2.htm";
                return;
              }
          }
        }

        filename = filename + "mammblack_1.htm";
        break;
      default:
        filename = this.getHtmlPath(npcId, val, player);
    }

    player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
  }
}
