---
description: A recent plugin overhaul introduced side-by-side support for vanilla enchants
---

# Vanilla Enchant Support

## Custom Enchants

* If an item has custom enchants and no vanilla enchants, then it will appear unenchanted to clients
* As a result, placeholder vanilla enchants are added to items if they have no vanilla enchants
  * All items (except helmets) get enchanted with `Respiration 1`
  * Helmets get enchanted with `Infinity 1`

## Anvil

* Repairing and renaming items with custom enchantments works as expected
* Custom enchantment books are NOT able to be used in an anvil. Players must apply them in their inventory
* Custom enchantment books can be merged when the same level
* Vanilla enchants and custom enchant both merge in the same way. There are multiple checks for valid custom enchanted items (max enchants, incompatible enchants, removed enchants are all held). If an item is deemed invalid, then the anvil action is cancelled
* Prerequisite enchants are not checked in the anvil because it is assumed the item already contains any prerequisite enchants
* The XP cost to merge items is unchanged. Items with placeholder enchants will impact the XP cost

## Enchantment Table

* The enchantment table is unchanged
* Items with custom enchants are unable to be used because of their placeholder enchant

## Grindstone

* The grindstone will remove all vanilla and custom enchants from an item and fix the lore

## Behavior

* An item with only vanilla enchants will be untouched by the plugin
* Once a custom enchantment is applied, any vanilla enchants will be applied via lore with the custom enchants
  * Placeholder enchants will not appear in the lore

## Legacy Vanilla Enchant Support

* Earlier versions of the plugin allowed vanilla enchants to be applied with a custom enchant
* This behavior still remains if you would like to add something on top of a vanilla enchant
  * Vanilla enchants added via custom enchants will appear twice in the lore (once in the vanilla section and once in the custom section)

## Editor Note

Some topics redacted
