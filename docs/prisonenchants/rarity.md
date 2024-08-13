---
description: Rarities are used for enchants, items, and armor
---

# Rarity

* You can create as many rarities as you would like and configure other parts of the plugin to use them
* Each rarity is created in the `armor.yml` file in the `PrisonEnchants` directory

## Config: `rarity.yml`

## Configuration

* See `example_configs/rarity.yml` for a full example configuration
* Rarities are referenced by `id` and `weight` be mindful of this when editing other files

```yaml
vanillaEnchantColor: '&7' # The color to use for vanilla enchants. &7 is the default color
rarities:
  '0': # This is a dummy rarity to use as the default. It is required but never actually shown
    id: none
    weight: 0
    colorCode: '&7'
    visual: ''
    permission: ''
    maxEnchants: 100
  '1':
    id: common # The rarity ID to use in commands and configuration
    weight: 1 # Higher weight means it is sorted first in lore and menus
    colorCode: <SOLID:2AC754> # Color "prefix" to apply when using this rarity
    visual: '&e&l✩' # A visual aid to show the rarity in the /enchants menu
    permission: '' # If set, the player will be unable to use enchants of this rarity (they will not activate)
    maxEnchants: 100 # The maximum number of enchants of this rarity allowed on an item. Set to a high number to ignore
  '2':
    id: rare
    weight: 2
    colorCode: <SOLID:189CFB>
    visual: '&e&l✩✩'
    permission: 'prisonenchants.rarity.rare' # Only players with this permission will have rare enchants activate
    maxEnchants: 100
```
