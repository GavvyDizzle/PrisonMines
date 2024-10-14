---
description: A custom MythicMobs boss support system
---

# Bosses

## Status - Private

## Requirements

* Dependencies: MythicMobs, PlaceholderAPI, ServerUtils

## Arenas

* Arenas operate on a one-mob-per-arena system. Each arena allows one player to fight a certain mob at a time
* Supports a queue system which makes players wait if a fight is active
* The strength of a mob can be changed by spawning mobs of different levels
  * This is calculated with an equation that parses PAPI placeholders
* The player receives rewards only if they kill the mob without dying

## Boss Event Arena

This is a place where server-wide boss events can take place

* When starting an event, you have the option to select which MythicMob to spawn
* Similar to arenas, the strength of a mob is linked to its level
  * The equation parses PAPI placeholders and becomes the sum of all participants' equations
* All remaining players will receive rewards when the boss is killed
  * The player who dealt the killing blow has the option to receive additional rewards

## Commands

### Player Commands

* The base command is `bosses` with the permission `bosses.player`
* All commands require permission to use which follows the format `bosses.player.command` where command is the name of the command
* `bosses help` Opens this command's help menu
* `bosses joinBossEvent` Join the boss event
* `bosses spectate <arenaID>` Spectate an arena
* `bosses spectateBossEvent` Spectate the boss arena

### Admin Commands

* The base command is `bossesAdmin` with the permission `bosses.admin`
* All commands require permission to use which follows the format `bosses.admin.command` where command is the name of the command
* `bossesAdmin cencelBossEvent` Ends the ongoing or queued boss event
* `bossesAdmin help` Opens this command's help menu
* `bossesAdmin join <arenaID> <player>` Force a player to join an arena
* `bossesAdmin reload [arg]` Reloads this plugin or a specified portion
* `bossesAdmin setBossArenaLocation <location> <exact|snap> [x] [y] [z] [pitch] [yaw]` Set the boss arena's location
* `bossesAdmin setLocation <arenaID> <location> <exact|snap> [x] [y] [z] [pitch] [yaw]` Set an arena's location
* `bossesAdmin startBossEvent <MythicMobID>` Start a boss event
* `bossesAdmin teleport <arenaID> <location>` Teleport to an arena's location
* `bossesAdmin teleportBossArena <location>` Teleport to the boss arena's location

## Notes

* The plugin does **not** stop players from teleporting away. You should ensure players cannot leave the arena during a fight. Consider using `WorldGuard`
* The plugin does **not** save the old location of spectating players. They will need to teleport out on their own
* Players will placed back at their old location when the fight ends after a short (configurable) delay. Players who die during the fight will be ignored (the server will handle where they spawn)
