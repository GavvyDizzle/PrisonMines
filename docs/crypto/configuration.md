---
description: Default configuration files
---

# Configuration

### chat.yml

```yaml
pattern: (?i)\[gpurack\]|\[gpus\]
lifetimeSeconds: 300
```

### coins.yml

```yaml
boosts:
  amount: 0.1
  amount-message: <SOLID:66aa00>(<SOLID:a7ff55>+{amount_pct}%<SOLID:66aa00>)
  rate: 0.0

list:
  '1':
    id: btc
    coinbaseID: BTC
    itemID: 0
    slot: 10
    price: 26570.54
    multiplier: 2.0
    taxRate: 5.0 # The % to tax for exchange and send actions
    item:
      customModelData: 0
      material: YELLOW_DYE
      name: Bitcoin
      lore:
        - '&7You have {amount_formatted} BTC'
        - '&2Sell Price: ${sell_price} &8(per 1)'
        - '&aValue: ${value}'
  '2':
    id: eth
    coinbaseID: ETH
    itemID: 2
    slot: 12
    price: 1589.62
    multiplier: 1.0
    taxRate: 6.0
    item:
      customModelData: 0
      material: PURPLE_DYE
      name: Ethereum
      lore:
        - '&7You have {amount_formatted} ETH'
        - '&2Sell Price: ${sell_price} &8(per 1)'
        - '&aValue: ${value}'
```

### commands.yml

```yaml
commandDisplayName:
  admin: cryptoadmin
  player: crypto
helpCommandPadding:
  admin: '&6-----(Crypto Admin Commands)-----'
  player: '&6-----(Crypto Player Commands)-----'
descriptions:
  admin:
    addItem: Adds a GPU to the player's /rew pages inventory
    confirm: Confirm an action
    editAmount: Edit the amount of a player's reward items
    giveGPU: Gives the player a GPU item
    help: Opens this help menu
    list: Opens the GPU list menu
    openPlayerMenu: Opens a player's menu system
    reload: Reloads this plugin or a specified portion
    reset: Resets all coins and stored GPUs for the player
    resetCoin: Resets this coin to 0 for all players
    resetCoins: Resets all coins for the player
    resetGPUCooldowns: Removes the cooldown on all of a player's GPUs
    simulateBlockMine: Simulates a block mine for the player
    updateSavedItems: Updates all ItemStacks saved to the database
  player:
    exchange: Opens the crypto exchange menu
    help: Opens this help menu
    rack: Opens the GPU rack menu
    sell: Opens the crypto sell menu
    sellGPU: Opens the GPU sell menu
    send: Send crypto to another player
```

### config.yml

```yaml
database:
  host: todo
  port: 3306
  user: todo
  password: todo
  database: todo
```

### gpus.yml

```yaml
groups:
  '0':
    id: 4000-series
    defaultLimit: 2
    limitOverrides:
      3:
        limit: 3
        permission: crypto.groups.4000-series.limit.3
      4:
        limit: 4
        permission: crypto.groups.4000-series.limit.4
```

### menus.yml

