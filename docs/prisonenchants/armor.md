# Armor

## Config: `armor.yml`

## Introduction

Allows the creation of custom armor sets with custom boosts

* Each armor set is created in the `armor.yml` file in the `PrisonEnchants` directory
* Armor has a custom selection menu for admins: `/peadmin customArmor menu`
* Armor can be given to the:
  * player directly: `/peadmin customArmor give <player> <armorID> <rarity> <type> <piece> [color]`
  * player's RewardInventory menu: `/peadmin customArmor putInMenu <player> <menuID> <armorID> <rarity> <type> <piece> [color]`
* If you wish to customize your armor item further, such as using non-helmet items (essentials /hat), consider using the `/peadmin customArmor setHeldArmor <armorID> <rarity>` command to edit the data of your held item to be custom armor

## Upgrading Armor with Fragments

* Players can upgrade their armor with fragments in the crafting table
* Upgrading armor gives that armor piece more enchantment slots
* The crafting recipe is a 3x3 with (x8) fragments of the next rarity on the outside and armor in the center

## Armor Boost Types

* `COMMAND` - Execute a command on equip/unequip
* `ENCHANT` - Boost an enchant's activation chance
* `POTION_EFFECT` - Apply a potion effect
* `REWARD_BOOST` - Boost rewards found (MineRewards - private)
* `XP_BOOST` - Boost XP found (PlayerLevels - private)

## General Configuration

* See `example_configs/armor.yml` for a full configuration example

```yaml
firstRarity: common # The rarity that is used for the first upgrade. Here the upgrade would be none to common
nameFormat: '{rarity} {type}' # Formatting for naming custom armor. Supports internal placeholders: {rarity}, {type},
useRarityColorCode: true # If the item's name should be colored using the rarity color
enchantCaps: # A mapping of armor rarity to enchant cap. Armor starts at the 'none' or 0 rarity
  list: # The ordering of this list determines the rarity upgrade chain
    '0': 4
    '1': 5
    '2': 6
    '3': 7
    '4': 8
    '5': 9
stopArmorBlockPlacement: true
stopArmorBlockPlacementMessage: '&cStopped you from placing down your custom armor'
lackingArmorPermissionMessage: '&cYou don''t have permission to wear this armor'
```

## Armor Set Configuration

```yaml
armor:
  '1': # This key does not mean anything but is needed in the config file
    type: TODO # The boost type for this armor
    nbtID: my_custom_armor # The key to use in the item's metadata. This must be unique
    permission: '' # Permission needed to wear any armor belonging to this set. Leave blank for no permission check
    modelDataID: 50000 # The CustomModel data for this armor (helmet/chest/legs/boots -> 50000/50001/50002/50003)
    isEnchantable: true # If this armor set can have its enchantments altered
    names: # Names for each piece of armor in the set
      helmet: <SOLID:F1FB00>Helmet
      chestplate: <SOLID:F1FB00>Chestplate
      leggings: <SOLID:F1FB00>Leggings
      boots: <SOLID:F1FB00>Boots
    lore: # Lore to apply to all armor for this set
    - '&aTODO Boost Armor'
    - ''
    - '&8Set Bonus:'
    - '&7Wearing this entire armor set will'
    - '&7boost TODO'
    - ''
    - '&8Obtained from the &e&lTutorial Set'
    equipMessage: '&aBoost activated!'
    removeMessage: '&cBoost disabled!'
    ...: # See the next section for per-boost configuration
```

### **Per Boost Configurations**

```yaml
armor:
  'command': # No / needed for commands
    ...:
    onEquipCommands: 
    - fly on
    onRemoveCommands: 
    - fly off
  'enchant':
    ...:
    enchantType: MAKE_IT_RAIN # The enchant identifier
    multiplier: 1.15 # The bonus to give when the full set is applied
  'potion_effect':
    ...:
    potionEffectType: SPEED # Potion type (see https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html)
    amplifier: 1
  'reward_boost':
    ...:
    isMultiplicative: # If the boost should be treated as additive or multiplicative
    multiplier: 1.05 # The bonus to give when the full set is applied
  'xp_boost':
    ...:
    isMultiplicative: # If the boost should be treated as additive or multiplicative
    multiplier: 1.05 # The bonus to give when the full set is applied
```

## Additional Notes

* CustomModelData for armor may seem confusing. Let's say you set `modelDataID: 500`
  * The helmet will be given `modelDataID: 500`
  * The chestplate will be given `modelDataID: 501`
  * The leggings will be given `modelDataID: 502`
  * The boots will be given `modelDataID: 503`
* This format was designed so all armor will have a different CustomModelID
* If any armor set has an overlapping range, it will warn you the in console
