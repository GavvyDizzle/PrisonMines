---
description: I love commands, you love commands, we all love commands!
---

# Commands

## Player Commands

Player commands are quite limited with this plugin since it is item-centric

* `/enchants` Opens the enchant list menu
  * permission: `prisonenchants.enchantlist`
* `/tinkerer` Opens the tinkerer menu (shard to dust and book to magic converter)
  * permission: `prisonenchants.tinkerer`

## Admin Commands

### Main Admin Commands: `/peadmin`

All `peadmin` commands require the base permission `prisonenchants.admin` along with the per-command permission

### Armor Commands:

* `/peadmin customArmor menu` Opens the armor menu
* `/peadmin customArmor give <player> <armorID> <rarity> <type> <piece> [color]` Give a player a piece of armor. Color is optional for leather armor coloring
* `/peadmin customArmor putInMenu <player> <menuID> <armorID> <rarity> <type> <piece> [color]` Put armor in a player's RewardsInventory menu. Color is optional for leather armor coloring
* `/peadmin customArmor setHeldArmor <armorID> <rarity>` Set the item in your hand to be custom armor (useful for alternative helmet items)
* Permissions are of the format `customarmor.{name}` (all lowercase)

#### List Commands:

* `/peadmin enchants` Open the enchants search menu
* `/peadmin list [type]` View custom item lists
* Permissions are of the format `subcommand` (all lowercase)

### Item Give Commands:

* The `<chance>` argument supports the format `x-y` where it picks a random number in the range `[x,y]`
* `/peadmin give book <player> <enchant> <level> <chance> [amount]` Give a player an enchant book
* `/peadmin give randomBook <player> <rarity> <chance> <level>` Give a player a random enchant book of this rarity
  * If the enchant has `isInShardPool: false`, then it will not be selected
  * `<level>` accepts a single number, range in the form of `x-y`, and `random` to choose a random level between 1 and max for the selected enchant
* `/peadmin give bookRandomizer <player> <rarity> <chance> [amount]` Give a player a book randomizer
  * `<chance>` only accepts inputs in the form of `x-y` since book randomizers require a success range
* `/peadmin give dust <player> <rarity> <chance> [amount]` Give a player dust
* `/peadmin give enchantClearer <player> [amount]` Give a player an enchant clearer
* `/peadmin give fragment <player> <rarity> [amount]` Give a player a fragment
* `/peadmin give magic <player> <rarity> [amount]` Give a player magic
* `/peadmin give scrapper <player> <type> <chance> [amount]` Give a player a scrapper
* `/peadmin give shard <player> <rarity> <chance> [amount]` Give a player a shard
* `/peadmin give talisman <player> <enchant>` Give a player a talisman
* Permissions are of the format `give.subcommand` (all lowercase)

### Enchant Commands:

* Enchant commands ignore all checks (allowing incompatible, over-leveled, hidden enchants)
* `/peadmin addEnchant <enchant> <level>` Enchant your held item
* `/peadmin removeEnchant <enchant>` Remove an existing enchant from your item
* Permissions are of the format `subcommand` (all lowercase)

### Reload Commands:

* `/peadmin reload` Reloads the entire plugin
* `/peadmin reload armor` Reloads all armor (THIS WILL SPAM ARMOR EQUIP/UNEQUIP MESSAGES FOR ALL PLAYERS)
  * Since this command is potentially spammy for players, it is only reloaded when `armor` or `rarity` is specified
* `/peadmin reload combos` Reloads all enchant combos
* `/peadmin reload enchants [enchant]` Reloads all enchants or the specified one
* `/peadmin reload incompatibleEnchants` Reloads all incompatible enchants
* `/peadmin reload items` Reloads all items
* `/peadmin reload messages` Reloads all plugin messages
* `/peadmin reload menus` Reloads all menus
* `/peadmin reload rarity` Reloads rarities and forces a reload of `items` and `armor`
* `/peadmin reload sounds` Reloads all plugin sounds
* Permission: `reload`

### Talisman Commands:

* `/peadmin talisman setItemMax` Update the max number of talismans allowed on your held item (default: 1)
* Permission: `talisman`

### Other Commands:

* `/peadmin help` Opens the `peadmin` command help menu
* `/peadmin print combos` Print all enchant combos to your chat
* `/peadmin print incompatible` Print all incompatible enchant groups to your chat
* Permissions are of the format `subcommand` (all lowercase)

## Modifier Admin Commands: `/modifier`

A special set of commands to edit unique item properties

* A popular plugin known as [ItemEdit](https://www.spigotmc.org/resources/itemedit-1-8-x-1-21-x.40993/) has similar functionality. I suggest using it over this command
* `/modifier add <attribute> <equpmentSlot>` Adds an attribute to your held item
* `/modifier remove <attribute>` Removes an attribute from your held item
* `/modifier visibility` Toggles hiding the item's attributes
* `/modifier hideColor` Toggles hiding the color ItemMeta value
* `/modifier setLeatherArmorColor` Edit the color of leather armor
* `/modifier unbreakable` Make an item unbreakable (infinite durability)
* `/modifier unenchantable` **Makes the held item unenchantable. Players will be unable to modify this item's enchants**
* Permission: `prisonenchants.admin.modifier`
