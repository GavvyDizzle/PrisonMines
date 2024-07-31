# Placeholders

PlaceholderAPI placeholders

## Course

* `%parkour_<id>_name%` Gets the formatted name of the course
* `%parkour_<id>_times%` The number of times on this leaderboard

## Course Times

* `%parkour_<id>_name_<rank>%` Gets the name of the player at this rank for this course
* `%parkour_<id>_time_<rank>%` Gets the time of the player at this rank for this course (formatted)
  * If a time does not yet exist for the given rank both placeholders will return `N/A`
* You know what placement you are showing. The best time is rank 1

## Player

* `%parkour_<id>_hastime` Returns `true` if the player has a time and `false` if not
* `%parkour_<id>_time%` The player's best time on this course (formatted)
  * No time gives `N/A`
* Get their name with some other placeholder