```yaml
backItem:
  material: RED_STAINED_GLASS_PANE
  name: '&cBack'
  lore: []
main:
  name: Crypto
  rows: 3
  filler: gray
  items:
    gpu_menu:
      slot: 11
      customModelData: 0
      material: CHEST
      name: '&eMount GPUs'
      lore:
        - '&7Access your GPU rack'
    sell_coins_menu:
      slot: 12
      customModelData: 0
      material: SUNFLOWER
      name: '&eSell Crypto'
      lore:
        - '&7Sell crypto for money'
    exchange_coins_menu:
      slot: 13
      customModelData: 0
      material: PAPER
      name: '&eExchange Crypto'
      lore:
        - '&7Enchange one type of crypto for another'
        - '&8- &oThese transactions are taxed'
    send_coins_menu:
      slot: 14
      customModelData: 0
      material: MAGENTA_GLAZED_TERRACOTTA
      name: '&eSend Crypto'
      lore:
        - '&7Send crypto to another player'
        - '&8- &oThese transactions are taxed'
    sell_gpus_menu:
      slot: 15
      customModelData: 0
      material: BRICK
      name: '&eSell GPUs'
      lore:
        - '&7Sell spare GPUs'
    toggle_messages:
      slot: 8
      customModelData: 0
      material: OAK_SIGN
      name: '&eToggle Messages'
      lore:
        - '&7Click here to toggle crypto find messages'
      messages:
        'on': '&aYou will now receive messages when finding crypto'
        'off': '&cYou will no longer receive messages when finding crypto'
gpu:
  name: GPU Rack
  adminName: GPU Rack
  rows: 5
  filler: gray
  backSlot: 36
  lockedGPUMessage: '&cYou must wait {time} before removing this GPU'
  lockedMenuMessage: '&cUnable to edit GPUs at this time'
  slots:
    - 12
    - 13
    - 14
    - 21
    - 22
    - 23
    - 30
    - 31
    - 32
  permissions: # One permission per slot. These just unlock slots in the order provided in the slots section
    - crypto.gpu.1
    - crypto.gpu.2
    - crypto.gpu.3
    - crypto.gpu.4
    - crypto.gpu.5
    - crypto.gpu.6
    - crypto.gpu.7
    - crypto.gpu.8
    - crypto.gpu.9
sell_coins: # Slots of GPUs are defined in coins.yml
  name: Sell Crypto
  rows: 3
  filler: gray
  backSlot: 18
sell_coins_anvil:
  name: Sell Crypto
  messages:
    invalidAmount: '&cUnable to make the transaction. Input a valid amount'
    notEnoughCrypto: '&cYou don''t have enough {coin} to make this transaction'
    successfulTransaction: '&aSuccessfully sold {crypto_amount} {coin} for ${money_amount}'
  items:
    money:
      customModelData: 0
      material: SUNFLOWER
      name: '&aMoney Active'
      lore:
        - '&7Selling &e{coin}'
        - ''
        - '&7You have {balance_formatted} {coin}'
        - '&2Total value: ${value}'
        - ''
        - '&7Input the money amount of &e{coin}'
        - '&7you would like to sell, or'
        - '&7type ''max'' to sell all'
    crypto:
      customModelData: 0
      material: GREEN_CONCRETE
      name: '&aCrypto Active'
      lore:
        - '&7Selling &e{coin}'
        - ''
        - '&7You have {balance_formatted} {coin}'
        - '&2Total value: ${value}'
        - ''
        - '&7Input the amount of &e{coin}'
        - '&7you would like to sell, or'
        - '&7type ''max'' to sell all'
    result_money:
      customModelData: 0
      material: PAPER
      name: '&eTransaction Result'
      lore:
        - '&2Selling: ${money_amount}'
        - '&aAmount: {crypto_amount} {coin}'
        - ''
        - '&7Click here to confirm your transaction'
    result_crypto:
      customModelData: 0
      material: PAPER
      name: '&eTransaction Result'
      lore:
        - '&2Selling: {crypto_amount} {coin}'
        - '&aValue: ${money_amount}'
        - ''
        - '&7Click here to confirm your transaction'
exchange_coins: # Slots of GPUs are defined in coins.yml
  name: Exchange Crypto
  rows: 3
  filler: gray
  backSlot: 18
  statusItem:
    customModelData: 0
    slot: 22
    material: PAPER
    name: '&eStatus'
    lore:
      - '&aSelling: {coin}'
      - ''
      - '&7First, click on which crypto you would'
      - '&7like to sell. Clicking on a second crypto'
      - '&7will open up a menu where you input how'
      - '&7much you would like to exchange.'
      - ''
      - '&7Click here to remove your selection'
exchange_coins_anvil:
  name: Exchange Crypto
  messages:
    invalidAmount: '&cUnable to make the transaction. Input a valid amount'
    notEnoughCrypto: '&cYou don''t have enough {coin} to make this transaction'
    successfulTransaction: '&aSuccessfully sold {crypto_amount} {coin} for {result_crypto_amount}{result_coin}'
  items:
    money:
      customModelData: 0
      material: GREEN_CONCRETE
      name: '&aCrypto Active'
      lore:
        - '&7Exchanging &e{coin} &7for &e{result_coin}'
        - ''
        - '&7You have {balance_formatted} {coin}'
        - '&2Total value: ${value}'
        - ''
        - '&7Input the money amount of &e{coin}'
        - '&7you would like to exchange, for'
        - '&e{result_coin} &7or type ''max'' to sell all'
    crypto:
      customModelData: 0
      material: SUNFLOWER
      name: '&aMoney Active'
      lore:
        - '&7Exchanging &e{coin} &7for &e{result_coin}'
        - ''
        - '&7You have {balance_formatted} {coin}'
        - '&2Total value: ${value}'
        - ''
        - '&7Input the amount of &e{coin}'
        - '&7you would like to exchange, for'
        - '&e{result_coin} &7or type ''max'' to sell all'
    result_money:
      customModelData: 0
      material: PAPER
      name: '&eTransaction Result'
      lore:
        - '&2Selling: ${crypto_amount}'
        - '&aAmount: {crypto_amount} {coin}'
        - ''
        - '&eTax Rate: {tax}%'
        - '&2Result: {result_crypto_amount} {result_coin}'
        - '&aValue: ${result_money_amount}'
        - ''
        - '&eClick here to confirm your transaction'
    result_crypto:
      customModelData: 0
      material: PAPER
      name: '&eTransaction Result'
      lore:
        - '&2Selling: {crypto_amount} {coin}'
        - '&aValue: ${money_amount}'
        - ''
        - '&eTax Rate: {tax}%'
        - '&2Result: {result_crypto_amount} {result_coin}'
        - '&aValue: ${result_money_amount}'
        - ''
        - '&eClick here to confirm your transaction'
send_coins:
  name: Send Crypto
  rows: 3
  filler: gray
  backSlot: 18
  prompt: '&2[Crypto] &aClick here! Balance: {amount} {coin}'
  command: '/crypto send {coin} '
sell_gpus:
  name: Sell GPUs
  rows: 3
  filler: gray
  backSlot: 18
  gpuSlot: 13
  confirmItem:
    slot: 16
    material: LIME_STAINED_GLASS_PANE
    name: '&aConfirm GPU Sell'
    none_selected_lore:
      - '&7Select a GPU to see its price'
    selected_lore:
      - '&aSell Price: ${price_formatted}'
```

