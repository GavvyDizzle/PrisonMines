# Commands

## Admin Commands

* The base command is `parkouradmin` with the permission `parkour.admin`
* All commands require permission to use which follows the format `parkour.admin.command` where command is the command's first argument
  * `/parkouradmin create <id>` Create a new course
  * `/parkouradmin delete <id>` Delete a course
  * `/parkouradmin endCompetition <giveRewards>` End the active competition
  * `/parkouradmin loadFromFile <id>` Load a new course from a file
  * `/parkouradmin reload [arg]` Reloads this plugin or a specified portion
    * You are able to reload individual courses with `/parkouradmin reload courses <id>`
    * Reloading courses does not reload their leaderboards (see the following command)
  * `/parkouradmin reloadLeaderboard [id]` Reload a course leaderboard or all leaderboards
  * `/parkouradmin set <id> <type> [value]` Set some field for a course
    * Valid types are `death_boundary_height` `material` `name`
    * Setting the `death_boundary_height` will override the global setting. To change it back, edit the course config file manually.
  * `/parkouradmin setLocation <id> <type> [exact|snap] [x] [y] [z] [pitch] [yaw]` Set a course's locations
  * `/parkouradmin startCompetition <id> <seconds>` Start a competition
  * `/parkouradmin startRandomCompetition <seconds>` Start a competition on a random course

## Player Commands

* The base command is `parkour` with the permission `parkour.player`
* All commands require permission to use which follows the format `parkour.player.command` where command is the command's first argument
  * `/parkour` Joins the competition course (no extra permission required)
  * `/parkour lb` Outputs the active competition's leaderboard to chat
  * `/parkour join <id>` Join a parkour course
  * `/parkour menu` Opens the parkour menu which displays leaderboard information
