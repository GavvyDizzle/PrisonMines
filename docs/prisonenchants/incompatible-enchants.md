---
description: Create groups of enchants that are not allowed together
---

# Incompatible Enchants

## Config: `incompatible_enchants.yml`

## Configuration

* Groups must have two or more enchants
* Only one enchant from the group is allowed on an item
* The `lineFormat` is automatically applied to applicable enchants in the `/enchants` menu

```yaml
lineFormat: <SOLID:EE8181>Incompatible with {enchant}
lists:
  '1':
  - PROTECTION
  - BLAST_PROTECTION
  - FIRE_PROTECTION
  - PROJECTILE_PROTECTION
  '2':
  - BOOMERANG
  - BURST
  - SNAKE
  - STATIC
  '3':
  - ORE_FORGE
  - SILK_FORGE
  '4':
  - GAPPLE_INFUSION
  - SATURATION
  '5':
  - VAMPIRIC
  - LEECH
```
