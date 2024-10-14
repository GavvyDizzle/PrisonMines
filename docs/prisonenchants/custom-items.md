---
description: Usage and configuration information for all custom items
---

# Custom Items

* Items use the PersistentDataContainer to store custom data, so it is safe to update items after players have obtained them

## Config: `items.yml`

## Usage

How each item is used within the plugin

### Book

* Books belong to an **enchant** and have a **level** and a **success chance**
* An alternative to shards
* Must be applied to a valid item in the player's inventory
* Can be merged in an anvil with a book of the same enchant and level to produce a book with +1 level

### Book Randomizer

* Book Randomizers belong to a **rarity** and have a **success range** (min and max)
* Must be applied to a valid book in the player's inventory
* Updates the success chance of the book to a value in the range \[min,max]

### Dust

* Dust belongs to a **rarity** and has a **success chance boost**
* Obtained by converting dust in the `/tinkerer` menu
* Applied to a single shard or book in the player's inventory of the same rarity to increase its success chance

### Enchant Clearer

* Enchant clearers are a simple item with no extra data
* Applied to an enchanted item to remove all enchants and give them back as books with a 100% success chance

### Fragment

* Fragments belong to a **rarity**
* Used in the crafting table to upgrade the rarity of armor

### Magic

* Fragments belong to a **rarity**
* Used to randomly obtain dust or junk

### Scrapper

* Scrappers have a **success chance** and a **type**
* Used on an item in the player's inventory to remove custom enchants

### Shard

* Shards belong to a **rarity** and have a **success chance**
  * They also have a **dust conversion rate**
* Used on an item in the player's inventory to add a custom enchant

### Talisman

* Talismans belong to an **enchant**
* Applied to an item in the player's inventory to over-level a custom enchant

## Configuration

* The configuration is quite verbose, but allows full customization of the items players will receive
* You can disable entire item categories
  * `isEnabled: false` will stop any default configuration from generating. Feel free to delete disabled configuration.
* You are unable to request items via the `/peadmin give` command unless it is enabled and defined
  * Similarly, all loaded items are visible in the `/peadmin list` menu (excluding `book` `enchant clearer` `talisman`)
* **Placeholders** apply to both the name and the lore

### Rarity Items

Types: `Book Randomizer` `Dust` `Fragment` `Magic` `Shard`

* Only rarities that are explicitly defined will be available in game
* If an item defines its name as `name: ''`, then the item type's `name` name will be used
* The item type's `lore.prefix` and `lore.suffix` will surround an item definition's `lore`. Set any to be blank with `[]`
* Category Placeholders: none
  * `{rarity}` The rarity name with the first character capitalized (uncolored)
  * `{rarity_lowercase}` The lowercase rarity name (uncolored, also the rarity ID)
  * `{rarity_color}` The rarity color (will be parsed later)
  * `{rarity_visual}` The rarity visual

### Typed Items (Enum Items)

Types: `Scrapper`

* You are able to disable individual typed items
* If an item defines its name as `name: ''`, then the item type's `name` name will be used
* The item type's `lore.prefix` and `lore.suffix` will surround an item definition's `lore`. Set any to be blank with `[]`
* Category Placeholders: none

### Book

* Placeholders:
  * `{rarity_color}` The color code for this rarity
  * `{enchant_name}` The enchant's name
  * `{level}` The level number
  * `{level_roman}` The level number as a roman numeral
  * `{success}` The success chance
  * `{fail}` The failure chance (100-success)
* Book Chat Placeholders:
  * `{description_item_name}` The name of the book's enchant in /enchants
  * `{description_item_lore}` The lore of the book's enchant in /enchants
  * `{item_types}` The types of items this book's enchant can apply to (lists will be separated with commas)

### Book Randomizer

* `rarity` The rarity weight of this specific item
* Placeholders:
  * `{success_min}` The minimum of the success range
  * `{success_max}` The maximum of the success range

### Dust

* `rarity` The rarity weight of this specific item
* `conversionRate` The conversion rate of shards of this rarity. The final value will be truncated to an integer
  * Supports equations where `x` is replaced with the success chance of the shard
* Placeholders:
  * `{boost}` The boost value

### Enchant Clearer

* Placeholders: none

### Fragment

* `rarity` The rarity weight of this specific item
* Placeholders: none

### Magic

* `rarity` The rarity weight of this specific item
* `dustChance` The chance this item has to become dust \[0,1]
* `minSuccess` The minimum of the success range
* `maxSuccess` The maximum of the success range
* You can define the junk item under the section `magic.junkItem`
* You can define boosts to the `dustChance` by giving a list of percentages under `magic.dustConversionBoosts`.
  * This tells the plugin to check for permissions of the format: `prisonenchants.magicdustboost.<boost>` where `<boost>` is defined in the list above
  * The plugin searches for permissions in the order the boosts are defined, so granting all boosts will always choose the first one
* Placeholders: none

### Scrapper

* There are three types of scrappers. These types are also used for a shard's `failType`
* `NOTHING` Do nothing on fail
* `REMOVE_ENCHANT` Remove a random enchant on fail
  * `blacklistOverlevelledEnchants` If over levelled enchants should be ignored by random scrapper removal
  * `rarityBlacklist` A list of rarities (by name) that will be ignored by random scrapper removal
* `REMOVE_ENCHANT_WITH_BOOK` Remove a random enchant and give a book back on fail
  * `blacklistOverlevelledEnchants` If over levelled enchants should be ignored by random scrapper removal
  * `rarityBlacklist` A list of rarities (by name) that will be ignored by random scrapper removal
  * `bookSuccess.min/bookSuccess.max` The min and max values for books that get created
* Placeholders:
  * `{success}` The success chance
  * `{fail}` The failure chance (100-success)

### Shard

* `rarity` The rarity weight of this specific item
* `failType` Determines whet happens when the shard fails. See scrappers for a list of valid types and descriptions
  * The fail type will inherit the properties defined in the scrapper sections
* `permission` Define a permission that players must have to use enchants of this rarity. Set to `''` to ignore the check
* Placeholders:
  * `{success}` The success chance
  * `{fail}` The failure chance (100-success)

### Talisman

* Placeholders:
  * `{rarity_color}` The color code for this rarity
  * `{enchant_name}` The enchant's name
