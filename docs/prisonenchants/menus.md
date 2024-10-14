---
description: Configuration options for all plugin menus
---

# Menus

* Most menus have options to change their name, size, and filler item color

## Config: `menus.yml`

## Player Menus

### Enchant List `/enchants, /enchants rarity`

* Allows you to edit the items and organization of the first menu opened with the command
* Configuration options for individual submenus do not exist. Unfortunately, it is hard-coded
  * If you would like to edit these menus, check `EnchantListTopMenu` and `EnchantListMenu`
* Various options **for admins** to edit the enchants of their held item

### Shard Menu

* Opened when the player applies a shard to an item in their inventory
* You have a few options for how this will work:
  * `forceRandomEnchant: true` will apply an enchant and not open a menu
  * `forceRandomEnchant: false, usingEnchantRoulette: false` will open up a menu where the player chooses one of three random enchants
  * `forceRandomEnchant: false, usingEnchantRoulette: true` will open up a "roulette" like menu where it selects a random one from an animation
* There are configuration options for both menu types under the `shardEnchanter` section

### Scrapper Menu

* Opened when the player uses a scrapper on an item in their inventory
* `removeEnchantOnFail: true` will cause a random enchantment to be removed when the scrapper fails
  * You can blacklist rarities from this random removal under `scrapper.rarityBlacklist` in `items.yml`
* Unfortunately, this menu is also hard coded. If you would like to edit it, check `ScrapperInventory`

### Tinkerer Menu `/tinkerer`

* Where players can convert shards to dust and books to magic
* `insertRow` is the 9 slots that shards will fill
* `resultRow` is the 9 slots the resulting dust will fill

## Admin Only Menus

These menus have little to no configuration options. They are view-only menus for admins

### Item Database `/peadmin list [type]`

* Allows you to view all items which are sorted by rarity
* Clicking on the item will put one in your inventory
* Buttons are available to swap between menus

### Enchant Database `/peadmin enchants`

* Allows you to view all enchants and sort them by various categories
* You can select your held item to view only enchants applied to that item
* Various options to edit the enchants of your held item

### Armor Catalogue `/peadmin customArmor menu`

* Allows you to view all armor sets and easily obtain custom armor pieces and sets
* Configuration is present for completeness (actually because I copied a menu that was configurable but who cares)
