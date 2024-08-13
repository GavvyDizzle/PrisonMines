---
description: >-
  A feature-rich custom enchantment plugin with incredible levels of
  customization
---

# PrisonEnchants

## Features

* Full control over items, messages, menus, and sounds
* A custom armor system that supports a set bonus
* A combo system that supports merging multiple enchant activations into a new one
* Make enchants incompatible (how vanilla bows don't allow infinity and mending)
* Many custom items to aid the enchantment process (books, dust, fragment, scrapper, shard)
* Ability to create and use custom rarities for enchants and items
* ... and so much more

## Dependencies

### Required

* [LoreManager](../loremanager.md) (private) - Handles lore formatting
* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - Some commands parse PAPI placeholders
* [ServerUtils](../serverutils.md) - Library plugin
* [WorldGuard](https://dev.bukkit.org/projects/worldguard) - Allows region flags to define where different enchant types work
* [Vault](https://www.spigotmc.org/resources/vault.34315/) - Used for the PennyPincher enchantment (which is not in use)

### Optional

* [Crypto](../crypto/) - Allows for boosting mechanics
* [FishingNets](../fishingnets.md) - Allows for boosting mechanics
* [mcMMO](https://www.spigotmc.org/resources/official-mcmmo-original-author-returns.64348/) - Allows for boosting mechanics
* [PrisonMines](../prisonmines.md) - Allows enchants to accurately remove blocks from mines (just for counting)
* RewardDrops (private) - Optional dependency used enchantments that might boost odds for getting drops.
* [RewardsInventory](../page.md) (private) - Supports giving armor to offline players

## Data Storage

* All configuration is done through `.yml` files
