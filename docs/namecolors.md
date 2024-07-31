---
description: Create custom name styles (patterns) for players on your server!
---

# NameColors

## Status - Open Source & Published

* Github [https://github.com/GavvyDizzle/NameColors](https://github.com/GavvyDizzle/NameColors)
* Spigot [https://www.spigotmc.org/resources/namecolors.106520/](https://www.spigotmc.org/resources/namecolors.106520/)

## Requirements

* There are two required dependencies: [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) and [ServerUtils](https://www.spigotmc.org/resources/serverutils.106515/)
* You will also need a permissions plugin and I recommend [LuckPerms](https://luckperms.net/)

## Commands

* /namecolor (/name, /namecolors) - Opens name selection GUI
* /namecoloradmin (/ncadmin) - Admin commands

## Permissions

* GUI permission: `namecolors.player`
* Admin permission: `namecolors.admin` - Gives access to all admin permissions
  * Each subcommand requires the permission `namecolors.admin.command` where `command` is the name of the subcommand
* Each color's permission is defined as `namecolor.color.name`
  * The name of a pattern is what you define it as in the config file

## Placeholders

* This plugin will automatically overwrite the player's name in chat unless the default formatting is changed by a chat manager
* `%namecolors_name%` gives the player's username formatted using their selected pattern (if online)

## Creating Name Styles

* Each style requires a unique name. This name is used in its permission node
* item - The Material to display as (when unlocked). See [materials](https://minecraft-ids.grahamedgecombe.com/)
* item-locked - The Material to display as when locked
* display-name - The name of the item in the GUI. The color defaults to the pattern of this style if no color codes are present
* lore - The lore of the item in the GUI
* pattern - The pattern to apply to the player's name (does not work with nicknames)
  * All supported color styles can be found [here](https://www.spigotmc.org/resources/serverutils.106515/)

## Default Pattern

* By default, a player's pattern is gray "&7" when they join the server (or have permission to use none)
* You can change the default pattern by changing 'settings.default' to the desired name
* If the config file ever becomes corrupted, the color will default to gray

## Patterns in-depth

* See [this](https://github.com/GavvyDizzle/NameColors/blob/master/examples/default\_config.yml) for a default config file with simple patterns
* Each pattern requires at least one color
* `SOLID` color patterns use & color codes and hex colors
  * & color codes (omit the &) \[0-9a-f]
  * Hex color codes `<SOLID:xxxxxx>` where xxxxxx is a hex code \[0-9a-f]
  * To have a pattern cycle through colors, put multiple separated by a space
    * `c f` - red/white/red/white... ![](.gitbook/assets/image.png)
    * `a c e` - green/red/yellow/green... ![](<.gitbook/assets/image (1).png>)
  * There is support to add helper codes to each color (k,l,m,n,o). You _must_ separate each code by '|' here
    * `c` for red ![](<.gitbook/assets/image (2).png>)
    * `c|l` for bold + red ![](<.gitbook/assets/image (3).png>)
    * `c|l|o|n` for bold + italics + underline + red ![](<.gitbook/assets/image (4).png>)
    * `<SOLID:abcdef>|l` to bold this hex color ![](<.gitbook/assets/image (5).png>)
    * `f|l <SOLID:e345df>|o|m` mixing the two also works ![](<.gitbook/assets/image (6).png>)
* `GRADIENT` color patterns are also supported
  * `<GRADIENT:xxxxxx> </GRADIENT:yyyyyy>` the name will go from xxxxxx to yyyyyy hex code
  * Each pattern only accepts one gradient
  * Helper codes also work here but are parsed differently
    * `<GRADIENT:44af1e>&l&o&n </GRADIENT:70cb68>` would bold + italicise + underline this pattern ![](<.gitbook/assets/image (7).png>)

## Saving Data

* This plugin uses MySQL to store its data (MariaDB also works)
* The plugin will fail to load unless you have a proper MySQL database defined in the database section of the config
