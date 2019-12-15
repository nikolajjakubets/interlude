//admin - Brings up the admin menu.
//gmchat - Sends a global message that only GMs can see.
Ussage - //gmchat Type message here
//gm - Turns on or off your GM status.
//invul - Makes you invulnerable.
//delete - Used to remove a targetted Mob. Mob will not respawn.
//kill - Used to kill a targetted Mob. Mob will respawn.
//target - Used to target a Mob or player.
Usage: //target kadar
//buy - Opens the GMShop.
//gmshop - Opens the GMShop.
//announce_menu - Opens the announce menu.
//list_announcements - Lists the current announcements.
//reload_announcements - Reloads the announcement list.
//announce_announcements - Posts all announcements in list.
//add_announcement - Adds an announcement.
Usage: //add_announcement Your announcement here
//del_announcement - Deletes an announcement.
Usage: //del_announcement Your announcement here
//announce - Posts an announcement.
Usage: //announce Your announcement here
//itemcreate - Opens the item creation menu.
//create_item - Creates an item.
Usage: //create_item item_id
//server_shutdown - Starts the shutdown process.
Usage: //server_shutdown Number of seconds
//server_restart - Starts the restart process.
Usage: //server_restart Number of seconds
//server_abort - Stops the server shutdown.
//show_spawns - Opens spawn menu.
//spawn - Spawns a Mob on current target.
Usage: //spawn npc_id
//spawn_monster - Spawns a Mob on current target.
Usage: //spawn_monster npc_id
//spawn_index - Shows menu for monsters with respective level.
Usage: //spawn_index level
//show_skills - Lists targetted players skills.
//remove_skills - Removes the targetted players skills.
//skill_list - Opens skill menu.
//skill_index - Opens skill menu according to players stats.
//add_skill - Adds a skill to a targetted player.
Usage: //add_skill Skill_id level
//remove_skill - Removes a skill from a targetted player.
Usage: //remove_skill Skill_id
//get_skills - Temporarily gives you the skills of a targetted player.
//reset_skills - Restores your skills from before the get_skills command.
//add_exp_sp_to_character - Opens the xp sp menu of the targetted player.
//add_exp_sp - Adds xp sp to targetted player.
Usage: //add_exp_sp xp_number sp_number
//edit_character - Opens menu to edit targetted player.
//current_player - Opens character list.
//character_list - Opens list of online players.
//show_characters - Opens list of online players.
//find_character - Opens the find player menu.
//save_modifications - Saves the edits done to the targetted player.
//show_moves - Opens the teleport menu.
//show_moves_other - Opens the teleport menu named other.
//show_teleport - Opens the teleport menu.
//teleport_to_character - Teleports you to the named player.
Usage: //teleport_to_character kadar
//teleportto - Teleports you to the named player.
Usage: //teleportto kadar
//move_to - Teleports you to coordinates given.
Usage: //move_to 1111 2222 3333
//teleport_character - Teleports targetted player to coordinates given.
Usage: //move_to 1111 2222 3333
//recall - Summons a named player to you.
Usage: //recall kadar
//restore - Restores a broken player info.
//repair - Repairs a broken player info.
//changelvl - Changes a targetted players access level.
Usage: //changelvl Access_level_number
//ban - Used to kick and ban a named players account.
Usage: //ban kadar
//unban - Used to unban a named players account.
Usage: //unban kadar
//kick - Used to disconnect named player.
Usage: //kick kadar
//ride_wyvern - Command used to summon and ride a wyvern.
//unride_wyvern - Command to dismount and unsummon a wyvern.
//mons - Used to start the Monster Races.
//edit_npc - Used to edit an NPCs stats.
Usage: //edit_npc npc_id
//save_npc - Saves modified NPC info.
//recall_npc - Teleports targetted NPC to you.
//show_droplist - Shows the named NPCs drop list.
Usage: //show_droplist npc_id
//edit_drop - Edits the named NPCs droplist.
Usage: //edit_drop npc_id item_id [min max sweep chance]
//add_drop - Adds a drop to the named NPCs droplist.
Usage: //add_drop npc_id item_id [min max sweep chance]
//del_drop - Deletes a drop to the named NPCs droplist.
Usage: //del_drop npc_id item_id
//showShop - Shows a targetted vendors shoplist.
//showShopList - Shows a targetted vendors shoplist.
//addShopItem - Adds an item to a vendors shop.
Usage: //addShopItem Shop_ID item_id Price
//delShopItem - Deletes an item from a vendors shoplist.
Usage: //delShopItem Shop_ID item_id
//editShopItem - Edits an item in a vendors shoplist.
Usage: //editShopItem Shop_ID item_id Price
//gonorth
//gosouth
//goeast
//gowest
//goup
//godown
//setcastle
//clean_up_siege
//spawn_doors
//move_defenders
//startsiege
//endsiege
//list_siege_clans
//add_defender
//add_attacker
//clear_siege_list
//sgspawn <npc_id> <group>
//siege - Castle names: gludio, giran, dion, oren
//box_access - with box targetted, shows access list
//box_access char1 char2 - To add players to box
Usage: //box_access kadar LadyPain
//box_access no char1 - Removes player from box access
Usage: //box_access LadyPain no kadar
//fight_calculator
//fight_calculator_show
//fcs
//play_sounds
//nokarma - Removes karma from target player
//setkarma value - Sets karma of target player to value
//setew value - Sets enchantment of target player currently equipped weapon to value
Usage: //setew 9999
//banchat - Mutes a player
Usage: //banchat kadar
//unbanchat - Unmutes a player
Usage: //unbanchat kadar
//polymorph character npc id
//polymorph item id to poly target into id|
//polyself <npc id> - Used to polymorph yourself into a mob
//unpolyself - Returns you to the normal state
//polymorph item <itemid> - Polymorphs an item
//invis - To hide your character
//invisible - To hide your character
//vis - To unhide your character
//visible - To unhide your character
//earthquake <Intensity> <Duration> - Creates an earthquake
//para_all - Paralyzes all players}}
//unpara_all - Unparalyze all players
//para - Paralyze Target
//unpara - Unparalyze Target
//bighead - Gives target a big head
//shrinkhead - Returns targets head to normal size
//test - These //test commands are for the universe mapping feature (developmental)
//test uni flush
//test hash <number>
//test uni
//res - Resurrects either target, playername or radius
//mammon_find - Lists the current location (if any) of the Mammon NPCs
//mammon_respawn - Lists the current respawn times (if any) of the Mammon NPCs
//list_spawns <npcId> - Lists all the spawns for the specified NPC ID. If teleIndex is specified, player targets to the location of that spawn.
//list_spawns <npcId> <teleIndex> - Teleports to the player based on the given spawn index (from using //list_spawns <npcId>Wink.
//gmliston - (To add Gm from gmlist)
//gmlistoff - (To remove GM from gmlist)
//silence - (To enable/disable private message mode)
//diet - Current status of diet mode for player.
//diet on - Enables diet mode.
//diet off - Disables diet mode.
//tradeoff - Current status of trading for player.
//tradeoff on -Enables trading with other players.
//tradeoff off - Disables trading with other players.
/block <name>
/unblock <name>
/blocklist
/allblock
/allunblock
//spawn mob_name|mob_id [quantity] [respawn_time_secs] - //spawn kaboo_orc
//heal - Heals yourself
//heal (with a character selected) - Heals selected character
//heal <radius> - Heals all characters within <radius>
//heal <name> - Heals character with the name <name>. Does not have to be targeted.
//give_all_skills - It will give your target all the skills he can learn at his level with his class
/friendlist
//openall - Opens all doors
//closeall - Closes all doors
//unspawnall - Remove all npcs from world
//respawnall - Reload npc data
//reload skill - Reloads skill data.
//reload multisell - Reloads multisell data.
//atmosphere <signsky> <dawn|dusk> - Sets current sky to either Dawn or Dusk, as seen by members of the winning cabal.
//atmosphere <sky> <day|night> - Sets current sky to either day or night.
//cache_htm_rebuild - Rebuilds and reloads the HTML cache.
//cache_htm_reload - Reloads the HTML cache.
//cache_crest_rebuild - Rebuilds the cache of pledge crests.
//cache_crest_fix - Fixes problems with incorrect displaying of pledge crests.
