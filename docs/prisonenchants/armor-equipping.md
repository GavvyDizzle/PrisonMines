# Armor Equipping

## Config: `armor.yml`

## Introduction

* The process of deciding when a player equips is complex. Follow the guidelines below
* Unfortunately neither Spigot nor Paper provide a complete event for when armor is equipped

## Inventory Equipping

* Handles when the player updates their armor when their inventory is open
* A custom event, `ArmorEquipEvent` is fired when any armor changes are made

### Right-click Equipping

* Handles when the player puts on armor by right-clicking with an item in their hotbar
* Since some blocks open inventories or interact in some way, not all interactions actually equip armor
* If the player is sneaking, no interaction takes place, so they will equip the armor

### **Internal Handling**

* Any blocks that open an inventory are ignored (see [InventoryHolder](broken-reference))
* Internally, the plugin checks Spigot's [Tag](broken-reference) class for multiple lists of block types
  * If more Tag types get added or any are missing, you can add it to the `ArmorListener` class
* The current list of Tags are:
  * `ALL_SIGNS`
  * `ALL_HANGING_SIGNS`
  * `DOORS`
  * `TRAPDOORS`
  * `BUTTONS`
  * `FENCE_GATES`
  * `BEDS`
  * `FLOWER_POTS`
  * `ANVIL`
  * `CANDLE_CAKES`

### **External Handling**

* Blocks that do not appear in any Tag list need to be defined individually
* Add specific blocks to `interactBlocks` in `armor.yml`

```yaml
# A (probably) complete list of additional blocks for 1.20.2
interactBlocks:
  - BEACON
  - CRAFTING_TABLE
  - ENCHANTING_TABLE
  - ENDER_CHEST
  - COMPARATOR
  - REPEATER
  - DAYLIGHT_DETECTOR
  - LEVER
  - CARTOGRAPHY_TABLE
  - GRINDSTONE
  - LOOM
  - STONECUTTER
  - BELL
  - SMITHING_TABLE
  - NOTE_BLOCK
  - RESPAWN_ANCHOR
  - CAKE
```

### Dispenser Equipping

* Handles when the player equips armor via a Dispenser
* If the event is cancelled, the item is placed in their inventory

### Essentials Support `/hat`

* Essentials allows players to put additional items in their helmet slot
* For full support, you need to keep `allow-direct-hat: false` in the Essentials `config.yml`
* The plugin listens to the hat command and aliases: `hat` `ehat` `head` `ehead`

## Editor Note

Some topics redacted
