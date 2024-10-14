---
description: To better contain enchants, WorldGuard flags are required to be used
---

# Regions

## Mining Flag `mining-enchants`

* Mining enchants are potentially the most dangerous, so they have two levels of defense
* Like other flags, enchants will not activate unless the player is in a region with the `mining-enchants` flag
* Additionally, enchants that edit blocks will only affect blocks contained in `mining-enchants` regions

## Other Flags

* All flags require the player to be located in a region containing the flag for applicable enchant types to activate
* PVP Flag `pvp-enchants`
* Fishing Flag `fishing-enchants`
* Farming Flag `farming-enchants`
* Treasure Flag `treasure-enchants`

## Notes

* Save yourself some time and create [template regions](https://worldguard.enginehub.org/en/latest/regions/priorities/?highlight=template#template-regions) for your mines, pvp areas, fishing areas, etc. that also contain these custom flags
