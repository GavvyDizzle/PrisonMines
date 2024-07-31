---
description: A simple Spigot plugin for renaming items with colorful text
---

# ItemColorizer

## Status - Private

## Renaming Options

Currently, there are three types of coloring:

* `DEFAULT_COLOR` Only allows the 22 & color codes
  * Certain ones can be removed by adding them to the `andColorBlacklist`
* `HEX` Allows & color codes and all hex colors
  * Format: `&#[A-Fa-f0-9]{6}`
* `GRADIENT` Allows & color codes, hex colors and gradient colors
  * The format _requires_ there to be both an opening and closing tag surrounding some text
  * Format: `&#G[A-Fa-f0-9]{6}text&#/G[A-Fa-f0-9]{6}`
  * Opening Tag: `&#G[A-Fa-f0-9]{6}`
  * Closing Tag: `&#/G[A-Fa-f0-9]{6}`

## Configuration

```yaml
messages:
  notRenamed: '&cYou must change the name before applying this item!'
  noPermission: '&cYou don''t have permission to use this item'
sounds:
  applyItemSound:
    sound: BLOCK_ANVIL_USE
    pitch: 1.0
    volume: 1.0
andColorBlacklist:
  - '&k'
  - '&m'
item:
  defaultColor:
    permission: itemcolorizer.item.defaultcolor
    material: NAME_TAG
    name: '&fRename Me!'
    lore:
      - '&eDefault Color Renamer'
      - '&7Click an item when holding this to rename it'
      - ''
      - '&7You can use & color codes'
      - '&7to rename this item'
  hex:
    permission: itemcolorizer.item.hex
    material: NAME_TAG
    name: '&fRename Me!'
    lore:
      - '&eHex Color Renamer'
      - '&7Click an item when holding this to rename it'
      - ''
      - '&7You can use & color codes'
      - '&7and hex color codes'
      - '&7to rename this item'
      - ''
      - '&7&#acf038[Text] &7-> <SOLID:acf038>[Text]'
  gradient:
    permission: itemcolorizer.item.gradient
    material: NAME_TAG
    name: '&fRename Me!'
    lore:
      - <GRADIENT:32cd23>Gradient Renamer</GRADIENT:ffff00>
      - '&7Click an item when holding this to rename it'
      - ''
      - '&7You can use & color codes,'
      - '&7hex color codes'
      - '&7and gradient color codes'
      - '&7to rename this item'
      - ''
      - '&7&#acf038[Text] &7-> <SOLID:acf038>[Text]'
      - '&7&#Gacf038[Text]&#/G943233 &7-> <GRADIENT:acf038>[Text]</GRADIENT:943233>'
```
