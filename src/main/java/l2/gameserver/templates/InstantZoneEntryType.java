//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.ItemFunctions;

public enum InstantZoneEntryType {
  SOLO {
    public boolean canEnter(Player player, InstantZone instancedZone) {
      if (player.isInParty()) {
        player.sendMessage(new CustomMessage("A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA", player, new Object[0]));
        return false;
      } else {
        CustomMessage cmsg = InstantZoneEntryType.checkPlayer(player, instancedZone);
        if (cmsg != null) {
          player.sendMessage(cmsg);
          return false;
        } else {
          return true;
        }
      }
    }

    public boolean canReEnter(Player player, InstantZone instancedZone) {
      if (!player.isCursedWeaponEquipped() && !player.isInFlyingTransform()) {
        return true;
      } else {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
        return false;
      }
    }
  },
  PARTY {
    public boolean canEnter(Player player, InstantZone instancedZone) {
      Party party = player.getParty();
      if (party == null) {
        player.sendMessage(new CustomMessage("YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER", player, new Object[0]));
        return false;
      } else if (!party.isLeader(player)) {
        player.sendMessage(new CustomMessage("ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER", player, new Object[0]));
        return false;
      } else if (party.getMemberCount() < instancedZone.getMinParty()) {
        player.sendMessage((new CustomMessage("YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE", player, new Object[0])).addNumber((long)instancedZone.getMinParty()));
        return false;
      } else if (party.getMemberCount() > instancedZone.getMaxParty()) {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
        return false;
      } else {
        Iterator var4 = party.getPartyMembers().iterator();

        CustomMessage cmsg;
        do {
          if (!var4.hasNext()) {
            return true;
          }

          Player member = (Player)var4.next();
          if (!player.isInRange(member, 500L)) {
            Iterator var8 = member.iterator();

            while(var8.hasNext()) {
              Player partyPlayer = (Player)var8.next();
              partyPlayer.sendMessage(new CustomMessage("C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED", partyPlayer, new Object[]{member}));
            }

            return false;
          }

          cmsg = InstantZoneEntryType.checkPlayer(member, instancedZone);
        } while(cmsg == null);

        player.sendMessage(cmsg);
        return false;
      }
    }

    public boolean canReEnter(Player player, InstantZone instanceZone) {
      Party party = player.getParty();
      if (party == null) {
        player.sendMessage(new CustomMessage("YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER", player, new Object[0]));
        return false;
      } else if (party.getMemberCount() > instanceZone.getMaxParty()) {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
        return false;
      } else if (!player.isCursedWeaponEquipped() && !player.isInFlyingTransform()) {
        return true;
      } else {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
        return false;
      }
    }
  },
  COMMAND_CHANNEL {
    public boolean canEnter(Player player, InstantZone instancedZone) {
      Party party = player.getParty();
      if (party != null && party.getCommandChannel() != null) {
        CommandChannel cc = party.getCommandChannel();
        if (cc.getMemberCount() < instancedZone.getMinParty()) {
          player.sendMessage((new CustomMessage("YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE", player, new Object[0])).addNumber((long)instancedZone.getMinParty()));
          return false;
        } else if (cc.getMemberCount() > instancedZone.getMaxParty()) {
          player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
          return false;
        } else {
          Iterator var5 = cc.iterator();

          CustomMessage cmsg;
          do {
            if (!var5.hasNext()) {
              return true;
            }

            Player member = (Player)var5.next();
            if (!player.isInRange(member, 500L)) {
              Iterator var9 = cc.iterator();

              while(var9.hasNext()) {
                Player partyPlayer = (Player)var9.next();
                partyPlayer.sendMessage(new CustomMessage("C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED", partyPlayer, new Object[]{member}));
              }

              return false;
            }

            cmsg = InstantZoneEntryType.checkPlayer(member, instancedZone);
          } while(cmsg == null);

          player.sendMessage(cmsg);
          return false;
        }
      } else {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL", player, new Object[0]));
        return false;
      }
    }

    public boolean canReEnter(Player player, InstantZone instanceZone) {
      Party commparty = player.getParty();
      if (commparty != null && commparty.getCommandChannel() != null) {
        CommandChannel cc = commparty.getCommandChannel();
        if (cc.getMemberCount() > instanceZone.getMaxParty()) {
          player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
          return false;
        } else if (!player.isCursedWeaponEquipped() && !player.isInFlyingTransform()) {
          return true;
        } else {
          player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
          return false;
        }
      } else {
        player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL", player, new Object[0]));
        return false;
      }
    }
  };

  private InstantZoneEntryType() {
  }

  public abstract boolean canEnter(Player var1, InstantZone var2);

  public abstract boolean canReEnter(Player var1, InstantZone var2);

  private static CustomMessage checkPlayer(Player player, InstantZone instancedZone) {
    if (player.getActiveReflection() != null) {
      return new CustomMessage("YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON", player, new Object[0]);
    } else if (player.getLevel() >= instancedZone.getMinLevel() && player.getLevel() <= instancedZone.getMaxLevel()) {
      if (!player.isCursedWeaponEquipped() && !player.isInFlyingTransform()) {
        if (InstantZoneHolder.getInstance().getMinutesToNextEntrance(instancedZone.getId(), player) > 0) {
          return (new CustomMessage("C1_MAY_NOT_REENTER_YET", player, new Object[0])).addCharName(player);
        } else if (instancedZone.getRemovedItemId() > 0 && instancedZone.getRemovedItemNecessity() && ItemFunctions.getItemCount(player, instancedZone.getRemovedItemId()) < 1L) {
          return (new CustomMessage("C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED", player, new Object[0])).addCharName(player);
        } else {
          if (instancedZone.getRequiredQuestId() > 0) {
            Quest q = QuestManager.getQuest(instancedZone.getRequiredQuestId());
            QuestState qs = player.getQuestState(q.getClass());
            if (qs == null || qs.getState() != 2) {
              return (new CustomMessage("C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED", player, new Object[0])).addCharName(player);
            }
          }

          return null;
        }
      } else {
        return new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]);
      }
    } else {
      return (new CustomMessage("C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY", player, new Object[0])).addCharName(player);
    }
  }
}