### messages.yml

```yaml
cannotSendSelf: '&cUnable to send crypto to yourself'
invalidCryptoID: '&cNo crypto exists with the ID: {id}'
invalidAmount: '&cInvalid amount: {amount}'
notEnoughCrypto: '&cYou don''t have enough {coin} to make this transaction ({self} < {amount})'
invalidPlayer: '&c{player} is not a valid player'
selfSendSuccessful: '&aSuccessfully sent {amount} {coin} to {other}'
sendSuccessful: '&aYou received {amount} {coin} from {player}'
```

### sounds.yml

```yaml
generalClickSound:
  enabled: true
  sound: UI_BUTTON_CLICK
  volume: 1.0
  pitch: 1.0
generalFailSound:
  enabled: true
  sound: BLOCK_NOTE_BLOCK_BASEDRUM
  volume: 1.0
  pitch: 1.0
pageTurnSound:
  enabled: true
  sound: ITEM_BOOK_PAGE_TURN
  volume: 1.0
  pitch: 1.3
sellSound:
  enabled: true
  sound: BLOCK_NOTE_BLOCK_CHIME
  volume: 1.0
  pitch: 1.75
```

### example\_gpu.yml (item)

```yaml
id: gpu
lockDurationSeconds: 3600 # This locks the GPU in the menu for 3600 seconds (the player is unable to remove it)
sellPrice: 100
permission: crypto.gpu.gpu # Empty string will ignore the permission check
permissionDenied: '&cYou don't have permission to mount this GPU'
item:
  skullLink: ''
  usingSkull: false
  material: BRICK
  customModelData: 0
  name: '&eExample GPU'
  lore:
    - '&7Example lore'

# Each reward will attempt to be found every block mined
rewards:
  '1':
    chance: 0.1
    coin: BTC
    amount: 0.005-0.0075
    permission: crypto.rewards.gpu.1
    commands: # Commands send before messages
      - eco give {player_name} 10
    messages:
      - 'You found {amount} BTC'
  '2':
    chance: 0.01
    coin: ETH
    amount: 0.005-0.0075
    permission: '' # Empty permission means none will be checked
    commands:
      - eco give {player_name} 100
    messages:
      - 'You found {amount} ETH'
```

### example\_gpu2.yml (skull)

```yaml
id: gpu2
lockDurationSeconds: 3600 # This locks the GPU in the menu for 3600 seconds (the player is unable to remove it)
sellPrice: 100
permission: crypto.gpu.gpu2 # Empty string will ignore the permission check
permissionDenied: '&cYou don't have permission to mount this GPU'
item:
  skullLink: 64f815bcbac237340464b777807f26b5e9317c19d579da2dd94cbbc6a0de5138
  usingSkull: true
  material: ''
  customModelData: 0
  name: '&eExample GPU 2'
  lore:
    - '&7Example lore 2'

# Each reward will attempt to be found every block mined
rewards:
  '1':
    chance: 0.1
    permission: crypto.rewards.gpu2.1
    commands: # Commands send before messages
      - eco give {player_name} 10
    messages:
      - '&7Line 1'
      - '&fLine 2'
  '2':
    chance: 0.01
    permission: '' # Empty permission means none will be checked
    commands:
      - eco give {player_name} 100
    messages:
      - '&7wooooo'
      - '&fhey there'
```
