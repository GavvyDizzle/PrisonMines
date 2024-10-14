---
description: Leaderboards keep track of the best time per player per course
---

# Leaderboards

## Configuration

```yaml
dateFormat: MM/dd/yyyy # How to display the DateFormat's time for {date} placeholders
format:
  general: # Defines the formatting for chat and menu leaderboards
    empty: '&7[No Times]' # What to show if there are no times
    rank: '&e{placement}. &a{name} - {time}' # What individual times look like
    blank: '&7 -----' # Extra line to split the top times from the player's time
    personalRank: '&f{name} - {time}' # Shows the player's PB if it is not in the top times
  personalTimes: # Defines the formatting for the personal leaderboard
    empty: '&7[No Times]' # What to show if the player has no times
    firstLine: '&e{placement}. &a{time} - {date}' # What the first entry looks like (no {delta})
    line: '&e{placement}. &a{time} - {date} &8(+{delta})' # What individual entries look like
placements:
  chat: 5 # Number of placements to show in chat after a competition
  menu: 7 # Number of placements to show in the course selection menu
  personal: 10 # Number of improvements to show for the player's PB log
```

## Efficiency

* Leaderboards use both Tree and Map structures to ensure efficient processing
* The Tree allows for times to be efficiently sorted and queried
* The Map allows for quick lookup of a player's time
* The top few times in a tree are cached to ensure quick queries for the most frequent requests

## PB Leaderboards

* Since personal best leaderboards would require _all_ data to be stored in memory, they are handled differently
* Requesting this leaderboard type makes a direct request to the database
  * This is why PB leaderboards are not shown in the course menu by default

## Competition Leaderboards - WIP

* All data about past competitions are stored in the database
* Currently, there is no way in-game to view this data or any aggregate data
