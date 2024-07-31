---
description: A remake of the Abilities plugin from JailbreakMC with improved configuration
---

# Abilities

## Status - Private

## Development Setup

There is some extra setup since this plugin relies on nms

* You need to install [BuildTools](https://www.spigotmc.org/wiki/buildtools/#wikiPage). Specifically this plugin relies on remapping the jar on build
  * Install with: `java -jar BuildTools.jar --rev 1.20.4 --remapped` (after downloading the BuildTools jar)
* The plugin relies _exclusively_ on Minecraft 1.20.4. Version updates will require a new plugin build

## Ability Configuration

Each ability has its own configuration file in the `/abilities` folder

```yaml
# If the ability will work. Allows for quick disabling if something is very wrong
isActive: true
# The friendly name of this ability
name: Nuke
# Individual permissions for each ability level
# They will be checked in the highest level order first
level-permissions:
  '1': abilities.level.1
  '2': abilities.level.2
# The cooldown time in seconds
cooldown-seconds: 10
# An equation which replaces "level" with the player's ability level
# Only present for certain active abilities
duration-seconds-equation: '10'
# Here you define how the item appears in the ability selection menu
item:
  material: TNT
  # Name and lore support the following placeholders:
  # {name}, {player_cooldown}, {player_level}, {cooldown}, {max_level}
  name: '&e{name}'
  lore:
  - '&7Cooldown: &e{player_cooldown}'
  - '&7Level: &e{player_level}'
  - ''
  - '&7Shoots a fireball which explodes'
  - '&7and breaks nearby blocks'
  - ''
  - '&7Ability Cooldown: &e{cooldown}'
  - '&7Max Level: &e{max_level}'
# Most enchants have per-level configuration options
levels:
  '1':
    explosionSize: 4.0
  '2':
    explosionSize: 5.0
# Some enchants have special configuration options which effect all levels
extras:
  placeBlocksAtFeet: true
  projectileSpeed: 10.0
```

## Other Configuration Options

* Ability selection menu
  * Ability item placement in the menu
* Database parameters
* ActionBar which shows on ability selection
* Messages
* Sounds
