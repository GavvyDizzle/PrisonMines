---
description: How to create and configure custom enchantments
---

# Enchants

## Max Enchants

* In `config.yml` you define how a player's number of allowed enchants is calculated
* In the `maxEnchantsMapping` section, you define the permission node and value it matches with
  * The plugin will check for each permission starting with the highest enchant cap first
* `cancelOverEnchantedItems` allows you to tell the plugin how to handle over enchanted items
  * Custom enchants for held items will not activate. The item is still usable
  * For armor, the player will not be able to equip it
* `netheriteMaxEnchantBonus` allows you to increase the enchant cap of netherite items

```yaml
# From config.yml example file
cancelOverEnchantedItems: true # If the plugin should stop over enchanted tools and armor equipping
maxEnchantsMapping:
  level: # Checks for the permission based on the key: prisonenchants.maxenchants.{key}
    '1': 1
    '2': 2
    '3': 3
    '4': 4
    '5': 5
    '6': 6
netheriteMaxEnchantBonus: # Additive max enchants bonus
  tool: 1
  armor: 1
```

## Enchant Level Modifiers

A special setting to alter the max level of an enchant for a specific item

* The example below shows how you could increase the max level of Unbreaking to 10 for a Golden Pickaxe

```yaml
# From config.yml example file
enchantLevelModifiers:
  GOLDEN_PICKAXE: # Edit a Golden Pickaxe (by material name)
    '1': # Each section needs an EnchantIdentifier and level
      enchant: UNBREAKING
      level: 10
```

## Configuration

### **General**

* `priority` determines when this enchant should activate relative to others
  * Higher priority causes it to activate sooner
  * If priorities match, they will activate in the order they appear in the EnchantIdentifier Enum class (essentially random, but still deterministic)
  * Useful for telling the plugin to activate some enchants before others that edit the environment
* `chance` tells the enchant its activation chance and method
  * `useEquation: true` Tells the plugin to solve the `equation` by replacing level with the number level of the enchant
    * The string `level` will be replaced with the level of the enchant inside the equation before solving it
  * `useEquation: false` Tells the plugin to read individual level chances from the `chances` map
    * You must define each level here otherwise it will never activate for that level
  * `usePercentChance: true` The chance is taken as a percent chance
  * `usePercentChance: false` The chance is taken as a 1/x chance
* `extras` A special-purpose section designed to contain all additional configuration information. This will be unique for each enchant when present

```yaml
version: 1 # The config version. Don't touch this unless you know what you are doing
name: Explosive # Name of the enchant
rarity: 4 # Rarity number of the enchant
maxLevel: 4 # Max level of the enchant (there are various ways to over level)
priority: 0
chance: # You don't need to define equation and chances. Both are done here for the sake of a complete example
  useEquation: true
  equation: level * -5 + 55
  usePercentChance: false
  chances:
    1: 0.05
    2: 0.06
    3: 0.07
    4: 0.075
state:
  isActive: true # If the enchant can activate. Useful toggle when things go wrong
  isInShardPool: true # If the enchant can apply via shards
  showInEnchantsMenu: true # If the enchant will appear in the /enchants menu
  isTalismanActive: false # If the talisman for this enchant can be obtained (via admin commands)
  hideVanillaEnchantOnLore: false # Optional - Hides this enchant from the vanilla enchant lore
conditional:
  prerequisites: [] # List of enchant that must be present before this enchant can be applied
  remove_when_applied: [] # List of enchants to remove when this enchant is applied
item: # Define how this enchant will look in the /enchants menu
  material: TNT
  lore:
  - '&7Creates a small explosion'
  - '&7after breaking a block!'
extras:
  explosionEquation: 2.8
```

### **Reward Enchants**

* You are required to define the reward for each level of the enchant

```yaml
rewards:
  '1':
    level: 1
    rewards:
      '1':
        commands: []
  '2':
    level: 2
    rewards:
      '1':
        commands: []
  '3':
    level: 3
    rewards:
      '1':
        commands: []
```

### **Potion Effect Armor Enchants**

* You are required to define the effect type and amplifier for each level of the enchant

```yaml
effects:
  '1':
    level: 1
    amplifier: 0
    effect: HEALTH_BOOST
  '2':
    level: 2
    amplifier: 1
    effect: HEALTH_BOOST
  '3':
    level: 3
    amplifier: 2
    effect: HEALTH_BOOST
```

### **Reward Drop Boost Enchants**

* You are required to define the boosts (including the boost calculation formula, drop label and permissions) for each level of the enchant

```yaml
drops:
  '1':
    level: 1
    boosts:
      '1':
        formula: 0.0005
        drop: fishing-net
        permissions: []
  '2':
    level: 2
    boosts:
      '1':
        formula: 0.0005
        drop: fishing-net
        permissions: []
  '3':
    level: 3
    boosts:
      '1':
        formula: 0.0005
        drop: fishing-net
        permissions: []
      '2':
        formula: 0.009
        drop: fishing-net-better
        permissions:
          - "emf.better-fishing-net"
```

## Creating New Enchants

Entering the realm of coding I see...

### **Initial Enchant Properties**

* Enchants have a few distinct properties that can only be set in the code
* Create and link a unique `EnchantIdentifier`
* Determine the `EnchantType` that it will belong to. This determines what items players can get this enchant on

### **Determine the Enchant Type**

* All enchants must extend the `PrisonEnchant` class
* There are alternatives such as `RewardEnchant` and `PotionEffectArmorEnchant` which can be used instead as they also extend `PrisonEnchant`

### **Add Additional Functionality**

* The plugin will call your enchant's `#onActivate()` method when triggered from an existing EventHandler in `EnchantManager`
* Additionally, you can implement from any of the following:
  * `AttributeEnchant` Handles adding and removing attributes from an item
  * `EquipableEnchant` Allows changes to be made when a player stats/stops wearing armor
  * `HoldableEnchant` Allows changes to be made when the player starts/stops holding an item in their main hand
  * `OnKillEnchant` Calls a new activation method when a player kills an entity
    * This bypasses any activation chance checks

### **Don't Reinvent the Wheel**

* Copy code from existing enchants, so you don't forget any code
* Reference other enchants if you are unsure how to do something
