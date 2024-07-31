---
description: A passive money making system for mining
---

# Crypto

### Requirements

* Dependencies: ServerUtils, Vault, WorldGuard
* Soft Dependencies: RewardsInventory
* Data Storage: MySQL or MariaDB

### GPUs

GPUs are custom items created for the player to collect. They allow passive collection of coins

* Passive collection simply runs commands, so you have more flexibility with it
* Each GPU is created with its own `.yml` file in the plugin's `/gpus` directory
* Reference the example GPUs when creating new ones

#### GPU Rewards

* For each GPU, you can create as many "rewards" as you would like
* Each reward has a `chance` of being run for every block mined (in a WorldGuard region)
* If the player does not have `permission` for this reward, they are unable to find/roll it
  * Leave the permission empty to ignore a permission check
* A random `amount` of the `coin` will be given to the player
  * Amount format: `x-y`
  * You can use the `{amount}` placeholder in commands to fill in the amount found (rounded to 6 decimal places)
* `commands` are run before `messages` when the reward is given

### Coins

These are the items players are meant to passively accumulate. They can be modified in multiple menus

* The coin saving system is analogous to static items from RewardsInventory (with a cap of 25 items)
* You can define up to 25 types of coins in `coins.yml`
  * Each coin requires a valid `coinbaseID`. This value will be used to retrieve the `price` from the CoinBase API
  * All other values are configurable by you. Check out the example file to see what is possible

### Menu System

* Opening the menu with `/crypto` brings you to the main menu that contains 5 submenus
* Each submenu has a back button that will bring the player back to the main menu

#### GPU Storage Menu

* In this menu, players can store up to 9 GPUs
* The stored GPUs will retain their place in the menu
* If slots are taken away from the player, GPUs in those slots will be deleted if their menu saves
* GPUs in this menu cannot be removed (after closing the menu) until their "lock time" has expired.
  * Each GPU has a configurable amount of time for the lock to last for
  * The system is smart and will allow items to be freely moved around that have not been locked (or whose lock time is up and the GPU is kept in the menu)

#### Coin Selling Menu

* Players will have different items that display amount/price/value of their coins
* Clicking on an item will open up a new menu where the player inputs the amount they want to sell

#### Coin Exchange Menu

* Players will have different items that display amount/price/value of their coins
  * The first item is the one they are "selling" and the second is the one they are "buying"
* The menu that opens after they click the second item is very descriptive and allows for input

#### Coin Sending Menu

* Players will have different items that display amount/price/value of their coins
* Clicking on an item will close the menu and prompt the player with a command
  * A command is used here for easier input of player names

#### GPU Selling Menu

* This is a simple menu that allows the player to select and then sell their GPUs
* You can configure the placement of these slots and the appearance of the confirmation item

### Commands:

#### Player Commands

* The base command is `crypto` with the permission `crypto.player`
* All commands require permission to use which follows the format `crypto.player.command` where command is the name of the command
  * `crypto` Base command that opens the main menu
  * `crypto exchange` Opens the coin exchange menu
  * `crypto help` Opens the command help menu
  * `crypto rack` Opens the GPU rack submenu
  * `crypto sell` Opens the sell coins submenu
  * `crypto sellGPU` Opens the sell GPU submenu
  * `crypto send <crypto> <amount> <player>` Sends crypto to a player (taxed)

#### Admin Commands

* The base command is `cryptoadmin` with the permission `crypto.admin`
* All commands require permission to use which follows the format `crypto.admin.command` where command is the name of the command
* `cryptoadmin addItem <player> <gpuID> <menuID>` Adds a GPU to the player's /rew pages inventory
* `cryptoadmin confirm` Confirm an action
* `cryptoadmin editAmount <player> <action> <itemID> <amount>` Edit the amount of a player's reward items (supports amount in the form of `min-max`)
* `cryptoadmin giveGPU <player> <gpuID>` Gives the player a GPU item
* `cryptoadmin help` Opens the command help menu
* `cryptoadmin list` Opens the GPU list menu
* `cryptoadmin openPlayerMenu <player>` Opens a player's menu system (only GPU storage can be edited)
* `cryptoadmin reload [arg]` Reloads this plugin or a specified portion
* `cryptoadmin reset <player>` Resets all coins and stored GPUs for the player
* `cryptoadmin resetCoin <itemID>` Resets this coin to 0 for all players
* `cryptoadmin resetCoins <player>` Resets all coins for the player
* `cryptoadmin resetGPUCooldowns <player>` Removes the cooldown on all of a player's GPUs (does not work if the menu is open)
* `cryptoadmin simulateBlockMine <player> [amount]` Simulates a block mine for the player (up to 10,000)
* `cryptoadmin updateSavedItems` Updates all ItemStacks saved to the database

### Custom Placeholders

* There are many custom placeholders throughout the plugin. They are surrounded by curly braces `{}`
* Instead of listing them all out here, it is your job to check the example configs or code to see they do
  * Most placeholders are already in use in the example configs. There may be more in the code but they are probably unnecessary

### Admin Menu Viewing

* Admins can view menus of online and offline players with `/cryptoadmin openPlayerMenu <player>`
* Currently, admins are only able to edit the "GPU Storage" menu (all other menus are view-only)
* As a warning, admins are given access to all slots of the GPU storage menu. If they place items in slots the player does not have permission to access, the items will delete the next time the player causes their menu to save. YOU HAVE BEEN WARNED

### Notes

* You are allowed to create up to 25 different coins
  * Changing this cap requires editing the `PlayerData` class, `dbsetup.sql`, and adding/removing columns from the `coins` database table
* You are allowed to create up to 9 GPU slots/permissions
  * You can change this value in the `GPUHolder` class
* There is a chance that transactions may be finicky with exact decimal values. I have tried my best to prevent this (doubles are fun)
